package com.maphaze.soulforge.filesync.core.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "UploadStatus",description = "文件上传状态")
public enum UploadStatus {
    INITIALIZED("INITIALIZED"),
    IN_PROGRESS("IN_PROGRESS"),
    COMPLETED("COMPLETED"),
    FAILED("FAILED"); // 注意分号

    @EnumValue
    private final String dbValue; // 存储括号中的字符串

    private UploadStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    // 若用 MyBatis-Plus，通过 @EnumValue 标记存储字段
    public String getDbValue() {
        return dbValue;
    }
}