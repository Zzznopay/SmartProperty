package com.smart.property.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.exception.BusinessException;
import com.smart.property.common.security.util.DesensitizeUtils;
import com.smart.property.common.security.util.EncryptUtils;
import com.smart.property.property.convert.TenantConverter;
import com.smart.property.property.domain.Tenant;
import com.smart.property.property.dto.TenantDTO;
import com.smart.property.property.mapper.TenantMapper;
import com.smart.property.property.service.TenantService;
import com.smart.property.property.vo.TenantVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 租户服务实现
 *
 * @author zzz
 * @since 2026-07-25
 */
@Service
@RequiredArgsConstructor
public class TenantServiceImpl extends ServiceImpl<TenantMapper, Tenant> implements TenantService {

    private final TenantConverter tenantConverter;
    private final EncryptUtils encryptUtils;

    @Override
    public PageResult<TenantVO> getTenantPage(PageQuery query, Long companyId) {
        Page<Tenant> page = new Page<>(query.getPageNum(), query.getPageSize());

        Page<Tenant> result = baseMapper.selectPage(page,
                new LambdaQueryWrapper<Tenant>()
                        .eq(Tenant::getCompanyId, companyId)
                        .eq(Tenant::getIsDeleted, 0)
                        .orderByDesc(Tenant::getCreateTime)
        );

        List<TenantVO> records = result.getRecords().stream()
                .map(tenantConverter::toVO)
                .collect(Collectors.toList());

        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), records);
    }

    @Override
    public TenantVO getTenantById(Long id, Long companyId) {
        return tenantConverter.toVO(getCompanyTenant(id, companyId));
    }

    @Override
    public void createTenant(TenantDTO dto, Long companyId, String operator) {
        Tenant tenant = tenantConverter.toEntity(dto);
        tenant.setCompanyId(companyId);
        applySensitive(tenant, dto);
        tenant.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        tenant.setCreateBy(operator);
        baseMapper.insert(tenant);
    }

    @Override
    public void updateTenant(Long id, TenantDTO dto, Long companyId, String operator) {
        Tenant existing = getCompanyTenant(id, companyId);
        Tenant patch = tenantConverter.toEntity(dto);
        patch.setId(id);
        patch.setCompanyId(existing.getCompanyId());
        patch.setCreateBy(existing.getCreateBy());
        patch.setCreateTime(existing.getCreateTime());
        applySensitive(patch, dto);
        patch.setStatus(dto.getStatus() != null ? dto.getStatus() : existing.getStatus());
        patch.setUpdateBy(operator);
        baseMapper.updateById(patch);
    }

    @Override
    public void deleteTenant(Long id, Long companyId) {
        getCompanyTenant(id, companyId);
        removeById(id);
    }

    private void applySensitive(Tenant tenant, TenantDTO dto) {
        if (dto.getPhone() != null) {
            tenant.setPhone(encryptUtils.encrypt(dto.getPhone()));
            tenant.setPhoneMask(DesensitizeUtils.maskPhone(dto.getPhone()));
        }
        if (dto.getIdCard() != null) {
            tenant.setIdCard(encryptUtils.encrypt(dto.getIdCard()));
            tenant.setIdCardMask(DesensitizeUtils.maskIdCard(dto.getIdCard()));
        }
    }

    private Tenant getCompanyTenant(Long id, Long companyId) {
        Tenant tenant = getOne(new LambdaQueryWrapper<Tenant>()
                .eq(Tenant::getId, id)
                .eq(Tenant::getCompanyId, companyId));
        if (tenant == null) {
            throw new BusinessException("租户不存在");
        }
        return tenant;
    }
}