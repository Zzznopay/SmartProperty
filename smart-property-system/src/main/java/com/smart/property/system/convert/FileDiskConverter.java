package com.smart.property.system.convert;

import com.smart.property.system.domain.FileDisk;
import com.smart.property.system.dto.FileDiskDTO;
import com.smart.property.system.vo.FileDiskVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * FileDisk Entity ↔ DTO/VO 转换器。
 *
 * @author zzz
 * @since 2026-07-29
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FileDiskConverter {

    FileDiskVO toVO(FileDisk entity);

    List<FileDiskVO> toVOList(List<FileDisk> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "isShared", ignore = true)
    @Mapping(target = "shareUserIds", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    FileDisk toEntity(FileDiskDTO dto);
}
