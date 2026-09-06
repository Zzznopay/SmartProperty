package com.smart.property.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.exception.BusinessException;
import com.smart.property.property.convert.LedgerConverter;
import com.smart.property.property.domain.Community;
import com.smart.property.property.domain.FeeItem;
import com.smart.property.property.domain.Ledger;
import com.smart.property.property.domain.Owner;
import com.smart.property.property.domain.Room;
import com.smart.property.property.dto.LedgerDTO;
import com.smart.property.property.mapper.CommunityMapper;
import com.smart.property.property.mapper.FeeItemMapper;
import com.smart.property.property.mapper.LedgerMapper;
import com.smart.property.property.mapper.OwnerMapper;
import com.smart.property.property.mapper.RoomMapper;
import com.smart.property.property.service.LedgerService;
import com.smart.property.property.vo.LedgerVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 物业费台帐服务实现
 *
 * @author zzz
 * @since 2026-07-25
 */
@Service
@RequiredArgsConstructor
public class LedgerServiceImpl extends ServiceImpl<LedgerMapper, Ledger> implements LedgerService {

    private final LedgerConverter ledgerConverter;
    private final RoomMapper roomMapper;
    private final FeeItemMapper feeItemMapper;
    private final CommunityMapper communityMapper;
    private final OwnerMapper ownerMapper;

    /** 批量回填 VO 的小区/房间/业主/费项名称（列表接口 VO 只带 id） */
    private void fillLedgerNames(List<LedgerVO> records) {
        if (records.isEmpty()) {
            return;
        }
        Map<Long, String> communityNames = communityNamesById(
                records.stream().map(LedgerVO::getCommunityId).filter(Objects::nonNull).distinct().toList());
        Map<Long, String> roomNos = roomNosById(
                records.stream().map(LedgerVO::getRoomId).filter(Objects::nonNull).distinct().toList());
        Map<Long, String> ownerNames = ownerNamesById(
                records.stream().map(LedgerVO::getOwnerId).filter(Objects::nonNull).distinct().toList());
        Map<Long, String> feeItemNames = feeItemNamesById(
                records.stream().map(LedgerVO::getFeeItemId).filter(Objects::nonNull).distinct().toList());
        records.forEach(vo -> {
            vo.setCommunityName(communityNames.get(vo.getCommunityId()));
            vo.setRoomNo(roomNos.get(vo.getRoomId()));
            vo.setOwnerName(ownerNames.get(vo.getOwnerId()));
            vo.setFeeItemName(feeItemNames.get(vo.getFeeItemId()));
        });
    }

