package com.smart.property.operation.controller;

import com.smart.property.common.core.context.SecurityContextHolder;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.domain.Result;
import com.smart.property.common.log.annotation.OperLog;
import com.smart.property.operation.dto.MessageDTO;
import com.smart.property.operation.service.MessageService;
import com.smart.property.operation.vo.MessageVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 消息管理控制器
 *
 * @author zzz
 * @since 2026-07-25
 */
@RestController
@RequestMapping("/api/v1/admin/messages")
@Tag(name = "消息中心")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping
    @OperLog(module = "消息中心", businessType = 4, description = "查询消息列表")
    public Result<PageResult<MessageVO>> list(PageQuery query,
                                              @RequestParam(required = false) Integer isRead) {
        PageResult<MessageVO> result = messageService.getMessagePage(query,
                SecurityContextHolder.getCompanyId(),
                SecurityContextHolder.getUserId(), isRead);
        return Result.success(result);
    }

    @PostMapping
    @OperLog(module = "消息中心", businessType = 1, description = "发送消息")
    public Result<Void> send(@Valid @RequestBody MessageDTO dto) {
        messageService.sendMessage(dto,
                SecurityContextHolder.getCompanyId(),
                SecurityContextHolder.getUserId(),
                SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PostMapping("/{id}/read")
    public Result<Void> markRead(@PathVariable Long id) {
        messageService.markRead(id, SecurityContextHolder.getUserId());
        return Result.success();
    }

    @PostMapping("/read-all")
    public Result<Void> markAllRead() {
        messageService.markAllRead(SecurityContextHolder.getUserId(), SecurityContextHolder.getCompanyId());
        return Result.success();
    }

    @GetMapping("/unread-count")
    public Result<Long> unreadCount() {
        return Result.success(messageService.getUnreadCount(
                SecurityContextHolder.getUserId(), SecurityContextHolder.getCompanyId()));
    }
}