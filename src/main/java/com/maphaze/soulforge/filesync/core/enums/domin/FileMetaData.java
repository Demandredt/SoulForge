package com.maphaze.soulforge.filesync.core.enums.domin;

import com.maphaze.soulforge.filesync.core.enums.FileType;
import com.maphaze.soulforge.filesync.core.enums.UploadStatus;
import io.minio.messages.Part;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


import java.time.ZonedDateTime;
import java.util.List;

@Schema(name = "FileMetaData",description = "文件元数据")
public class FileMetaData {
    @Schema(description = "文件UUID")
    private String fileId;

    @Schema(description = "文件的原文件名")
    private String originName;

    @Schema(description = "文件类型")
    private FileType type;

    @Schema(description = "文件上传状态")
    private UploadStatus status;

    @Schema(description = "文件存储路径")
    private String objectKey;

    @Schema(description = "创建时间")
    private ZonedDateTime createdAt;



}
