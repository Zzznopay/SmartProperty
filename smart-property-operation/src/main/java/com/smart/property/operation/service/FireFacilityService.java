package com.smart.property.operation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.operation.domain.FireFacility;
import com.smart.property.operation.dto.FireFacilityDTO;
import com.smart.property.operation.vo.FireFacilityVO;

/**
 * 消防设施服务接口
 *
 * @author zzz
 * @since 2026-07-25
 */
public interface FireFacilityService extends IService<FireFacility> {

    PageResult<FireFacilityVO> getFireFacilityPage(PageQuery query, Long companyId, Long communityId, Integer facilityType);

    void createFacility(FireFacilityDTO dto, Long companyId, String operator);

    void checkFacility(Long id, String operator);
}