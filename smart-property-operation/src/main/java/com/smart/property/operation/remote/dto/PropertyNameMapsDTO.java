package com.smart.property.operation.remote.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * property 内部名称批量解析响应（operation 端镜像 DTO）
 *
 * @author zzz
 * @since 2026-09-06
 */
@Data
public class PropertyNameMapsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 小区 id → 小区名称 */
    private Map<Long, String> communities = new HashMap<>();

    /** 楼宇 id → 楼宇名称 */
    private Map<Long, String> buildings = new HashMap<>();

    /** 房间 id → 房号 */
    private Map<Long, String> rooms = new HashMap<>();
}
