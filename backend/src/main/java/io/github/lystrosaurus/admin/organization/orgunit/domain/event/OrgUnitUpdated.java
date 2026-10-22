package io.github.lystrosaurus.admin.organization.orgunit.domain.event;

import io.github.lystrosaurus.admin.shared.domain.primitive.DomainEvent;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 组织单元已更新事件。
 *
 * @param orgUnitId 组织单元ID
 * @param eventId 事件唯一标识
 * @param occurredAt 事件发生时间
 * @param aggregateId 来源聚合标识
 */
public record OrgUnitUpdated(
    Long orgUnitId, String eventId, LocalDateTime occurredAt, String aggregateId)
    implements DomainEvent {

  /** 创建事件实例 */
  public static OrgUnitUpdated of(Long orgUnitId) {
    return new OrgUnitUpdated(
        orgUnitId, UUID.randomUUID().toString(), LocalDateTime.now(), String.valueOf(orgUnitId));
  }
}
