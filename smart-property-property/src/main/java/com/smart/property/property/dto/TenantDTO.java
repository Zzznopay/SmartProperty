package com.smart.property.property.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 租户 DTO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class TenantDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String tenantCode;

    @NotBlank(message = "租户姓名不能为空")
    @Size(max = 64, message = "租户姓名长度不能超过64个字符")
    private String tenantName;

    private Integer gender;

    private String idCard;

    private String phone;

    private String email;

    private String companyName;

    private Integer status;

    private String remark;
}