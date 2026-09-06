package com.smart.property.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.exception.BusinessException;
import com.smart.property.common.core.service.TenantGuard;
import com.smart.property.common.security.util.DesensitizeUtils;
import com.smart.property.common.security.util.EncryptUtils;
import com.smart.property.system.convert.SysUserConverter;
import com.smart.property.system.domain.SysUser;
import com.smart.property.system.domain.SysUserRole;
import com.smart.property.system.dto.UserDTO;
import com.smart.property.system.dto.UserQuery;
import com.smart.property.system.mapper.SysUserMapper;
import com.smart.property.system.mapper.SysUserRoleMapper;
import com.smart.property.system.service.SysUserService;
import com.smart.property.system.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现
 *
 * @author zzz
 * @since 2026-07-25
 */
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final SysUserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;
    private final EncryptUtils encryptUtils;
    private final SysUserConverter sysUserConverter;

    private static final String DEFAULT_PASSWORD = "123456";

    @Override
    public PageResult<UserVO> getUserPage(UserQuery query, Long companyId) {
        Page<SysUser> page = new Page<>(query.getPageNum(), query.getPageSize());

        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getCompanyId, companyId)
                .eq(SysUser::getIsDeleted, 0)
                .like(query.getUsername() != null, SysUser::getUsername, query.getUsername())
                .eq(query.getStatus() != null, SysUser::getStatus, query.getStatus())
                .eq(query.getDeptId() != null, SysUser::getDeptId, query.getDeptId())
                .orderByDesc(SysUser::getCreateTime);

        Page<SysUser> result = baseMapper.selectPage(page, wrapper);

        List<UserVO> records = result.getRecords().stream()
                .map(sysUserConverter::toVO)
                .collect(Collectors.toList());

        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), records);
    }

    @Override
    public UserVO getUserById(Long id) {
        SysUser user = getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return sysUserConverter.toVO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addUser(UserDTO dto, Long companyId, String operator) {
        // 检查用户名唯一性
        Long count = baseMapper.selectCount(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, dto.getUsername())
                        .eq(SysUser::getIsDeleted, 0)
        );
        if (count > 0) {
            throw new BusinessException("用户名已存在");
        }

        SysUser user = sysUserConverter.toEntity(dto);
        user.setCompanyId(companyId);
        user.setPassword(passwordEncoder.encode(dto.getPassword() != null ? dto.getPassword() : DEFAULT_PASSWORD));
        if (dto.getPhone() != null) {
            user.setPhone(encryptUtils.encrypt(dto.getPhone()));
            user.setPhoneMask(DesensitizeUtils.maskPhone(dto.getPhone()));
        }
        user.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        user.setCreateBy(operator);

        baseMapper.insert(user);

        // 保存用户角色关联
        if (dto.getRoleIds() != null && !dto.getRoleIds().isEmpty()) {
            for (Long roleId : dto.getRoleIds()) {
                SysUserRole userRole = new SysUserRole();
                userRole.setUserId(user.getId());
                userRole.setRoleId(roleId);
                userRoleMapper.insert(userRole);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(Long id, UserDTO dto, String operator) {
        SysUser user = getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        TenantGuard.requireSameCompany(user.getCompanyId());

        SysUser patch = sysUserConverter.toEntity(dto);
        user.setDeptId(patch.getDeptId());
        user.setRealName(patch.getRealName());
        if (dto.getPhone() != null) {
            user.setPhone(encryptUtils.encrypt(dto.getPhone()));
            user.setPhoneMask(DesensitizeUtils.maskPhone(dto.getPhone()));
        }
        user.setEmail(patch.getEmail());
        user.setGender(patch.getGender());
        user.setStatus(patch.getStatus());
        user.setRemark(patch.getRemark());
        user.setUpdateBy(operator);

        baseMapper.updateById(user);

        // 更新用户角色关联
        if (dto.getRoleIds() != null) {
            userRoleMapper.delete(
                    new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, id)
            );
            for (Long roleId : dto.getRoleIds()) {
                SysUserRole userRole = new SysUserRole();
                userRole.setUserId(id);
                userRole.setRoleId(roleId);
                userRoleMapper.insert(userRole);
            }
        }
    }

    @Override
    public void deleteUser(Long id) {
        SysUser user = getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        TenantGuard.requireSameCompany(user.getCompanyId());
        baseMapper.deleteById(id);
    }

    @Override
    public void resetPassword(Long id, String newPassword) {
        SysUser user = getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        TenantGuard.requireSameCompany(user.getCompanyId());
        user.setPassword(passwordEncoder.encode(newPassword != null ? newPassword : DEFAULT_PASSWORD));
        baseMapper.updateById(user);
    }
}