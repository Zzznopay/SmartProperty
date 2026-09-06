package com.smart.property.property.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.property.domain.DecorationRecord;
import com.smart.property.property.dto.DecorationRecordDTO;
import com.smart.property.property.vo.DecorationRecordVO;

/**
 * 装修记录服务接口
 *
 * @author zzz
 * @since 2026-07-25
 */
public interface DecorationRecordService extends IService<DecorationRecord> {

    PageResult<DecorationRecordVO> getDecorationPage(PageQuery query, Long companyId, Long roomId);

    DecorationRecordVO getByDecorationId(Long id, Long companyId);

    void createDecoration(DecorationRecordDTO dto, Long companyId, String operator);

    void startDecoration(Long id, Long companyId, String operator);

    void completeDecoration(Long id, Long companyId, String operator);

    void checkDecoration(Long id, Long companyId, Integer checkResult, String operator);

    void deleteDecoration(Long id, Long companyId);
}