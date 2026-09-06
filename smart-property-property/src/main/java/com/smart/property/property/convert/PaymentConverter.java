package com.smart.property.property.convert;

import com.smart.property.property.domain.Payment;
import com.smart.property.property.dto.PaymentDTO;
import com.smart.property.property.vo.PaymentVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * Payment Entity ↔ DTO/VO 转换器。
 *
 * @author zzz
 * @since 2026-07-29
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PaymentConverter {

    PaymentVO toVO(Payment entity);

    List<PaymentVO> toVOList(List<Payment> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "paymentNo", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "auditStatus", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    Payment toEntity(PaymentDTO dto);
}
