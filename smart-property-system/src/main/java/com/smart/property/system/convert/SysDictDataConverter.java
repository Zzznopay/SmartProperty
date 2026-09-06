package com.smart.property.system.convert;

import com.smart.property.system.domain.SysDictData;
import com.smart.property.system.dto.SysDictDataDTO;
import com.smart.property.system.vo.SysDictDataVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * SysDictData Entity ↔ DTO/VO 转换器。
 *
 * @author zzz
 * @since 2026-07-29
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SysDictDataConverter {

    SysDictDataVO toVO(SysDictData entity);

    List<SysDictDataVO> toVOList(List<SysDictData> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    SysDictData toEntity(SysDictDataDTO dto);
}