package com.smart.property.property.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smart.property.common.core.domain.Result;
import com.smart.property.property.domain.Building;
import com.smart.property.property.domain.Community;
import com.smart.property.property.domain.Room;
import com.smart.property.property.mapper.BuildingMapper;
import com.smart.property.property.mapper.CommunityMapper;
import com.smart.property.property.mapper.RoomMapper;
import com.smart.property.property.vo.InternalNameMapsVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * 内部名称批量解析接口（微服务间 Feign 调用专用）
 *
 * <p>operation 等服务与本服务不同库，列表 VO 的名称字段（小区/楼宇/房号）
 * 由调用方把 id 批量传过来，本接口一次返回 id → 名称 映射。
 * 网关侧同样受登录校验保护；服务间直连无需用户上下文。</p>
 *
 * @author zzz
 * @since 2026-09-06
 */
@RestController
@RequestMapping("/api/v1/internal/property/names")
@Tag(name = "内部-名称解析")
@RequiredArgsConstructor
public class InternalNameController {

    private final CommunityMapper communityMapper;
    private final BuildingMapper buildingMapper;
    private final RoomMapper roomMapper;

    @GetMapping
    public Result<InternalNameMapsVO> batch(
            @RequestParam(required = false) List<Long> communityIds,
            @RequestParam(required = false) List<Long> buildingIds,
            @RequestParam(required = false) List<Long> roomIds) {
        InternalNameMapsVO vo = new InternalNameMapsVO();
        vo.setCommunities(resolve(communityIds, communityMapper::selectBatchIds, Community::getId, Community::getCommunityName));
        vo.setBuildings(resolve(buildingIds, buildingMapper::selectBatchIds, Building::getId, Building::getBuildingName));
        vo.setRooms(resolve(roomIds, roomMapper::selectBatchIds, Room::getId, Room::getRoomNo));
        return Result.success(vo);
    }

    private <T> java.util.Map<Long, String> resolve(List<Long> ids,
                                                    Function<List<Long>, List<T>> loader,
                                                    Function<T, Long> keyGetter,
                                                    Function<T, String> valueGetter) {
        java.util.Map<Long, String> map = new java.util.HashMap<>();
        List<Long> distinctIds = ids == null ? List.of()
                : ids.stream().filter(Objects::nonNull).distinct().toList();
        if (distinctIds.isEmpty()) {
            return map;
        }
        for (T entity : loader.apply(distinctIds)) {
            map.put(keyGetter.apply(entity), valueGetter.apply(entity));
        }
        return map;
    }
}
