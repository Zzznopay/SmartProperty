package com.smart.property.operation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.exception.BusinessException;
import com.smart.property.operation.convert.VehicleRecordConverter;
import com.smart.property.operation.domain.VehicleRecord;
import com.smart.property.operation.dto.VehicleRecordDTO;
import com.smart.property.operation.mapper.VehicleRecordMapper;
import com.smart.property.operation.remote.RemoteNameService;
import com.smart.property.operation.service.VehicleRecordService;
import com.smart.property.operation.vo.VehicleRecordVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 车辆进出记录服务实现
 *
 * @author zzz
 * @since 2026-07-25
 */
@Service
@RequiredArgsConstructor
public class VehicleRecordServiceImpl extends ServiceImpl<VehicleRecordMapper, VehicleRecord> implements VehicleRecordService {

    private final VehicleRecordConverter vehicleRecordConverter;
    private final RemoteNameService remoteNameService;

    @Override
    public PageResult<VehicleRecordVO> getVehiclePage(PageQuery query, Long companyId, Long communityId, Integer recordType) {
        Page<VehicleRecord> page = new Page<>(query.getPageNum(), query.getPageSize());

        LambdaQueryWrapper<VehicleRecord> wrapper = new LambdaQueryWrapper<VehicleRecord>()
                .eq(VehicleRecord::getCompanyId, companyId)
                .eq(communityId != null, VehicleRecord::getCommunityId, communityId)
                .eq(recordType != null, VehicleRecord::getRecordType, recordType)
                .orderByDesc(VehicleRecord::getRecordTime);

        Page<VehicleRecord> result = baseMapper.selectPage(page, wrapper);
        List<VehicleRecordVO> records = result.getRecords().stream()
                .map(vehicleRecordConverter::toVO)
                .collect(Collectors.toList());
        remoteNameService.fillCommunityNames(records, VehicleRecordVO::getCommunityId, VehicleRecordVO::setCommunityName);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), records);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void vehicleEntry(VehicleRecordDTO dto, Long companyId) {
        VehicleRecord record = vehicleRecordConverter.toEntity(dto);
        record.setCompanyId(companyId);
        record.setRecordType(1);
        record.setRecordTime(LocalDateTime.now());
        record.setPayStatus(0);
        baseMapper.insert(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void vehicleExit(Long id, String operator) {
        VehicleRecord record = getById(id);
        if (record == null) {
            throw new BusinessException("车辆记录不存在");
        }
        if (record.getRecordType() == 2) {
            throw new BusinessException("车辆已出场");
        }
        VehicleRecord exitRecord = new VehicleRecord();
        exitRecord.setCompanyId(record.getCompanyId());
        exitRecord.setCommunityId(record.getCommunityId());
        exitRecord.setPlateNo(record.getPlateNo());
        exitRecord.setVehicleType(record.getVehicleType());
        exitRecord.setRecordType(2);
        exitRecord.setRecordTime(LocalDateTime.now());
        exitRecord.setGateName(record.getGateName());
        exitRecord.setParkingId(record.getParkingId());
        exitRecord.setIsTemporary(record.getIsTemporary());
        baseMapper.insert(exitRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payFee(Long id, String operator) {
        VehicleRecord record = getById(id);
        if (record == null) {
            throw new BusinessException("车辆记录不存在");
        }
        if (record.getPayStatus() == 1) {
            throw new BusinessException("已缴费");
        }
        record.setPayStatus(1);
        record.setPayTime(LocalDateTime.now());
        baseMapper.updateById(record);
    }
}