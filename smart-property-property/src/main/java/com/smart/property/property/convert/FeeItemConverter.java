package com.smart.property.property.convert;

import com.smart.property.property.domain.FeeItem;
import com.smart.property.property.dto.FeeItemDTO;
import com.smart.property.property.vo.FeeItemVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * FeeItem Entity ↔ DTO/VO 转换器。
 *
 * @author zzz
 * @since 2026-07-29
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FeeItemConverter {

    @Mapping(target = "communityName", ignore = true)
    FeeItemVO toVO(FeeItem entity);

    List<FeeItemVO> toVOList(List<FeeItem> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    FeeItem toEntity(FeeItemDTO dto);
}