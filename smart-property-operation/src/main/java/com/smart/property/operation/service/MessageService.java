package com.smart.property.operation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.operation.domain.Message;
import com.smart.property.operation.dto.MessageDTO;
import com.smart.property.operation.vo.MessageVO;

/**
 * 消息服务接口
 *
 * @author zzz
 * @since 2026-07-25
 */
public interface MessageService extends IService<Message> {

    PageResult<MessageVO> getMessagePage(PageQuery query, Long companyId, Long receiverId, Integer isRead);

    void sendMessage(MessageDTO dto, Long companyId, Long senderId, String senderName);

    void markRead(Long id, Long userId);

    void markAllRead(Long userId, Long companyId);

    long getUnreadCount(Long userId, Long companyId);
}