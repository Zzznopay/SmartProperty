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
 * 来访登记Controller测试
 *
 * @author zzz
 * @since 2026-07-25
 */
@SpringBootTest
@Transactional
class VisitRecordControllerTest {

    @Autowired
    private VisitRecordService visitRecordService;

    private VisitRecordDTO buildDto(String visitorName, String visitReason, Integer visitorCount) {
        VisitRecordDTO dto = new VisitRecordDTO();
        dto.setCommunityId(1L);
        dto.setVisitorName(visitorName);
        dto.setVisitReason(visitReason);
        dto.setVisitorCount(visitorCount);
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
        String visitorName = "OD-VG-T1-01 访客A";
        visitRecordService.registerVisit(buildDto(visitorName, "拜访", 1), 1L, 1L, "test");

        VisitRecordVO created = findByVisitor(visitorName);

        assertNotNull(created.getId());
        assertEquals(1, created.getStatus());
    }

    @Test
    void testGetVisitPage() {
        visitRecordService.registerVisit(buildDto("OD-VG-T2-01 分页测试", "测试", null), 1L, 1L, "test");

        PageQuery query = new PageQuery();
        query.setPageNum(1);
        query.setPageSize(10);

        PageResult<VisitRecordVO> result = visitRecordService.getVisitPage(query, 1L, null);

        assertNotNull(result);
        assertTrue(result.getTotal() > 0);
    }

    @Test
    void testRegisterLeave() {
        String visitorName = "OD-VG-T3-01 离开测试";
        visitRecordService.registerVisit(buildDto(visitorName, "测试", null), 1L, 1L, "test");

        VisitRecordVO created = findByVisitor(visitorName);
        visitRecordService.registerLeave(created.getId(), "test");

        VisitRecord left = visitRecordService.getById(created.getId());
        assertEquals(2, left.getStatus());
    }
}
