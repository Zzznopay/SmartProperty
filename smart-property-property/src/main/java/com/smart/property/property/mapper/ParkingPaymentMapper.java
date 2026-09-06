package com.smart.property.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smart.property.property.domain.ParkingPayment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 车位缴费 Mapper
 *
 * @author zzz
 * @since 2026-07-28
 */
@Mapper
public interface ParkingPaymentMapper extends BaseMapper<ParkingPayment> {
}
