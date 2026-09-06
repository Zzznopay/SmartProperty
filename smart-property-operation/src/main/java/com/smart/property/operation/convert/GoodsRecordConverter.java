package com.smart.property.operation.convert;

import com.smart.property.operation.domain.GoodsRecord;
import com.smart.property.operation.dto.GoodsRecordDTO;
import com.smart.property.operation.vo.GoodsRecordVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * GoodsRecord Entity ↔ DTO/VO 转换器。
 *
 * @author zzz
 * @since 2026-07-30
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface GoodsRecordConverter {

    @Mapping(target = "communityName", ignore = true)
    @Mapping(target = "roomNo", ignore = true)
    GoodsRecordVO toVO(GoodsRecord entity);

    List<GoodsRecordVO> toVOList(List<GoodsRecord> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    GoodsRecord toEntity(GoodsRecordDTO dto);
}