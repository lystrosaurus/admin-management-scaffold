package io.github.lystrosaurus.admin.organization.orgunit.domain.event;

import io.github.lystrosaurus.admin.shared.domain.primitive.DomainEvent;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 组织单元已创建事件。
 *
 * @param orgUnitId 组织单元ID
 * @param code 组织编码
 * @param eventId 事件唯一标识
 * @param occurredAt 事件发生时间
 * @param aggregateId 来源聚合标识
 */
public record OrgUnitCreated(
    Long orgUnitId, String code, String eventId, LocalDateTime occurredAt, String aggregateId)
    implements DomainEvent {

  /** 创建事件实例 */
  public static OrgUnitCreated of(Long orgUnitId, String code) {
    return new OrgUnitCreated(
        orgUnitId, code, UUID.randomUUID().toString(), LocalDateTime.now(), String.valueOf(orgUnitId));
  }
}
