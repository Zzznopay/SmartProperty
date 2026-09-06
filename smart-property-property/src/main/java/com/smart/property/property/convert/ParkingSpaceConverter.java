package com.smart.property.property.convert;

import com.smart.property.property.domain.ParkingSpace;
import com.smart.property.property.dto.ParkingSpaceDTO;
import com.smart.property.property.vo.ParkingSpaceVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * ParkingSpace Entity ↔ DTO/VO 转换器。
 *
 * @author zzz
 * @since 2026-07-29
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ParkingSpaceConverter {

    @Mapping(target = "communityName", ignore = true)
    @Mapping(target = "ownerName", ignore = true)
    @Mapping(target = "tenantName", ignore = true)
    ParkingSpaceVO toVO(ParkingSpace entity);

    List<ParkingSpaceVO> toVOList(List<ParkingSpace> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    ParkingSpace toEntity(ParkingSpaceDTO dto);
}