package com.smart.property.operation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.operation.domain.GreeneryCheck;
import com.smart.property.operation.dto.GreeneryCheckDTO;
import com.smart.property.operation.vo.GreeneryCheckVO;

public interface GreeneryCheckService extends IService<GreeneryCheck> {

    PageResult<GreeneryCheckVO> getPage(PageQuery query, Long companyId, Long communityId);

    GreeneryCheckVO getById(Long id);

    void create(GreeneryCheckDTO dto, Long companyId, String operator);

    void update(Long id, GreeneryCheckDTO dto, String operator);
}