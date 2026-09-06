package com.smart.property.property.service;

import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.property.dto.PrepaymentDTO;
import com.smart.property.property.vo.PrepaymentVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 预收款Controller测试
 *
 * @author zzz
 * @since 2026-07-25
 */
@SpringBootTest
@Transactional
class PrepaymentControllerTest {

    @Autowired
    private PrepaymentService prepaymentService;

    private PrepaymentDTO buildDto(Long ownerId, String amount, int payType) {
        PrepaymentDTO dto = new PrepaymentDTO();
        dto.setOwnerId(ownerId);
        dto.setAmount(new BigDecimal(amount));
        dto.setPayType(payType);
        dto.setPayTime(LocalDateTime.now());
        dto.setPaymentNo("YSK-T1-" + payType + "-" + ownerId);
        return dto;
    }

    @Test
    void testCreatePrepayment() {
        PrepaymentDTO dto = buildDto(1L, "5000", 1);

        prepaymentService.createPrepayment(dto, 1L, "test");

        PageQuery query = new PageQuery();
        query.setPageNum(1);
        query.setPageSize(10);
        PageResult<PrepaymentVO> page = prepaymentService.getPrepaymentPage(query, 1L, 1L);
        PrepaymentVO created = page.getRecords().stream()
                .filter(v -> v.getAmount() != null && v.getAmount().compareTo(new BigDecimal("5000")) == 0)
                .findFirst().orElseThrow();

        assertNotNull(created.getId());
        assertEquals(0, new BigDecimal("5000").compareTo(created.getAmount()));
        assertEquals(0, new BigDecimal("5000").compareTo(created.getBalance()));
    }

    @Test
    void testGetPrepaymentPage() {
        prepaymentService.createPrepayment(buildDto(1L, "3000", 1), 1L, "test");

        PageQuery query = new PageQuery();
        query.setPageNum(1);
        query.setPageSize(10);

        PageResult<PrepaymentVO> result = prepaymentService.getPrepaymentPage(query, 1L, 1L);

        assertNotNull(result);
        assertTrue(result.getTotal() > 0);
    }

    @Test
    void testGetBalance() {
        prepaymentService.createPrepayment(buildDto(999L, "2000", 1), 1L, "test");
        prepaymentService.createPrepayment(buildDto(999L, "3000", 2), 1L, "test");

        BigDecimal balance = prepaymentService.getBalance(999L, 1L);

        assertEquals(0, new BigDecimal("5000").compareTo(balance));
    }

    @Test
    void testRefundPrepayment() {
        prepaymentService.createPrepayment(buildDto(1L, "1000", 1), 1L, "test");

        PageQuery query = new PageQuery();
        query.setPageNum(1);
        query.setPageSize(10);
        PageResult<PrepaymentVO> page = prepaymentService.getPrepaymentPage(query, 1L, 1L);
        PrepaymentVO created = page.getRecords().stream()
                .filter(v -> v.getAmount() != null && v.getAmount().compareTo(new BigDecimal("1000")) == 0)
                .findFirst().orElseThrow();

        prepaymentService.refundPrepayment(created.getId(), 1L, "test");

        PrepaymentVO refunded = prepaymentService.getById(created.getId(), 1L);
        assertEquals(2, refunded.getStatus());
    }
}
