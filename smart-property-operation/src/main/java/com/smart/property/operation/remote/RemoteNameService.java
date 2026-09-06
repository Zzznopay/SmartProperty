package com.smart.property.operation.remote;

import com.smart.property.common.core.domain.Result;
import com.smart.property.operation.remote.dto.PropertyNameMapsDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * 跨服务名称回填组件（operation ↔ property 跨库）
 *
 * <p>运营列表 VO 的 communityName / buildingName / roomNo 字段在本库没有名称列，
 * 统一经 {@link PropertyRemoteClient} 按 id 批量解析后回填。
 * 用法（ServiceImpl 内一行）：</p>
 * <pre>{@code
 * remoteNameService.fillCommunityNames(vos, VisitRecordVO::getCommunityId, VisitRecordVO::setCommunityName);
 * }</pre>
 *
 * <p>远端不可达时按空映射处理，字段保持 null（前端显示 "-"），主流程不受影响。</p>
 *
 * @author zzz
 * @since 2026-09-06
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RemoteNameService {

    private final PropertyRemoteClient propertyRemoteClient;

    /** 回填 VO 的所属小区名称（communityId → communityName） */
    public <T> void fillCommunityNames(List<T> records,
                                       Function<T, Long> idGetter,
                                       BiConsumer<T, String> nameSetter) {
        fill(records, idGetter, nameSetter, NameType.COMMUNITIES);
    }

    /** 回填 VO 的所属楼宇名称（buildingId → buildingName） */
    public <T> void fillBuildingNames(List<T> records,
                                      Function<T, Long> idGetter,
                                      BiConsumer<T, String> nameSetter) {
        fill(records, idGetter, nameSetter, NameType.BUILDINGS);
    }

    /** 回填 VO 的房号（roomId → roomNo） */
    public <T> void fillRoomNos(List<T> records,
                                Function<T, Long> idGetter,
                                BiConsumer<T, String> nameSetter) {
        fill(records, idGetter, nameSetter, NameType.ROOMS);
    }

    private enum NameType {
        COMMUNITIES, BUILDINGS, ROOMS
    }

    private <T> void fill(List<T> records,
                          Function<T, Long> idGetter,
                          BiConsumer<T, String> nameSetter,
                          NameType type) {
        if (records == null || records.isEmpty()) {
            return;
        }
        List<Long> ids = records.stream().map(idGetter).filter(Objects::nonNull).distinct().toList();
        if (ids.isEmpty()) {
            return;
        }
        Map<Long, String> nameMap = fetch(type, ids);
        records.forEach(vo -> {
            Long id = idGetter.apply(vo);
            if (id != null) {
                nameSetter.accept(vo, nameMap.get(id));
            }
        });
    }

    private Map<Long, String> fetch(NameType type, List<Long> ids) {
        try {
            Result<PropertyNameMapsDTO> resp = propertyRemoteClient.batchNames(
                    type == NameType.COMMUNITIES ? ids : null,
                    type == NameType.BUILDINGS ? ids : null,
                    type == NameType.ROOMS ? ids : null);
            PropertyNameMapsDTO data = resp == null ? null : resp.getData();
            if (data == null) {
                return new HashMap<>();
            }
            return switch (type) {
                case COMMUNITIES -> data.getCommunities();
                case BUILDINGS -> data.getBuildings();
                case ROOMS -> data.getRooms();
            };
        } catch (Exception e) {
            log.warn("跨服务名称解析失败（{}），按空映射处理: {}", type, e.getMessage());
            return new HashMap<>();
        }
    }
}
