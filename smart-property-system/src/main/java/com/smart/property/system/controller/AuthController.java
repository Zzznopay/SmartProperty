package com.smart.property.system.controller;

import com.smart.property.common.core.context.SecurityContextHolder;
import com.smart.property.common.core.domain.Result;
import com.smart.property.common.log.annotation.OperLog;
import com.smart.property.system.dto.LoginDTO;
import com.smart.property.system.service.AuthService;
import com.smart.property.system.vo.CaptchaVO;
import com.smart.property.system.vo.LoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证授权控制器
 *
 * @author zzz
 * @since 2026-07-25
 */
@Tag(name = "认证授权")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "获取图形验证码")
    @GetMapping("/captcha")
    public Result<CaptchaVO> captcha() {
        return Result.success(authService.generateCaptcha());
    }

    @PostMapping("/login")
    @OperLog(module = "认证授权", businessType = 1, description = "用户登录")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto, HttpServletRequest request) {
        String ip = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");
        LoginVO loginVO = authService.login(dto, ip, userAgent);
        return Result.success(loginVO);
    }

    /**
     * 刷新会话有效期（Sa-Token 滑动续期）。
     * Sa-Token 为单 Token 模型：优先取当前请求 Authorization 头中的 Token，
     * 兼容历史客户端在 body 中携带 refreshToken 的调用方式。
     */
    @PostMapping("/refresh")
    public Result<LoginVO> refreshToken(@RequestBody(required = false) Map<String, String> body,
                                        HttpServletRequest request) {
        String token = null;
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring("Bearer ".length()).trim();
        }
        if ((token == null || token.isEmpty()) && body != null) {
            token = body.get("refreshToken");
        }
        LoginVO loginVO = authService.refreshToken(token);
        return Result.success(loginVO);
    }

    /**
     * 注销：直接从请求头取 Token 交给 Service 做框架级注销（logout 在网关白名单中）。
     */
    @PostMapping("/logout")
    @OperLog(module = "认证授权", businessType = 3, description = "用户注销")
    public Result<Void> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        String userAgent = request.getHeader("User-Agent");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            authService.logout(authHeader.substring("Bearer ".length()).trim(), userAgent);
        }
        return Result.success();
    }

    @GetMapping("/user-info")
    public Result<LoginVO.UserInfoVO> getUserInfo() {
        LoginVO.UserInfoVO userInfo = authService.getUserInfo(SecurityContextHolder.getUserId());
        return Result.success(userInfo);
    }

    @GetMapping("/menus")
    public Result<Object> getMenus() {
        Object menus = authService.getMenuTree(SecurityContextHolder.getUserId());
        return Result.success(menus);
    }

    @PostMapping("/change-password")
    @OperLog(module = "认证授权", businessType = 2, description = "修改密码")
    public Result<Void> changePassword(@RequestParam String oldPassword,
                                       @RequestParam String newPassword) {
        authService.changePassword(SecurityContextHolder.getUserId(), oldPassword, newPassword);
        return Result.success();
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}