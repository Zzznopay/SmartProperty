package com.smart.property.system.service.impl;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smart.property.common.core.exception.BusinessException;
import com.smart.property.common.redis.util.RedisUtils;
import com.smart.property.common.security.satoken.StpInterfaceImpl;
import com.smart.property.system.domain.SysLoginLog;
import com.smart.property.system.domain.SysMenu;
import com.smart.property.system.domain.SysRole;
import com.smart.property.system.domain.SysUser;
import com.smart.property.system.domain.SysUserRole;
import com.smart.property.system.dto.LoginDTO;
import com.smart.property.system.mapper.SysMenuMapper;
import com.smart.property.system.mapper.SysRoleMapper;
import com.smart.property.system.mapper.SysUserMapper;
import com.smart.property.system.mapper.SysUserRoleMapper;
import com.smart.property.system.service.AuthService;
import com.smart.property.system.service.SysLoginLogService;
import com.smart.property.system.vo.CaptchaVO;
import com.smart.property.system.vo.LoginVO;
import com.wf.captcha.SpecCaptcha;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 认证授权服务实现（Sa-Token）
 *
 * <p>登录态由 Sa-Token 管理：{@code StpUtil.login(id)} 创建 Token 与会话（Redis 存储），
 * 角色 / 权限码写入 SaSession 供网关与各服务的 StpInterfaceImpl 读取；
 * Token 有效性校验、续期、踢人下线均由框架完成。</p>
 *
 * @author zzz
 * @since 2026-07-25
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper userMapper;
    private final SysRoleMapper roleMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final SysMenuMapper menuMapper;
    private final RedisUtils redisUtils;
    private final PasswordEncoder passwordEncoder;
    private final SysLoginLogService loginLogService;

    private static final String USER_CACHE_KEY = "sys:user:";
    private static final String CAPTCHA_CACHE_KEY = "auth:captcha:";
    private static final long CAPTCHA_TTL_SECONDS = 300L;

    /** SaSession 中存放当前登录用户租户（公司）ID 的 key，网关读取后转发 X-Company-Id */
    public static final String SESSION_KEY_COMPANY_ID = "companyId";

    /** SaSession 中存放当前登录用户名的 key，网关读取后转发 X-Username */
    public static final String SESSION_KEY_USERNAME = "username";

    @Override
    public CaptchaVO generateCaptcha() {
        SpecCaptcha captcha = new SpecCaptcha(120, 40, 4);
        captcha.setCharType(SpecCaptcha.TYPE_DEFAULT);
        String code = captcha.text().toLowerCase();
        String key = UUID.randomUUID().toString().replace("-", "");
        redisUtils.set(CAPTCHA_CACHE_KEY + key, code, CAPTCHA_TTL_SECONDS, TimeUnit.SECONDS);
        return CaptchaVO.builder()
                .captchaKey(key)
                .img(captcha.toBase64())
                .build();
    }

    private void verifyCaptcha(String captchaKey, String captchaCode) {
        if (captchaKey == null || captchaKey.isEmpty()
                || captchaCode == null || captchaCode.isEmpty()) {
            throw new BusinessException("请输入图形验证码");
        }
        Object cached = redisUtils.get(CAPTCHA_CACHE_KEY + captchaKey);
        // 一次性使用：取出后立即删除，防止验证码被暴力枚举
        redisUtils.delete(CAPTCHA_CACHE_KEY + captchaKey);
        if (cached == null) {
            throw new BusinessException("验证码已过期，请刷新后重试");
        }
        if (!String.valueOf(cached).equalsIgnoreCase(captchaCode.trim())) {
            throw new BusinessException("验证码错误");
        }
    }

    @Override
    public LoginVO login(LoginDTO dto, String ip, String userAgent) {
        String username = dto.getUsername();
        try {
            // 验证码校验
            verifyCaptcha(dto.getCaptchaKey(), dto.getCaptchaCode());

            // 查询用户
            SysUser user = userMapper.selectOne(
                    new LambdaQueryWrapper<SysUser>()
                            .eq(SysUser::getUsername, username)
                            .eq(SysUser::getIsDeleted, 0)
            );

            if (user == null) {
                recordLoginLog(username, ip, userAgent, 0, "用户不存在");
                throw new BusinessException("用户名或密码错误");
            }

            if (user.getStatus() == 0) {
                recordLoginLog(username, ip, userAgent, 0, "账号已被禁用");
                throw new BusinessException("账号已被禁用");
            }

            // 验证密码
            if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
                recordLoginLog(username, ip, userAgent, 0, "密码错误");
                throw new BusinessException("用户名或密码错误");
            }

            // 查询用户角色 / 权限（写入会话，供网关与各服务注解鉴权使用）
            List<String> roles = getUserRoles(user.getId());
            List<String> permissions = getUserPermissions(user.getId());

            // Sa-Token 会话登录：生成 Token、创建会话（is-concurrent=false，新登录挤掉旧登录）
            StpUtil.login(user.getId());
            String accessToken = StpUtil.getTokenValue();
            long expiresIn = StpUtil.getTokenTimeout();

            // 身份与权限数据放入 SaSession（Redis），网关与所有业务服务共享
            SaSession session = StpUtil.getSession();
            session.set(SESSION_KEY_USERNAME, user.getUsername());
            session.set(SESSION_KEY_COMPANY_ID, user.getCompanyId());
            session.set(StpInterfaceImpl.SESSION_KEY_ROLES, roles);
            session.set(StpInterfaceImpl.SESSION_KEY_PERMISSIONS, permissions);

            // 更新登录信息
            user.setLoginIp(ip);
            user.setLoginTime(LocalDateTime.now());
            userMapper.updateById(user);

            // 构建响应
            LoginVO.UserInfoVO userInfo = LoginVO.UserInfoVO.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .realName(user.getRealName())
                    .avatar(user.getAvatar())
                    .companyId(user.getCompanyId())
                    .roles(roles)
                    .permissions(permissions)
                    .build();

            recordLoginLog(username, ip, userAgent, 1, "登录成功");

            // Sa-Token 为单 Token 模型：无独立 refresh token，refreshToken 返回同一 Token，
            // /auth/refresh 依据该 Token 对会话续期
            return LoginVO.builder()
                    .accessToken(accessToken)
                    .refreshToken(accessToken)
                    .expiresIn(expiresIn)
                    .userInfo(userInfo)
                    .build();
        } catch (BusinessException be) {
            throw be;
        } catch (RuntimeException e) {
            recordLoginLog(username, ip, userAgent, 0, "登录异常: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public LoginVO refreshToken(String token) {
        if (token == null || token.isBlank()) {
            throw new BusinessException("刷新令牌不存在");
        }

        // Token 必须仍是有效登录态：已过期 / 已注销 / 被踢下线时这里返回 null
        Object loginId = StpUtil.getLoginIdByToken(token);
        if (loginId == null) {
            throw new BusinessException("登录已失效，请重新登录");
        }

        Long userId = Long.valueOf(loginId.toString());
        SysUser user = userMapper.selectById(userId);

        if (user == null || user.getStatus() == 0) {
            throw new BusinessException("用户不存在或已被禁用");
        }

        // 会话续期：以配置的 timeout 为新的有效期（滑动过期）
        StpUtil.renewTimeout(token, SaManager.getConfig().getTimeout());

        List<String> roles = getUserRoles(userId);
        List<String> permissions = getUserPermissions(userId);

        // 权限发生变化时同步刷新会话中的权限数据
        SaSession session = StpUtil.getSessionByLoginId(userId, false);
        if (session != null) {
            session.set(StpInterfaceImpl.SESSION_KEY_ROLES, roles);
            session.set(StpInterfaceImpl.SESSION_KEY_PERMISSIONS, permissions);
        }

        LoginVO.UserInfoVO userInfo = LoginVO.UserInfoVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .avatar(user.getAvatar())
                .companyId(user.getCompanyId())
                .roles(roles)
                .permissions(permissions)
                .build();

        String accessToken = token;
        return LoginVO.builder()
                .accessToken(accessToken)
                .refreshToken(accessToken)
                .expiresIn(StpUtil.getTokenTimeout(token))
                .userInfo(userInfo)
                .build();
    }

    @Override
    public String logout(String token, String userAgent) {
        Object loginId = StpUtil.getLoginIdByToken(token);
        String username = null;
        if (loginId != null) {
            Long userId = Long.valueOf(loginId.toString());
            redisUtils.delete(USER_CACHE_KEY + userId);
            SysUser user = userMapper.selectById(userId);
            username = user != null ? user.getUsername() : null;
        }
        // 框架级注销：删除 Token 登录态（Redis），此后该 Token 不可用
        StpUtil.logoutByTokenValue(token);
        recordLoginLog(username, null, userAgent, 1, "退出成功");
        return username;
    }

    @Override
    public void kickout(Long userId) {
        // 踢人下线：目标用户的所有 Token 立即失效（被踢端收到「已被踢下线」语义）
        StpUtil.kickout(userId);
    }

    /**
     * 记录登录日志（成功 1 / 失败 0）
     */
    private void recordLoginLog(String username, String ip, String userAgent, int status, String msg) {
        try {
            UserAgentInfo ua = parseUserAgent(userAgent);
            SysLoginLog log = new SysLoginLog();
            log.setUsername(username);
            log.setIp(ip);
            log.setBrowser(ua.browser);
            log.setOs(ua.os);
            log.setStatus(status);
            log.setMsg(msg);
            log.setLoginTime(LocalDateTime.now());
            loginLogService.save(log);
        } catch (Exception e) {
            // 日志写不进去不能影响主流程
            log.warn("写入登录日志失败: {}", e.getMessage());
        }
    }

    private record UserAgentInfo(String browser, String os) {}

    private static UserAgentInfo parseUserAgent(String ua) {
        if (ua == null || ua.isEmpty()) {
            return new UserAgentInfo("Unknown", "Unknown");
        }
        String browser;
        if (ua.contains("Edg/") || ua.contains("Edge/")) {
            browser = "Edge";
        } else if (ua.contains("Chrome/") && !ua.contains("Chromium/")) {
            browser = "Chrome";
        } else if (ua.contains("Firefox/")) {
            browser = "Firefox";
        } else if (ua.contains("Safari/") && ua.contains("Version/")) {
            browser = "Safari";
        } else if (ua.contains("MSIE") || ua.contains("Trident/")) {
            browser = "IE";
        } else {
            browser = "Other";
        }
        String os;
        if (ua.contains("Windows")) {
            os = "Windows";
        } else if (ua.contains("Mac OS X") || ua.contains("Macintosh")) {
            os = "MacOS";
        } else if (ua.contains("Android")) {
            os = "Android";
        } else if (ua.contains("iPhone") || ua.contains("iPad")) {
            os = "iOS";
        } else if (ua.contains("Linux")) {
            os = "Linux";
        } else {
            os = "Other";
        }
        return new UserAgentInfo(browser, os);
    }

    @Override
    public LoginVO.UserInfoVO getUserInfo(Long userId) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        List<String> roles = getUserRoles(userId);
        List<String> permissions = getUserPermissions(userId);

        return LoginVO.UserInfoVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .avatar(user.getAvatar())
                .companyId(user.getCompanyId())
                .roles(roles)
                .permissions(permissions)
                .build();
    }

    @Override
    public Object getMenuTree(Long userId) {
        List<SysMenu> menus = menuMapper.selectMenusByUserId(userId);
        return buildMenuTree(menus, 0L);
    }

    private List<String> getUserRoles(Long userId) {
        List<SysUserRole> userRoles = userRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId)
        );

        if (userRoles.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> roleIds = userRoles.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());

        List<SysRole> roles = roleMapper.selectBatchIds(roleIds);
        return roles.stream()
                .map(SysRole::getRoleKey)
                .collect(Collectors.toList());
    }

    private List<String> getUserPermissions(Long userId) {
        // 管理员拥有所有权限
        List<String> roles = getUserRoles(userId);
        if (roles.contains("admin")) {
            List<String> perms = new ArrayList<>();
            perms.add("*:*:*");
            return perms;
        }

        List<SysMenu> menus = menuMapper.selectMenusByUserId(userId);
        return menus.stream()
                .filter(m -> m.getPerms() != null && !m.getPerms().isEmpty())
                .map(SysMenu::getPerms)
                .collect(Collectors.toList());
    }

    private List<SysMenu> buildMenuTree(List<SysMenu> menus, Long parentId) {
        return menus.stream()
                .filter(m -> parentId.equals(m.getParentId()))
                .peek(m -> m.setChildren(buildMenuTree(menus, m.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
    }
}
