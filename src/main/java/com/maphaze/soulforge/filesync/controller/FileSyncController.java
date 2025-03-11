package com.maphaze.soulforge.filesync.controller;

import com.maphaze.soulforge.core.utils.ResponseUtil;
import com.maphaze.soulforge.filesync.core.enums.UploadStatus;
import com.maphaze.soulforge.filesync.dto.request.InitUploadRequest;
import com.maphaze.soulforge.filesync.dto.request.UploadRequest;
import com.maphaze.soulforge.filesync.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@MapperScan("com.maphaze.soulforge.filesync.mapper")
@RequestMapping("/api/fileSync")

public class FileSyncController {

    @Autowired
    private FileService fileService;

    @Operation(summary = "初始化文件上传",description = "创建上传任务并且返回uploadId")
    @PostMapping("/initUpload")
    public ResponseUtil initUpload(@RequestBody InitUploadRequest request){
//        前端携带UploadId，说明是断点续传任务
        if (request.getUploadId()!=null){
            Map<Integer, String> integerStringMap = fileService.returnExistingParts(request.getUploadId());
            return ResponseUtil.success("parts",integerStringMap);
        }
//        返回uploadId
        String uploadId = fileService.initUpload(request);
        return ResponseUtil.success("uploadId",uploadId);
    }
    @Operation(summary = "文件上传",description = "接收分块的文件并且上传到minio桶内，返回期待接收的文件序号List")
    @PostMapping("/upload")
    public ResponseUtil upload(@RequestBody UploadRequest uploadRequest){
        List<Integer> partsWanted = fileService.uploadPart(uploadRequest);
        if (partsWanted==null){
            fileService.changeStatus(uploadRequest.getUploadId(), UploadStatus.FAILED);
            return ResponseUtil.fail(422,"分片上传时出错");
        }
        if (partsWanted.isEmpty()){
            if (fileService.composeFile(uploadRequest.getUploadId())==0) {
                fileService.changeStatus(uploadRequest.getUploadId(), UploadStatus.FAILED);
                return ResponseUtil.fail(422,"文件合并校验失败，请重传");
            }else {
                fileService.changeStatus(uploadRequest.getUploadId(), UploadStatus.COMPLETED);
                return ResponseUtil.success("上传成功");
            }
        }

        return ResponseUtil.success(partsWanted);
    }




}
