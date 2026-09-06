package com.smart.property.system.controller;

import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import com.smart.property.system.dto.LoginDTO;
import com.smart.property.common.redis.util.RedisUtils;
import com.smart.property.system.service.AuthService;
import com.smart.property.system.vo.LoginVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 认证控制器测试
 *
 * @author zzz
 * @since 2026-07-25
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuthControllerTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private RedisUtils redisUtils;

    private void injectCaptcha(String key) {
        redisUtils.set("auth:captcha:" + key, "abcd", 300, java.util.concurrent.TimeUnit.SECONDS);
    }

    @Test
    void testLogin() {
        injectCaptcha("ctl-login-1");
        LoginDTO dto = new LoginDTO();
        dto.setUsername("admin");
        dto.setPassword("admin123");
        dto.setCaptchaKey("ctl-login-1");
        dto.setCaptchaCode("abcd");

        LoginVO result = SaTokenContextMockUtil.setMockContext(() -> authService.login(dto, "127.0.0.1", null));

        assertNotNull(result);
        assertNotNull(result.getAccessToken());
        assertNotNull(result.getUserInfo());
        assertEquals("admin", result.getUserInfo().getUsername());
    }

    @Test
    void testLoginWithWrongPassword() {
        LoginDTO dto = new LoginDTO();
        dto.setUsername("admin");
        dto.setPassword("wrong_password");

        assertThrows(Exception.class, () -> authService.login(dto, "127.0.0.1", null));
    }

    @Test
    void testGetUserInfo() {
        LoginVO.UserInfoVO userInfo = authService.getUserInfo(1L);

        assertNotNull(userInfo);
        assertEquals(1L, userInfo.getId());
        assertEquals("admin", userInfo.getUsername());
    }

    @Test
    void testGetMenuTree() {
        Object menus = authService.getMenuTree(1L);

        assertNotNull(menus);
    }
}
