package com.smart.property.operation.convert;

import com.smart.property.operation.domain.Regulation;
import com.smart.property.operation.dto.RegulationDTO;
import com.smart.property.operation.vo.RegulationVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * Regulation Entity ↔ DTO/VO 转换器。
 *
 * @author zzz
 * @since 2026-07-29
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RegulationConverter {

    RegulationVO toVO(Regulation entity);

    List<RegulationVO> toVOList(List<Regulation> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "publishTime", ignore = true)
    @Mapping(target = "viewCount", ignore = true)
    Regulation toEntity(RegulationDTO dto);
}