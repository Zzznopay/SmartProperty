package com.smart.property.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 网盘文件 DTO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class FileDiskDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "文件ID不能为空")
    private Long fileId;

    private Long folderId;

    @NotBlank(message = "文件名不能为空")
    private String fileName;
}
