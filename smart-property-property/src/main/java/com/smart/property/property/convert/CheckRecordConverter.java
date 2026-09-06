package com.smart.property.property.convert;

import com.smart.property.property.domain.CheckRecord;
import com.smart.property.property.dto.CheckRecordDTO;
import com.smart.property.property.vo.CheckRecordVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * CheckRecord Entity ↔ DTO/VO 转换器。
 *
 * @author zzz
 * @since 2026-07-29
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CheckRecordConverter {

    @Mapping(target = "roomNo", ignore = true)
    @Mapping(target = "ownerName", ignore = true)
    CheckRecordVO toVO(CheckRecord entity);

    List<CheckRecordVO> toVOList(List<CheckRecord> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    CheckRecord toEntity(CheckRecordDTO dto);
}