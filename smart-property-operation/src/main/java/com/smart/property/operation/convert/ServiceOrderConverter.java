package com.smart.property.operation.convert;

import com.smart.property.operation.domain.ServiceOrder;
import com.smart.property.operation.dto.ServiceOrderDTO;
import com.smart.property.operation.vo.ServiceOrderVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * ServiceOrder Entity ↔ DTO/VO 转换器。
 *
 * @author zzz
 * @since 2026-07-29
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ServiceOrderConverter {

    @Mapping(target = "communityName", ignore = true)
    @Mapping(target = "roomNo", ignore = true)
    ServiceOrderVO toVO(ServiceOrder entity);

    List<ServiceOrderVO> toVOList(List<ServiceOrder> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "orderNo", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "assignTime", ignore = true)
    @Mapping(target = "handleTime", ignore = true)
    @Mapping(target = "visitTime", ignore = true)
    @Mapping(target = "closeTime", ignore = true)
    ServiceOrder toEntity(ServiceOrderDTO dto);
}