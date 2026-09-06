package com.smart.property.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.system.domain.SysRole;
import com.smart.property.system.dto.SysRoleDTO;
import com.smart.property.system.vo.SysRoleVO;

import java.util.List;

public interface SysRoleService extends IService<SysRole> {
    PageResult<SysRoleVO> getRolePage(PageQuery query, Long companyId);
    SysRoleVO getRoleById(Long id);
    void createRole(SysRoleDTO dto, Long companyId, String operator);
    void updateRole(Long id, SysRoleDTO dto, String operator);
    void assignMenus(Long roleId, List<Long> menuIds);
}