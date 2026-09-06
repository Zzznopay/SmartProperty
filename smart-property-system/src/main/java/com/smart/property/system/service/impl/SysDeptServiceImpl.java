package com.smart.property.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.property.system.convert.SysDeptConverter;
import com.smart.property.system.domain.SysDept;
import com.smart.property.system.mapper.SysDeptMapper;
import com.smart.property.system.service.SysDeptService;
import com.smart.property.system.vo.SysDeptVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 部门服务实现
 *
 * @author zzz
 * @since 2026-07-25
 */
@Service
@RequiredArgsConstructor
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements SysDeptService {

    private final SysDeptConverter sysDeptConverter;

    @Override
    public List<SysDeptVO> getDeptTree(Long companyId) {
        List<SysDept> depts = baseMapper.selectList(
                new LambdaQueryWrapper<SysDept>()
                        .eq(SysDept::getCompanyId, companyId)
                        .eq(SysDept::getIsDeleted, 0)
                        .eq(SysDept::getStatus, 1)
                        .orderByAsc(SysDept::getSort)
        );
        return buildDeptTree(depts, 0L);
    }

    private List<SysDeptVO> buildDeptTree(List<SysDept> depts, Long parentId) {
        return depts.stream()
                .filter(d -> parentId.equals(d.getParentId()))
                .map(d -> {
                    SysDeptVO vo = sysDeptConverter.toVO(d);
                    vo.setChildren(buildDeptTree(depts, d.getId()));
                    return vo;
                })
                .collect(Collectors.toList());
    }
}