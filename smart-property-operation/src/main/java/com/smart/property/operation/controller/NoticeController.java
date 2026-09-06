package com.smart.property.operation.controller;

import com.smart.property.common.core.context.SecurityContextHolder;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.domain.Result;
import com.smart.property.common.log.annotation.OperLog;
import com.smart.property.operation.dto.NoticeDTO;
import com.smart.property.operation.service.NoticeService;
import com.smart.property.operation.vo.NoticeVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 公告管理控制器
 *
 * @author zzz
 * @since 2026-07-25
 */
@RestController
@RequestMapping("/api/v1/admin/notices")
@Tag(name = "公告")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping
    @OperLog(module = "公告管理", businessType = 4, description = "查询公告列表")
    public Result<PageResult<NoticeVO>> list(PageQuery query,
                                             @RequestParam(required = false) Integer noticeType) {
        PageResult<NoticeVO> result = noticeService.getNoticePage(query,
                SecurityContextHolder.getCompanyId(), noticeType);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @OperLog(module = "公告管理", businessType = 4, description = "查询公告详情")
    public Result<NoticeVO> getById(@PathVariable Long id) {
        return Result.success(noticeService.getNoticeById(id));
    }

    @PostMapping
    @OperLog(module = "公告管理", businessType = 1, description = "新增公告")
    public Result<Void> add(@Valid @RequestBody NoticeDTO dto) {
        noticeService.createNotice(dto,
                SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PutMapping("/{id}")
    @OperLog(module = "公告管理", businessType = 2, description = "修改公告")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody NoticeDTO dto) {
        noticeService.updateNotice(id, dto, SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PostMapping("/{id}/publish")
    @OperLog(module = "公告管理", businessType = 2, description = "发布公告")
    public Result<Void> publish(@PathVariable Long id) {
        noticeService.publishNotice(id, SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PostMapping("/{id}/revoke")
    @OperLog(module = "公告管理", businessType = 2, description = "撤回公告")
    public Result<Void> revoke(@PathVariable Long id) {
        noticeService.revokeNotice(id, SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PostMapping("/{id}/read")
    public Result<Void> markRead(@PathVariable Long id) {
        noticeService.markRead(id);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @OperLog(module = "公告管理", businessType = 3, description = "删除公告")
    public Result<Void> delete(@PathVariable Long id) {
        noticeService.removeById(id);
        return Result.success();
    }
}