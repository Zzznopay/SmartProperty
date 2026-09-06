package com.smart.property.common.mybatis.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.smart.property.common.core.context.SecurityContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 自动填充处理器
 *
 * <p>填充规则：
 * <ul>
 *     <li>INSERT：createTime / updateTime / isDeleted(0) / createBy / updateBy / companyId</li>
 *     <li>UPDATE：updateTime / updateBy</li>
 * </ul>
 *
 * <p>createBy / updateBy / companyId 从 {@link SecurityContextHolder} 读取（网关注入 X-User-Id/X-Company-Id
 * 头 → SecurityHeaderFilter 写入 ThreadLocal）。未登录场景（公开接口、定时任务、单元测试）兜底为
 * "system" / 0L，避免审计字段缺失。</p>
 *
 * @author zzz
 * @since 2026-07-25
 */
@Slf4j
@Component
public class AutoFillHandler implements MetaObjectHandler {

    /** 兜底操作人，用于未登录场景（公开接口/任务/测试） */
    private static final String SYSTEM_USER = "system";
    private static final Long SYSTEM_COMPANY_ID = 0L;

    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "isDeleted", Integer.class, 0);

        String operator = currentUsername();
        Long companyId = currentCompanyId();
        this.strictInsertFill(metaObject, "createBy", String.class, operator);
        this.strictInsertFill(metaObject, "updateBy", String.class, operator);
        this.strictInsertFill(metaObject, "companyId", Long.class, companyId);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
        this.setFieldValByName("updateBy", currentUsername(), metaObject);
    }

    private String currentUsername() {
        try {
            String username = SecurityContextHolder.getUsernameOrNull();
            return username == null ? SYSTEM_USER : username;
        } catch (Exception e) {
            return SYSTEM_USER;
        }
    }

    private Long currentCompanyId() {
        try {
            Long companyId = SecurityContextHolder.getCompanyIdOrNull();
            return companyId == null ? SYSTEM_COMPANY_ID : companyId;
        } catch (Exception e) {
            return SYSTEM_COMPANY_ID;
        }
    }
}
