package com.smart.property.system.convert;

import com.smart.property.system.domain.SysCompany;
import com.smart.property.system.dto.SysCompanyDTO;
import com.smart.property.system.vo.SysCompanyVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * SysCompany Entity ↔ DTO/VO 转换器。
 *
 * @author zzz
 * @since 2026-07-29
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SysCompanyConverter {

    @Mapping(target = "children", ignore = true)
    SysCompanyVO toVO(SysCompany entity);

    List<SysCompanyVO> toVOList(List<SysCompany> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "children", ignore = true)
    SysCompany toEntity(SysCompanyDTO dto);
}