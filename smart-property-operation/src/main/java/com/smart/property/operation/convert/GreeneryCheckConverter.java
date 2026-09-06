package com.smart.property.operation.convert;

import com.smart.property.operation.domain.GreeneryCheck;
import com.smart.property.operation.dto.GreeneryCheckDTO;
import com.smart.property.operation.vo.GreeneryCheckVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * GreeneryCheck Entity ↔ DTO/VO 转换器。
 *
 * @author zzz
 * @since 2026-07-29
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface GreeneryCheckConverter {

    @Mapping(target = "communityName", ignore = true)
    GreeneryCheckVO toVO(GreeneryCheck entity);

    List<GreeneryCheckVO> toVOList(List<GreeneryCheck> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    GreeneryCheck toEntity(GreeneryCheckDTO dto);
}