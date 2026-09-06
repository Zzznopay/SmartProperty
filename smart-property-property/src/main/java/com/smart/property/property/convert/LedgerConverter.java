package com.smart.property.property.convert;

import com.smart.property.property.domain.Ledger;
import com.smart.property.property.dto.LedgerDTO;
import com.smart.property.property.vo.LedgerVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * Ledger Entity ↔ DTO/VO 转换器。
 *
 * @author zzz
 * @since 2026-07-29
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LedgerConverter {

    @Mapping(target = "communityName", ignore = true)
    @Mapping(target = "roomNo", ignore = true)
    @Mapping(target = "ownerName", ignore = true)
    @Mapping(target = "feeItemName", ignore = true)
    LedgerVO toVO(Ledger entity);

    List<LedgerVO> toVOList(List<Ledger> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "payTime", ignore = true)
    Ledger toEntity(LedgerDTO dto);
}