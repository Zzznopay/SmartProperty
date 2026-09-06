package com.smart.property.operation.convert;

import com.smart.property.operation.domain.CommitteeMeeting;
import com.smart.property.operation.dto.CommitteeMeetingDTO;
import com.smart.property.operation.vo.CommitteeMeetingVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * CommitteeMeeting Entity ↔ DTO/VO 转换器。
 *
 * @author zzz
 * @since 2026-07-30
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CommitteeMeetingConverter {

    @Mapping(target = "communityName", ignore = true)
    CommitteeMeetingVO toVO(CommitteeMeeting entity);

    List<CommitteeMeetingVO> toVOList(List<CommitteeMeeting> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    CommitteeMeeting toEntity(CommitteeMeetingDTO dto);
}