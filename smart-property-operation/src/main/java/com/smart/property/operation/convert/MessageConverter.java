package com.smart.property.operation.convert;

import com.smart.property.operation.domain.Message;
import com.smart.property.operation.dto.MessageDTO;
import com.smart.property.operation.vo.MessageVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * Message Entity ↔ DTO/VO 转换器。
 *
 * @author zzz
 * @since 2026-07-29
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MessageConverter {

    MessageVO toVO(Message entity);

    List<MessageVO> toVOList(List<Message> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "senderId", ignore = true)
    @Mapping(target = "senderName", ignore = true)
    @Mapping(target = "isRead", ignore = true)
    @Mapping(target = "readTime", ignore = true)
    @Mapping(target = "sendStatus", ignore = true)
    @Mapping(target = "sendTime", ignore = true)
    @Mapping(target = "failReason", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    Message toEntity(MessageDTO dto);
}