package com.smart.property.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.exception.BusinessException;
import com.smart.property.common.security.util.DesensitizeUtils;
import com.smart.property.common.security.util.EncryptUtils;
import com.smart.property.property.convert.OwnerConverter;
import com.smart.property.property.domain.Owner;
import com.smart.property.property.dto.OwnerDTO;
import com.smart.property.property.mapper.OwnerMapper;
import com.smart.property.property.service.OwnerService;
import com.smart.property.property.vo.OwnerVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 业主服务实现
 *
 * @author zzz
 * @since 2026-07-25
 */
@Service
@RequiredArgsConstructor
public class OwnerServiceImpl extends ServiceImpl<OwnerMapper, Owner> implements OwnerService {

    private final OwnerConverter ownerConverter;
    private final EncryptUtils encryptUtils;

    @Override
    public PageResult<OwnerVO> getOwnerPage(PageQuery query, Long companyId) {
        Page<Owner> page = new Page<>(query.getPageNum(), query.getPageSize());

        Page<Owner> result = baseMapper.selectPage(page,
                new LambdaQueryWrapper<Owner>()
                        .eq(Owner::getCompanyId, companyId)
                        .eq(Owner::getIsDeleted, 0)
                        .orderByDesc(Owner::getCreateTime)
        );

        List<OwnerVO> records = result.getRecords().stream()
                .map(ownerConverter::toVO)
                .collect(Collectors.toList());

        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), records);
    }

    @Override
    public OwnerVO getOwnerById(Long id, Long companyId) {
        return ownerConverter.toVO(getCompanyOwner(id, companyId));
    }

    @Override
    public void addOwner(OwnerDTO dto, Long companyId, String operator) {
        Owner owner = ownerConverter.toEntity(dto);
        owner.setCompanyId(companyId);
        applySensitive(owner, dto);
        owner.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        owner.setCreateBy(operator);
        baseMapper.insert(owner);
    }

    @Override
    public void updateOwner(Long id, OwnerDTO dto, String companyId, String operator) {
        Owner existing = getCompanyOwner(id, Long.parseLong(companyId));
        Owner patch = ownerConverter.toEntity(dto);
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
    public void deleteOwner(Long id, Long companyId) {
        getCompanyOwner(id, companyId);
        removeById(id);
    }

    /**
     * 加密 phone/idCard + 计算脱敏 mask。Converter 已 ignore 这两个字段。
     */
    private void applySensitive(Owner owner, OwnerDTO dto) {
        if (dto.getPhone() != null) {
            owner.setPhone(encryptUtils.encrypt(dto.getPhone()));
            owner.setPhoneMask(DesensitizeUtils.maskPhone(dto.getPhone()));
        }
        if (dto.getIdCard() != null) {
            owner.setIdCard(encryptUtils.encrypt(dto.getIdCard()));
            owner.setIdCardMask(DesensitizeUtils.maskIdCard(dto.getIdCard()));
        }
    }

    /**
     * 按公司隔离查询业主，防止跨租户访问
     */
    private Owner getCompanyOwner(Long id, Long companyId) {
        Owner owner = getOne(new LambdaQueryWrapper<Owner>()
                .eq(Owner::getId, id)
                .eq(Owner::getCompanyId, companyId));
        if (owner == null) {
            throw new BusinessException("业主不存在");
        }
        return owner;
    }
}