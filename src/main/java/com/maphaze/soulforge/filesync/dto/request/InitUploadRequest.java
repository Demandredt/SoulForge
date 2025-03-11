package com.maphaze.soulforge.filesync.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.beans.factory.annotation.Required;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Map;

/**
 * 文件上传初始化请求参数封装类
 * <p>
 * 用于接收前端传递的文件元数据信息，包括分块哈希、文件大小等
 */
@Data // Lombok 自动生成 getter/setter/toString 等方法
@Schema(description = "文件上传初始化请求参数") // OpenAPI/Swagger 文档描述
public class InitUploadRequest {

    /**
     * 文件名（含扩展名）
     * <p>示例：example.pdf</p>
     */
    @NotBlank(message = "文件名不能为空")
    @Schema(description = "文件名", example = "example.pdf")
    private String filename;

    /**
     * 原文件存储路径（可选）
     * <p>示例：/uploads/2023/example.pdf</p>
     */
    @Schema(description = "原文件存储路径", example = "/uploads/2023/example.pdf")
    private String filepath;

    /**
     * 文件总大小（单位：字节）
     * <p>示例：1048576（表示1MB）</p>
     */
    @NotNull(message = "文件大小不能为空")
    @Positive(message = "文件大小必须为正整数")
    @Schema(description = "文件总大小（字节）", example = "1048576")
    private Long fileSize;

    /**
     * 文件分块哈希映射表
     * <p>
     * - Key：分块序号（从1开始）
     * - Value：对应分块的哈希值（建议使用SHA-256）
     * <p>示例：{ "1": "a1b2c3...", "2": "d4e5f6..." }</p>
     */
    @NotNull(message = "分块哈希表不能为空")
    @Schema(description = "文件分块哈希映射表（Key=分块序号, Value=哈希值）")
    private Map<Integer, String> filePartHash;

    /**
     * 文件整体ETag标识（可选，用于秒传校验）
     * <p>示例：d41d8cd98f00b204e9800998ecf8427e</p>
     */
    @Schema(description = "文件整体ETag标识", example = "d41d8cd98f00b204e9800998ecf8427e")
    private String fileETag;

    /**
     * 文件分块总数
     * <p>示例：5</p>
     */
    @NotNull(message = "分块总数不能为空")
    @Positive(message = "分块总数必须为正整数")
    @Schema(description = "文件分块总数", example = "5")
    private Integer filePartNumber;

    /**
     * 单个分块大小（单位：字节）
     * <p>示例：1048576（表示每块1MB）</p>
     */
    @NotNull(message = "分块大小不能为空")
    @Positive(message = "分块大小必须为正整数")
    @Schema(description = "单个分块大小（字节）", example = "1048576")
    private Long filePartSize;

    @Schema(description = "上传Id，如果非空说明需要断点续传")
    private String uploadId;
}