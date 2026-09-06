package com.smart.property.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smart.property.property.domain.Tenant;
import org.apache.ibatis.annotations.Mapper;

/**
 * 租户Mapper接口
 *
 * @author zzz
 * @since 2026-07-25
 */
@Mapper
public interface TenantMapper extends BaseMapper<Tenant> {
}
