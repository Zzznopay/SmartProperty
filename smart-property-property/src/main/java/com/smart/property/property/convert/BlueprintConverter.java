package com.smart.property.property.convert;

import com.smart.property.property.domain.Blueprint;
import com.smart.property.property.dto.BlueprintDTO;
import com.smart.property.property.vo.BlueprintVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * Blueprint Entity ↔ DTO/VO 转换器。
 *
 * @author zzz
 * @since 2026-07-29
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BlueprintConverter {

    @Mapping(target = "communityName", ignore = true)
    @Mapping(target = "buildingName", ignore = true)
    BlueprintVO toVO(Blueprint entity);

    List<BlueprintVO> toVOList(List<Blueprint> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    Blueprint toEntity(BlueprintDTO dto);
}