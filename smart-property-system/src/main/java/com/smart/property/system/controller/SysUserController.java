package com.smart.property.system.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.smart.property.common.core.context.SecurityContextHolder;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.domain.Result;
import com.smart.property.common.log.annotation.OperLog;
import com.smart.property.system.dto.UserDTO;
import com.smart.property.system.dto.UserQuery;
import com.smart.property.system.service.AuthService;
import com.smart.property.system.service.SysUserService;
import com.smart.property.system.vo.UserVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 用户管理控制器
 *
 * @author zzz
 * @since 2026-07-25
 */
@RestController
@RequestMapping("/api/v1/system/users")
@Tag(name = "用户")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService userService;
    private final AuthService authService;

    @GetMapping
    @OperLog(module = "用户管理", businessType = 4, description = "查询用户列表")
    public Result<PageResult<UserVO>> list(UserQuery query) {
        PageResult<UserVO> result = userService.getUserPage(query, SecurityContextHolder.getCompanyId());
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @OperLog(module = "用户管理", businessType = 4, description = "查询用户详情")
    public Result<UserVO> getById(@PathVariable Long id) {
        UserVO user = userService.getUserById(id);
        return Result.success(user);
    }

    @PostMapping
    @OperLog(module = "用户管理", businessType = 1, description = "新增用户")
    public Result<Void> add(@Valid @RequestBody UserDTO dto) {
        userService.addUser(dto, SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PutMapping("/{id}")
    @OperLog(module = "用户管理", businessType = 2, description = "修改用户")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody UserDTO dto) {
        userService.updateUser(id, dto, SecurityContextHolder.getUsername());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @OperLog(module = "用户管理", businessType = 3, description = "删除用户")
    public Result<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }

    @PutMapping("/{id}/reset-password")
    @OperLog(module = "用户管理", businessType = 2, description = "重置密码")
    public Result<Void> resetPassword(@PathVariable Long id,
                                      @RequestParam(required = false) String newPassword) {
        userService.resetPassword(id, newPassword);
        return Result.success();
    }

    /**
     * 踢指定账号下线（Sa-Token kickout）：该账号所有 Token 立即失效，
     * 被踢端下一次请求将收到未登录异常。
     */
    @PostMapping("/{id}/kickout")
    @SaCheckRole("admin")
    @OperLog(module = "用户管理", businessType = 3, description = "踢人下线")
    public Result<Void> kickout(@PathVariable Long id) {
        authService.kickout(id);
        return Result.success();
    }
}