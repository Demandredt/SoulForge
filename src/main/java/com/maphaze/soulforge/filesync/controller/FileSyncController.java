package com.maphaze.soulforge.filesync.controller;

import com.maphaze.soulforge.core.utils.ResponseUtil;
import com.maphaze.soulforge.filesync.core.domin.FileMetaData;
import com.maphaze.soulforge.filesync.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/fileSync")
public class FileSyncController {

    @Autowired
    private FileService fileService;

    @Operation(summary = "初始化文件上传",description = "创建上传任务并且返回uploadId")
    @PostMapping("/upload")
    public ResponseUtil initUpload(
            @RequestPart("file")
            @Parameter(description = "文件内容（首块或者占位文件）")MultipartFile file,
            @RequestPart("metadata")
            @Parameter(description = "文件元数据") FileMetaData fileMetaData
            ){



    }


}
