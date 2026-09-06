package com.smart.property.operation.convert;

import com.smart.property.operation.domain.SurveyOption;
import com.smart.property.operation.vo.SurveyOptionVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * SurveyOption Entity -> VO 转换器。
 *
 * <p>仅出参转换：选项由 Service 内部创建，无需 toEntity。</p>
 *
 * @author zzz
 * @since 2026-07-30
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SurveyOptionConverter {

    SurveyOptionVO toVO(SurveyOption entity);

    List<SurveyOptionVO> toVOList(List<SurveyOption> entities);
}