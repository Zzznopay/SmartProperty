package com.smart.property.system.convert;

import com.smart.property.system.domain.SysLoginLog;
import com.smart.property.system.vo.SysLoginLogVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * SysLoginLog Entity -> VO 转换器。
 *
 * @author zzz
 * @since 2026-07-30
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SysLoginLogConverter {

    SysLoginLogVO toVO(SysLoginLog entity);

    List<SysLoginLogVO> toVOList(List<SysLoginLog> entities);
}