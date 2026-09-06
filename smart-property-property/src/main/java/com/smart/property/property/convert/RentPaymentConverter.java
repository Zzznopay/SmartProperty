package com.smart.property.property.convert;

import com.smart.property.property.domain.RentPayment;
import com.smart.property.property.dto.RentPaymentDTO;
import com.smart.property.property.vo.RentPaymentVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * RentPayment Entity ↔ DTO/VO 转换器。
 *
 * @author zzz
 * @since 2026-07-29
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RentPaymentConverter {

    @Mapping(target = "contractNo", ignore = true)
    @Mapping(target = "tenantName", ignore = true)
    @Mapping(target = "roomNo", ignore = true)
    RentPaymentVO toVO(RentPayment entity);

    List<RentPaymentVO> toVOList(List<RentPayment> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "payTime", ignore = true)
    RentPayment toEntity(RentPaymentDTO dto);
}