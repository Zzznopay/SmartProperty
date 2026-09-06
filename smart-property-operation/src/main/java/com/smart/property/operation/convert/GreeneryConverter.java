package com.smart.property.operation.convert;

import com.smart.property.operation.domain.Greenery;
import com.smart.property.operation.dto.GreeneryDTO;
import com.smart.property.operation.vo.GreeneryVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * Greenery Entity ↔ DTO/VO 转换器。
 *
 * @author zzz
 * @since 2026-07-29
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface GreeneryConverter {

    @Mapping(target = "communityName", ignore = true)
    GreeneryVO toVO(Greenery entity);

    List<GreeneryVO> toVOList(List<Greenery> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    Greenery toEntity(GreeneryDTO dto);
}