package com.smart.property.property.convert;

import com.smart.property.property.domain.LeaseContract;
import com.smart.property.property.dto.LeaseContractDTO;
import com.smart.property.property.vo.LeaseContractVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * LeaseContract Entity ↔ DTO/VO 转换器。
 *
 * @author zzz
 * @since 2026-07-29
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LeaseContractConverter {

    @Mapping(target = "roomNo", ignore = true)
    @Mapping(target = "tenantName", ignore = true)
    LeaseContractVO toVO(LeaseContract entity);

    List<LeaseContractVO> toVOList(List<LeaseContract> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "terminateDate", ignore = true)
    @Mapping(target = "terminateReason", ignore = true)
    LeaseContract toEntity(LeaseContractDTO dto);
}