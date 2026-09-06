package com.smart.property.property.convert;

import com.smart.property.property.domain.Invoice;
import com.smart.property.property.dto.InvoiceDTO;
import com.smart.property.property.vo.InvoiceVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * Invoice Entity ↔ DTO/VO 转换器。
 *
 * @author zzz
 * @since 2026-07-29
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface InvoiceConverter {

    InvoiceVO toVO(Invoice entity);

    List<InvoiceVO> toVOList(List<Invoice> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "useTime", ignore = true)
    @Mapping(target = "voidTime", ignore = true)
    @Mapping(target = "voidReason", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    Invoice toEntity(InvoiceDTO dto);
}