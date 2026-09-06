package com.smart.property.operation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smart.property.operation.domain.OrderFlow;
import org.apache.ibatis.annotations.Mapper;

/**
 * 工单流转记录Mapper接口
 *
 * @author zzz
 * @since 2026-07-25
 */
@Mapper
public interface OrderFlowMapper extends BaseMapper<OrderFlow> {
}
