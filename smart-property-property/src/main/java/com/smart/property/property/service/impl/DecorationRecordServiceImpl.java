package com.smart.property.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.exception.BusinessException;
import com.smart.property.property.convert.DecorationRecordConverter;
import com.smart.property.property.domain.DecorationRecord;
import com.smart.property.property.domain.Owner;
import com.smart.property.property.domain.Room;
import com.smart.property.property.dto.DecorationRecordDTO;
import com.smart.property.property.mapper.DecorationRecordMapper;
import com.smart.property.property.mapper.OwnerMapper;
import com.smart.property.property.mapper.RoomMapper;
import com.smart.property.property.service.DecorationRecordService;
import com.smart.property.property.vo.DecorationRecordVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 装修记录服务实现
 *
 * @author zzz
 * @since 2026-07-25
 */
@Service
@RequiredArgsConstructor
public class DecorationRecordServiceImpl extends ServiceImpl<DecorationRecordMapper, DecorationRecord> implements DecorationRecordService {

    private final DecorationRecordConverter decorationRecordConverter;
    private final RoomMapper roomMapper;
    private final OwnerMapper ownerMapper;

    /** 批量回填 VO 的房间号/业主名称（列表接口 VO 只带 id） */
    private void fillDecorationRecordNames(List<DecorationRecordVO> records) {
        if (records.isEmpty()) {
            return;
        }
        Map<Long, String> roomNos = roomNosById(
                records.stream().map(DecorationRecordVO::getRoomId).filter(Objects::nonNull).distinct().toList());
        Map<Long, String> ownerNames = ownerNamesById(
                records.stream().map(DecorationRecordVO::getOwnerId).filter(Objects::nonNull).distinct().toList());
        records.forEach(vo -> {
            vo.setRoomNo(roomNos.get(vo.getRoomId()));
            vo.setOwnerName(ownerNames.get(vo.getOwnerId()));
        });
    }

    private Map<Long, String> roomNosById(List<Long> ids) {
        if (ids.isEmpty()) {
            return new HashMap<>();
        }
        return roomMapper.selectList(new LambdaQueryWrapper<Room>().in(Room::getId, ids))
                .stream()
                .collect(Collectors.toMap(Room::getId, Room::getRoomNo, (a, b) -> a));
    }

    private Map<Long, String> ownerNamesById(List<Long> ids) {
        if (ids.isEmpty()) {
            return new HashMap<>();
        }
        return ownerMapper.selectList(new LambdaQueryWrapper<Owner>().in(Owner::getId, ids))
                .stream()
                .collect(Collectors.toMap(Owner::getId, Owner::getOwnerName, (a, b) -> a));
    }

    @Override
    public PageResult<DecorationRecordVO> getDecorationPage(PageQuery query, Long companyId, Long roomId) {
        Page<DecorationRecord> page = new Page<>(query.getPageNum(), query.getPageSize());

        LambdaQueryWrapper<DecorationRecord> wrapper = new LambdaQueryWrapper<DecorationRecord>()
                .eq(DecorationRecord::getCompanyId, companyId)
                .eq(DecorationRecord::getIsDeleted, 0)
                .eq(roomId != null, DecorationRecord::getRoomId, roomId)
                .orderByDesc(DecorationRecord::getCreateTime);

        Page<DecorationRecord> result = baseMapper.selectPage(page, wrapper);
        List<DecorationRecordVO> records = result.getRecords().stream()
                .map(decorationRecordConverter::toVO)
                .collect(Collectors.toList());
        fillDecorationRecordNames(records);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), records);
    }

    @Override
    public DecorationRecordVO getByDecorationId(Long id, Long companyId) {
        DecorationRecordVO vo = decorationRecordConverter.toVO(getCompanyDecoration(id, companyId));
        fillDecorationRecordNames(List.of(vo));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createDecoration(DecorationRecordDTO dto, Long companyId, String operator) {
        DecorationRecord record = decorationRecordConverter.toEntity(dto);
        record.setCompanyId(companyId);
        record.setApplyDate(LocalDate.now());
        record.setDepositStatus(1);
        record.setStatus(1);
        record.setCreateBy(operator);
        baseMapper.insert(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startDecoration(Long id, Long companyId, String operator) {
        DecorationRecord record = getCompanyDecoration(id, companyId);
        if (record.getStatus() != 1) {
            throw new BusinessException("当前状态不允许开工");
        }
        record.setStartDate(LocalDate.now());
        record.setStatus(2);
        record.setUpdateBy(operator);
        baseMapper.updateById(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeDecoration(Long id, Long companyId, String operator) {
        DecorationRecord record = getCompanyDecoration(id, companyId);
        if (record.getStatus() != 2) {
            throw new BusinessException("当前状态不允许完工");
        }
        record.setEndDate(LocalDate.now());
        record.setStatus(3);
        record.setUpdateBy(operator);
        baseMapper.updateById(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkDecoration(Long id, Long companyId, Integer checkResult, String operator) {
        DecorationRecord record = getCompanyDecoration(id, companyId);
        if (record.getStatus() != 3) {
            throw new BusinessException("当前状态不允许验收");
        }
        record.setCheckResult(checkResult);
        record.setStatus(4);
        record.setUpdateBy(operator);
        baseMapper.updateById(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDecoration(Long id, Long companyId) {
        getCompanyDecoration(id, companyId);
        removeById(id);
    }

    private DecorationRecord getCompanyDecoration(Long id, Long companyId) {
        DecorationRecord record = getOne(new LambdaQueryWrapper<DecorationRecord>()
                .eq(DecorationRecord::getId, id)
                .eq(DecorationRecord::getCompanyId, companyId));
        if (record == null) {
            throw new BusinessException("装修记录不存在");
        }
        return record;
    }
}