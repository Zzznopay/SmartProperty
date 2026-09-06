package com.smart.property.operation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.operation.domain.CommitteeMeeting;
import com.smart.property.operation.dto.CommitteeMeetingDTO;
import com.smart.property.operation.vo.CommitteeMeetingVO;

public interface CommitteeMeetingService extends IService<CommitteeMeeting> {

    PageResult<CommitteeMeetingVO> getPage(PageQuery query, Long companyId, Long communityId);

    CommitteeMeetingVO getById(Long id);

    void create(CommitteeMeetingDTO dto, Long companyId, String operator);

    void update(Long id, CommitteeMeetingDTO dto, String operator);
}