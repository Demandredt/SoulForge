package com.maphaze.soulforge.filesync.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.maphaze.soulforge.filesync.core.entity.UploadPart;
import com.maphaze.soulforge.filesync.core.entity.UploadTask;
import com.maphaze.soulforge.filesync.core.enums.UploadStatus;
import com.maphaze.soulforge.filesync.dto.request.InitUploadRequest;
import com.maphaze.soulforge.filesync.dto.request.UploadRequest;
import com.maphaze.soulforge.filesync.mapper.UploadPartMapper;
import com.maphaze.soulforge.filesync.mapper.UploadTaskMapper;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.CRC32;

@Service
@Slf4j
public class FileService {

        @Autowired
        private MinioClient minioclient;
        @Autowired
        private UploadPartMapper uploadPartMapper;
        @Autowired
        private UploadTaskMapper uploadTaskMapper;
        @Value("${soul-forge.minio.bucket}")
        private String bucket;

        List<Integer> existingParts = new LinkedList<>();

        /**
         * 初始化文件上传，返回UploadId
         *
         */
        public String initUpload(InitUploadRequest request){
                String filename = request.getFilename();
                String originPath = request.getFilepath();
                Long fileSize = request.getFileSize();
                Map<Integer, String> fileHash = request.getFilePartHash();
                String fileETag = request.getFileETag();
                Integer filePartNumber = request.getFilePartNumber();
                Long filePartSize = request.getFilePartSize();



//               生成随机的UploadId
                String uploadId = String.valueOf(UUID.randomUUID());

//                MINIO中的存储路径
//                todo：根据用户id，在Minio存储桶中分配不同的路径.注意这里Admin前不能有斜杠，应该放在admin后面
                String objectKey = "Admin/"+originPath;
                log.info("存储路径：{}",objectKey);
                UploadTask uploadTask = new UploadTask(uploadId,
                        bucket,
                        objectKey,
                        UploadStatus.INITIALIZED,
                        LocalDateTime.now(),
                        fileETag,
                        fileHash
                        );
//                存储分块对应表
                fileHash.forEach((key,value)->{
                        UploadPart uploadPart = new UploadPart(uploadId,key,value);
                        uploadPartMapper.insert(uploadPart);
                });
                uploadTaskMapper.insert(uploadTask);


                return uploadId;
        }


        /**
         * 从数据库中读取UploadTask类，并且自动注入parts到uploadTask类中
         * @param uploadId
         * @return
         */
        public UploadTask getUploadTaskFromDataBase(String uploadId){
                UploadTask uploadTask = uploadTaskMapper.selectById(uploadId);
                if (uploadTask == null){
                        return null;
                }
                Map<Integer, String> map = getUploadPartsMap(uploadId);
                uploadTask.setParts(map);
                return uploadTask;
        }


        /**
         * 从数据库获取未接收到的分块，返回前端进行断点续传
         * @param uploadId
         * @return
         */
        public Map<Integer,String> returnExistingParts(String uploadId){
                Map<Integer,String> parts = getUploadPartsMap(uploadId);
                return parts;
        }


        /**
         * 上传文件分块
         * @param request upload请求封装
         * @return null-文件上传出错。正常情况返回期待收到的分块序号列表，重传则返回需要重传的序号
         */
        public List<Integer> uploadPart(UploadRequest request){
                String uploadId = request.getUploadId();
                Integer partNumber = request.getPartNumber();
                String partHash = request.getPartHash();
                MultipartFile file = request.getFile();

                String crc32 = getCRC32(file);
                log.debug("请求中的文件计算出的CRC32校验值{}",crc32);
//                校验值不同则重传
                if (!partHash.equals(crc32)){
                        System.out.printf("校验值不同：计算：%s，请求中：%s\n",crc32,partHash);
                        return List.of(partNumber);
                }

                UploadTask uploadTask = getUploadTaskFromDataBase(uploadId);

                try {

                        minioclient.putObject(PutObjectArgs.builder()
                                .bucket(bucket)
                                .object(uploadTask.getObjectKey()+"_"+Integer.toString(partNumber))
                                .stream(file.getInputStream(), file.getSize(), -1)
                                .contentType(file.getContentType())
                                .build());
                }catch (Exception e){
                        System.out.println("文件上传到minio时错误");
                        e.printStackTrace();
                        return null;
                }
                QueryWrapper<UploadPart> wrapper = new QueryWrapper<>();
                wrapper.eq("upload_id",uploadId).eq("part_number",partNumber);
                uploadPartMapper.delete(wrapper);
                List<Integer> list = new ArrayList<>();
                getUploadPartsMap(uploadId).forEach((key,value)-> {
                        list.add(key);
                });
        return list;

        }

