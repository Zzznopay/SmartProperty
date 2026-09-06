package com.smart.property.operation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 规章制度 DTO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class RegulationDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "标题不能为空")
    private String title;

    private String content;

    private String category;

    private String fileUrl;

    private String fileName;

    private Integer isPublish;

    private Integer status;

    private String remark;
}