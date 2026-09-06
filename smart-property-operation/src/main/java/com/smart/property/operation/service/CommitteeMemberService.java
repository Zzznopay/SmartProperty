package com.smart.property.operation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.operation.domain.CommitteeMember;
import com.smart.property.operation.dto.CommitteeMemberDTO;
import com.smart.property.operation.vo.CommitteeMemberVO;

public interface CommitteeMemberService extends IService<CommitteeMember> {

    PageResult<CommitteeMemberVO> getPage(PageQuery query, Long companyId, Long communityId);

    CommitteeMemberVO getById(Long id);

    void create(CommitteeMemberDTO dto, Long companyId, String operator);

    void update(Long id, CommitteeMemberDTO dto, String operator);
}