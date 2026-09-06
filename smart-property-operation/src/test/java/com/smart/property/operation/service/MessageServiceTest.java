package com.smart.property.operation.service;

import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.operation.domain.Message;
import com.smart.property.operation.dto.MessageDTO;
import com.smart.property.operation.vo.MessageVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 消息服务测试
 *
 * @author zzz
 * @since 2026-07-25
 */
@SpringBootTest
@Transactional
class MessageServiceTest {

    @Autowired
    private MessageService messageService;

    private MessageDTO buildDto(String title, Long receiverId) {
        MessageDTO dto = new MessageDTO();
        dto.setMessageType(1);
        dto.setTitle(title);
        dto.setContent("消息内容");
        dto.setReceiverId(receiverId);
        dto.setReceiverName("测试用户");
        return dto;
    }

    /**
     * 通过新接口发送消息，并按唯一标题从分页结果中查回创建的 VO
     */
    private MessageVO sendAndFind(String title, Long receiverId) {
        messageService.sendMessage(buildDto(title, receiverId), 1L, 1L, "管理员");

        PageQuery query = new PageQuery();
        query.setPageNum(1);
        query.setPageSize(100);
        PageResult<MessageVO> page = messageService.getMessagePage(query, 1L, receiverId, null);
        return page.getRecords().stream()
                .filter(v -> title.equals(v.getTitle()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("发送的消息未查询到: " + title));
    }

    @Test
    void testSendMessage() {
        MessageVO sent = sendAndFind("MSG-T1-01", 2L);

        assertNotNull(sent.getId());
        assertEquals(1, sent.getSendStatus());
    }

    @Test
    void testGetMessagePage() {
        sendAndFind("MSG-T1-02", 2L);

        PageQuery query = new PageQuery();
        query.setPageNum(1);
        query.setPageSize(10);

        PageResult<MessageVO> result = messageService.getMessagePage(query, 1L, 2L, null);

        assertNotNull(result);
        assertTrue(result.getTotal() > 0);
    }

    @Test
    void testMarkRead() {
        MessageVO sent = sendAndFind("MSG-T1-03", 2L);

        messageService.markRead(sent.getId(), 2L);

        Message updated = messageService.getById(sent.getId());
        assertEquals(1, updated.getIsRead());
        assertNotNull(updated.getReadTime());
    }

    @Test
    void testGetUnreadCount() {
        sendAndFind("MSG-T1-04", 2L);

        long count = messageService.getUnreadCount(2L, 1L);

        assertTrue(count > 0);
    }

    @Test
    void testMarkAllRead() {
        sendAndFind("MSG-T1-05", 2L);

        messageService.markAllRead(2L, 1L);

        long count = messageService.getUnreadCount(2L, 1L);
        assertEquals(0, count);
    }
}
