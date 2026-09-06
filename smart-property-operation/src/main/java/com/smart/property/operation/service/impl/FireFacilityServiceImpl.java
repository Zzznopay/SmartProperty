package com.smart.property.operation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.exception.BusinessException;
import com.smart.property.operation.convert.FireFacilityConverter;
import com.smart.property.operation.domain.FireFacility;
import com.smart.property.operation.dto.FireFacilityDTO;
import com.smart.property.operation.mapper.FireFacilityMapper;
import com.smart.property.operation.remote.RemoteNameService;
import com.smart.property.operation.service.FireFacilityService;
import com.smart.property.operation.vo.FireFacilityVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 消防设施服务实现
 *
 * @author zzz
 * @since 2026-07-25
 */
@Service
@RequiredArgsConstructor
public class FireFacilityServiceImpl extends ServiceImpl<FireFacilityMapper, FireFacility> implements FireFacilityService {

    private final FireFacilityConverter fireFacilityConverter;
    private final RemoteNameService remoteNameService;

    @Override
    public PageResult<FireFacilityVO> getFireFacilityPage(PageQuery query, Long companyId, Long communityId, Integer facilityType) {
        Page<FireFacility> page = new Page<>(query.getPageNum(), query.getPageSize());

        LambdaQueryWrapper<FireFacility> wrapper = new LambdaQueryWrapper<FireFacility>()
                .eq(FireFacility::getCompanyId, companyId)
                .eq(FireFacility::getIsDeleted, 0)
                .eq(communityId != null, FireFacility::getCommunityId, communityId)
                .eq(facilityType != null, FireFacility::getFacilityType, facilityType)
                .orderByAsc(FireFacility::getExpireDate);

        Page<FireFacility> result = baseMapper.selectPage(page, wrapper);
        List<FireFacilityVO> records = result.getRecords().stream()
                .map(fireFacilityConverter::toVO)
                .collect(Collectors.toList());
        remoteNameService.fillCommunityNames(records, FireFacilityVO::getCommunityId, FireFacilityVO::setCommunityName);
        remoteNameService.fillBuildingNames(records, FireFacilityVO::getBuildingId, FireFacilityVO::setBuildingName);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), records);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createFacility(FireFacilityDTO dto, Long companyId, String operator) {
        FireFacility facility = fireFacilityConverter.toEntity(dto);
        facility.setCompanyId(companyId);
        facility.setCreateBy(operator);
        save(facility);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkFacility(Long id, String operator) {
        FireFacility facility = getById(id);
        if (facility == null) {
            throw new BusinessException("消防设施不存在");
        }
        facility.setLastCheckDate(LocalDate.now());
        facility.setNextCheckDate(LocalDate.now().plusMonths(1));
        facility.setUpdateBy(operator);
        baseMapper.updateById(facility);
    }
}