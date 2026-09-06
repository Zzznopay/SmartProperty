package com.smart.property.system.convert;

import com.smart.property.system.domain.SysOperLog;
import com.smart.property.system.vo.SysOperLogVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * SysOperLog Entity -> VO 转换器。
 *
 * @author zzz
 * @since 2026-07-30
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SysOperLogConverter {

    SysOperLogVO toVO(SysOperLog entity);

    List<SysOperLogVO> toVOList(List<SysOperLog> entities);
}