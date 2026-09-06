package com.smart.property.operation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.operation.domain.SecurityArrange;
import com.smart.property.operation.dto.SecurityArrangeDTO;
import com.smart.property.operation.vo.SecurityArrangeVO;

import java.time.LocalDate;

/**
 * 保安安排服务接口
 *
 * @author zzz
 * @since 2026-07-25
 */
public interface SecurityArrangeService extends IService<SecurityArrange> {

    PageResult<SecurityArrangeVO> getSecurityArrangePage(PageQuery query, Long companyId, Long communityId, LocalDate arrangeDate);

    void createArrange(SecurityArrangeDTO dto, Long companyId, String operator);

    void completeDuty(Long id, String operator);
}