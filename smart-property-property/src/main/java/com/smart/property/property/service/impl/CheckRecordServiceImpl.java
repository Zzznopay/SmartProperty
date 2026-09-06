package com.smart.property.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.exception.BusinessException;
import com.smart.property.property.convert.CheckRecordConverter;
import com.smart.property.property.domain.CheckRecord;
import com.smart.property.property.domain.Owner;
import com.smart.property.property.domain.Room;
import com.smart.property.property.dto.CheckRecordDTO;
import com.smart.property.property.mapper.CheckRecordMapper;
import com.smart.property.property.mapper.OwnerMapper;
import com.smart.property.property.mapper.RoomMapper;
import com.smart.property.property.service.CheckRecordService;
import com.smart.property.property.vo.CheckRecordVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 验房记录服务实现
 *
 * @author zzz
 * @since 2026-07-25
 */
@Service
@RequiredArgsConstructor
public class CheckRecordServiceImpl extends ServiceImpl<CheckRecordMapper, CheckRecord> implements CheckRecordService {

    private final CheckRecordConverter checkRecordConverter;
    private final RoomMapper roomMapper;
    private final OwnerMapper ownerMapper;

    /** 批量回填 VO 的房间号/业主名称（列表接口 VO 只带 id） */
    private void fillCheckRecordNames(List<CheckRecordVO> records) {
        if (records.isEmpty()) {
            return;
        }
        Map<Long, String> roomNos = roomNosById(
                records.stream().map(CheckRecordVO::getRoomId).filter(Objects::nonNull).distinct().toList());
        Map<Long, String> ownerNames = ownerNamesById(
                records.stream().map(CheckRecordVO::getOwnerId).filter(Objects::nonNull).distinct().toList());
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
    public PageResult<CheckRecordVO> getCheckRecordPage(PageQuery query, Long companyId, Long roomId) {
        Page<CheckRecord> page = new Page<>(query.getPageNum(), query.getPageSize());

        LambdaQueryWrapper<CheckRecord> wrapper = new LambdaQueryWrapper<CheckRecord>()
                .eq(CheckRecord::getCompanyId, companyId)
                .eq(CheckRecord::getIsDeleted, 0)
                .eq(roomId != null, CheckRecord::getRoomId, roomId)
                .orderByDesc(CheckRecord::getCheckDate);

        Page<CheckRecord> result = baseMapper.selectPage(page, wrapper);
        List<CheckRecordVO> records = result.getRecords().stream()
                .map(checkRecordConverter::toVO)
                .collect(Collectors.toList());
        fillCheckRecordNames(records);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), records);
    }

    @Override
    public CheckRecordVO getByCheckId(Long id, Long companyId) {
        CheckRecordVO vo = checkRecordConverter.toVO(getCompanyCheckRecord(id, companyId));
        fillCheckRecordNames(List.of(vo));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createCheckRecord(CheckRecordDTO dto, Long companyId, String operator) {
        CheckRecord record = checkRecordConverter.toEntity(dto);
        record.setCompanyId(companyId);
        record.setStatus(1);
        record.setCreateBy(operator);
        baseMapper.insert(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeRectification(Long id, Long companyId, String operator) {
        CheckRecord record = getCompanyCheckRecord(id, companyId);
        if (record.getStatus() == 2) {
            throw new BusinessException("已整改");
        }
        record.setStatus(2);
        record.setUpdateBy(operator);
        baseMapper.updateById(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCheckRecord(Long id, Long companyId) {
        getCompanyCheckRecord(id, companyId);
        removeById(id);
    }

    private CheckRecord getCompanyCheckRecord(Long id, Long companyId) {
        CheckRecord record = getOne(new LambdaQueryWrapper<CheckRecord>()
                .eq(CheckRecord::getId, id)
                .eq(CheckRecord::getCompanyId, companyId));
        if (record == null) {
            throw new BusinessException("验房记录不存在");
        }
        return record;
    }
}