package com.maphaze.soulforge.filesync.core.enums.domin;

import io.swagger.v3.oas.annotations.media.Schema;

public class PartInfo {
    @Schema(description = "分块编号")
    private int partNumber;

    @Schema(description = "分块的唯一校验码")
    private String eTag;

    @Schema(description = "分块大小(字节数)")
    private long size;
}
