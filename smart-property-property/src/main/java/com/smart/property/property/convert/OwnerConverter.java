package com.smart.property.property.convert;

import com.smart.property.property.domain.Owner;
import com.smart.property.property.dto.OwnerDTO;
import com.smart.property.property.vo.OwnerVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * Owner Entity ↔ DTO/VO 转换器。
 *
 * <p>字段差异处理：
 * <ul>
 *   <li>Entity → VO：phoneMask/idCardMask 同名直通；companyId/createBy/updateBy/isDeleted/loginIp/password 等不存在</li>
 *   <li>DTO → Entity：忽略 id/companyId/createBy/createTime/updateBy/updateTime/isDeleted；
 *       phone/idCard 由 Service 加密 + 脱敏处理。</li>
 * </ul>
 *
 * @author zzz
 * @since 2026-07-29
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OwnerConverter {

    OwnerVO toVO(Owner entity);

    List<OwnerVO> toVOList(List<Owner> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "phone", ignore = true)
    @Mapping(target = "idCard", ignore = true)
    Owner toEntity(OwnerDTO dto);
}