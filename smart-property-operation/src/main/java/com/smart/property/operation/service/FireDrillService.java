package com.smart.property.operation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.operation.domain.FireDrill;
import com.smart.property.operation.dto.FireDrillDTO;
import com.smart.property.operation.vo.FireDrillVO;

public interface FireDrillService extends IService<FireDrill> {

    PageResult<FireDrillVO> getPage(PageQuery query, Long companyId, Long communityId);

    FireDrillVO getById(Long id);

    void create(FireDrillDTO dto, Long companyId, String operator);

    void update(Long id, FireDrillDTO dto, String operator);
}