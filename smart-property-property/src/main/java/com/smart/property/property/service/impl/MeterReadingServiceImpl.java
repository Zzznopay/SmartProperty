package com.smart.property.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.exception.BusinessException;
import com.smart.property.property.convert.MeterReadingConverter;
import com.smart.property.property.domain.MeterReading;
import com.smart.property.property.domain.Room;
import com.smart.property.property.dto.MeterReadingDTO;
import com.smart.property.property.mapper.MeterReadingMapper;
import com.smart.property.property.mapper.RoomMapper;
import com.smart.property.property.service.MeterReadingService;
import com.smart.property.property.vo.MeterReadingVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 抄表记录服务实现
 *
 * @author zzz
 * @since 2026-07-25
 */
@Service
@RequiredArgsConstructor
public class MeterReadingServiceImpl extends ServiceImpl<MeterReadingMapper, MeterReading> implements MeterReadingService {

    private final MeterReadingConverter meterReadingConverter;
    private final RoomMapper roomMapper;

    /** 批量回填 VO 的房间号（列表接口 VO 只带 roomId） */
    private void fillRoomNos(List<MeterReadingVO> records) {
        if (records.isEmpty()) {
            return;
        }
        List<Long> roomIds = records.stream()
                .map(MeterReadingVO::getRoomId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (roomIds.isEmpty()) {
            return;
        }
        Map<Long, String> nameMap = roomMapper.selectList(
                        new LambdaQueryWrapper<Room>().in(Room::getId, roomIds))
                .stream()
                .collect(Collectors.toMap(Room::getId, Room::getRoomNo, (a, b) -> a));
        records.forEach(vo -> vo.setRoomNo(nameMap.get(vo.getRoomId())));
    }

    @Override
    public PageResult<MeterReadingVO> getMeterReadingPage(PageQuery query, Long companyId, Long roomId, Integer meterType, String readingMonth) {
        Page<MeterReading> page = new Page<>(query.getPageNum(), query.getPageSize());

        LambdaQueryWrapper<MeterReading> wrapper = new LambdaQueryWrapper<MeterReading>()
                .eq(MeterReading::getCompanyId, companyId)
                .eq(roomId != null, MeterReading::getRoomId, roomId)
                .eq(meterType != null, MeterReading::getMeterType, meterType)
                .eq(readingMonth != null, MeterReading::getReadingMonth, readingMonth)
                .orderByDesc(MeterReading::getReadingMonth);

        Page<MeterReading> result = baseMapper.selectPage(page, wrapper);
        List<MeterReadingVO> records = result.getRecords().stream()
                .map(meterReadingConverter::toVO)
                .collect(Collectors.toList());
        fillRoomNos(records);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), records);
    }

    @Override
    public MeterReadingVO getById(Long id, Long companyId) {
        MeterReadingVO vo = meterReadingConverter.toVO(getCompanyMeterReading(id, companyId));
        fillRoomNos(List.of(vo));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createMeterReading(MeterReadingDTO dto, Long companyId, String operator) {
        MeterReading record = meterReadingConverter.toEntity(dto);
        record.setCompanyId(companyId);
        // 计算用量
        if (record.getLastReading() != null && record.getCurrentReading() != null) {
            record.setUsageAmount(record.getCurrentReading().subtract(record.getLastReading()));
        }
        record.setReadingDate(LocalDate.now());
        record.setCreateBy(operator);
        record.setCreateTime(java.time.LocalDateTime.now());
        record.setUpdateTime(java.time.LocalDateTime.now());
        baseMapper.insert(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMeterReading(Long id, Long companyId) {
        getCompanyMeterReading(id, companyId);
        removeById(id);
    }

    private MeterReading getCompanyMeterReading(Long id, Long companyId) {
        MeterReading reading = getOne(new LambdaQueryWrapper<MeterReading>()
                .eq(MeterReading::getId, id)
                .eq(MeterReading::getCompanyId, companyId));
        if (reading == null) {
            throw new BusinessException("抄表记录不存在");
        }
        return reading;
    }
}