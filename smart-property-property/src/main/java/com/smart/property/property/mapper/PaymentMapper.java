package com.smart.property.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smart.property.property.domain.Payment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 收费记录Mapper接口
 *
 * @author zzz
 * @since 2026-07-25
 */
@Mapper
public interface PaymentMapper extends BaseMapper<Payment> {
}
