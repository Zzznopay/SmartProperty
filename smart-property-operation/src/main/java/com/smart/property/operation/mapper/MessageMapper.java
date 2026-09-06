package com.smart.property.operation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smart.property.operation.domain.Message;
import org.apache.ibatis.annotations.Mapper;

/**
 * 消息Mapper接口
 *
 * @author zzz
 * @since 2026-07-25
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {
}
