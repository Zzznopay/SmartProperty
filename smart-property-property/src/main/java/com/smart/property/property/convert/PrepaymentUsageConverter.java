package com.smart.property.property.convert;

import com.smart.property.property.domain.PrepaymentUsage;
import com.smart.property.property.vo.PrepaymentUsageVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * PrepaymentUsage Entity -> VO 转换器。
 *
 * @author zzz
 * @since 2026-07-29
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PrepaymentUsageConverter {

    PrepaymentUsageVO toVO(PrepaymentUsage entity);

    List<PrepaymentUsageVO> toVOList(List<PrepaymentUsage> entities);
}
