package com.smart.property.operation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.operation.domain.VehicleRecord;
import com.smart.property.operation.dto.VehicleRecordDTO;
import com.smart.property.operation.vo.VehicleRecordVO;

/**
 * 车辆进出记录服务接口
 *
 * @author zzz
 * @since 2026-07-25
 */
public interface VehicleRecordService extends IService<VehicleRecord> {

    PageResult<VehicleRecordVO> getVehiclePage(PageQuery query, Long companyId, Long communityId, Integer recordType);

    void vehicleEntry(VehicleRecordDTO dto, Long companyId);

    void vehicleExit(Long id, String operator);

    void payFee(Long id, String operator);
}