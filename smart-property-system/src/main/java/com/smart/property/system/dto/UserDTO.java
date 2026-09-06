package com.smart.property.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 用户DTO
 *
 * @author zzz
 * @since 2026-07-25
 */
@Data
public class UserDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long deptId;

    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 64, message = "用户名长度4-64个字符")
    private String username;

    @Size(min = 6, max = 128, message = "密码长度6-128个字符")
    private String password;

    private String realName;

    private String phone;

    private String email;

    private Integer gender;

    private Integer status;

    private String remark;

    /** 角色ID列表 */
    private List<Long> roleIds;
}
