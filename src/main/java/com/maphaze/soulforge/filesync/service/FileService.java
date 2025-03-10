package com.maphaze.soulforge.filesync.service;

import com.maphaze.soulforge.filesync.core.entity.UploadTask;
import com.maphaze.soulforge.filesync.core.enums.UploadStatus;
import com.maphaze.soulforge.filesync.dto.request.InitUploadRequest;
import com.maphaze.soulforge.filesync.mapper.UploadPartMapper;
import com.maphaze.soulforge.filesync.mapper.UploadTaskMapper;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
public class FileService {

        @Autowired
        private MinioClient minioclient;
        @Autowired
        private UploadPartMapper uploadPartMapper;
        @Autowired
        private UploadTaskMapper uploadTaskMapper;
        @Value("${soul-forge.minio.bucket}")
        private String bucket;

        /**
         * 初始化文件上传，返回UploadId
         * @param filename
         * @param originPath 文件原路径
         * @param fileSize
         * @param fileHash  分块md5值对应
         * @param fileETag  文件总md5值
         * @param filePartNumber
         * @param filePartSize
         * @return
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
//                todo：根据用户id，在Minio存储桶中分配不同的路径
                String objectKey = "/Admin"+originPath;
                UploadTask uploadTask = new UploadTask(uploadId,
                        bucket,
                        objectKey,
                        UploadStatus.INITIALIZED,
                        LocalDateTime.now(),
                        fileETag,
                        fileHash
                        );
                System.out.println(uploadTask);
                return uploadId;
        }


        /**
         * 从数据库中获取UploadTask类，自动注入parts
         * @param uploadId
         * @return
         */
        public UploadTask getUploadTaskFromDataBase(String uploadId){
                UploadTask uploadTask = uploadTaskMapper.selectById(uploadId);
                if (uploadTask == null){
                        return null;
                }
                Map<Integer, String> map = uploadPartMapper.selectFileUploadParts(uploadId);
                uploadTask.setParts(map);
                return uploadTask;
        }

}
