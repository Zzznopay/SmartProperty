package com.smart.property.property.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.property.domain.Owner;
import com.smart.property.property.dto.OwnerDTO;
import com.smart.property.property.vo.OwnerVO;

/**
 * 业主服务接口
 *
 * @author zzz
 * @since 2026-07-25
 */
public interface OwnerService extends IService<Owner> {

    /**
     * 分页查询业主列表
     */
    PageResult<OwnerVO> getOwnerPage(PageQuery query, Long companyId);

    /**
     * 按公司隔离查询业主详情
     */
    OwnerVO getOwnerById(Long id, Long companyId);

    /**
     * 新增业主
     */
    void addOwner(OwnerDTO dto, Long companyId, String operator);

    /**
     * 按公司隔离更新业主
     */
    void updateOwner(Long id, OwnerDTO dto, String companyId, String operator);

    /**
     * 按公司隔离逻辑删除业主
     */
    void deleteOwner(Long id, Long companyId);
}