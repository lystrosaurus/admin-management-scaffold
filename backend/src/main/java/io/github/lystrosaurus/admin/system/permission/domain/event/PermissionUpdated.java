package io.github.lystrosaurus.admin.system.permission.domain.event;

import io.github.lystrosaurus.admin.shared.domain.primitive.DomainEvent;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 权限已更新事件。
 *
 * @param permissionId 权限ID
 * @param eventId 事件唯一标识
 * @param occurredAt 事件发生时间
 * @param aggregateId 来源聚合标识
 */
public record PermissionUpdated(
    Long permissionId, String eventId, LocalDateTime occurredAt, String aggregateId)
    implements DomainEvent {

  /** 创建事件实例 */
  public static PermissionUpdated of(Long permissionId) {
    return new PermissionUpdated(
        permissionId,
        UUID.randomUUID().toString(),
        LocalDateTime.now(),
        String.valueOf(permissionId));
  }
}
