package com.smart.property.operation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smart.property.operation.domain.Survey;
import org.apache.ibatis.annotations.Mapper;

/**
 * 投票调查Mapper接口
 *
 * @author zzz
 * @since 2026-07-25
 */
@Mapper
public interface SurveyMapper extends BaseMapper<Survey> {
}
