package com.smart.property.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.property.property.domain.LadderConfig;
import com.smart.property.property.mapper.LadderConfigMapper;
import com.smart.property.property.service.LadderConfigService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 阶梯收费配置服务实现
 *
 * @author zzz
 * @since 2026-07-28
 */
@Service
public class LadderConfigServiceImpl extends ServiceImpl<LadderConfigMapper, LadderConfig> implements LadderConfigService {

    @Override
    public List<LadderConfig> getByFeeItemId(Long feeItemId) {
        return baseMapper.selectList(
                new LambdaQueryWrapper<LadderConfig>()
                        .eq(LadderConfig::getFeeItemId, feeItemId)
                        .orderByAsc(LadderConfig::getSort)
        );
    }
}
