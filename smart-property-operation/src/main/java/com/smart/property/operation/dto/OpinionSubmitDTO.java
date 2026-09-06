package com.smart.property.operation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 意见提交 DTO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class OpinionSubmitDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "意见箱ID不能为空")
    private Long boxId;

    @NotBlank(message = "标题不能为空")
    private String title;

    private String content;

    private Integer isAnonymous;

    private String images;

    private Integer satisfaction;

    private String remark;
}