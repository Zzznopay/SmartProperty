package com.smart.property.system.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 分片合并请求
 *
 * @author zzz
 * @since 2026-07-27
 */
@Data
public class ChunkMergeDTO {

    /** 整个文件的 MD5 */
    @NotBlank(message = "文件MD5不能为空")
    private String fileMd5;

    /** 原始文件名 */
    @NotBlank(message = "文件名不能为空")
    private String fileName;

    /** 分片总数 */
    @Min(value = 1, message = "分片数必须≥1")
    private Integer totalChunks;

    /** 业务类型 */
    private String businessType = "default";

    /** 业务ID */
    private Long businessId;
}