    private Map<Long, String> communityNamesById(List<Long> ids) {
        if (ids.isEmpty()) {
            return new HashMap<>();
        }
        return communityMapper.selectList(new LambdaQueryWrapper<Community>().in(Community::getId, ids))
                .stream()
                .collect(Collectors.toMap(Community::getId, Community::getCommunityName, (a, b) -> a));
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

    private Map<Long, String> feeItemNamesById(List<Long> ids) {
        if (ids.isEmpty()) {
            return new HashMap<>();
        }
        return feeItemMapper.selectList(new LambdaQueryWrapper<FeeItem>().in(FeeItem::getId, ids))
                .stream()
                .collect(Collectors.toMap(FeeItem::getId, FeeItem::getFeeName, (a, b) -> a));
    }

    @Override
    public PageResult<LedgerVO> getLedgerPage(PageQuery query, Long companyId, Long roomId, Long ownerId, Integer status) {
        Page<Ledger> page = new Page<>(query.getPageNum(), query.getPageSize());

        LambdaQueryWrapper<Ledger> wrapper = new LambdaQueryWrapper<Ledger>()
                .eq(Ledger::getCompanyId, companyId)
                .eq(Ledger::getIsDeleted, 0)
                .eq(roomId != null, Ledger::getRoomId, roomId)
                .eq(ownerId != null, Ledger::getOwnerId, ownerId)
                .eq(status != null, Ledger::getStatus, status)
                .orderByDesc(Ledger::getLedgerMonth);

        Page<Ledger> result = baseMapper.selectPage(page, wrapper);
        List<LedgerVO> records = result.getRecords().stream()
                .map(ledgerConverter::toVO)
                .collect(Collectors.toList());
        fillLedgerNames(records);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), records);
    }

    @Override
    public List<LedgerVO> getLedgersByRoomId(Long roomId, Long companyId) {
        List<Ledger> records = baseMapper.selectList(
                new LambdaQueryWrapper<Ledger>()
                        .eq(Ledger::getRoomId, roomId)
                        .eq(Ledger::getCompanyId, companyId)
                        .eq(Ledger::getIsDeleted, 0)
                        .orderByDesc(Ledger::getLedgerMonth)
        );
        List<LedgerVO> vos = ledgerConverter.toVOList(records);
        fillLedgerNames(vos);
        return vos;
    }

    @Override
    public PageResult<LedgerVO> getArrearsPage(PageQuery query, Long companyId) {
        Page<Ledger> page = new Page<>(query.getPageNum(), query.getPageSize());

        LambdaQueryWrapper<Ledger> wrapper = new LambdaQueryWrapper<Ledger>()
                .eq(Ledger::getCompanyId, companyId)
                .eq(Ledger::getIsDeleted, 0)
                .in(Ledger::getStatus, 1, 2)
                .orderByDesc(Ledger::getLedgerMonth);

        Page<Ledger> result = baseMapper.selectPage(page, wrapper);
        List<LedgerVO> records = result.getRecords().stream()
                .map(ledgerConverter::toVO)
                .collect(Collectors.toList());
        fillLedgerNames(records);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), records);
    }

    @Override
    public LedgerVO getLedgerById(Long id, Long companyId) {
        LedgerVO vo = ledgerConverter.toVO(getCompanyLedger(id, companyId));
        fillLedgerNames(List.of(vo));
        return vo;
    }

    @Override
    public void updateLedger(Long id, LedgerDTO dto, Long companyId, String operator) {
        Ledger existing = getCompanyLedger(id, companyId);
        Ledger patch = ledgerConverter.toEntity(dto);
        patch.setId(id);
        patch.setCompanyId(existing.getCompanyId());
        patch.setCreateBy(existing.getCreateBy());
        patch.setCreateTime(existing.getCreateTime());
        patch.setUpdateBy(operator);
        baseMapper.updateById(patch);
    }

    @Override
    public void deleteLedger(Long id, Long companyId) {
        getCompanyLedger(id, companyId);
        removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generatePropertyFee(Long communityId, Long roomId, String month, Long companyId, String operator) {
        Room room = roomMapper.selectById(roomId);
        if (room == null || !communityId.equals(room.getCommunityId())) {
            throw new BusinessException("房间不存在或不属于该小区");
        }
        if (room.getOwnerId() == null) {
            throw new BusinessException("房间未绑定业主，无法生成物业费台账");
        }
        // 取该小区按面积收费的物业管理费费项（唯一）
        FeeItem feeItem = feeItemMapper.selectOne(new LambdaQueryWrapper<FeeItem>()
                .eq(FeeItem::getCompanyId, companyId)
                .eq(FeeItem::getCommunityId, communityId)
                .eq(FeeItem::getChargeMode, 1)
                .eq(FeeItem::getIsActive, 1)
                .last("LIMIT 1"));
        if (feeItem == null || feeItem.getUnitPrice() == null) {
            throw new BusinessException("未配置按面积收费的物业费费项，无法生成台账");
        }

        Ledger ledger = new Ledger();
        ledger.setCompanyId(companyId);
        ledger.setCommunityId(communityId);
        ledger.setRoomId(roomId);
        ledger.setOwnerId(room.getOwnerId());
        ledger.setFeeItemId(feeItem.getId());
        ledger.setLedgerMonth(month);
        ledger.setAmount(room.getBuildArea() == null ? BigDecimal.ZERO
                : room.getBuildArea().multiply(feeItem.getUnitPrice()).setScale(2, RoundingMode.HALF_UP));
        ledger.setPaidAmount(BigDecimal.ZERO);
        ledger.setDiscountAmount(BigDecimal.ZERO);
        ledger.setLateFee(BigDecimal.ZERO);
        ledger.setStatus(1);
        ledger.setDueDate(LocalDate.parse(month + "-25"));
        ledger.setCreateBy(operator);
        baseMapper.insert(ledger);
    }

    private Ledger getCompanyLedger(Long id, Long companyId) {
        Ledger ledger = getOne(new LambdaQueryWrapper<Ledger>()
                .eq(Ledger::getId, id)
                .eq(Ledger::getCompanyId, companyId));
        if (ledger == null) {
            throw new BusinessException("台帐不存在");
        }
        return ledger;
    }
}