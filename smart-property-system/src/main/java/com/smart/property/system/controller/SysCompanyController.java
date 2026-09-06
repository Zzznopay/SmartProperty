package com.smart.property.system.controller;

import com.smart.property.common.core.context.SecurityContextHolder;
import com.smart.property.common.core.domain.Result;
import com.smart.property.common.core.exception.BusinessException;
import com.smart.property.common.log.annotation.OperLog;
import com.smart.property.system.dto.SysCompanyDTO;
import com.smart.property.system.service.SysCompanyService;
import com.smart.property.system.vo.SysCompanyVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 物业公司控制器
 *
 * @author zzz
 * @since 2026-07-25
 */
@RestController
@RequestMapping("/api/v1/system/companys")
@Tag(name = "公司")
@RequiredArgsConstructor
public class SysCompanyController {

    private final SysCompanyService sysCompanyService;

    @GetMapping
    @OperLog(module = "物业公司", businessType = 4, description = "查询物业公司")
    public Result<List<SysCompanyVO>> list(@RequestParam(defaultValue = "1") int pageNum,
                                           @RequestParam(defaultValue = "500") int pageSize,
                                           @RequestParam(required = false) String companyName) {
        if (pageSize <= 0) pageSize = 500;
        return Result.success(sysCompanyService.getCompanyPage(pageNum, pageSize, companyName).getRecords());
    }

    @GetMapping("/all")
    @OperLog(module = "物业公司", businessType = 4, description = "查询全部公司(扁平)")
    public Result<List<SysCompanyVO>> listAll() {
        List<SysCompanyVO> all = sysCompanyService.getCompanyTree();
        List<SysCompanyVO> flat = new ArrayList<>();
        flatten(all, flat);
        return Result.success(flat);
    }

    @GetMapping("/tree")
    @OperLog(module = "物业公司", businessType = 4, description = "查询公司树")
    public Result<List<SysCompanyVO>> tree() {
        return Result.success(sysCompanyService.getCompanyTree());
    }

    @GetMapping("/{id}")
    @OperLog(module = "物业公司", businessType = 4, description = "查询公司详情")
    public Result<SysCompanyVO> getById(@PathVariable Long id) {
        return Result.success(sysCompanyService.getCompanyById(id));
    }

    @PostMapping
    @OperLog(module = "物业公司", businessType = 1, description = "新增公司")
    public Result<Void> add(@Valid @RequestBody SysCompanyDTO dto) {
        sysCompanyService.createCompany(dto, SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PutMapping("/{id}")
    @OperLog(module = "物业公司", businessType = 2, description = "修改公司")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody SysCompanyDTO dto) {
        if (dto.getParentId() != null && id.equals(dto.getParentId())) {
            throw new BusinessException("上级公司不能为本公司");
        }
        sysCompanyService.updateCompany(id, dto, SecurityContextHolder.getUsername());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @OperLog(module = "物业公司", businessType = 3, description = "删除公司")
    public Result<Void> delete(@PathVariable Long id) {
        sysCompanyService.removeById(id);
        return Result.success();
    }

    private void flatten(List<SysCompanyVO> source, List<SysCompanyVO> sink) {
        if (source == null) return;
        for (SysCompanyVO n : source) {
            SysCompanyVO node = new SysCompanyVO();
            node.setId(n.getId());
            node.setParentId(n.getParentId());
            node.setCompanyName(n.getCompanyName());
            node.setCompanyCode(n.getCompanyCode());
            node.setContactName(n.getContactName());
            node.setContactPhone(n.getContactPhone());
            node.setAddress(n.getAddress());
            node.setLogo(n.getLogo());
            node.setStatus(n.getStatus());
            node.setRemark(n.getRemark());
            node.setCreateBy(n.getCreateBy());
            node.setCreateTime(n.getCreateTime());
            node.setUpdateBy(n.getUpdateBy());
            node.setUpdateTime(n.getUpdateTime());
            sink.add(node);
            if (n.getChildren() != null && !n.getChildren().isEmpty()) {
                flatten(n.getChildren(), sink);
            }
        }
    }
}