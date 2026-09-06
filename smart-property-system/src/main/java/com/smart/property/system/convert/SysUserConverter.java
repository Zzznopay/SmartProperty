package com.smart.property.system.convert;

import com.smart.property.system.domain.SysUser;
import com.smart.property.system.dto.UserDTO;
import com.smart.property.system.vo.UserVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * SysUser Entity ↔ DTO/VO 转换器。
 *
 * <p>字段差异处理：
 * <ul>
 *   <li>Entity → VO：phoneMask/avatar/email/realName 等同名直通；
 *       deptName/roles 需在 Service 二次填充。</li>
 *   <li>DTO → Entity：忽略 id/companyId/createBy/createTime/updateBy/updateTime/
 *       isDeleted/loginIp/loginTime/password/avatar；phone/phoneMask 在 Service 中做加密/脱敏；
 *       password 由 Service 调用 PasswordEncoder 处理。</li>
 * </ul>
 *
 * @author zzz
 * @since 2026-07-29
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SysUserConverter {

    @Mapping(target = "deptName", ignore = true)
    @Mapping(target = "roles", ignore = true)
    UserVO toVO(SysUser entity);

    List<UserVO> toVOList(List<SysUser> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "loginIp", ignore = true)
    @Mapping(target = "loginTime", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "phone", ignore = true)
    @Mapping(target = "phoneMask", ignore = true)
    @Mapping(target = "avatar", ignore = true)
    SysUser toEntity(UserDTO dto);
}