package com.smart.property.operation.service;

import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.operation.dto.ServiceOrderDTO;
import com.smart.property.operation.vo.ServiceOrderVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 服务工单Controller测试
 *
 * @author zzz
 * @since 2026-07-25
 */
@SpringBootTest
@Transactional
class ServiceOrderControllerTest {

    @Autowired
    private ServiceOrderService serviceOrderService;

    /**
     * 通过新接口创建工单，并按唯一标题从分页结果中查回创建的 VO
     */
    private ServiceOrderVO createOrder(String title) {
        ServiceOrderDTO dto = new ServiceOrderDTO();
        dto.setCommunityId(1L);
        dto.setOrderType(1);
        dto.setTitle(title);
        dto.setContent("内容");
        dto.setPriority(2);
        serviceOrderService.createOrder(dto, 1L, 1L, "test");

        PageQuery query = new PageQuery();
        query.setPageNum(1);
        query.setPageSize(100);
        PageResult<ServiceOrderVO> page = serviceOrderService.getOrderPage(query, 1L, 1, null);
        return page.getRecords().stream()
                .filter(v -> title.equals(v.getTitle()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("创建的工单未查询到: " + title));
    }

    @Test
    void testCreateOrder() {
        ServiceOrderVO created = createOrder("SOC-T1-01");

        assertNotNull(created.getId());
        assertNotNull(created.getOrderNo());
    }

    @Test
    void testGetOrderById() {
        ServiceOrderVO created = createOrder("SOC-T1-02");

        ServiceOrderVO result = serviceOrderService.getOrderById(created.getId());

        assertNotNull(result);
        assertEquals("SOC-T1-02", result.getTitle());
    }

    @Test
    void testAssignOrder() {
        ServiceOrderVO created = createOrder("SOC-T1-03");

        serviceOrderService.assignOrder(created.getId(), 2L, "处理人", 1L, "admin");

        ServiceOrderVO assigned = serviceOrderService.getOrderById(created.getId());
        assertEquals(2, assigned.getStatus());
    }

    @Test
    void testHandleOrder() {
        ServiceOrderVO created = createOrder("SOC-T1-04");

        serviceOrderService.assignOrder(created.getId(), 2L, "处理人", 1L, "admin");
        serviceOrderService.handleOrder(created.getId(), "已处理", 2L, "处理人");

        ServiceOrderVO handled = serviceOrderService.getOrderById(created.getId());
        assertEquals(3, handled.getStatus());
    }

    @Test
    void testCompleteOrderFlow() {
        ServiceOrderVO created = createOrder("SOC-T1-05");

        serviceOrderService.assignOrder(created.getId(), 2L, "处理人", 1L, "admin");
        serviceOrderService.handleOrder(created.getId(), "已处理", 2L, "处理人");
        serviceOrderService.visitOrder(created.getId(), "满意", 5, 1L, "admin");

        ServiceOrderVO completed = serviceOrderService.getOrderById(created.getId());
        assertEquals(4, completed.getStatus());
    }
}
