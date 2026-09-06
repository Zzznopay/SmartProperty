package com.smart.property.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.property.convert.LateFeeConfigConverter;
import com.smart.property.property.domain.LateFeeConfig;
import com.smart.property.property.dto.LateFeeConfigDTO;
import com.smart.property.property.mapper.LateFeeConfigMapper;
import com.smart.property.property.service.LateFeeConfigService;
import com.smart.property.property.vo.LateFeeConfigVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 滞纳金配置服务实现
 *
 * @author zzz
 * @since 2026-07-28
 */
@Service
@RequiredArgsConstructor
public class LateFeeConfigServiceImpl extends ServiceImpl<LateFeeConfigMapper, LateFeeConfig> implements LateFeeConfigService {

    private final LateFeeConfigConverter lateFeeConfigConverter;

    @Override
    public PageResult<LateFeeConfigVO> getLateFeeConfigPage(PageQuery query, Long companyId, Long communityId) {
        Page<LateFeeConfig> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<LateFeeConfig> wrapper = new LambdaQueryWrapper<LateFeeConfig>()
                .eq(LateFeeConfig::getCompanyId, companyId)
                .eq(communityId != null, LateFeeConfig::getCommunityId, communityId)
                .orderByDesc(LateFeeConfig::getCreateTime);
        Page<LateFeeConfig> result = baseMapper.selectPage(page, wrapper);
        List<LateFeeConfigVO> records = result.getRecords().stream()
                .map(lateFeeConfigConverter::toVO)
                .collect(Collectors.toList());
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), records);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveLateFeeConfig(LateFeeConfigDTO dto, Long companyId, String operator) {
        LateFeeConfig config = lateFeeConfigConverter.toEntity(dto);
        config.setCompanyId(companyId);
        if (config.getId() == null) {
            config.setCreateBy(operator);
            save(config);
        } else {
            config.setUpdateBy(operator);
            updateById(config);
        }
    }
}