package com.smart.property.system.service;

import com.smart.property.common.core.domain.PageResult;
import com.smart.property.system.dto.UserDTO;
import com.smart.property.system.dto.UserQuery;
import com.smart.property.system.vo.UserVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户服务测试
 *
 * @author zzz
 * @since 2026-07-25
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SysUserServiceTest {

    @Autowired
    private SysUserService userService;

    @Test
    void testGetUserPage() {
        UserQuery query = new UserQuery();
        query.setPageNum(1);
        query.setPageSize(10);

        PageResult<UserVO> result = userService.getUserPage(query, 1L);

        assertNotNull(result);
        assertNotNull(result.getRecords());
    }

    @Test
    void testGetUserById() {
        UserVO user = userService.getUserById(1L);

        assertNotNull(user);
        assertEquals(1L, user.getId());
        assertEquals("admin", user.getUsername());
    }

    @Test
    void testAddUser() {
        UserDTO dto = new UserDTO();
        dto.setUsername("testuser");
        dto.setPassword("test123456");
        dto.setRealName("测试用户");
        dto.setPhone("13800138000");
        dto.setGender(1);
        dto.setStatus(1);

        userService.addUser(dto, 1L, "admin");

        // 验证用户已创建
        UserQuery query = new UserQuery();
        query.setUsername("testuser");
        PageResult<UserVO> result = userService.getUserPage(query, 1L);

        assertFalse(result.getRecords().isEmpty());
        assertEquals("testuser", result.getRecords().get(0).getUsername());
    }

    @Test
    void testUpdateUser() {
        UserDTO dto = new UserDTO();
        dto.setRealName("更新后的名称");
        dto.setPhone("13900139000");
        dto.setGender(2);
        dto.setStatus(1);

        userService.updateUser(1L, dto, "admin");

        UserVO user = userService.getUserById(1L);
        assertEquals("更新后的名称", user.getRealName());
    }

    @Test
    void testResetPassword() {
        userService.resetPassword(1L, "newpassword123");

        // 验证密码已重置（通过登录测试验证）
        assertNotNull(userService.getUserById(1L));
    }
}
