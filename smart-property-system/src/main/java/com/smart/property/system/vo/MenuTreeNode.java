package com.smart.property.system.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 菜单树节点 VO
 *
 * @author zzz
 * @since 2026-07-28
 */
@Data
public class MenuTreeNode implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long parentId;
    private String menuName;
    private String path;
    private String component;
    private String perms;
    private String icon;
    private String menuType;
    private Integer sort;
    private Integer visible;
    private Integer status;
    private List<MenuTreeNode> children;
}
