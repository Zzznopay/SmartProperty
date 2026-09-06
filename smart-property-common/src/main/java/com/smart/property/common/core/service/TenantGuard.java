package com.smart.property.common.core.service;

import com.smart.property.common.core.context.SecurityContextHolder;
import com.smart.property.common.core.domain.BaseEntity;
import com.smart.property.common.core.exception.BusinessException;

/**
 * 多租户守卫（越权校验助手）
 *
 * <p>所有按 ID 操作的 update/delete 接口，调用本类方法校验实体的 companyId 与当前操作人公司一致。
 * 不使用 MyBatis 拦截器自动拼接条件（隐式行为不利于 review），改为显式调用，便于排查。</p>
 *
 * <h3>使用示例</h3>
 * <pre>{@code
 * SysUser user = getById(id);
 * TenantGuard.requireSameCompany(user.getCompanyId());  // 不一致抛 BusinessException
 * baseMapper.deleteById(id);
 * }</pre>
 *
 * <p>未登录时（公开接口/任务）{@link SecurityContextHolder#getCompanyIdOrNull()} 返回 null，
 * 调用方应自行决定是否放行（例如内部任务用 SYSTEM_COMPANY_ID 0L）。</p>
 *
 * @author zzz
 * @since 2026-07-31
 */
public final class TenantGuard {

    /** 系统租户 ID：兜底场景（定时任务、数据初始化） */
    public static final long SYSTEM_COMPANY_ID = 0L;

    private static final String CROSS_TENANT_MESSAGE = "无权操作其他租户的数据";
    private static final String NOT_FOUND_MESSAGE = "记录不存在";

    private TenantGuard() {
    }

    /**
     * 校验 entity.companyId == 当前操作人 companyId，不一致抛 BusinessException。
     * 实体为 null 时直接抛"记录不存在"。
     */
    public static <T extends BaseEntity> T requireSameCompany(T entity) {
        if (entity == null) {
            throw new BusinessException(NOT_FOUND_MESSAGE);
        }
        requireSameCompany(entity.getCompanyId());
        return entity;
    }

    /**
     * 仅校验 companyId（适用于不继承 BaseEntity 的实体）
     *
     * @param entityCompanyId 实体所属公司 ID，null 视为未授权场景跳过
     */
    public static void requireSameCompany(Long entityCompanyId) {
        if (entityCompanyId == null) {
            return;
        }
        Long currentCompanyId = SecurityContextHolder.getCompanyIdOrNull();
        if (currentCompanyId == null || currentCompanyId == SYSTEM_COMPANY_ID) {
            return;
        }
        if (!entityCompanyId.equals(currentCompanyId)) {
            throw new BusinessException(CROSS_TENANT_MESSAGE);
        }
    }

    /**
     * 与 {@link #requireSameCompany(BaseEntity)} 类似，但 entity 为 null 时返回 null，不抛异常。
     */
    public static <T extends BaseEntity> T assertSameCompany(T entity) {
        if (entity == null) {
            return null;
        }
        requireSameCompany(entity.getCompanyId());
        return entity;
    }
}

