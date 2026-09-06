package com.smart.property.property.convert;

import com.smart.property.property.domain.SaleContract;
import com.smart.property.property.dto.SaleContractDTO;
import com.smart.property.property.vo.SaleContractVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * SaleContract Entity ↔ DTO/VO 转换器。
 *
 * @author zzz
 * @since 2026-07-29
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SaleContractConverter {

    @Mapping(target = "roomNo", ignore = true)
    @Mapping(target = "ownerName", ignore = true)
    SaleContractVO toVO(SaleContract entity);

    List<SaleContractVO> toVOList(List<SaleContract> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    SaleContract toEntity(SaleContractDTO dto);
}