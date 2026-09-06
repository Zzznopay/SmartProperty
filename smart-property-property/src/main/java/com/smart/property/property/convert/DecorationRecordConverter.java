package com.smart.property.property.convert;

import com.smart.property.property.domain.DecorationRecord;
import com.smart.property.property.dto.DecorationRecordDTO;
import com.smart.property.property.vo.DecorationRecordVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * DecorationRecord Entity ↔ DTO/VO 转换器。
 *
 * @author zzz
 * @since 2026-07-29
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DecorationRecordConverter {

    @Mapping(target = "roomNo", ignore = true)
    @Mapping(target = "ownerName", ignore = true)
    DecorationRecordVO toVO(DecorationRecord entity);

    List<DecorationRecordVO> toVOList(List<DecorationRecord> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    DecorationRecord toEntity(DecorationRecordDTO dto);
}