        /**
         * 计算CRC32校验值，不反转，不异或
         * @param file 待校验的文件
         * @return
         */
        public String getCRC32(MultipartFile file) {
                try (InputStream in = file.getInputStream()) {
                        CRC32 crc32 = new CRC32();
                        byte[] buffer = new byte[8192];
                        int bytesRead;


                        while ((bytesRead = in.read(buffer)) != -1) {
                                crc32.update(buffer, 0, bytesRead);
                        }


                        long result = crc32.getValue();
                        return Long.toString(result);

                } catch (IOException e) {
                        System.out.println("CRC32校验时读取文件错误");
                        return "0";
                }
        }


        /**
         * 检查校验值，合并文件
         * @param uploadId 上传id
         * @return 0代表校验失败，重新上传；1代表成功
         */
        public int composeFile(String uploadId){
                UploadTask uploadTask = getUploadTaskFromDataBase(uploadId);
//                先获取总的CRC校验值
                CRC32 crc32 = new CRC32();
                Long totalCrc = Long.parseLong(uploadTask.getFinalEtag());
                List<String> partsName = new ArrayList<>();
                try {
                        for (Result<Item> result : minioclient.listObjects(ListObjectsArgs.builder()
                                .bucket(bucket)
                                .prefix(uploadTask.getObjectKey() + "_")
                                .build())) {

                                partsName.add(result.get().objectName());
                        }
                }catch (Exception e){
                        System.out.println("合并文件出错");
                        e.printStackTrace();
                }



//                要考虑文件名中有_的情况
//                partsName.sort(Comparator.comparingInt(name -> Integer.parseInt(name.split("_")[1])));

                partsName.sort(Comparator.comparingInt(name -> {
                        String[] parts = name.split("_");
                        return Integer.parseInt(parts[parts.length - 1]); // 取最后一个元素
                }));



                for (String partName : partsName){
                        try(InputStream stream = minioclient.getObject(
                                GetObjectArgs.builder()
                                        .bucket(bucket)
                                        .object(partName)
                                        .build()
                        )) {
                                byte[] buffer = new byte[8192];
                                int bytesRead = 0;
                                while ((bytesRead=stream.read(buffer))!=-1){
                                        crc32.update(buffer,0,bytesRead);
                                }


                        }
                        catch (Exception e){
                                System.out.println("合并时校验出错");
                                e.printStackTrace();
                        }
                }

                long crc32ValueFromParts = crc32.getValue();

                List<ComposeSource> sources = partsName.stream()
                        .map(name->{
                                return ComposeSource.builder().bucket(bucket).object(name).build();
                        })
                        .collect(Collectors.toList());
                try{
//
                minioclient.composeObject(ComposeObjectArgs.builder()
                        .bucket(bucket)
                        .object(uploadTask.getObjectKey())
                        .sources(sources)
                        .build());

//                删除分块文件
                        for (String partName :partsName){
                                minioclient.removeObject(
                                        RemoveObjectArgs.builder()
                                                .bucket(bucket)
                                                .object(partName)
                                                .build()
                                );
                        }


                int bytesRead = 0;
                byte[] buffer = new byte[8192];
                CRC32 crcTotal = new CRC32();
                InputStream in = minioclient.getObject (GetObjectArgs.builder()
                        .bucket(bucket)

                        .object(uploadTask.getObjectKey())
                        .build());
                while ((bytesRead=in.read(buffer))!=-1){
                        crcTotal.update(buffer,0,bytesRead);
                }
                totalCrc = crcTotal.getValue();





                in.close();
                }catch (Exception e){
                        System.out.println("校验整体文件时出错");
                        e.printStackTrace();
                }

                if (totalCrc!=crc32ValueFromParts){
                        return 0;
                }else {
                        return 1;
                }


        }


        /**
         * 改变uploadTask中状态字段
         * @param uploadId
         * @return
         */
        public int changeStatus(String uploadId,UploadStatus status){
                uploadTaskMapper.update(Wrappers.<UploadTask>update()
                        .set("status",status)
                        .eq("upload_id",uploadId)
                );
                return 0;
        }


        /**
         * 从数据库读取UploadPart为一个Map
         * @param uploadId
         * @return
         */
        public Map<Integer,String> getUploadPartsMap(String uploadId){
                Map<Integer, UploadPart> uploadParts = uploadPartMapper.selectFileUploadParts(uploadId);

                return uploadParts.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                        entry->entry.getValue().getEtag()));
        }




}
