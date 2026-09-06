package com.smart.property.operation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.operation.domain.DutyRecord;
import com.smart.property.operation.dto.DutyRecordDTO;
import com.smart.property.operation.vo.DutyRecordVO;

public interface DutyRecordService extends IService<DutyRecord> {

    PageResult<DutyRecordVO> getPage(PageQuery query, Long companyId, Long communityId);

    DutyRecordVO getById(Long id);

    void create(DutyRecordDTO dto, Long companyId, String operator);

    void update(Long id, DutyRecordDTO dto, String operator);
}