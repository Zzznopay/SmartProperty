package com.smart.property.operation.service;

import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.operation.domain.VisitRecord;
import com.smart.property.operation.dto.VisitRecordDTO;
import com.smart.property.operation.vo.VisitRecordVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 来访登记服务测试
 *
 * @author zzz
 * @since 2026-07-25
 */
@SpringBootTest
@Transactional
class VisitRecordServiceTest {

    @Autowired
    private VisitRecordService visitRecordService;

    private VisitRecordDTO buildDto(String visitorName) {
        VisitRecordDTO dto = new VisitRecordDTO();
        dto.setCommunityId(1L);
        dto.setVisitorName(visitorName);
        dto.setVisitorPhone("13800138000");
        dto.setVisitReason("拜访");
        dto.setVisitTarget("李四");
        dto.setVisitorCount(1);
        return dto;
    }

    /**
     * registerVisit 无返回值，按唯一访客姓名从分页结果中查回创建的 VO
     */
    private VisitRecordVO findByVisitor(String visitorName) {
        PageQuery query = new PageQuery();
        query.setPageNum(1);
        query.setPageSize(100);
        PageResult<VisitRecordVO> page = visitRecordService.getVisitPage(query, 1L, null);
        return page.getRecords().stream()
                .filter(vo -> visitorName.equals(vo.getVisitorName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("来访记录未查询到: " + visitorName));
    }

    @Test
    void testRegisterVisit() {
        String visitorName = "OD-VS-T1-01 张三";
        visitRecordService.registerVisit(buildDto(visitorName), 1L, 1L, "test");

        VisitRecordVO created = findByVisitor(visitorName);

        assertNotNull(created.getId());
        assertEquals(1, created.getStatus());
        assertNotNull(created.getVisitTime());
    }

    @Test
    void testGetVisitPage() {
        visitRecordService.registerVisit(buildDto("OD-VS-T2-01 分页测试"), 1L, 1L, "test");

        PageQuery query = new PageQuery();
        query.setPageNum(1);
        query.setPageSize(10);

        PageResult<VisitRecordVO> result = visitRecordService.getVisitPage(query, 1L, null);

        assertNotNull(result);
        assertTrue(result.getTotal() > 0);
    }

    @Test
    void testRegisterLeave() {
        String visitorName = "OD-VS-T3-01 离开测试";
        visitRecordService.registerVisit(buildDto(visitorName), 1L, 1L, "test");

        VisitRecordVO created = findByVisitor(visitorName);
        visitRecordService.registerLeave(created.getId(), "test");

        VisitRecord left = visitRecordService.getById(created.getId());
        assertEquals(2, left.getStatus());
        assertNotNull(left.getLeaveTime());
    }
}
