package com.smart.property.operation.convert;

import com.smart.property.operation.domain.Notice;
import com.smart.property.operation.dto.NoticeDTO;
import com.smart.property.operation.vo.NoticeVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * Notice Entity ↔ DTO/VO 转换器。
 *
 * @author zzz
 * @since 2026-07-29
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NoticeConverter {

    @Mapping(target = "communityName", ignore = true)
    NoticeVO toVO(Notice entity);

    List<NoticeVO> toVOList(List<Notice> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "readCount", ignore = true)
    @Mapping(target = "publishTime", ignore = true)
    Notice toEntity(NoticeDTO dto);
}