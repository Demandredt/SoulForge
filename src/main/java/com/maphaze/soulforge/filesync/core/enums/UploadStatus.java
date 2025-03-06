package com.maphaze.soulforge.filesync.core.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "UploadStatus",description = "文件上传状态")
public enum UploadStatus {

    @Schema(description = "已经生成元数据，尚未上传")
    INITIALIZED,
    @Schema(description = "分块上传中")
    IN_PROGRESS,
    @Schema(description = "全部分块上传完成并且合并成功")
    COMPLETED,
    @Schema(description = "上传失败")
    FAILED
}
