package com.smart.property.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 部门 DTO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class SysDeptDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long parentId;

    @NotBlank(message = "部门名称不能为空")
    @Size(max = 64, message = "部门名称长度不能超过64个字符")
    private String deptName;

    private Integer sort;

    private String leader;

    private String phone;

    private String email;

    private Integer status;
}