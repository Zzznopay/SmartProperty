package com.smart.property.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smart.property.system.domain.SysOperLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志Mapper接口
 *
 * @author zzz
 * @since 2026-07-25
 */
@Mapper
public interface SysOperLogMapper extends BaseMapper<SysOperLog> {
}
