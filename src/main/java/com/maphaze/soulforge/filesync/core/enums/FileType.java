package com.maphaze.soulforge.filesync.core.enums;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 文件分类
 */
@Schema(name = "FileType",description = "枚举类，用于列举文件分类")
public enum FileType {
    IMAGE,
    DOCUMENT,
    CODE,
    OTHER
}
