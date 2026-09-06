package com.smart.property.property.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 业主 DTO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class OwnerDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String ownerCode;

    @NotBlank(message = "业主姓名不能为空")
    @Size(max = 64, message = "业主姓名长度不能超过64个字符")
    private String ownerName;

    private Integer gender;

    private String idCard;

    private String phone;

    private String email;

    private String wechat;

    private String address;

    /** 业主类型：1 个人 2 企业 */
    private Integer ownerType;

    private String emergencyContact;

    private String emergencyPhone;

    private Integer status;

    private String remark;
}