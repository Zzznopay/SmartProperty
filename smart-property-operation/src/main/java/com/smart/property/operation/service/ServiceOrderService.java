package com.smart.property.operation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.operation.domain.ServiceOrder;
import com.smart.property.operation.dto.ServiceOrderDTO;
import com.smart.property.operation.vo.ServiceOrderVO;

/**
 * 服务工单服务接口
 *
 * @author zzz
 * @since 2026-07-25
 */
public interface ServiceOrderService extends IService<ServiceOrder> {

    PageResult<ServiceOrderVO> getOrderPage(PageQuery query, Long companyId, Integer orderType, Integer status);

    ServiceOrderVO getOrderById(Long id);

    void createOrder(ServiceOrderDTO dto, Long companyId, Long userId, String username);

    void assignOrder(Long orderId, Long assignUserId, String assignUserName, Long operatorId, String operatorName);

    void handleOrder(Long orderId, String handleContent, Long operatorId, String operatorName);

    void visitOrder(Long orderId, String visitContent, Integer visitScore, Long operatorId, String operatorName);

    void closeOrder(Long orderId, Long operatorId, String operatorName);
}