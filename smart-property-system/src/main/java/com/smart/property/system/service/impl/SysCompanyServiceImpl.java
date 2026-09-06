package com.smart.property.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.exception.BusinessException;
import com.smart.property.common.security.util.EncryptUtils;
import com.smart.property.system.convert.SysCompanyConverter;
import com.smart.property.system.domain.SysCompany;
import com.smart.property.system.dto.SysCompanyDTO;
import com.smart.property.system.mapper.SysCompanyMapper;
import com.smart.property.system.service.SysCompanyService;
import com.smart.property.system.vo.SysCompanyVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 物业公司服务实现
 *
 * @author zzz
 * @since 2026-07-25
 */
@Service
@RequiredArgsConstructor
public class SysCompanyServiceImpl extends ServiceImpl<SysCompanyMapper, SysCompany> implements SysCompanyService {

    private final EncryptUtils encryptUtils;
    private final SysCompanyConverter sysCompanyConverter;

    @Override
    public List<SysCompanyVO> getCompanyTree() {
        List<SysCompany> all = list();
        return buildTree(all);
    }

    @Override
    public PageResult<SysCompanyVO> getCompanyPage(int pageNum, int pageSize, String companyName) {
        Page<SysCompany> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysCompany> wrapper = new LambdaQueryWrapper<SysCompany>()
                .like(companyName != null && !companyName.isEmpty(), SysCompany::getCompanyName, companyName)
                .orderByAsc(SysCompany::getParentId)
                .orderByAsc(SysCompany::getId);
        Page<SysCompany> result = baseMapper.selectPage(page, wrapper);
        List<SysCompanyVO> records = sysCompanyConverter.toVOList(result.getRecords());
        for (SysCompanyVO vo : records) {
            decryptPhoneVO(vo);
        }
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), records);
    }

    @Override
    public SysCompanyVO getCompanyById(Long id) {
        SysCompany company = getById(id);
        if (company == null) {
            throw new BusinessException("公司不存在");
        }
        SysCompanyVO vo = sysCompanyConverter.toVO(company);
        decryptPhoneVO(vo);
        return vo;
    }

    @Override
    public void createCompany(SysCompanyDTO dto, String operator) {
        Long dup = baseMapper.selectCount(new LambdaQueryWrapper<SysCompany>()
                .eq(SysCompany::getCompanyCode, dto.getCompanyCode()));
        if (dup != null && dup > 0) {
            throw new BusinessException("公司编码已存在");
        }
        SysCompany company = sysCompanyConverter.toEntity(dto);
        encryptPhoneEntity(company, dto.getContactPhone());
        company.setCreateBy(operator);
        save(company);
    }

    @Override
    public void updateCompany(Long id, SysCompanyDTO dto, String operator) {
        getCompanyById(id);
        SysCompany company = sysCompanyConverter.toEntity(dto);
        company.setId(id);
        encryptPhoneEntity(company, dto.getContactPhone());
        company.setUpdateBy(operator);
        updateById(company);
    }

    /**
     * 联系手机加密写入数据库。
     * <p>DTO 中的 contactPhone 是明文，Converter 默认不会拷贝（已 ignore），由本方法显式处理。</p>
     */
    private void encryptPhoneEntity(SysCompany company, String plain) {
        if (plain == null || plain.isEmpty()) {
            return;
        }
        // 已是密文(包含 Base64 字符可能含 '/',长度>=16)不再加密,避免双重加密
        if (plain.matches("^[A-Za-z0-9+/=]{16,}$") && !plain.matches("^[0-9+\\-\\s]+$")) {
            company.setContactPhone(plain);
            return;
        }
        company.setContactPhone(encryptUtils.encrypt(plain));
    }

    /**
     * VO 中的 contactPhone 出参解密。
     */
    private void decryptPhoneVO(SysCompanyVO vo) {
        if (vo.getContactPhone() == null || vo.getContactPhone().isEmpty()) {
            return;
        }
        try {
            vo.setContactPhone(encryptUtils.decrypt(vo.getContactPhone()));
        } catch (Exception ignored) {
            // 非密文则保留原值
        }
    }

    private List<SysCompanyVO> buildTree(List<SysCompany> all) {
        List<SysCompanyVO> nodes = sysCompanyConverter.toVOList(all);
        for (SysCompanyVO n : nodes) {
            decryptPhoneVO(n);
        }
        Map<Long, SysCompanyVO> map = new HashMap<>();
        for (SysCompanyVO n : nodes) {
            map.put(n.getId(), n);
        }
        List<SysCompanyVO> roots = new ArrayList<>();
        for (SysCompanyVO n : map.values()) {
            if (n.getParentId() == null || n.getParentId() == 0) {
                roots.add(n);
            } else {
                SysCompanyVO parent = map.get(n.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(n);
                }
            }
        }
        return roots;
    }
}