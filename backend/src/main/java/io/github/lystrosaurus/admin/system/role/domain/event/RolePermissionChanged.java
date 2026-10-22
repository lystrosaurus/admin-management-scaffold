package io.github.lystrosaurus.admin.system.role.domain.event;

import io.github.lystrosaurus.admin.shared.domain.primitive.DomainEvent;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 角色权限已变更事件（波次7 用于权限缓存失效）。
 *
 * @param roleId 角色ID
 * @param permissionIds 变更后的权限ID列表
 * @param eventId 事件唯一标识
 * @param occurredAt 事件发生时间
 * @param aggregateId 来源聚合标识
 */
public record RolePermissionChanged(
    Long roleId,
    List<Long> permissionIds,
    String eventId,
    LocalDateTime occurredAt,
    String aggregateId)
    implements DomainEvent {

  /** 创建事件实例 */
  public static RolePermissionChanged of(Long roleId, List<Long> permissionIds) {
    return new RolePermissionChanged(
        roleId,
        permissionIds == null ? List.of() : List.copyOf(permissionIds),
        UUID.randomUUID().toString(),
        LocalDateTime.now(),
        String.valueOf(roleId));
  }
}
