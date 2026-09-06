package com.smart.property.property.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.property.property.domain.LadderConfig;

import java.util.List;

/**
 * 阶梯收费配置服务接口
 *
 * @author zzz
 * @since 2026-07-28
 */
public interface LadderConfigService extends IService<LadderConfig> {

    /**
     * 根据费项ID查询阶梯配置
     */
    List<LadderConfig> getByFeeItemId(Long feeItemId);
}
