package com.smart.property.operation.remote;

import com.smart.property.common.core.domain.Result;
import com.smart.property.operation.remote.dto.PropertyNameMapsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 房产服务远程客户端（operation → property）
 *
 * <p>两服务不同库：运营侧 VO 的小区/楼宇/房号名称字段通过本客户端
 * 按 id 批量解析。fallback 见 {@link PropertyRemoteFallbackFactory}。</p>
 *
 * @author zzz
 * @since 2026-09-06
 */
@FeignClient(
        name = "smart-property-property",
        contextId = "propertyRemoteClient-operation",
        fallbackFactory = PropertyRemoteFallbackFactory.class
)
public interface PropertyRemoteClient {

    @GetMapping("/api/v1/internal/property/names")
    Result<PropertyNameMapsDTO> batchNames(
            @RequestParam("communityIds") List<Long> communityIds,
            @RequestParam("buildingIds") List<Long> buildingIds,
            @RequestParam("roomIds") List<Long> roomIds);
}
