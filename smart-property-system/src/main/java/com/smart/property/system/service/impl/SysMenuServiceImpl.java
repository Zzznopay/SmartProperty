package com.smart.property.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.property.common.core.exception.BusinessException;
import com.smart.property.system.convert.SysMenuConverter;
import com.smart.property.system.domain.SysMenu;
import com.smart.property.system.dto.SysMenuDTO;
import com.smart.property.system.mapper.SysMenuMapper;
import com.smart.property.system.service.SysMenuService;
import com.smart.property.system.vo.MenuTreeNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    private final SysMenuConverter sysMenuConverter;

    @Override
    public List<MenuTreeNode> getMenuTree() {
        List<SysMenu> all = baseMapper.selectList(
                new LambdaQueryWrapper<SysMenu>()
                        .orderByAsc(SysMenu::getSort)
        );
        return buildTree(all);
    }

    @Override
    public MenuTreeNode getMenuById(Long id) {
        SysMenu menu = getById(id);
        if (menu == null) throw new BusinessException("菜单不存在");
        return sysMenuConverter.toTreeNode(menu);
    }

    @Override
    public void createMenu(SysMenuDTO dto, String operator) {
        SysMenu menu = sysMenuConverter.toEntity(dto);
        menu.setCreateBy(operator);
        save(menu);
    }

    @Override
    public void updateMenu(Long id, SysMenuDTO dto, String operator) {
        SysMenu existing = getById(id);
        if (existing == null) throw new BusinessException("菜单不存在");
        SysMenu menu = sysMenuConverter.toEntity(dto);
        menu.setId(id);
        menu.setCreateBy(existing.getCreateBy());
        menu.setCreateTime(existing.getCreateTime());
        menu.setUpdateBy(operator);
        updateById(menu);
    }

    private List<MenuTreeNode> buildTree(List<SysMenu> all) {
        List<MenuTreeNode> nodes = sysMenuConverter.toTreeNodeList(all);
        Map<Long, MenuTreeNode> map = new HashMap<>();
        for (MenuTreeNode n : nodes) {
            map.put(n.getId(), n);
        }
        List<MenuTreeNode> roots = new ArrayList<>();
        for (MenuTreeNode n : map.values()) {
            if (n.getParentId() == null || n.getParentId() == 0) {
                roots.add(n);
            } else {
                MenuTreeNode parent = map.get(n.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) parent.setChildren(new ArrayList<>());
                    parent.getChildren().add(n);
                }
            }
        }
        return roots;
    }
}