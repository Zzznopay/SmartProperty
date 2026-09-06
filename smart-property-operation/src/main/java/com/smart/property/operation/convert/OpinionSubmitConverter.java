package com.smart.property.operation.convert;

import com.smart.property.operation.domain.OpinionSubmit;
import com.smart.property.operation.dto.OpinionSubmitDTO;
import com.smart.property.operation.vo.OpinionSubmitVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * OpinionSubmit Entity ↔ DTO/VO 转换器。
 *
 * @author zzz
 * @since 2026-07-29
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OpinionSubmitConverter {

    @Mapping(target = "boxName", ignore = true)
    OpinionSubmitVO toVO(OpinionSubmit entity);

    List<OpinionSubmitVO> toVOList(List<OpinionSubmit> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "submitUserId", ignore = true)
    @Mapping(target = "submitUserName", ignore = true)
    @Mapping(target = "submitTime", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "replyContent", ignore = true)
    @Mapping(target = "replyUserId", ignore = true)
    @Mapping(target = "replyUserName", ignore = true)
    @Mapping(target = "replyTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    OpinionSubmit toEntity(OpinionSubmitDTO dto);
}