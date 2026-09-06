package com.smart.property.operation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.exception.BusinessException;
import com.smart.property.operation.convert.GoodsRecordConverter;
import com.smart.property.operation.domain.GoodsRecord;
import com.smart.property.operation.dto.GoodsRecordDTO;
import com.smart.property.operation.mapper.GoodsRecordMapper;
import com.smart.property.operation.remote.RemoteNameService;
import com.smart.property.operation.service.GoodsRecordService;
import com.smart.property.operation.vo.GoodsRecordVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GoodsRecordServiceImpl extends ServiceImpl<GoodsRecordMapper, GoodsRecord> implements GoodsRecordService {

    private final GoodsRecordConverter goodsRecordConverter;
    private final RemoteNameService remoteNameService;

    @Override
    public PageResult<GoodsRecordVO> getPage(PageQuery query, Long companyId, Long communityId) {
        Page<GoodsRecord> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<GoodsRecord> wrapper = new LambdaQueryWrapper<GoodsRecord>()
                .eq(GoodsRecord::getCompanyId, companyId)
                .eq(GoodsRecord::getIsDeleted, 0)
                .eq(communityId != null, GoodsRecord::getCommunityId, communityId)
                .orderByDesc(GoodsRecord::getOperateTime);
        Page<GoodsRecord> result = baseMapper.selectPage(page, wrapper);
        List<GoodsRecordVO> records = result.getRecords().stream()
                .map(goodsRecordConverter::toVO)
                .collect(Collectors.toList());
        remoteNameService.fillCommunityNames(records, GoodsRecordVO::getCommunityId, GoodsRecordVO::setCommunityName);
        remoteNameService.fillRoomNos(records, GoodsRecordVO::getRoomId, GoodsRecordVO::setRoomNo);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), records);
    }

    @Override
    public GoodsRecordVO getById(Long id) {
        GoodsRecord entity = baseMapper.selectById(id);
        if (entity == null) throw new BusinessException("物品出入记录不存在");
        GoodsRecordVO vo = goodsRecordConverter.toVO(entity);
        remoteNameService.fillCommunityNames(List.of(vo), GoodsRecordVO::getCommunityId, GoodsRecordVO::setCommunityName);
        remoteNameService.fillRoomNos(List.of(vo), GoodsRecordVO::getRoomId, GoodsRecordVO::setRoomNo);
        return vo;
    }

    @Override
    public void create(GoodsRecordDTO dto, Long companyId, String operator) {
        GoodsRecord entity = goodsRecordConverter.toEntity(dto);
        entity.setCompanyId(companyId);
        entity.setCreateBy(operator);
        save(entity);
    }
}