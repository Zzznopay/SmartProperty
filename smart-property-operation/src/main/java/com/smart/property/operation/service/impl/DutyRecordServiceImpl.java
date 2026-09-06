package com.smart.property.operation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.exception.BusinessException;
import com.smart.property.operation.convert.DutyRecordConverter;
import com.smart.property.operation.domain.DutyRecord;
import com.smart.property.operation.dto.DutyRecordDTO;
import com.smart.property.operation.mapper.DutyRecordMapper;
import com.smart.property.operation.remote.RemoteNameService;
import com.smart.property.operation.service.DutyRecordService;
import com.smart.property.operation.vo.DutyRecordVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DutyRecordServiceImpl extends ServiceImpl<DutyRecordMapper, DutyRecord> implements DutyRecordService {

    private final DutyRecordConverter dutyRecordConverter;
    private final RemoteNameService remoteNameService;

    @Override
    public PageResult<DutyRecordVO> getPage(PageQuery query, Long companyId, Long communityId) {
        Page<DutyRecord> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<DutyRecord> wrapper = new LambdaQueryWrapper<DutyRecord>()
                .eq(DutyRecord::getCompanyId, companyId)
                .eq(DutyRecord::getIsDeleted, 0)
                .eq(communityId != null, DutyRecord::getCommunityId, communityId)
                .orderByDesc(DutyRecord::getDutyDate);
        Page<DutyRecord> result = baseMapper.selectPage(page, wrapper);
        List<DutyRecordVO> records = result.getRecords().stream()
                .map(dutyRecordConverter::toVO)
                .collect(Collectors.toList());
        remoteNameService.fillCommunityNames(records, DutyRecordVO::getCommunityId, DutyRecordVO::setCommunityName);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), records);
    }

    @Override
    public DutyRecordVO getById(Long id) {
        DutyRecord entity = baseMapper.selectById(id);
        if (entity == null) throw new BusinessException("执勤记录不存在");
        DutyRecordVO vo = dutyRecordConverter.toVO(entity);
        remoteNameService.fillCommunityNames(List.of(vo), DutyRecordVO::getCommunityId, DutyRecordVO::setCommunityName);
        return vo;
    }

    @Override
    public void create(DutyRecordDTO dto, Long companyId, String operator) {
        DutyRecord entity = dutyRecordConverter.toEntity(dto);
        entity.setCompanyId(companyId);
        if (entity.getStatus() == null) entity.setStatus(1);
        entity.setCreateBy(operator);
        save(entity);
    }

    @Override
    public void update(Long id, DutyRecordDTO dto, String operator) {
        DutyRecord existing = baseMapper.selectById(id);
        if (existing == null) throw new BusinessException("执勤记录不存在");
        DutyRecord patch = dutyRecordConverter.toEntity(dto);
        patch.setId(id);
        patch.setCompanyId(existing.getCompanyId());
        patch.setCreateBy(existing.getCreateBy());
        patch.setCreateTime(existing.getCreateTime());
        patch.setUpdateBy(operator);
        baseMapper.updateById(patch);
    }
}