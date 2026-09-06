package com.smart.property.operation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.operation.domain.VisitRecord;
import com.smart.property.operation.dto.VisitRecordDTO;
import com.smart.property.operation.vo.VisitRecordVO;

/**
 * 来访登记服务接口
 *
 * @author zzz
 * @since 2026-07-25
 */
public interface VisitRecordService extends IService<VisitRecord> {

    PageResult<VisitRecordVO> getVisitPage(PageQuery query, Long companyId, Long communityId);

    void registerVisit(VisitRecordDTO dto, Long companyId, Long guardId, String guardName);

    void registerLeave(Long id, String operator);
}