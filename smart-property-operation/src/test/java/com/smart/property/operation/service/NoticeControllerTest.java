package com.smart.property.operation.service;

import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.operation.dto.NoticeDTO;
import com.smart.property.operation.vo.NoticeVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 公告Controller测试
 *
 * @author zzz
 * @since 2026-07-25
 */
@SpringBootTest
@Transactional
class NoticeControllerTest {

    @Autowired
    private NoticeService noticeService;

    /**
     * 通过新接口创建公告，并按唯一标题从分页结果中查回创建的 VO
     */
    private NoticeVO createNotice(String title, Integer status) {
        NoticeDTO dto = new NoticeDTO();
        dto.setNoticeTitle(title);
        dto.setNoticeContent("内容");
        dto.setNoticeType(1);
        dto.setStatus(status);
        noticeService.createNotice(dto, 1L, "test");

        PageQuery query = new PageQuery();
        query.setPageNum(1);
        query.setPageSize(100);
        PageResult<NoticeVO> page = noticeService.getNoticePage(query, 1L, 1);
        return page.getRecords().stream()
                .filter(v -> title.equals(v.getNoticeTitle()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("创建的公告未查询到: " + title));
    }

    @Test
    void testAddNotice() {
        NoticeVO created = createNotice("NOTICEC-T1-01", 1);

        assertNotNull(created.getId());
        assertEquals("NOTICEC-T1-01", created.getNoticeTitle());
    }

    @Test
    void testPublishNotice() {
        NoticeVO created = createNotice("NOTICEC-T1-02", 1);

        noticeService.publishNotice(created.getId(), "admin");

        NoticeVO published = noticeService.getNoticeById(created.getId());
        assertEquals(2, published.getStatus());
    }

    @Test
    void testRevokeNotice() {
        NoticeVO created = createNotice("NOTICEC-T1-03", 1);

        noticeService.publishNotice(created.getId(), "admin");
        noticeService.revokeNotice(created.getId(), "admin");

        NoticeVO revoked = noticeService.getNoticeById(created.getId());
        assertEquals(3, revoked.getStatus());
    }

    @Test
    void testMarkRead() {
        NoticeVO created = createNotice("NOTICEC-T1-04", 2);

        noticeService.markRead(created.getId());

        NoticeVO updated = noticeService.getNoticeById(created.getId());
        assertEquals(1, updated.getReadCount());
    }
}
