package com.smart.property.operation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.exception.BusinessException;
import com.smart.property.common.mq.event.OrderCreatedEvent;
import com.smart.property.common.mq.util.MqUtils;
import com.smart.property.operation.convert.ServiceOrderConverter;
import com.smart.property.operation.domain.OrderFlow;
import com.smart.property.operation.domain.ServiceOrder;
import com.smart.property.operation.dto.ServiceOrderDTO;
import com.smart.property.operation.mapper.OrderFlowMapper;
import com.smart.property.operation.mapper.ServiceOrderMapper;
import com.smart.property.operation.remote.RemoteNameService;
import com.smart.property.operation.service.ServiceOrderService;
import com.smart.property.operation.vo.ServiceOrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 服务工单服务实现
 *
 * @author zzz
 * @since 2026-07-25
 */
@Service
@RequiredArgsConstructor
public class ServiceOrderServiceImpl extends ServiceImpl<ServiceOrderMapper, ServiceOrder> implements ServiceOrderService {

    private final OrderFlowMapper orderFlowMapper;
    private final MqUtils mqUtils;
    private final ServiceOrderConverter serviceOrderConverter;
    private final RemoteNameService remoteNameService;

    @Override
    public PageResult<ServiceOrderVO> getOrderPage(PageQuery query, Long companyId, Integer orderType, Integer status) {
        Page<ServiceOrder> page = new Page<>(query.getPageNum(), query.getPageSize());

        LambdaQueryWrapper<ServiceOrder> wrapper = new LambdaQueryWrapper<ServiceOrder>()
                .eq(ServiceOrder::getCompanyId, companyId)
                .eq(ServiceOrder::getIsDeleted, 0)
                .eq(orderType != null, ServiceOrder::getOrderType, orderType)
                .eq(status != null, ServiceOrder::getStatus, status)
                .orderByDesc(ServiceOrder::getCreateTime);

        Page<ServiceOrder> result = baseMapper.selectPage(page, wrapper);
        List<ServiceOrderVO> records = result.getRecords().stream()
                .map(serviceOrderConverter::toVO)
                .collect(Collectors.toList());
        remoteNameService.fillCommunityNames(records, ServiceOrderVO::getCommunityId, ServiceOrderVO::setCommunityName);
        remoteNameService.fillRoomNos(records, ServiceOrderVO::getRoomId, ServiceOrderVO::setRoomNo);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), records);
    }

    @Override
    public ServiceOrderVO getOrderById(Long id) {
        ServiceOrder order = getById(id);
        if (order == null) {
            throw new BusinessException("工单不存在");
        }
        ServiceOrderVO vo = serviceOrderConverter.toVO(order);
        remoteNameService.fillCommunityNames(List.of(vo), ServiceOrderVO::getCommunityId, ServiceOrderVO::setCommunityName);
        remoteNameService.fillRoomNos(List.of(vo), ServiceOrderVO::getRoomId, ServiceOrderVO::setRoomNo);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createOrder(ServiceOrderDTO dto, Long companyId, Long userId, String username) {
        ServiceOrder order = serviceOrderConverter.toEntity(dto);
        order.setCompanyId(companyId);
        order.setOrderNo(generateOrderNo());
        order.setStatus(1);
        order.setCreateBy(username);
        baseMapper.insert(order);

        saveFlow(order.getCompanyId(), order.getId(), 1, "创建工单", userId, username);

        mqUtils.sendOrderCreated(new OrderCreatedEvent(
                order.getId(),
                order.getOrderNo(),
                order.getCompanyId(),
                order.getTitle(),
                username,
                LocalDateTime.now()
        ));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignOrder(Long orderId, Long assignUserId, String assignUserName, Long operatorId, String operatorName) {
        ServiceOrder order = getById(orderId);
        if (order == null) throw new BusinessException("工单不存在");
        if (order.getStatus() != 1) {
            throw new BusinessException("当前状态不允许分配");
        }
        order.setAssignUserId(assignUserId);
        order.setAssignUserName(assignUserName);
        order.setAssignTime(LocalDateTime.now());
        order.setStatus(2);
        order.setUpdateBy(operatorName);
        baseMapper.updateById(order);
        saveFlow(order.getCompanyId(), orderId, 2, "分配给" + assignUserName, operatorId, operatorName);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleOrder(Long orderId, String handleContent, Long operatorId, String operatorName) {
        ServiceOrder order = getById(orderId);
        if (order == null) throw new BusinessException("工单不存在");
        if (order.getStatus() != 2) {
            throw new BusinessException("当前状态不允许处理");
        }
        order.setHandleContent(handleContent);
        order.setHandleTime(LocalDateTime.now());
        order.setStatus(3);
        order.setUpdateBy(operatorName);
        baseMapper.updateById(order);
        saveFlow(order.getCompanyId(), orderId, 3, handleContent, operatorId, operatorName);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void visitOrder(Long orderId, String visitContent, Integer visitScore, Long operatorId, String operatorName) {
        ServiceOrder order = getById(orderId);
        if (order == null) throw new BusinessException("工单不存在");
        if (order.getStatus() != 3) {
            throw new BusinessException("当前状态不允许回访");
        }
        order.setVisitContent(visitContent);
        order.setVisitScore(visitScore);
        order.setVisitTime(LocalDateTime.now());
        order.setStatus(4);
        order.setUpdateBy(operatorName);
        baseMapper.updateById(order);
        saveFlow(order.getCompanyId(), orderId, 4, "满意度评分:" + visitScore, operatorId, operatorName);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void closeOrder(Long orderId, Long operatorId, String operatorName) {
        ServiceOrder order = getById(orderId);
        if (order == null) throw new BusinessException("工单不存在");
        if (order.getStatus() == 5) {
            throw new BusinessException("工单已关闭");
        }
        order.setCloseTime(LocalDateTime.now());
        order.setStatus(5);
        order.setUpdateBy(operatorName);
        baseMapper.updateById(order);
        saveFlow(order.getCompanyId(), orderId, 5, "关闭工单", operatorId, operatorName);
    }

    private void saveFlow(Long companyId, Long orderId, Integer flowType, String content, Long operatorId, String operatorName) {
        OrderFlow flow = new OrderFlow();
        flow.setCompanyId(companyId);
        flow.setOrderId(orderId);
        flow.setFlowType(flowType);
        flow.setContent(content);
        flow.setOperatorId(operatorId);
        flow.setOperatorName(operatorName);
        orderFlowMapper.insert(flow);
    }

    private String generateOrderNo() {
        return "WO" + System.currentTimeMillis();
    }
}