package com.smart.property.operation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.exception.BusinessException;
import com.smart.property.operation.convert.NoticeConverter;
import com.smart.property.operation.domain.Notice;
import com.smart.property.operation.dto.NoticeDTO;
import com.smart.property.operation.mapper.NoticeMapper;
import com.smart.property.operation.remote.RemoteNameService;
import com.smart.property.operation.service.NoticeService;
import com.smart.property.operation.vo.NoticeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 公告服务实现
 *
 * @author zzz
 * @since 2026-07-25
 */
@Service
@RequiredArgsConstructor
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {

    private final NoticeConverter noticeConverter;
    private final RemoteNameService remoteNameService;

    @Override
    public PageResult<NoticeVO> getNoticePage(PageQuery query, Long companyId, Integer noticeType) {
        Page<Notice> page = new Page<>(query.getPageNum(), query.getPageSize());

        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<Notice>()
                .eq(Notice::getCompanyId, companyId)
                .eq(Notice::getIsDeleted, 0)
                .eq(noticeType != null, Notice::getNoticeType, noticeType)
                .orderByDesc(Notice::getIsTop)
                .orderByDesc(Notice::getCreateTime);

        Page<Notice> result = baseMapper.selectPage(page, wrapper);
        List<NoticeVO> records = result.getRecords().stream()
                .map(noticeConverter::toVO)
                .collect(Collectors.toList());
        remoteNameService.fillCommunityNames(records, NoticeVO::getCommunityId, NoticeVO::setCommunityName);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), records);
    }

    @Override
    public NoticeVO getNoticeById(Long id) {
        Notice notice = getById(id);
        if (notice == null) {
            throw new BusinessException("公告不存在");
        }
        NoticeVO vo = noticeConverter.toVO(notice);
        remoteNameService.fillCommunityNames(List.of(vo), NoticeVO::getCommunityId, NoticeVO::setCommunityName);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createNotice(NoticeDTO dto, Long companyId, String operator) {
        Notice notice = noticeConverter.toEntity(dto);
        notice.setCompanyId(companyId);
        notice.setCreateBy(operator);
        if (notice.getStatus() == null) {
            notice.setStatus(1);
        }
        save(notice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateNotice(Long id, NoticeDTO dto, String operator) {
        Notice existing = getById(id);
        if (existing == null) throw new BusinessException("公告不存在");
        Notice patch = noticeConverter.toEntity(dto);
        patch.setId(id);
        patch.setCompanyId(existing.getCompanyId());
        patch.setCreateBy(existing.getCreateBy());
        patch.setCreateTime(existing.getCreateTime());
        patch.setUpdateBy(operator);
        baseMapper.updateById(patch);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishNotice(Long id, String operatorName) {
        Notice notice = getById(id);
        if (notice == null) throw new BusinessException("公告不存在");
        if (notice.getStatus() == 2) {
            throw new BusinessException("公告已发布");
        }
        notice.setStatus(2);
        notice.setIsPublish(1);
        notice.setPublishTime(LocalDateTime.now());
        notice.setUpdateBy(operatorName);
        baseMapper.updateById(notice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void revokeNotice(Long id, String operatorName) {
        Notice notice = getById(id);
        if (notice == null) throw new BusinessException("公告不存在");
        if (notice.getStatus() != 2) {
            throw new BusinessException("公告未发布，无法撤回");
        }
        notice.setStatus(3);
        notice.setIsPublish(0);
        notice.setUpdateBy(operatorName);
        baseMapper.updateById(notice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markRead(Long id) {
        Notice notice = getById(id);
        if (notice == null) throw new BusinessException("公告不存在");
        notice.setReadCount(notice.getReadCount() + 1);
        baseMapper.updateById(notice);
    }
}