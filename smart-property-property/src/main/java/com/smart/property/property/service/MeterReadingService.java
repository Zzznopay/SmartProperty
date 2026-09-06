package com.smart.property.property.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.property.domain.MeterReading;
import com.smart.property.property.dto.MeterReadingDTO;
import com.smart.property.property.vo.MeterReadingVO;

/**
 * 抄表记录服务接口
 *
 * @author zzz
 * @since 2026-07-25
 */
public interface MeterReadingService extends IService<MeterReading> {

    PageResult<MeterReadingVO> getMeterReadingPage(PageQuery query, Long companyId, Long roomId, Integer meterType, String readingMonth);

    MeterReadingVO getById(Long id, Long companyId);

    void createMeterReading(MeterReadingDTO dto, Long companyId, String operator);

    void deleteMeterReading(Long id, Long companyId);
}