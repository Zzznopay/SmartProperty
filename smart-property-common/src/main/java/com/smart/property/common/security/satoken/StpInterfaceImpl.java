package com.smart.property.common.security.satoken;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Sa-Token 权限数据提供者。
 *
 * <p>登录成功时由 AuthServiceImpl 把角色 / 权限码写入 SaSession（Redis 共享），
 * 网关与各业务服务共用同一份 Redis 数据，因此本实现放在 common 模块即可在
 * 任意服务中为注解鉴权（@SaCheckRole / @SaCheckPermission）提供数据，无需跨服务调用。</p>
 *
 * @author zzz
 * @since 2026-09-06
 */
@Component
public class StpInterfaceImpl implements StpInterface {

    /** SaSession 中存放角色列表的 key */
    public static final String SESSION_KEY_ROLES = "roles";

    /** SaSession 中存放权限码列表的 key */
    public static final String SESSION_KEY_PERMISSIONS = "permissions";

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return readSessionList(loginId, SESSION_KEY_PERMISSIONS);
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return readSessionList(loginId, SESSION_KEY_ROLES);
    }

    private List<String> readSessionList(Object loginId, String key) {
        // isCreate=false：会话不存在（未登录或会话已被注销）时返回空列表而非新建
        SaSession session = StpUtil.getSessionByLoginId(loginId, false);
        if (session == null) {
            return List.of();
        }
        Object value = session.get(key);
        if (value instanceof List<?> list) {
            return list.stream().map(String::valueOf).toList();
        }
        return List.of();
    }
}
