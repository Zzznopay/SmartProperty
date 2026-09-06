package com.smart.property.operation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.exception.BusinessException;
import com.smart.property.operation.convert.CleanArrangeConverter;
import com.smart.property.operation.domain.CleanArrange;
import com.smart.property.operation.dto.CleanArrangeDTO;
import com.smart.property.operation.mapper.CleanArrangeMapper;
import com.smart.property.operation.service.CleanArrangeService;
import com.smart.property.operation.vo.CleanArrangeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 清洁安排服务实现
 *
 * @author zzz
 * @since 2026-07-25
 */
@Service
@RequiredArgsConstructor
public class CleanArrangeServiceImpl extends ServiceImpl<CleanArrangeMapper, CleanArrange> implements CleanArrangeService {

    private final CleanArrangeConverter cleanArrangeConverter;

    @Override
    public PageResult<CleanArrangeVO> getCleanArrangePage(PageQuery query, Long companyId, Long communityId, LocalDate arrangeDate) {
        Page<CleanArrange> page = new Page<>(query.getPageNum(), query.getPageSize());

        LambdaQueryWrapper<CleanArrange> wrapper = new LambdaQueryWrapper<CleanArrange>()
                .eq(CleanArrange::getCompanyId, companyId)
                .eq(communityId != null, CleanArrange::getCommunityId, communityId)
                .eq(arrangeDate != null, CleanArrange::getArrangeDate, arrangeDate)
                .orderByDesc(CleanArrange::getArrangeDate);

        Page<CleanArrange> result = baseMapper.selectPage(page, wrapper);
        List<CleanArrangeVO> records = result.getRecords().stream()
                .map(cleanArrangeConverter::toVO)
                .collect(Collectors.toList());
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), records);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createCleanArrange(CleanArrangeDTO dto, Long companyId, String operator) {
        CleanArrange clean = cleanArrangeConverter.toEntity(dto);
        clean.setCompanyId(companyId);
        clean.setStatus(1);
        clean.setCreateBy(operator);
        save(clean);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeClean(Long id, String operator) {
        CleanArrange clean = getById(id);
        if (clean == null) {
            throw new BusinessException("清洁安排不存在");
        }
        if (clean.getStatus() == 3) {
            throw new BusinessException("已完成");
        }

        clean.setStatus(3); // 已完成
        clean.setCompleteTime(LocalDateTime.now());
        clean.setUpdateBy(operator);
        baseMapper.updateById(clean);
    }
}
