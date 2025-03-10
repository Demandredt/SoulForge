package com.maphaze.soulforge.filesync.controller;

import com.maphaze.soulforge.core.utils.ResponseUtil;
import com.maphaze.soulforge.filesync.dto.request.InitUploadRequest;
import com.maphaze.soulforge.filesync.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@MapperScan("com.maphaze.soulforge.filesync.mapper")
@RequestMapping("/api/fileSync")

public class FileSyncController {

    @Autowired
    private FileService fileService;

    @Operation(summary = "初始化文件上传",description = "创建上传任务并且返回uploadId")
    @PostMapping("/initUpload")
    public ResponseUtil initUpload(@RequestBody InitUploadRequest request){

        String uploadId = fileService.initUpload(request);

//        返回uploadId
        return ResponseUtil.success("uploadId",uploadId);
    }


}
