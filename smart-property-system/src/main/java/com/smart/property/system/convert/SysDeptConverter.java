package com.smart.property.system.convert;

import com.smart.property.system.domain.SysDept;
import com.smart.property.system.dto.SysDeptDTO;
import com.smart.property.system.vo.SysDeptVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * SysDept Entity ↔ DTO/VO 转换器。
 *
 * @author zzz
 * @since 2026-07-29
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SysDeptConverter {

    @Mapping(target = "children", ignore = true)
    SysDeptVO toVO(SysDept entity);

    List<SysDeptVO> toVOList(List<SysDept> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "children", ignore = true)
    SysDept toEntity(SysDeptDTO dto);
}