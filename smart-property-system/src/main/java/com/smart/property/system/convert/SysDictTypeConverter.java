package com.smart.property.system.convert;

import com.smart.property.system.domain.SysDictType;
import com.smart.property.system.dto.SysDictTypeDTO;
import com.smart.property.system.vo.SysDictTypeVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * SysDictType Entity ↔ DTO/VO 转换器。
 *
 * @author zzz
 * @since 2026-07-29
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SysDictTypeConverter {

    SysDictTypeVO toVO(SysDictType entity);

    List<SysDictTypeVO> toVOList(List<SysDictType> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    SysDictType toEntity(SysDictTypeDTO dto);
}