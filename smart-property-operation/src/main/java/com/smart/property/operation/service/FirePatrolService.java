package com.smart.property.operation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.operation.domain.FirePatrol;
import com.smart.property.operation.dto.FirePatrolDTO;
import com.smart.property.operation.vo.FirePatrolVO;

public interface FirePatrolService extends IService<FirePatrol> {

    PageResult<FirePatrolVO> getPage(PageQuery query, Long companyId, Long communityId);

    FirePatrolVO getById(Long id);

    void create(FirePatrolDTO dto, Long companyId, String operator);

    void update(Long id, FirePatrolDTO dto, String operator);
}