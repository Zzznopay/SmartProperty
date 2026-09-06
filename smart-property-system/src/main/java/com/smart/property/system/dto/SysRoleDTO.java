package com.smart.property.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 角色 DTO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class SysRoleDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "角色名称不能为空")
    @Size(max = 64, message = "角色名称长度不能超过64个字符")
    private String roleName;

    @NotBlank(message = "角色标识不能为空")
    @Size(max = 64, message = "角色标识长度不能超过64个字符")
    private String roleKey;

    private Integer sort;

    private Integer status;

    private String remark;
}