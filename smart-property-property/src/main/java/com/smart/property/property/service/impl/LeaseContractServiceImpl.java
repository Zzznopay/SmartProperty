package com.smart.property.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.exception.BusinessException;
import com.smart.property.property.convert.LeaseContractConverter;
import com.smart.property.property.domain.LeaseContract;
import com.smart.property.property.domain.Room;
import com.smart.property.property.domain.Tenant;
import com.smart.property.property.dto.LeaseContractDTO;
import com.smart.property.property.mapper.LeaseContractMapper;
import com.smart.property.property.mapper.RoomMapper;
import com.smart.property.property.mapper.TenantMapper;
import com.smart.property.property.service.LeaseContractService;
import com.smart.property.property.vo.LeaseContractVO;
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
 * 租赁合同服务实现
 *
 * @author zzz
 * @since 2026-07-25
 */
@Service
@RequiredArgsConstructor
public class LeaseContractServiceImpl extends ServiceImpl<LeaseContractMapper, LeaseContract> implements LeaseContractService {

    private final LeaseContractConverter leaseContractConverter;
    private final RoomMapper roomMapper;
    private final TenantMapper tenantMapper;

    /** 批量回填 VO 的房间号/租户名称（列表接口 VO 只带 id） */
    private void fillLeaseContractNames(List<LeaseContractVO> records) {
        if (records.isEmpty()) {
            return;
        }
        Map<Long, String> roomNos = roomNosById(
                records.stream().map(LeaseContractVO::getRoomId).filter(Objects::nonNull).distinct().toList());
        Map<Long, String> tenantNames = tenantNamesById(
                records.stream().map(LeaseContractVO::getTenantId).filter(Objects::nonNull).distinct().toList());
        records.forEach(vo -> {
            vo.setRoomNo(roomNos.get(vo.getRoomId()));
            vo.setTenantName(tenantNames.get(vo.getTenantId()));
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

    private Map<Long, String> tenantNamesById(List<Long> ids) {
        if (ids.isEmpty()) {
            return new HashMap<>();
        }
        return tenantMapper.selectList(new LambdaQueryWrapper<Tenant>().in(Tenant::getId, ids))
                .stream()
                .collect(Collectors.toMap(Tenant::getId, Tenant::getTenantName, (a, b) -> a));
    }

    @Override
    public PageResult<LeaseContractVO> getContractPage(PageQuery query, Long companyId, Integer status) {
        Page<LeaseContract> page = new Page<>(query.getPageNum(), query.getPageSize());

        LambdaQueryWrapper<LeaseContract> wrapper = new LambdaQueryWrapper<LeaseContract>()
                .eq(LeaseContract::getCompanyId, companyId)
                .eq(LeaseContract::getIsDeleted, 0)
                .eq(status != null, LeaseContract::getStatus, status)
                .orderByDesc(LeaseContract::getCreateTime);

        Page<LeaseContract> result = baseMapper.selectPage(page, wrapper);
        List<LeaseContractVO> records = result.getRecords().stream()
                .map(leaseContractConverter::toVO)
                .collect(Collectors.toList());
        fillLeaseContractNames(records);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), records);
    }

    @Override
    public LeaseContractVO getContractById(Long id, Long companyId) {
        LeaseContractVO vo = leaseContractConverter.toVO(getCompanyContract(id, companyId));
        fillLeaseContractNames(List.of(vo));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createContract(LeaseContractDTO dto, Long companyId, String operator) {
        LeaseContract contract = leaseContractConverter.toEntity(dto);
        contract.setCompanyId(companyId);
        if (contract.getStatus() == null) {
            contract.setStatus(2);
        }
        contract.setCreateBy(operator);
        save(contract);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateContract(Long id, LeaseContractDTO dto, Long companyId, String operator) {
        LeaseContract existing = getCompanyContract(id, companyId);
        LeaseContract patch = leaseContractConverter.toEntity(dto);
        patch.setId(id);
        patch.setCompanyId(existing.getCompanyId());
        patch.setCreateBy(existing.getCreateBy());
        patch.setCreateTime(existing.getCreateTime());
        patch.setUpdateBy(operator);
        baseMapper.updateById(patch);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void terminateContract(Long id, Long companyId, String reason, String operator) {
        LeaseContract contract = getCompanyContract(id, companyId);
        if (contract.getStatus() != 2) {
            throw new BusinessException("合同未生效，无法终止");
        }
        contract.setStatus(3);
        contract.setTerminateDate(LocalDate.now());
        contract.setTerminateReason(reason);
        contract.setUpdateBy(operator);
        baseMapper.updateById(contract);
    }

    @Override
    public PageResult<LeaseContractVO> getExpiringContracts(PageQuery query, Long companyId, int days) {
        Page<LeaseContract> page = new Page<>(query.getPageNum(), query.getPageSize());

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(days);

        LambdaQueryWrapper<LeaseContract> wrapper = new LambdaQueryWrapper<LeaseContract>()
                .eq(LeaseContract::getCompanyId, companyId)
                .eq(LeaseContract::getIsDeleted, 0)
                .eq(LeaseContract::getStatus, 2)
                .between(LeaseContract::getEndDate, today, endDate)
                .orderByAsc(LeaseContract::getEndDate);

        Page<LeaseContract> result = baseMapper.selectPage(page, wrapper);
        List<LeaseContractVO> records = result.getRecords().stream()
                .map(leaseContractConverter::toVO)
                .collect(Collectors.toList());
        fillLeaseContractNames(records);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), records);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void activateContract(Long id, Long companyId, String operator) {
        LeaseContract contract = getCompanyContract(id, companyId);
        if (contract.getStatus() != 1) {
            throw new BusinessException("仅草稿状态的合同可激活");
        }
        contract.setStatus(2);
        contract.setUpdateBy(operator);
        baseMapper.updateById(contract);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void transferContract(Long id, Long companyId, Long newTenantId, String operator) {
        LeaseContract contract = getCompanyContract(id, companyId);
        if (contract.getStatus() != 2) {
            throw new BusinessException("仅生效状态的合同可转兑");
        }
        contract.setTenantId(newTenantId);
        contract.setUpdateBy(operator);
        baseMapper.updateById(contract);
    }

    @Override
    public void deleteContract(Long id, Long companyId) {
        getCompanyContract(id, companyId);
        removeById(id);
    }

    private LeaseContract getCompanyContract(Long id, Long companyId) {
        LeaseContract contract = getOne(new LambdaQueryWrapper<LeaseContract>()
                .eq(LeaseContract::getId, id)
                .eq(LeaseContract::getCompanyId, companyId));
        if (contract == null) {
            throw new BusinessException("租赁合同不存在");
        }
        return contract;
    }
}