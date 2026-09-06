package com.smart.property.property.convert;

import com.smart.property.property.domain.ParkingPayment;
import com.smart.property.property.dto.ParkingPaymentDTO;
import com.smart.property.property.vo.ParkingPaymentVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * ParkingPayment Entity ↔ DTO/VO 转换器。
 *
 * @author zzz
 * @since 2026-07-29
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ParkingPaymentConverter {

    ParkingPaymentVO toVO(ParkingPayment entity);

    List<ParkingPaymentVO> toVOList(List<ParkingPayment> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "paymentNo", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    ParkingPayment toEntity(ParkingPaymentDTO dto);
}
