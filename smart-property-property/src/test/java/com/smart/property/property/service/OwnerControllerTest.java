package com.smart.property.property.service;

import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.exception.BusinessException;
import com.smart.property.property.dto.OwnerDTO;
import com.smart.property.property.vo.OwnerVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 业主Controller测试
 *
 * @author zzz
 * @since 2026-07-25
 */
@SpringBootTest
@Transactional
class OwnerControllerTest {

    @Autowired
    private OwnerService ownerService;

    /**
     * 通过新接口新增业主，并按唯一编码从分页结果中查回创建的 VO
     */
    private OwnerVO createOwner(String code, String name) {
        OwnerDTO dto = new OwnerDTO();
        dto.setOwnerCode(code);
        dto.setOwnerName(name);
        dto.setGender(1);
        dto.setPhone("13800138000");
        dto.setOwnerType(1);
        dto.setStatus(1);
        ownerService.addOwner(dto, 1L, "test");

        PageQuery query = new PageQuery();
        query.setPageNum(1);
        query.setPageSize(100);
        PageResult<OwnerVO> page = ownerService.getOwnerPage(query, 1L);
        return page.getRecords().stream()
                .filter(vo -> code.equals(vo.getOwnerCode()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("创建的业主未查询到: " + code));
    }

    @Test
    void testAddOwner() {
        OwnerVO created = createOwner("OVC-T1-01", "张三");

        assertNotNull(created.getId());
        assertEquals("138****8000", created.getPhoneMask());
    }

    @Test
    void testGetOwnerPage() {
        createOwner("OVC-T1-02", "李四");

        PageQuery query = new PageQuery();
        query.setPageNum(1);
        query.setPageSize(10);

        PageResult<OwnerVO> result = ownerService.getOwnerPage(query, 1L);

        assertNotNull(result);
        assertTrue(result.getTotal() > 0);
    }

    @Test
    void testGetOwnerById() {
        OwnerVO created = createOwner("OVC-T1-03", "王五");

        OwnerVO result = ownerService.getOwnerById(created.getId(), 1L);

        assertNotNull(result);
        assertEquals("王五", result.getOwnerName());
    }

    @Test
    void testUpdateOwner() {
        OwnerVO created = createOwner("OVC-T1-04", "更新前");

        OwnerDTO update = new OwnerDTO();
        update.setOwnerCode("OVC-T1-04");
        update.setOwnerName("更新后");
        // 注意：updateOwner 的 companyId 参数为 String 类型
        ownerService.updateOwner(created.getId(), update, "1", "test");

        OwnerVO updated = ownerService.getOwnerById(created.getId(), 1L);
        assertEquals("更新后", updated.getOwnerName());
    }

    @Test
    void testDeleteOwner() {
        OwnerVO created = createOwner("OVC-T1-05", "删除测试");

        ownerService.deleteOwner(created.getId(), 1L);

        // 逻辑删除后按公司隔离查询会抛业务异常
        assertThrows(BusinessException.class,
                () -> ownerService.getOwnerById(created.getId(), 1L));
    }
}
