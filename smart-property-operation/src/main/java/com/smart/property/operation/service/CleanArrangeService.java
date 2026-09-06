package com.smart.property.operation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.operation.domain.CleanArrange;
import com.smart.property.operation.dto.CleanArrangeDTO;
import com.smart.property.operation.vo.CleanArrangeVO;

import java.time.LocalDate;

/**
 * 清洁安排服务接口
 *
 * @author zzz
 * @since 2026-07-25
 */
public interface CleanArrangeService extends IService<CleanArrange> {

    /**
     * 分页查询清洁安排
     */
    PageResult<CleanArrangeVO> getCleanArrangePage(PageQuery query, Long companyId, Long communityId, LocalDate arrangeDate);

    /**
     * 新增清洁安排
     */
    void createCleanArrange(CleanArrangeDTO dto, Long companyId, String operator);

    /**
     * 完成清洁
     */
    void completeClean(Long id, String operator);
}
