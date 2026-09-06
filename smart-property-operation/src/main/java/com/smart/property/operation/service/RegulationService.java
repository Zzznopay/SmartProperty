package com.smart.property.operation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.operation.domain.Regulation;
import com.smart.property.operation.dto.RegulationDTO;
import com.smart.property.operation.vo.RegulationVO;

/**
 * 规章制度服务接口
 *
 * @author zzz
 * @since 2026-07-25
 */
public interface RegulationService extends IService<Regulation> {

    PageResult<RegulationVO> getRegulationPage(PageQuery query, Long companyId, String category);

    RegulationVO getRegulationVOById(Long id);

    void createRegulation(RegulationDTO dto, Long companyId, String operator);

    void publishRegulation(Long id, String operator);

    void incrementViewCount(Long id);
}