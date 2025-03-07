package com.maphaze.soulforge.filesync.controller;

import com.maphaze.soulforge.core.utils.ResponseUtil;
import com.maphaze.soulforge.filesync.core.domin.FileMetaData;
import com.maphaze.soulforge.filesync.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@MapperScan("com.maphaze.soulforge.filesync.mapper")
@RequestMapping("/api/fileSync")
public class FileSyncController {

    @Autowired
    private FileService fileService;

    @Operation(summary = "初始化文件上传",description = "创建上传任务并且返回uploadId")
    @PostMapping("/upload")
    public ResponseUtil initUpload(
            @RequestPart("filename")
            @Parameter(description = "文件名") String filename,
            @RequestPart("filepath")
            @Parameter(description = "原文件路径") String originPath,
            @RequestPart("fileSize")
            @Parameter(description = "文件大小") String fileSize,
            @RequestPart("filePartHash")
            @Parameter(description = "文件分块的哈希值") Map<Integer,String> fileHash,
            @RequestPart("fileETag")
            @Parameter(description = "文件总哈希值") String fileETag,
            @RequestPart("filePartNumber")
            @Parameter(description = "文件分块数量") String filePartNumber,
            @RequestPart("filePartSize")
            @Parameter(description = "文件分块大小") String filePartSize
            ){

        String uploadId = fileService.initUpload(filename, originPath, fileSize, fileHash, fileETag, filePartNumber, filePartSize);

//        返回uploadId
        return ResponseUtil.success(uploadId);
    }


}
