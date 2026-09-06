package com.smart.property.property.controller;

import com.smart.property.common.core.context.SecurityContextHolder;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.domain.Result;
import com.smart.property.common.log.annotation.OperLog;
import com.smart.property.property.dto.RoomDTO;
import com.smart.property.property.service.RoomService;
import com.smart.property.property.vo.RoomVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 房间管理控制器
 *
 * @author zzz
 * @since 2026-07-25
 */
@RestController
@RequestMapping("/api/v1/property/rooms")
@Tag(name = "房间")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    @OperLog(module = "房间管理", businessType = 4, description = "查询房间列表")
    public Result<PageResult<RoomVO>> list(PageQuery query,
                                           @RequestParam(required = false) Long communityId,
                                           @RequestParam(required = false) Long buildingId) {
        PageResult<RoomVO> result = roomService.getRoomPage(query,
                SecurityContextHolder.getCompanyId(), communityId, buildingId);
        return Result.success(result);
    }

    @GetMapping("/by-building")
    public Result<List<RoomVO>> listByBuilding(@RequestParam Long buildingId) {
        return Result.success(roomService.getRoomsByBuildingId(buildingId,
                SecurityContextHolder.getCompanyId()));
    }

    @GetMapping("/{id}")
    @OperLog(module = "房间管理", businessType = 4, description = "查询房间详情")
    public Result<RoomVO> getById(@PathVariable Long id) {
        return Result.success(roomService.getRoomById(id, SecurityContextHolder.getCompanyId()));
    }

    @PostMapping
    @OperLog(module = "房间管理", businessType = 1, description = "新增房间")
    public Result<Void> add(@Valid @RequestBody RoomDTO dto) {
        roomService.createRoom(dto, SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PutMapping("/{id}")
    @OperLog(module = "房间管理", businessType = 2, description = "修改房间")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody RoomDTO dto) {
        roomService.updateRoom(id, dto, SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @OperLog(module = "房间管理", businessType = 3, description = "删除房间")
    public Result<Void> delete(@PathVariable Long id) {
        roomService.deleteRoom(id, SecurityContextHolder.getCompanyId());
        return Result.success();
    }
}