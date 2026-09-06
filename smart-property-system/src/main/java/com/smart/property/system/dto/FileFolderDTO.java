package com.smart.property.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 文件夹DTO
 *
 * @author zzz
 * @since 2026-07-27
 */
@Data
public class FileFolderDTO {

    @NotBlank(message = "文件夹名不能为空")
    private String folderName;

    private Long parentId;
}
