package com.smart.property.property.convert;

import com.smart.property.property.domain.Prepayment;
import com.smart.property.property.dto.PrepaymentDTO;
import com.smart.property.property.vo.PrepaymentVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * Prepayment Entity ↔ DTO/VO 转换器。
 *
 * @author zzz
 * @since 2026-07-29
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PrepaymentConverter {

    @Mapping(target = "ownerName", ignore = true)
    PrepaymentVO toVO(Prepayment entity);

    List<PrepaymentVO> toVOList(List<Prepayment> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "payTime", ignore = true)
    @Mapping(target = "balance", ignore = true)
    Prepayment toEntity(PrepaymentDTO dto);
}