package com.smart.property.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.exception.BusinessException;
import com.smart.property.property.convert.RoomConverter;
import com.smart.property.property.domain.Building;
import com.smart.property.property.domain.Community;
import com.smart.property.property.domain.Room;
import com.smart.property.property.domain.Unit;
import com.smart.property.property.dto.RoomDTO;
import com.smart.property.property.mapper.BuildingMapper;
import com.smart.property.property.mapper.CommunityMapper;
import com.smart.property.property.mapper.RoomMapper;
import com.smart.property.property.mapper.UnitMapper;
import com.smart.property.property.service.RoomService;
import com.smart.property.property.vo.RoomVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 房间服务实现
 *
 * @author zzz
 * @since 2026-07-25
 */
@Service
@RequiredArgsConstructor
public class RoomServiceImpl extends ServiceImpl<RoomMapper, Room> implements RoomService {

    private final RoomConverter roomConverter;
    private final CommunityMapper communityMapper;
    private final BuildingMapper buildingMapper;
    private final UnitMapper unitMapper;

    /** 批量回填 VO 的小区/楼宇/单元名称（列表接口 VO 只带 id） */
    private void fillRoomNames(List<RoomVO> records) {
        if (records.isEmpty()) {
            return;
        }
        Map<Long, String> communityNames = communityNamesById(
                records.stream().map(RoomVO::getCommunityId).filter(Objects::nonNull).distinct().toList());
        Map<Long, String> buildingNames = buildingNamesById(
                records.stream().map(RoomVO::getBuildingId).filter(Objects::nonNull).distinct().toList());
        Map<Long, String> unitNames = unitNamesById(
                records.stream().map(RoomVO::getUnitId).filter(Objects::nonNull).distinct().toList());
        records.forEach(vo -> {
            vo.setCommunityName(communityNames.get(vo.getCommunityId()));
            vo.setBuildingName(buildingNames.get(vo.getBuildingId()));
            vo.setUnitName(unitNames.get(vo.getUnitId()));
        });
    }

    private Map<Long, String> communityNamesById(List<Long> ids) {
        if (ids.isEmpty()) {
            return new HashMap<>();
        }
        return communityMapper.selectList(new LambdaQueryWrapper<Community>().in(Community::getId, ids))
                .stream()
                .collect(Collectors.toMap(Community::getId, Community::getCommunityName, (a, b) -> a));
    }

    private Map<Long, String> buildingNamesById(List<Long> ids) {
        if (ids.isEmpty()) {
            return new HashMap<>();
        }
        return buildingMapper.selectList(new LambdaQueryWrapper<Building>().in(Building::getId, ids))
                .stream()
                .collect(Collectors.toMap(Building::getId, Building::getBuildingName, (a, b) -> a));
    }

    private Map<Long, String> unitNamesById(List<Long> ids) {
        if (ids.isEmpty()) {
            return new HashMap<>();
        }
        return unitMapper.selectList(new LambdaQueryWrapper<Unit>().in(Unit::getId, ids))
                .stream()
                .collect(Collectors.toMap(Unit::getId, Unit::getUnitName, (a, b) -> a));
    }

    @Override
    public PageResult<RoomVO> getRoomPage(PageQuery query, Long companyId, Long communityId, Long buildingId) {
        Page<Room> page = new Page<>(query.getPageNum(), query.getPageSize());

        LambdaQueryWrapper<Room> wrapper = new LambdaQueryWrapper<Room>()
                .eq(Room::getCompanyId, companyId)
                .eq(Room::getIsDeleted, 0)
                .eq(communityId != null, Room::getCommunityId, communityId)
                .eq(buildingId != null, Room::getBuildingId, buildingId)
                .orderByAsc(Room::getRoomNo);

        Page<Room> result = baseMapper.selectPage(page, wrapper);

        List<RoomVO> records = result.getRecords().stream()
                .map(roomConverter::toVO)
                .collect(Collectors.toList());
        fillRoomNames(records);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), records);
    }

    @Override
    public List<RoomVO> getRoomsByBuildingId(Long buildingId, Long companyId) {
        List<Room> records = baseMapper.selectList(
                new LambdaQueryWrapper<Room>()
                        .eq(Room::getBuildingId, buildingId)
                        .eq(Room::getCompanyId, companyId)
                        .eq(Room::getIsDeleted, 0)
                        .orderByAsc(Room::getFloor)
                        .orderByAsc(Room::getRoomNo)
        );
        List<RoomVO> vos = roomConverter.toVOList(records);
        fillRoomNames(vos);
        return vos;
    }

    @Override
    public RoomVO getRoomById(Long id, Long companyId) {
        RoomVO vo = roomConverter.toVO(getCompanyRoom(id, companyId));
        fillRoomNames(List.of(vo));
        return vo;
    }

    @Override
    public void createRoom(RoomDTO dto, Long companyId, String operator) {
        Room room = roomConverter.toEntity(dto);
        room.setCompanyId(companyId);
        room.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        room.setCreateBy(operator);
        baseMapper.insert(room);
    }

    @Override
    public void updateRoom(Long id, RoomDTO dto, Long companyId, String operator) {
        Room existing = getCompanyRoom(id, companyId);
        Room patch = roomConverter.toEntity(dto);
        patch.setId(id);
        patch.setCompanyId(existing.getCompanyId());
        patch.setCreateBy(existing.getCreateBy());
        patch.setCreateTime(existing.getCreateTime());
        patch.setStatus(dto.getStatus() != null ? dto.getStatus() : existing.getStatus());
        patch.setUpdateBy(operator);
        baseMapper.updateById(patch);
    }

    @Override
    public void deleteRoom(Long id, Long companyId) {
        getCompanyRoom(id, companyId);
        removeById(id);
    }

    private Room getCompanyRoom(Long id, Long companyId) {
        Room room = getOne(new LambdaQueryWrapper<Room>()
                .eq(Room::getId, id)
                .eq(Room::getCompanyId, companyId));
        if (room == null) {
            throw new BusinessException("房间不存在");
        }
        return room;
    }
}