package com.maphaze.soulforge.filesync.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Schema(description = "文件上传请求封装类")
public class UploadRequest {


    @Schema(description = "上传Id")
    String uploadId;

    @Schema(description = "上传分块的序号")
    Integer partNumber;

    @Schema(description = "上传分块的哈希值")
    String partHash;

    @Schema(description = "上传分块")
    MultipartFile file;
}
