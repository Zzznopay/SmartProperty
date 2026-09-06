package com.smart.property.property.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.property.domain.Ledger;
import com.smart.property.property.dto.LedgerDTO;
import com.smart.property.property.vo.LedgerVO;

import java.util.List;

/**
 * 物业费台帐服务接口
 *
 * @author zzz
 * @since 2026-07-25
 */
public interface LedgerService extends IService<Ledger> {

    PageResult<LedgerVO> getLedgerPage(PageQuery query, Long companyId, Long roomId, Long ownerId, Integer status);

    List<LedgerVO> getLedgersByRoomId(Long roomId, Long companyId);

    PageResult<LedgerVO> getArrearsPage(PageQuery query, Long companyId);

    LedgerVO getLedgerById(Long id, Long companyId);

    void updateLedger(Long id, LedgerDTO dto, Long companyId, String operator);

    void deleteLedger(Long id, Long companyId);

    void generatePropertyFee(Long communityId, Long roomId, String month, Long companyId, String operator);
}