package com.smart.property.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.exception.BusinessException;
import com.smart.property.property.convert.FeeItemConverter;
import com.smart.property.property.convert.LadderConfigConverter;
import com.smart.property.property.domain.Community;
import com.smart.property.property.domain.FeeItem;
import com.smart.property.property.domain.LadderConfig;
import com.smart.property.property.dto.FeeItemDTO;
import com.smart.property.property.dto.LadderConfigDTO;
import com.smart.property.property.mapper.CommunityMapper;
import com.smart.property.property.mapper.FeeItemMapper;
import com.smart.property.property.mapper.LadderConfigMapper;
import com.smart.property.property.service.FeeItemService;
import com.smart.property.property.vo.FeeItemVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 费项服务实现
 *
 * @author zzz
 * @since 2026-07-25
 */
@Service
@RequiredArgsConstructor
public class FeeItemServiceImpl extends ServiceImpl<FeeItemMapper, FeeItem> implements FeeItemService {

    private final LadderConfigMapper ladderConfigMapper;
    private final FeeItemConverter feeItemConverter;
    private final LadderConfigConverter ladderConfigConverter;
    private final CommunityMapper communityMapper;

    /** 批量回填 VO 的所属小区名称（列表接口 VO 只带 communityId） */
    private void fillCommunityNames(List<FeeItemVO> records) {
        if (records.isEmpty()) {
            return;
        }
        List<Long> communityIds = records.stream()
                .map(FeeItemVO::getCommunityId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (communityIds.isEmpty()) {
            return;
        }
        Map<Long, String> nameMap = communityMapper.selectList(
                        new LambdaQueryWrapper<Community>().in(Community::getId, communityIds))
                .stream()
                .collect(Collectors.toMap(Community::getId, Community::getCommunityName, (a, b) -> a));
        records.forEach(vo -> vo.setCommunityName(nameMap.get(vo.getCommunityId())));
    }

    @Override
    public FeeItemVO getFeeItemById(Long id, Long companyId) {
        FeeItemVO vo = feeItemConverter.toVO(getCompanyFeeItem(id, companyId));
        fillCommunityNames(List.of(vo));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createFeeItem(FeeItemDTO dto, List<LadderConfigDTO> ladders, Long companyId, String operator) {
        FeeItem feeItem = feeItemConverter.toEntity(dto);
        feeItem.setCompanyId(companyId);
        if (feeItem.getIsActive() == null) {
            feeItem.setIsActive(1);
        }
        feeItem.setCreateBy(operator);
        save(feeItem);

        if (feeItem.getIsLadder() != null && feeItem.getIsLadder() == 1
                && ladders != null && !ladders.isEmpty()) {
            for (LadderConfigDTO ladderDto : ladders) {
                LadderConfig ladder = ladderConfigConverter.toEntity(ladderDto);
                ladder.setCompanyId(feeItem.getCompanyId());
                ladder.setFeeItemId(feeItem.getId());
                ladder.setCreateBy(operator);
                ladderConfigMapper.insert(ladder);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateFeeItem(Long id, FeeItemDTO dto, List<LadderConfigDTO> ladders, Long companyId, String operator) {
        FeeItem existing = getCompanyFeeItem(id, companyId);
        FeeItem patch = feeItemConverter.toEntity(dto);
        patch.setId(id);
        patch.setCompanyId(existing.getCompanyId());
        patch.setCreateBy(existing.getCreateBy());
        patch.setCreateTime(existing.getCreateTime());
        patch.setUpdateBy(operator);
        baseMapper.updateById(patch);

        if (ladders != null) {
            ladderConfigMapper.delete(
                    new LambdaQueryWrapper<LadderConfig>().eq(LadderConfig::getFeeItemId, id));
            for (LadderConfigDTO ladderDto : ladders) {
                LadderConfig ladder = ladderConfigConverter.toEntity(ladderDto);
                ladder.setId(null);
                ladder.setCompanyId(patch.getCompanyId());
                ladder.setFeeItemId(id);
                ladder.setCreateBy(operator);
                ladder.setUpdateBy(operator);
                ladderConfigMapper.insert(ladder);
            }
        }
    }

    @Override
    public void deleteFeeItem(Long id, Long companyId) {
        getCompanyFeeItem(id, companyId);
        removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void toggleActive(Long id, Integer isActive, Long companyId, String operator) {
        FeeItem feeItem = getCompanyFeeItem(id, companyId);
        feeItem.setIsActive(isActive);
        feeItem.setUpdateBy(operator);
        updateById(feeItem);
    }

    @Override
    public PageResult<FeeItemVO> getFeeItemPage(PageQuery query, Long companyId, Long communityId) {
        Page<FeeItem> page = new Page<>(query.getPageNum(), query.getPageSize());

        LambdaQueryWrapper<FeeItem> wrapper = new LambdaQueryWrapper<FeeItem>()
                .eq(FeeItem::getCompanyId, companyId)
                .eq(FeeItem::getIsDeleted, 0)
                .eq(communityId != null, FeeItem::getCommunityId, communityId)
                .orderByAsc(FeeItem::getFeeCode);

        Page<FeeItem> result = baseMapper.selectPage(page, wrapper);
        List<FeeItemVO> records = result.getRecords().stream()
                .map(feeItemConverter::toVO)
                .collect(Collectors.toList());
        fillCommunityNames(records);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), records);
    }

    @Override
    public List<FeeItemVO> getFeeItemsByCommunityId(Long communityId, Long companyId) {
        List<FeeItem> records = baseMapper.selectList(
                new LambdaQueryWrapper<FeeItem>()
                        .eq(FeeItem::getCommunityId, communityId)
                        .eq(FeeItem::getCompanyId, companyId)
                        .eq(FeeItem::getIsDeleted, 0)
                        .eq(FeeItem::getIsActive, 1)
                        .orderByAsc(FeeItem::getFeeCode)
        );
        List<FeeItemVO> vos = feeItemConverter.toVOList(records);
        fillCommunityNames(vos);
        return vos;
    }

    private FeeItem getCompanyFeeItem(Long id, Long companyId) {
        FeeItem feeItem = getOne(new LambdaQueryWrapper<FeeItem>()
                .eq(FeeItem::getId, id)
                .eq(FeeItem::getCompanyId, companyId));
        if (feeItem == null) {
            throw new BusinessException("费项不存在");
        }
        return feeItem;
    }
}