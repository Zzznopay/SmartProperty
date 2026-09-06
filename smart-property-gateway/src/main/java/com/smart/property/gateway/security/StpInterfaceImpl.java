package com.smart.property.gateway.security;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Sa-Token 权限数据提供者（网关侧）。
 *
 * <p>角色 / 权限码在登录时由 smart-property-system 写入 SaSession（Redis 共享），
 * 网关据此执行 SaRouter 路由角色校验（如 /api/v1/system/** 仅 admin）。</p>
 *
 * @author zzz
 * @since 2026-09-06
 */
@Component
public class StpInterfaceImpl implements StpInterface {

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return readSessionList(loginId, "permissions");
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return readSessionList(loginId, "roles");
    }

    private List<String> readSessionList(Object loginId, String key) {
        // isCreate=false：会话不存在（未登录或已注销）时返回空列表而非新建
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
