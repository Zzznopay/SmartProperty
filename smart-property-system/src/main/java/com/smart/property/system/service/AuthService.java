package com.smart.property.system.service;

import com.smart.property.system.dto.LoginDTO;
import com.smart.property.system.vo.CaptchaVO;
import com.smart.property.system.vo.LoginVO;

/**
 * 认证授权服务接口
 *
 * @author zzz
 * @since 2026-07-25
 */
public interface AuthService {

    /**
     * 生成图形验证码
     */
    CaptchaVO generateCaptcha();

    /**
     * 用户登录
     */
    LoginVO login(LoginDTO dto, String ip, String userAgent);

    /**
     * 刷新Token
     */
    LoginVO refreshToken(String refreshToken);

    /**
     * 用户注销（返回注销的用户名，便于写登录日志）
     */
    String logout(String token, String userAgent);

    /**
     * 踢指定账号下线（Sa-Token kickout，被踢端 Token 立即失效）
     */
    void kickout(Long userId);

    /**
     * 获取当前用户信息
     */
    LoginVO.UserInfoVO getUserInfo(Long userId);

    /**
     * 获取用户菜单树
     */
    Object getMenuTree(Long userId);

    /**
     * 修改当前用户密码
     */
    void changePassword(Long userId, String oldPassword, String newPassword);
}
