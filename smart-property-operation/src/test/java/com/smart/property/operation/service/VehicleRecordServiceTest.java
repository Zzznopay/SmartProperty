package com.smart.property.operation.service;

import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.operation.dto.VehicleRecordDTO;
import com.smart.property.operation.vo.VehicleRecordVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 车辆进出服务测试
 *
 * @author zzz
 * @since 2026-07-25
 */
@SpringBootTest
@Transactional
class VehicleRecordServiceTest {

    @Autowired
    private VehicleRecordService vehicleRecordService;

    private VehicleRecordDTO buildDto(String plateNo, Integer isTemporary, String feeAmount) {
        VehicleRecordDTO dto = new VehicleRecordDTO();
        dto.setCommunityId(1L);
        dto.setPlateNo(plateNo);
        dto.setVehicleType(1);
        dto.setGateName("东门");
        dto.setIsTemporary(isTemporary);
        if (feeAmount != null) {
            dto.setFeeAmount(new BigDecimal(feeAmount));
        }
        return dto;
    }

    /**
     * vehicleEntry 无返回值，按唯一车牌从分页结果中查回创建的 VO
     */
    private VehicleRecordVO findByPlate(String plateNo) {
        PageQuery query = new PageQuery();
        query.setPageNum(1);
        query.setPageSize(100);
        PageResult<VehicleRecordVO> page = vehicleRecordService.getVehiclePage(query, 1L, null, null);
        return page.getRecords().stream()
                .filter(vo -> plateNo.equals(vo.getPlateNo()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("车辆记录未查询到: " + plateNo));
    }

    @Test
    void testVehicleEntry() {
        String plateNo = "OD-VR-T1-01";
        vehicleRecordService.vehicleEntry(buildDto(plateNo, 0, null), 1L);

        VehicleRecordVO created = findByPlate(plateNo);

        assertNotNull(created.getId());
        assertEquals(1, created.getRecordType());
    }

    @Test
    void testGetVehiclePage() {
        vehicleRecordService.vehicleEntry(buildDto("OD-VR-T2-01", 0, null), 1L);

        PageQuery query = new PageQuery();
        query.setPageNum(1);
        query.setPageSize(10);

        PageResult<VehicleRecordVO> result = vehicleRecordService.getVehiclePage(query, 1L, null, null);

        assertNotNull(result);
        assertTrue(result.getTotal() > 0);
    }

    @Test
    void testVehicleExit() {
        String plateNo = "OD-VR-T3-01";
        vehicleRecordService.vehicleEntry(buildDto(plateNo, 1, "10"), 1L);

        VehicleRecordVO entry = findByPlate(plateNo);
        vehicleRecordService.vehicleExit(entry.getId(), "test");

        // 验证出场记录已创建
        PageQuery query = new PageQuery();
        query.setPageNum(1);
        query.setPageSize(100);

        PageResult<VehicleRecordVO> result = vehicleRecordService.getVehiclePage(query, 1L, null, 2);

        assertNotNull(result);
        assertTrue(result.getRecords().stream().anyMatch(vo -> plateNo.equals(vo.getPlateNo())));
    }

    @Test
    void testPayFee() {
        String plateNo = "OD-VR-T4-01";
        vehicleRecordService.vehicleEntry(buildDto(plateNo, 1, "15"), 1L);

        VehicleRecordVO entry = findByPlate(plateNo);
        vehicleRecordService.payFee(entry.getId(), "test");

        // 验证缴费状态已更新
        VehicleRecordVO paid = findByPlate(plateNo);
        assertEquals(1, paid.getPayStatus());
    }
}
