package com.smart.property.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.property.system.domain.SysMenu;
import com.smart.property.system.dto.SysMenuDTO;
import com.smart.property.system.vo.MenuTreeNode;

import java.util.List;

public interface SysMenuService extends IService<SysMenu> {
    List<MenuTreeNode> getMenuTree();
    MenuTreeNode getMenuById(Long id);
    void createMenu(SysMenuDTO dto, String operator);
    void updateMenu(Long id, SysMenuDTO dto, String operator);
}