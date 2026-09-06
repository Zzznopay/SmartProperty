package com.smart.property.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.system.convert.SysOperLogConverter;
import com.smart.property.system.domain.SysOperLog;
import com.smart.property.system.mapper.SysOperLogMapper;
import com.smart.property.system.service.SysOperLogService;
import com.smart.property.system.vo.SysOperLogVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 操作日志服务实现
 *
 * @author zzz
 * @since 2026-07-25
 */
@Service
@RequiredArgsConstructor
public class SysOperLogServiceImpl extends ServiceImpl<SysOperLogMapper, SysOperLog> implements SysOperLogService {

    private final SysOperLogConverter sysOperLogConverter;

    @Override
    public PageResult<SysOperLogVO> getOperLogPage(PageQuery query) {
        Page<SysOperLog> page = new Page<>(query.getPageNum(), query.getPageSize());

        Page<SysOperLog> result = baseMapper.selectPage(page,
                new LambdaQueryWrapper<SysOperLog>()
                        .orderByDesc(SysOperLog::getOperTime)
        );

        List<SysOperLogVO> records = result.getRecords().stream()
                .map(sysOperLogConverter::toVO)
                .collect(Collectors.toList());
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), records);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cleanAll() {
        baseMapper.delete(new LambdaQueryWrapper<SysOperLog>());
    }

    @Override
    public void removeLog(Long id) {
        baseMapper.deleteById(id);
    }
}