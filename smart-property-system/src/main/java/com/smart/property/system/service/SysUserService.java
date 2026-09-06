package com.smart.property.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.system.domain.SysUser;
import com.smart.property.system.dto.UserDTO;
import com.smart.property.system.dto.UserQuery;
import com.smart.property.system.vo.UserVO;

/**
 * 用户服务接口
 *
 * @author zzz
 * @since 2026-07-25
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 分页查询用户列表
     */
    PageResult<UserVO> getUserPage(UserQuery query, Long companyId);

    /**
     * 根据ID查询用户详情
     */
    UserVO getUserById(Long id);

    /**
     * 新增用户
     */
    void addUser(UserDTO dto, Long companyId, String operator);

    /**
     * 修改用户
     */
    void updateUser(Long id, UserDTO dto, String operator);

    /**
     * 删除用户
     */
    void deleteUser(Long id);

    /**
     * 重置密码
     */
    void resetPassword(Long id, String newPassword);
}
