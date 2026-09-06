package com.smart.property.operation.service;

import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.operation.dto.OpinionBoxDTO;
import com.smart.property.operation.dto.OpinionSubmitDTO;
import com.smart.property.operation.vo.OpinionBoxVO;
import com.smart.property.operation.vo.OpinionSubmitVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 意见箱Controller测试
 *
 * @author zzz
 * @since 2026-07-25
 */
@SpringBootTest
@Transactional
class OpinionBoxControllerTest {

    @Autowired
    private OpinionBoxService opinionBoxService;

    /**
     * 通过新接口创建意见箱，并按唯一名称从分页结果中查回创建的 VO
     */
    private OpinionBoxVO createBox(String boxName) {
        OpinionBoxDTO dto = new OpinionBoxDTO();
        dto.setCommunityId(1L);
        dto.setBoxName(boxName);
        dto.setAdminUserId(1L);
        dto.setAdminUserName("管理员");
        dto.setIsAnonymous(1);
        dto.setIsActive(1);
        opinionBoxService.createBox(dto, 1L, "test");

        PageQuery query = new PageQuery();
        query.setPageNum(1);
        query.setPageSize(100);
        PageResult<OpinionBoxVO> page = opinionBoxService.getOpinionBoxPage(query, 1L, null);
        return page.getRecords().stream()
                .filter(v -> boxName.equals(v.getBoxName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("创建的意见箱未查询到: " + boxName));
    }

    /**
     * 通过新接口提交意见，并按唯一标题从分页结果中查回创建的 VO
     */
    private OpinionSubmitVO submitOpinion(Long boxId, String title) {
        OpinionSubmitDTO dto = new OpinionSubmitDTO();
        dto.setBoxId(boxId);
        dto.setTitle(title);
        dto.setContent("小区停车位不足，建议增加...");
        dto.setIsAnonymous(0);
        opinionBoxService.submitOpinion(dto, 1L, 1L, "业主A");

        PageQuery query = new PageQuery();
        query.setPageNum(1);
        query.setPageSize(100);
        PageResult<OpinionSubmitVO> page = opinionBoxService.getOpinionSubmitPage(query, 1L, boxId, null);
        return page.getRecords().stream()
                .filter(v -> title.equals(v.getTitle()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("提交的意见未查询到: " + title));
    }

    @Test
    void testAddOpinionBox() {
        OpinionBoxVO created = createBox("BOXC-T1-01");

        assertNotNull(created.getId());
    }

    @Test
    void testGetOpinionBoxPage() {
        createBox("BOXC-T1-02");

        PageQuery query = new PageQuery();
        query.setPageNum(1);
        query.setPageSize(10);

        PageResult<OpinionBoxVO> result = opinionBoxService.getOpinionBoxPage(query, 1L, null);

        assertNotNull(result);
        assertTrue(result.getTotal() > 0);
    }

    @Test
    void testSubmitOpinion() {
        OpinionBoxVO box = createBox("BOXC-T1-03");

        OpinionSubmitVO submit = submitOpinion(box.getId(), "OPC-T1-03-01");

        assertNotNull(submit.getId());
        assertEquals(1, submit.getStatus());
    }

    @Test
    void testReplyOpinion() {
        OpinionBoxVO box = createBox("BOXC-T1-04");

        OpinionSubmitVO submit = submitOpinion(box.getId(), "OPC-T1-04-01");

        opinionBoxService.replyOpinion(submit.getId(), "感谢您的建议，我们会认真考虑", 1L, "管理员");

        OpinionSubmitVO replied = opinionBoxService.getOpinionSubmitPage(
                pageQuery(), 1L, box.getId(), null).getRecords().stream()
                .filter(v -> submit.getId().equals(v.getId()))
                .findFirst().orElseThrow();
        assertEquals(3, replied.getStatus());
        assertEquals("感谢您的建议，我们会认真考虑", replied.getReplyContent());
    }

    @Test
    void testEvaluateOpinion() {
        OpinionBoxVO box = createBox("BOXC-T1-05");

        OpinionSubmitVO submit = submitOpinion(box.getId(), "OPC-T1-05-01");

        opinionBoxService.evaluateOpinion(submit.getId(), 5);

        OpinionSubmitVO evaluated = opinionBoxService.getOpinionSubmitPage(
                pageQuery(), 1L, box.getId(), null).getRecords().stream()
                .filter(v -> submit.getId().equals(v.getId()))
                .findFirst().orElseThrow();
        assertEquals(5, evaluated.getSatisfaction());
    }

    private PageQuery pageQuery() {
        PageQuery query = new PageQuery();
        query.setPageNum(1);
        query.setPageSize(100);
        return query;
    }
}
