package com.smart.property.property.convert;

import com.smart.property.property.domain.MeterReading;
import com.smart.property.property.dto.MeterReadingDTO;
import com.smart.property.property.vo.MeterReadingVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * MeterReading Entity ↔ DTO/VO 转换器。
 *
 * @author zzz
 * @since 2026-07-29
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MeterReadingConverter {

    @Mapping(target = "roomNo", ignore = true)
    MeterReadingVO toVO(MeterReading entity);

    List<MeterReadingVO> toVOList(List<MeterReading> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "usageAmount", ignore = true)
    MeterReading toEntity(MeterReadingDTO dto);
}