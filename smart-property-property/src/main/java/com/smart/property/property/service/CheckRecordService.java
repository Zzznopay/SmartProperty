package com.smart.property.property.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.property.domain.CheckRecord;
import com.smart.property.property.dto.CheckRecordDTO;
import com.smart.property.property.vo.CheckRecordVO;

/**
 * 验房记录服务接口
 *
 * @author zzz
 * @since 2026-07-25
 */
public interface CheckRecordService extends IService<CheckRecord> {

    PageResult<CheckRecordVO> getCheckRecordPage(PageQuery query, Long companyId, Long roomId);

    CheckRecordVO getByCheckId(Long id, Long companyId);

    void createCheckRecord(CheckRecordDTO dto, Long companyId, String operator);

    void completeRectification(Long id, Long companyId, String operator);

    void deleteCheckRecord(Long id, Long companyId);
}