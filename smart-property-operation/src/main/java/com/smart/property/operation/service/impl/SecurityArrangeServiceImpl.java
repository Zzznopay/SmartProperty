package com.smart.property.operation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.exception.BusinessException;
import com.smart.property.operation.convert.SecurityArrangeConverter;
import com.smart.property.operation.domain.SecurityArrange;
import com.smart.property.operation.dto.SecurityArrangeDTO;
import com.smart.property.operation.mapper.SecurityArrangeMapper;
import com.smart.property.operation.remote.RemoteNameService;
import com.smart.property.operation.service.SecurityArrangeService;
import com.smart.property.operation.vo.SecurityArrangeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 保安安排服务实现
 *
 * @author zzz
 * @since 2026-07-25
 */
@Service
@RequiredArgsConstructor
public class SecurityArrangeServiceImpl extends ServiceImpl<SecurityArrangeMapper, SecurityArrange> implements SecurityArrangeService {

    private final SecurityArrangeConverter securityArrangeConverter;
    private final RemoteNameService remoteNameService;

    @Override
    public PageResult<SecurityArrangeVO> getSecurityArrangePage(PageQuery query, Long companyId, Long communityId, LocalDate arrangeDate) {
        Page<SecurityArrange> page = new Page<>(query.getPageNum(), query.getPageSize());

        LambdaQueryWrapper<SecurityArrange> wrapper = new LambdaQueryWrapper<SecurityArrange>()
                .eq(SecurityArrange::getCompanyId, companyId)
                .eq(communityId != null, SecurityArrange::getCommunityId, communityId)
                .eq(arrangeDate != null, SecurityArrange::getArrangeDate, arrangeDate)
                .orderByDesc(SecurityArrange::getArrangeDate);

        Page<SecurityArrange> result = baseMapper.selectPage(page, wrapper);
        List<SecurityArrangeVO> records = result.getRecords().stream()
                .map(securityArrangeConverter::toVO)
                .collect(Collectors.toList());
        remoteNameService.fillCommunityNames(records, SecurityArrangeVO::getCommunityId, SecurityArrangeVO::setCommunityName);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), records);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createArrange(SecurityArrangeDTO dto, Long companyId, String operator) {
        SecurityArrange arrange = securityArrangeConverter.toEntity(dto);
        arrange.setCompanyId(companyId);
        if (arrange.getStatus() == null) {
            arrange.setStatus(1);
        }
        arrange.setCreateBy(operator);
        save(arrange);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeDuty(Long id, String operator) {
        SecurityArrange arrange = getById(id);
        if (arrange == null) {
            throw new BusinessException("保安安排不存在");
        }
        if (arrange.getStatus() == 3) {
            throw new BusinessException("已完成");
        }
        arrange.setStatus(3);
        arrange.setUpdateBy(operator);
        baseMapper.updateById(arrange);
    }
}