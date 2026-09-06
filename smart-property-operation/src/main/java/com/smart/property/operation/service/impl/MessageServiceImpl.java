package com.smart.property.operation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.operation.convert.MessageConverter;
import com.smart.property.operation.domain.Message;
import com.smart.property.operation.dto.MessageDTO;
import com.smart.property.operation.mapper.MessageMapper;
import com.smart.property.operation.service.MessageService;
import com.smart.property.operation.vo.MessageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 消息服务实现
 *
 * @author zzz
 * @since 2026-07-25
 */
@Service
@RequiredArgsConstructor
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    private final MessageConverter messageConverter;

    @Override
    public PageResult<MessageVO> getMessagePage(PageQuery query, Long companyId, Long receiverId, Integer isRead) {
        Page<Message> page = new Page<>(query.getPageNum(), query.getPageSize());

        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<Message>()
                .eq(Message::getCompanyId, companyId)
                .eq(Message::getIsDeleted, 0)
                .eq(receiverId != null, Message::getReceiverId, receiverId)
                .eq(isRead != null, Message::getIsRead, isRead)
                .orderByDesc(Message::getCreateTime);

        Page<Message> result = baseMapper.selectPage(page, wrapper);
        List<MessageVO> records = result.getRecords().stream()
                .map(messageConverter::toVO)
                .collect(Collectors.toList());
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), records);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendMessage(MessageDTO dto, Long companyId, Long senderId, String senderName) {
        Message message = messageConverter.toEntity(dto);
        message.setCompanyId(companyId);
        message.setSenderId(senderId);
        message.setSenderName(senderName);
        message.setIsRead(0);
        message.setSendStatus(1);
        message.setSendTime(LocalDateTime.now());
        baseMapper.insert(message);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markRead(Long id, Long userId) {
        Message message = getById(id);
        if (message != null && message.getReceiverId().equals(userId)) {
            message.setIsRead(1);
            message.setReadTime(LocalDateTime.now());
            baseMapper.updateById(message);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAllRead(Long userId, Long companyId) {
        baseMapper.update(null,
                new LambdaUpdateWrapper<Message>()
                        .eq(Message::getCompanyId, companyId)
                        .eq(Message::getReceiverId, userId)
                        .eq(Message::getIsRead, 0)
                        .set(Message::getIsRead, 1)
                        .set(Message::getReadTime, LocalDateTime.now())
        );
    }

    @Override
    public long getUnreadCount(Long userId, Long companyId) {
        return baseMapper.selectCount(
                new LambdaQueryWrapper<Message>()
                        .eq(Message::getCompanyId, companyId)
                        .eq(Message::getReceiverId, userId)
                        .eq(Message::getIsRead, 0)
                        .eq(Message::getIsDeleted, 0)
        );
    }
}