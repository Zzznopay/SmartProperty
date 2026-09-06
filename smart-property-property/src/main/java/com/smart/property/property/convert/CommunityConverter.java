package com.smart.property.property.convert;

import com.smart.property.property.domain.Community;
import com.smart.property.property.dto.CommunityDTO;
import com.smart.property.property.vo.CommunityVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * Community Entity ↔ DTO/VO 转换器。
 *
 * <p>contactPhone 由 Service 单独加密/解密（Converter 忽略该字段）。</p>
 *
 * @author zzz
 * @since 2026-07-29
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CommunityConverter {

    CommunityVO toVO(Community entity);

    List<CommunityVO> toVOList(List<Community> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "contactPhone", ignore = true)
    Community toEntity(CommunityDTO dto);
}