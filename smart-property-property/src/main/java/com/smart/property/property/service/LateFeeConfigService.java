package com.smart.property.property.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.property.domain.LateFeeConfig;
import com.smart.property.property.dto.LateFeeConfigDTO;
import com.smart.property.property.vo.LateFeeConfigVO;

/**
 * 滞纳金配置服务接口
 *
 * @author zzz
 * @since 2026-07-28
 */
public interface LateFeeConfigService extends IService<LateFeeConfig> {

    PageResult<LateFeeConfigVO> getLateFeeConfigPage(PageQuery query, Long companyId, Long communityId);

    void saveLateFeeConfig(LateFeeConfigDTO dto, Long companyId, String operator);
}