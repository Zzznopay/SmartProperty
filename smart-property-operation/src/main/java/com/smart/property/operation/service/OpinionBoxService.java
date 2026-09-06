package com.smart.property.operation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.operation.domain.OpinionBox;
import com.smart.property.operation.dto.OpinionBoxDTO;
import com.smart.property.operation.dto.OpinionSubmitDTO;
import com.smart.property.operation.vo.OpinionBoxVO;
import com.smart.property.operation.vo.OpinionSubmitVO;

/**
 * 意见箱服务接口
 *
 * @author zzz
 * @since 2026-07-25
 */
public interface OpinionBoxService extends IService<OpinionBox> {

    PageResult<OpinionBoxVO> getOpinionBoxPage(PageQuery query, Long companyId, Long communityId);

    void createBox(OpinionBoxDTO dto, Long companyId, String operator);

    void submitOpinion(OpinionSubmitDTO dto, Long companyId, Long submitUserId, String submitUserName);

    PageResult<OpinionSubmitVO> getOpinionSubmitPage(PageQuery query, Long companyId, Long boxId, Integer status);

    void replyOpinion(Long id, String replyContent, Long replyUserId, String replyUserName);

    void evaluateOpinion(Long id, Integer satisfaction);

    void closeOpinion(Long id, String operator);
}