package com.smart.property.operation.convert;

import com.smart.property.operation.domain.CommunityActivity;
import com.smart.property.operation.dto.CommunityActivityDTO;
import com.smart.property.operation.vo.CommunityActivityVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * CommunityActivity Entity ↔ DTO/VO 转换器。
 *
 * @author zzz
 * @since 2026-07-29
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CommunityActivityConverter {

    @Mapping(target = "communityName", ignore = true)
    CommunityActivityVO toVO(CommunityActivity entity);

    List<CommunityActivityVO> toVOList(List<CommunityActivity> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    CommunityActivity toEntity(CommunityActivityDTO dto);
}