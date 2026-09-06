package com.smart.property.operation.convert;

import com.smart.property.operation.domain.DutyRecord;
import com.smart.property.operation.dto.DutyRecordDTO;
import com.smart.property.operation.vo.DutyRecordVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * DutyRecord Entity ↔ DTO/VO 转换器。
 *
 * @author zzz
 * @since 2026-07-29
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DutyRecordConverter {

    @Mapping(target = "communityName", ignore = true)
    DutyRecordVO toVO(DutyRecord entity);

    List<DutyRecordVO> toVOList(List<DutyRecord> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    DutyRecord toEntity(DutyRecordDTO dto);
}