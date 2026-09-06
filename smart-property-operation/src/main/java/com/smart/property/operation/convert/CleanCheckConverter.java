package com.smart.property.operation.convert;

import com.smart.property.operation.domain.CleanCheck;
import com.smart.property.operation.dto.CleanCheckDTO;
import com.smart.property.operation.vo.CleanCheckVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * CleanCheck Entity ↔ DTO/VO 转换器。
 *
 * @author zzz
 * @since 2026-07-30
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CleanCheckConverter {

    @Mapping(target = "communityName", ignore = true)
    CleanCheckVO toVO(CleanCheck entity);

    List<CleanCheckVO> toVOList(List<CleanCheck> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    CleanCheck toEntity(CleanCheckDTO dto);
}