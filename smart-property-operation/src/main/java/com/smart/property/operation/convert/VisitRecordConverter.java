package com.smart.property.operation.convert;

import com.smart.property.operation.domain.VisitRecord;
import com.smart.property.operation.dto.VisitRecordDTO;
import com.smart.property.operation.vo.VisitRecordVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * VisitRecord Entity ↔ DTO/VO 转换器。
 *
 * @author zzz
 * @since 2026-07-29
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface VisitRecordConverter {

    @Mapping(target = "communityName", ignore = true)
    @Mapping(target = "roomNo", ignore = true)
    VisitRecordVO toVO(VisitRecord entity);

    List<VisitRecordVO> toVOList(List<VisitRecord> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "guardId", ignore = true)
    @Mapping(target = "guardName", ignore = true)
    VisitRecord toEntity(VisitRecordDTO dto);
}