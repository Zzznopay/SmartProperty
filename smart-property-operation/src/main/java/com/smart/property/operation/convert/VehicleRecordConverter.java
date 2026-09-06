package com.smart.property.operation.convert;

import com.smart.property.operation.domain.VehicleRecord;
import com.smart.property.operation.dto.VehicleRecordDTO;
import com.smart.property.operation.vo.VehicleRecordVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * VehicleRecord Entity ↔ DTO/VO 转换器。
 *
 * @author zzz
 * @since 2026-07-29
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface VehicleRecordConverter {

    @Mapping(target = "communityName", ignore = true)
    VehicleRecordVO toVO(VehicleRecord entity);

    List<VehicleRecordVO> toVOList(List<VehicleRecord> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "payTime", ignore = true)
    VehicleRecord toEntity(VehicleRecordDTO dto);
}