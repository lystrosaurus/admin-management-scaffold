package io.github.lystrosaurus.admin.system.role.domain.event;

import io.github.lystrosaurus.admin.shared.domain.primitive.DomainEvent;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 角色已更新事件。
 *
 * @param roleId 角色ID
 * @param eventId 事件唯一标识
 * @param occurredAt 事件发生时间
 * @param aggregateId 来源聚合标识
 */
public record RoleUpdated(
    Long roleId, String eventId, LocalDateTime occurredAt, String aggregateId)
    implements DomainEvent {

  /** 创建事件实例 */
  public static RoleUpdated of(Long roleId) {
    return new RoleUpdated(
        roleId, UUID.randomUUID().toString(), LocalDateTime.now(), String.valueOf(roleId));
  }
}
