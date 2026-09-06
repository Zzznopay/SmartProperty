package com.smart.property.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.exception.BusinessException;
import com.smart.property.property.convert.SaleContractConverter;
import com.smart.property.property.domain.Owner;
import com.smart.property.property.domain.Room;
import com.smart.property.property.domain.SaleContract;
import com.smart.property.property.dto.SaleContractDTO;
import com.smart.property.property.mapper.OwnerMapper;
import com.smart.property.property.mapper.RoomMapper;
import com.smart.property.property.mapper.SaleContractMapper;
import com.smart.property.property.service.SaleContractService;
import com.smart.property.property.vo.SaleContractVO;
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
 * 销售合同服务实现
 *
 * @author zzz
 * @since 2026-07-28
 */
@Service
@RequiredArgsConstructor
public class SaleContractServiceImpl extends ServiceImpl<SaleContractMapper, SaleContract> implements SaleContractService {

    private final RoomMapper roomMapper;
    private final SaleContractConverter saleContractConverter;
    private final OwnerMapper ownerMapper;

    /** 批量回填 VO 的房间号/业主名称（列表接口 VO 只带 id） */
    private void fillSaleContractNames(List<SaleContractVO> records) {
        if (records.isEmpty()) {
            return;
        }
        Map<Long, String> roomNos = roomNosById(
                records.stream().map(SaleContractVO::getRoomId).filter(Objects::nonNull).distinct().toList());
        Map<Long, String> ownerNames = ownerNamesById(
                records.stream().map(SaleContractVO::getOwnerId).filter(Objects::nonNull).distinct().toList());
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
    public PageResult<SaleContractVO> getSaleContractPage(PageQuery query, Long companyId, Long roomId, Integer status) {
        Page<SaleContract> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<SaleContract> wrapper = new LambdaQueryWrapper<SaleContract>()
                .eq(SaleContract::getCompanyId, companyId)
                .eq(SaleContract::getIsDeleted, 0)
                .eq(roomId != null, SaleContract::getRoomId, roomId)
                .eq(status != null, SaleContract::getStatus, status)
                .orderByDesc(SaleContract::getCreateTime);
        Page<SaleContract> result = baseMapper.selectPage(page, wrapper);
        List<SaleContractVO> records = result.getRecords().stream()
                .map(saleContractConverter::toVO)
                .collect(Collectors.toList());
        fillSaleContractNames(records);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), records);
    }

    @Override
    public SaleContractVO getByContractId(Long id, Long companyId) {
        SaleContractVO vo = saleContractConverter.toVO(getCompanySaleContract(id, companyId));
        fillSaleContractNames(List.of(vo));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createSaleContract(SaleContractDTO dto, Long companyId, String operator) {
        SaleContract contract = saleContractConverter.toEntity(dto);
        contract.setCompanyId(companyId);
        if (contract.getStatus() == null) {
            contract.setStatus(2);
        }
        if (contract.getDeliveryStatus() == null) {
            contract.setDeliveryStatus(0);
        }
        contract.setCreateBy(operator);
        save(contract);

        // 关联房间状态：空置 → 已售
        Room room = roomMapper.selectById(contract.getRoomId());
        if (room != null && room.getStatus() == 1) {
            room.setStatus(2);
            room.setOwnerId(contract.getOwnerId());
            room.setUpdateBy(operator);
            roomMapper.updateById(room);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSaleContract(Long id, SaleContractDTO dto, Long companyId, String operator) {
        SaleContract existing = getCompanySaleContract(id, companyId);
        SaleContract patch = saleContractConverter.toEntity(dto);
        patch.setId(id);
        patch.setCompanyId(existing.getCompanyId());
        patch.setCreateBy(existing.getCreateBy());
        patch.setCreateTime(existing.getCreateTime());
        patch.setUpdateBy(operator);
        baseMapper.updateById(patch);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deliver(Long id, Long companyId, String operator) {
        SaleContract contract = getCompanySaleContract(id, companyId);
        if (contract.getDeliveryStatus() != null && contract.getDeliveryStatus() == 1) {
            throw new BusinessException("已交付，不能重复交付");
        }
        contract.setDeliveryStatus(1);
        contract.setUpdateBy(operator);
        baseMapper.updateById(contract);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSaleContract(Long id, Long companyId) {
        SaleContract contract = getCompanySaleContract(id, companyId);
        contract.setStatus(3);
        baseMapper.updateById(contract);
    }

    private SaleContract getCompanySaleContract(Long id, Long companyId) {
        SaleContract contract = getOne(new LambdaQueryWrapper<SaleContract>()
                .eq(SaleContract::getId, id)
                .eq(SaleContract::getCompanyId, companyId));
        if (contract == null) {
            throw new BusinessException("销售合同不存在");
        }
        return contract;
    }
}