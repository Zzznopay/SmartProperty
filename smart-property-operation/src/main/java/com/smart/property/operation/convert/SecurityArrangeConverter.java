package com.smart.property.operation.convert;

import com.smart.property.operation.domain.SecurityArrange;
import com.smart.property.operation.dto.SecurityArrangeDTO;
import com.smart.property.operation.vo.SecurityArrangeVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * SecurityArrange Entity ↔ DTO/VO 转换器。
 *
 * @author zzz
 * @since 2026-07-30
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SecurityArrangeConverter {

    @Mapping(target = "communityName", ignore = true)
    SecurityArrangeVO toVO(SecurityArrange entity);

    List<SecurityArrangeVO> toVOList(List<SecurityArrange> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    SecurityArrange toEntity(SecurityArrangeDTO dto);
}