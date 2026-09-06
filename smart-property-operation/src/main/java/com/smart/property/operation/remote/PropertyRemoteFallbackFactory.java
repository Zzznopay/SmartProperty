package com.smart.property.operation.remote;

import com.smart.property.common.core.domain.Result;
import com.smart.property.operation.remote.dto.PropertyNameMapsDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * PropertyRemoteClient 的 fallback 工厂：property 服务不可达时返回空映射兜底，
 * 列表页名称列降级为 "-"（前端另有 id→名称 解析兜底），不影响主数据查询。
 *
 * @author zzz
 * @since 2026-09-06
 */
@Slf4j
@Component
public class PropertyRemoteFallbackFactory implements FallbackFactory<PropertyRemoteClient> {

    @Override
    public PropertyRemoteClient create(Throwable cause) {
        log.warn("PropertyRemoteClient 不可用，名称解析使用空结果兜底: {}", cause.getMessage());
        return (communityIds, buildingIds, roomIds) -> Result.success(new PropertyNameMapsDTO());
    }
}
