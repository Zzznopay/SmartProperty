package com.smart.property.operation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.operation.domain.CleanCheck;
import com.smart.property.operation.dto.CleanCheckDTO;
import com.smart.property.operation.vo.CleanCheckVO;

public interface CleanCheckService extends IService<CleanCheck> {

    PageResult<CleanCheckVO> getPage(PageQuery query, Long companyId, Long communityId);

    CleanCheckVO getById(Long id);

    void create(CleanCheckDTO dto, Long companyId, String operator);

    void update(Long id, CleanCheckDTO dto, String operator);
}