package com.smart.property.operation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 意见箱 DTO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class OpinionBoxDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long communityId;

    @NotBlank(message = "意见箱名称不能为空")
    private String boxName;

    private Long adminUserId;

    private String adminUserName;

    private Integer isAnonymous;

    private Integer isActive;

    private String remark;
}