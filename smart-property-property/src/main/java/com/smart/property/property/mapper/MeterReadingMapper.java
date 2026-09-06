package com.smart.property.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smart.property.property.domain.MeterReading;
import org.apache.ibatis.annotations.Mapper;

/**
 * 抄表记录Mapper接口
 *
 * @author zzz
 * @since 2026-07-25
 */
@Mapper
public interface MeterReadingMapper extends BaseMapper<MeterReading> {
}
