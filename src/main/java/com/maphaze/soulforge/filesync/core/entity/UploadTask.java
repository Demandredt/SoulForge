package com.maphaze.soulforge.filesync.core.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.maphaze.soulforge.filesync.core.enums.UploadStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@TableName("upload_task")
@Schema(description = "实体类，记录文件分块上传的总信息")
public class UploadTask {
    @TableId
    private String uploadId;
    private String bucket;
    private String objectKey;
    private UploadStatus status;
    private LocalDateTime createdAt;
    private String finalEtag;


    @TableField(exist = false)
    private Map<Integer,String> parts;

    public UploadTask(String uploadId, String bucket, String objectKey, UploadStatus status, LocalDateTime createdAt, String finalEtag, Map<Integer, String> parts) {
        this.uploadId = uploadId;
        this.bucket = bucket;
        this.objectKey = objectKey;
        this.status = status;
        this.createdAt = createdAt;
        this.finalEtag = finalEtag;
        this.parts = parts;
    }
}
