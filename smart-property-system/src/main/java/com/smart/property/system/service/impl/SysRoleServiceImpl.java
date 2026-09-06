package com.smart.property.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.exception.BusinessException;
import com.smart.property.system.convert.SysRoleConverter;
import com.smart.property.system.domain.SysRole;
import com.smart.property.system.domain.SysRoleMenu;
import com.smart.property.system.dto.SysRoleDTO;
import com.smart.property.system.mapper.SysRoleMapper;
import com.smart.property.system.mapper.SysRoleMenuMapper;
import com.smart.property.system.service.SysRoleService;
import com.smart.property.system.vo.SysRoleVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    private final SysRoleMenuMapper sysRoleMenuMapper;
    private final SysRoleConverter sysRoleConverter;

    @Override
    public PageResult<SysRoleVO> getRolePage(PageQuery query, Long companyId) {
        Page<SysRole> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getCompanyId, companyId);
        Page<SysRole> result = baseMapper.selectPage(page, wrapper);
        List<SysRoleVO> records = sysRoleConverter.toVOList(result.getRecords());
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), records);
    }

    @Override
    public SysRoleVO getRoleById(Long id) {
        SysRole role = getById(id);
        if (role == null) throw new BusinessException("角色不存在");
        return sysRoleConverter.toVO(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createRole(SysRoleDTO dto, Long companyId, String operator) {
        SysRole role = sysRoleConverter.toEntity(dto);
        role.setCompanyId(companyId);
        role.setCreateBy(operator);
        save(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(Long id, SysRoleDTO dto, String operator) {
        SysRole existing = getById(id);
        if (existing == null) throw new BusinessException("角色不存在");
        SysRole role = sysRoleConverter.toEntity(dto);
        role.setId(id);
        role.setCompanyId(existing.getCompanyId());
        role.setCreateBy(existing.getCreateBy());
        role.setCreateTime(existing.getCreateTime());
        role.setUpdateBy(operator);
        updateById(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignMenus(Long roleId, List<Long> menuIds) {
        getRoleById(roleId);
        sysRoleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>()
                .eq(SysRoleMenu::getRoleId, roleId));
        if (menuIds != null) {
            for (Long menuId : menuIds) {
                SysRoleMenu rm = new SysRoleMenu();
                rm.setRoleId(roleId);
                rm.setMenuId(menuId);
                sysRoleMenuMapper.insert(rm);
            }
        }
    }
}