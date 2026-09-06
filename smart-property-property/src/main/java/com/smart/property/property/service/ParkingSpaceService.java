package com.smart.property.property.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.property.domain.ParkingSpace;
import com.smart.property.property.dto.ParkingSpaceDTO;
import com.smart.property.property.vo.ParkingSpaceVO;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 车位服务接口
 *
 * @author zzz
 * @since 2026-07-25
 */
public interface ParkingSpaceService extends IService<ParkingSpace> {

    PageResult<ParkingSpaceVO> getParkingPage(PageQuery query, Long companyId, Long communityId, Integer status);

    ParkingSpaceVO getParkingById(Long id, Long companyId);

    void createParking(ParkingSpaceDTO dto, Long companyId, String operator);

    void updateParking(Long id, ParkingSpaceDTO dto, Long companyId, String operator);

    void deleteParking(Long id, Long companyId);

    void saleParking(Long id, Long ownerId, BigDecimal salePrice, Long companyId, String operator);

    void rentParking(Long id, Long tenantId, BigDecimal rentPrice, LocalDate startDate, LocalDate endDate, Long companyId, String operator);

    void releaseParking(Long id, Long companyId, String operator);
}