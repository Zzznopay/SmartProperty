package com.smart.property.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 菜单 DTO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class SysMenuDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long parentId;

    @NotBlank(message = "菜单名称不能为空")
    @Size(max = 64, message = "菜单名称长度不能超过64个字符")
    private String menuName;

    private String path;

    private String component;

    private String perms;

    private String icon;

    /** 菜单类型：M 目录 C 菜单 F 按钮 */
    private String menuType;

    private Integer sort;

    private Integer visible;

    private Integer status;
}