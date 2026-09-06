package com.smart.property.property.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.property.domain.Room;
import com.smart.property.property.dto.RoomDTO;
import com.smart.property.property.vo.RoomVO;

import java.util.List;

/**
 * 房间服务接口
 *
 * @author zzz
 * @since 2026-07-25
 */
public interface RoomService extends IService<Room> {

    PageResult<RoomVO> getRoomPage(PageQuery query, Long companyId, Long communityId, Long buildingId);

    List<RoomVO> getRoomsByBuildingId(Long buildingId, Long companyId);

    RoomVO getRoomById(Long id, Long companyId);

    void createRoom(RoomDTO dto, Long companyId, String operator);

    void updateRoom(Long id, RoomDTO dto, Long companyId, String operator);

    void deleteRoom(Long id, Long companyId);
}