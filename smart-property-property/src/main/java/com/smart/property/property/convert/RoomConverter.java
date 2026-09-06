package com.smart.property.property.convert;

import com.smart.property.property.domain.Room;
import com.smart.property.property.dto.RoomDTO;
import com.smart.property.property.vo.RoomVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * Room Entity ↔ DTO/VO 转换器。
 *
 * @author zzz
 * @since 2026-07-29
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RoomConverter {

    @Mapping(target = "communityName", ignore = true)
    @Mapping(target = "buildingName", ignore = true)
    @Mapping(target = "unitName", ignore = true)
    @Mapping(target = "ownerName", ignore = true)
    @Mapping(target = "tenantName", ignore = true)
    RoomVO toVO(Room entity);

    List<RoomVO> toVOList(List<Room> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    Room toEntity(RoomDTO dto);
}