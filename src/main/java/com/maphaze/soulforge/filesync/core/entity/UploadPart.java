package com.maphaze.soulforge.filesync.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("upload_part")
@Schema(description = "实体类，用于记录文件分块上传的分块信息")
public class UploadPart {
    private String uploadId;
    private Integer partNumber;
    private String etag;
}
