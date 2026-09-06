package com.smart.property.property.convert;

import com.smart.property.property.domain.PaymentDetail;
import com.smart.property.property.dto.PaymentDetailDTO;
import com.smart.property.property.vo.PaymentDetailVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * PaymentDetail Entity ↔ DTO/VO 转换器。
 *
 * @author zzz
 * @since 2026-07-29
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PaymentDetailConverter {

    PaymentDetailVO toVO(PaymentDetail entity);

    List<PaymentDetailVO> toVOList(List<PaymentDetail> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "paymentId", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    PaymentDetail toEntity(PaymentDetailDTO dto);
}
