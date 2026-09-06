package com.smart.property.operation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.exception.BusinessException;
import com.smart.property.operation.convert.VisitRecordConverter;
import com.smart.property.operation.domain.VisitRecord;
import com.smart.property.operation.dto.VisitRecordDTO;
import com.smart.property.operation.mapper.VisitRecordMapper;
import com.smart.property.operation.remote.RemoteNameService;
import com.smart.property.operation.service.VisitRecordService;
import com.smart.property.operation.vo.VisitRecordVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 来访登记服务实现
 *
 * @author zzz
 * @since 2026-07-25
 */
@Service
@RequiredArgsConstructor
public class VisitRecordServiceImpl extends ServiceImpl<VisitRecordMapper, VisitRecord> implements VisitRecordService {

    private final VisitRecordConverter visitRecordConverter;
    private final RemoteNameService remoteNameService;

    @Override
    public PageResult<VisitRecordVO> getVisitPage(PageQuery query, Long companyId, Long communityId) {
        Page<VisitRecord> page = new Page<>(query.getPageNum(), query.getPageSize());

        LambdaQueryWrapper<VisitRecord> wrapper = new LambdaQueryWrapper<VisitRecord>()
                .eq(VisitRecord::getCompanyId, companyId)
                .eq(communityId != null, VisitRecord::getCommunityId, communityId)
                .orderByDesc(VisitRecord::getVisitTime);

        Page<VisitRecord> result = baseMapper.selectPage(page, wrapper);
        List<VisitRecordVO> records = result.getRecords().stream()
                .map(visitRecordConverter::toVO)
                .collect(Collectors.toList());
        remoteNameService.fillCommunityNames(records, VisitRecordVO::getCommunityId, VisitRecordVO::setCommunityName);
        remoteNameService.fillRoomNos(records, VisitRecordVO::getRoomId, VisitRecordVO::setRoomNo);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), records);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void registerVisit(VisitRecordDTO dto, Long companyId, Long guardId, String guardName) {
        VisitRecord record = visitRecordConverter.toEntity(dto);
        record.setCompanyId(companyId);
        record.setGuardId(guardId);
        record.setGuardName(guardName);
        record.setVisitTime(LocalDateTime.now());
        record.setStatus(1);
        baseMapper.insert(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void registerLeave(Long id, String operator) {
        VisitRecord record = getById(id);
        if (record == null) {
            throw new BusinessException("来访记录不存在");
        }
        if (record.getStatus() == 2) {
            throw new BusinessException("已离开");
        }
        record.setLeaveTime(LocalDateTime.now());
        record.setStatus(2);
        record.setUpdateBy(operator);
        baseMapper.updateById(record);
    }
}