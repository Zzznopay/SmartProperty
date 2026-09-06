package com.smart.property.operation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.exception.BusinessException;
import com.smart.property.operation.convert.RegulationConverter;
import com.smart.property.operation.domain.Regulation;
import com.smart.property.operation.dto.RegulationDTO;
import com.smart.property.operation.mapper.RegulationMapper;
import com.smart.property.operation.service.RegulationService;
import com.smart.property.operation.vo.RegulationVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 规章制度服务实现
 *
 * @author zzz
 * @since 2026-07-25
 */
@Service
@RequiredArgsConstructor
public class RegulationServiceImpl extends ServiceImpl<RegulationMapper, Regulation> implements RegulationService {

    private final RegulationConverter regulationConverter;

    @Override
    public PageResult<RegulationVO> getRegulationPage(PageQuery query, Long companyId, String category) {
        Page<Regulation> page = new Page<>(query.getPageNum(), query.getPageSize());

        LambdaQueryWrapper<Regulation> wrapper = new LambdaQueryWrapper<Regulation>()
                .eq(Regulation::getCompanyId, companyId)
                .eq(Regulation::getIsDeleted, 0)
                .eq(category != null, Regulation::getCategory, category)
                .orderByDesc(Regulation::getCreateTime);

        Page<Regulation> result = baseMapper.selectPage(page, wrapper);
        List<RegulationVO> records = result.getRecords().stream()
                .map(regulationConverter::toVO)
                .collect(Collectors.toList());
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), records);
    }

    @Override
    public RegulationVO getRegulationVOById(Long id) {
        Regulation regulation = getById(id);
        if (regulation == null) {
            throw new BusinessException("规章制度不存在");
        }
        return regulationConverter.toVO(regulation);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createRegulation(RegulationDTO dto, Long companyId, String operator) {
        Regulation regulation = regulationConverter.toEntity(dto);
        regulation.setCompanyId(companyId);
        regulation.setCreateBy(operator);
        if (regulation.getStatus() == null) {
            regulation.setStatus(1);
        }
        regulation.setViewCount(0);
        save(regulation);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishRegulation(Long id, String operator) {
        Regulation regulation = getById(id);
        if (regulation == null) {
            throw new BusinessException("规章制度不存在");
        }
        if (regulation.getStatus() == 2) {
            throw new BusinessException("已发布");
        }
        regulation.setStatus(2);
        regulation.setIsPublish(1);
        regulation.setPublishTime(LocalDateTime.now());
        regulation.setUpdateBy(operator);
        baseMapper.updateById(regulation);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrementViewCount(Long id) {
        Regulation regulation = getById(id);
        if (regulation != null) {
            regulation.setViewCount(regulation.getViewCount() + 1);
            baseMapper.updateById(regulation);
        }
    }
}