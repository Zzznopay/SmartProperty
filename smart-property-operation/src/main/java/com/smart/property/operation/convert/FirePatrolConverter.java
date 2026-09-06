package com.smart.property.operation.convert;

import com.smart.property.operation.domain.FirePatrol;
import com.smart.property.operation.dto.FirePatrolDTO;
import com.smart.property.operation.vo.FirePatrolVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * FirePatrol Entity ↔ DTO/VO 转换器。
 *
 * @author zzz
 * @since 2026-07-30
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FirePatrolConverter {

    @Mapping(target = "communityName", ignore = true)
    FirePatrolVO toVO(FirePatrol entity);

    List<FirePatrolVO> toVOList(List<FirePatrol> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    FirePatrol toEntity(FirePatrolDTO dto);
}