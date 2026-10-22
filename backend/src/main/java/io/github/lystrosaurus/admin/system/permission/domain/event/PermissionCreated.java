package io.github.lystrosaurus.admin.system.permission.domain.event;

import io.github.lystrosaurus.admin.shared.domain.primitive.DomainEvent;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 权限已创建事件。
 *
 * @param permissionId 权限ID
 * @param code 权限编码
 * @param eventId 事件唯一标识
 * @param occurredAt 事件发生时间
 * @param aggregateId 来源聚合标识
 */
public record PermissionCreated(
    Long permissionId, String code, String eventId, LocalDateTime occurredAt, String aggregateId)
    implements DomainEvent {

  /** 创建事件实例 */
  public static PermissionCreated of(Long permissionId, String code) {
    return new PermissionCreated(
        permissionId,
        code,
        UUID.randomUUID().toString(),
        LocalDateTime.now(),
        String.valueOf(permissionId));
  }
}
