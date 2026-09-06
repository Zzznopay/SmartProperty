package com.smart.property.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 物业公司 DTO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class SysCompanyDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long parentId;

    @NotBlank(message = "公司名称不能为空")
    @Size(max = 128, message = "公司名称长度不能超过128个字符")
    private String companyName;

    @Size(max = 64, message = "公司编码长度不能超过64个字符")
    private String companyCode;

    private String contactName;

    private String contactPhone;

    private String address;

    private String logo;

    private Integer status;

    private String remark;
}