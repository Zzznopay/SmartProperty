package com.smart.property.operation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.operation.domain.Notice;
import com.smart.property.operation.dto.NoticeDTO;
import com.smart.property.operation.vo.NoticeVO;

/**
 * 公告服务接口
 *
 * @author zzz
 * @since 2026-07-25
 */
public interface NoticeService extends IService<Notice> {

    PageResult<NoticeVO> getNoticePage(PageQuery query, Long companyId, Integer noticeType);

    NoticeVO getNoticeById(Long id);

    void createNotice(NoticeDTO dto, Long companyId, String operator);

    void updateNotice(Long id, NoticeDTO dto, String operator);

    void publishNotice(Long id, String operatorName);

    void revokeNotice(Long id, String operatorName);

    void markRead(Long id);
}