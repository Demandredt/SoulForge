package com.maphaze.soulforge.filesync.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.maphaze.soulforge.core.utils.ResponseUtil;
import com.maphaze.soulforge.filesync.core.entity.UploadTask;
import com.maphaze.soulforge.filesync.core.enums.UploadStatus;
import com.maphaze.soulforge.filesync.dto.request.InitUploadRequest;
import com.maphaze.soulforge.filesync.dto.request.UploadRequest;
import com.maphaze.soulforge.filesync.mapper.UploadTaskMapper;
import com.maphaze.soulforge.filesync.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@MapperScan("com.maphaze.soulforge.filesync.mapper")
@RequestMapping("/api/fileSync")
@Slf4j
public class FileSyncController {


    @Autowired
    private FileService fileService;

    @Autowired
    private UploadTaskMapper uploadTaskMapper;

    @Operation(summary = "初始化文件上传",description = "创建上传任务并且返回uploadId")
    @PostMapping("/initUpload")
    public ResponseUtil initUpload(@RequestBody InitUploadRequest request){

//        数据库中有该完整文件的hash，说明是断点续传！
        String fileTotalHash = request.getFileETag();
        UploadTask continueUplaod =  uploadTaskMapper.selectOne(new QueryWrapper<UploadTask>().eq("final_etag",fileTotalHash).eq("status",UploadStatus.IN_PROGRESS));
//        说明是断点续传任务
        if (continueUplaod!=null){
            log.debug("请求已找到，进入断点续传流程");
            Map<Integer, String> integerStringMap = fileService.returnExistingParts(request.getUploadId());
            return ResponseUtil.success("uploadId",continueUplaod.getUploadId());
        }
//        返回uploadId
        String uploadId = fileService.initUpload(request);
        log.info("返回uploadId{}",uploadId);
        return ResponseUtil.success("uploadId",uploadId);
    }



    @Operation(summary = "文件上传",description = "接收分块的文件并且上传到minio桶内，返回期待接收的文件序号List")
    @PostMapping("/upload")
    public ResponseUtil upload(@ModelAttribute UploadRequest uploadRequest,
                               @RequestParam("file")MultipartFile file
                               ){
        uploadRequest.setFile(file);
        log.debug("接收到分块id：{}，分块序号：{}进入fileservice上传流程",uploadRequest.getUploadId(),uploadRequest.getPartNumber());
        fileService.changeStatus(uploadRequest.getUploadId(), UploadStatus.IN_PROGRESS);
        List<Integer> partsWanted = fileService.uploadPart(uploadRequest);
        if (partsWanted==null){
            fileService.changeStatus(uploadRequest.getUploadId(), UploadStatus.FAILED);
            log.error("分片上传返回的期望分块List为null");
            return ResponseUtil.fail(422,"分片上传时出错");
        }
        if (partsWanted.isEmpty()){
            if (fileService.composeFile(uploadRequest.getUploadId())==0) {
                fileService.changeStatus(uploadRequest.getUploadId(), UploadStatus.FAILED);
                log.error("文件合并校验失败");
                return ResponseUtil.fail(422,"文件合并校验失败，请重传");
            }else {
                log.info("上传成功");
                fileService.changeStatus(uploadRequest.getUploadId(), UploadStatus.COMPLETED);
                return ResponseUtil.success("上传成功");
            }
        }

        return ResponseUtil.success(partsWanted);
    }




}
