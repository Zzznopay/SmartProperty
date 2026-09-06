package com.smart.property.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.exception.BusinessException;
import com.smart.property.common.security.util.EncryptUtils;
import com.smart.property.property.convert.CommunityConverter;
import com.smart.property.property.domain.Community;
import com.smart.property.property.dto.CommunityDTO;
import com.smart.property.property.mapper.CommunityMapper;
import com.smart.property.property.service.CommunityService;
import com.smart.property.property.vo.CommunityVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 小区服务实现
 *
 * @author zzz
 * @since 2026-07-25
 */
@Service
@RequiredArgsConstructor
public class CommunityServiceImpl extends ServiceImpl<CommunityMapper, Community> implements CommunityService {

    private final CommunityConverter communityConverter;
    private final EncryptUtils encryptUtils;

    @Override
    public PageResult<CommunityVO> getCommunityPage(PageQuery query, Long companyId) {
        Page<Community> page = new Page<>(query.getPageNum(), query.getPageSize());

        Page<Community> result = baseMapper.selectPage(page,
                new LambdaQueryWrapper<Community>()
                        .eq(Community::getCompanyId, companyId)
                        .eq(Community::getIsDeleted, 0)
                        .orderByDesc(Community::getCreateTime)
        );

        List<CommunityVO> records = result.getRecords().stream()
                .map(c -> {
                    CommunityVO vo = communityConverter.toVO(c);
                    decryptPhone(vo);
                    return vo;
                })
                .collect(Collectors.toList());

        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), records);
    }

    @Override
    public CommunityVO getCommunityById(Long id, Long companyId) {
        CommunityVO vo = communityConverter.toVO(getCompanyCommunity(id, companyId));
        decryptPhone(vo);
        return vo;
    }

    @Override
    public void createCommunity(CommunityDTO dto, Long companyId, String operator) {
        Community community = communityConverter.toEntity(dto);
        community.setCompanyId(companyId);
        if (dto.getContactPhone() != null) {
            community.setContactPhone(encryptUtils.encrypt(dto.getContactPhone()));
        }
        community.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        community.setCreateBy(operator);
        baseMapper.insert(community);
    }

    @Override
    public void updateCommunity(Long id, CommunityDTO dto, Long companyId, String operator) {
        Community existing = getCompanyCommunity(id, companyId);
        Community patch = communityConverter.toEntity(dto);
        patch.setId(id);
        patch.setCompanyId(existing.getCompanyId());
        patch.setCreateBy(existing.getCreateBy());
        patch.setCreateTime(existing.getCreateTime());
        if (dto.getContactPhone() != null) {
            patch.setContactPhone(encryptUtils.encrypt(dto.getContactPhone()));
        }
        patch.setStatus(dto.getStatus() != null ? dto.getStatus() : existing.getStatus());
        patch.setUpdateBy(operator);
        baseMapper.updateById(patch);
    }

    @Override
    public void deleteCommunity(Long id, Long companyId) {
        getCompanyCommunity(id, companyId);
        removeById(id);
    }

    private void decryptPhone(CommunityVO vo) {
        if (vo.getContactPhone() == null || vo.getContactPhone().isEmpty()) {
            return;
        }
        try {
            vo.setContactPhone(encryptUtils.decrypt(vo.getContactPhone()));
        } catch (Exception ignored) {
            // 非密文则保留原值
        }
    }

    private Community getCompanyCommunity(Long id, Long companyId) {
        Community community = getOne(new LambdaQueryWrapper<Community>()
                .eq(Community::getId, id)
                .eq(Community::getCompanyId, companyId));
        if (community == null) {
            throw new BusinessException("小区不存在");
        }
        return community;
    }
}