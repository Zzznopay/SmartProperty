package com.smart.property.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.system.convert.SysLoginLogConverter;
import com.smart.property.system.domain.SysLoginLog;
import com.smart.property.system.mapper.SysLoginLogMapper;
import com.smart.property.system.service.SysLoginLogService;
import com.smart.property.system.vo.SysLoginLogVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 登录日志服务实现
 *
 * @author zzz
 * @since 2026-07-25
 */
@Service
@RequiredArgsConstructor
public class SysLoginLogServiceImpl extends ServiceImpl<SysLoginLogMapper, SysLoginLog> implements SysLoginLogService {

    private final SysLoginLogConverter sysLoginLogConverter;

    @Override
    public PageResult<SysLoginLogVO> getLoginLogPage(PageQuery query) {
        Page<SysLoginLog> page = new Page<>(query.getPageNum(), query.getPageSize());

        Page<SysLoginLog> result = baseMapper.selectPage(page,
                new LambdaQueryWrapper<SysLoginLog>()
                        .orderByDesc(SysLoginLog::getLoginTime)
        );

        List<SysLoginLogVO> records = result.getRecords().stream()
                .map(sysLoginLogConverter::toVO)
                .collect(Collectors.toList());
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), records);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cleanAll() {
        // 物理删除：登录日志表无 is_deleted 字段
        baseMapper.delete(new LambdaQueryWrapper<SysLoginLog>());
    }

    @Override
    public void removeLog(Long id) {
        baseMapper.deleteById(id);
    }
}