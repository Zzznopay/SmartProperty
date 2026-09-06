package com.smart.property.system.convert;

import com.smart.property.system.domain.FileFolder;
import com.smart.property.system.dto.FileFolderDTO;
import com.smart.property.system.vo.FileFolderVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * FileFolder Entity ↔ DTO/VO 转换器。
 *
 * @author zzz
 * @since 2026-07-29
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FileFolderConverter {

    FileFolderVO toVO(FileFolder entity);

    List<FileFolderVO> toVOList(List<FileFolder> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "folderPath", ignore = true)
    @Mapping(target = "isShared", ignore = true)
    @Mapping(target = "shareUserIds", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    FileFolder toEntity(FileFolderDTO dto);
}
