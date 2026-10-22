package io.github.lystrosaurus.admin.system.role.domain.event;

import io.github.lystrosaurus.admin.shared.domain.primitive.DomainEvent;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 角色菜单已变更事件（波次7 用于菜单缓存失效）。
 *
 * @param roleId 角色ID
 * @param menuIds 变更后的菜单ID列表
 * @param eventId 事件唯一标识
 * @param occurredAt 事件发生时间
 * @param aggregateId 来源聚合标识
 */
public record RoleMenuChanged(
    Long roleId, List<Long> menuIds, String eventId, LocalDateTime occurredAt, String aggregateId)
    implements DomainEvent {

  /** 创建事件实例 */
  public static RoleMenuChanged of(Long roleId, List<Long> menuIds) {
    return new RoleMenuChanged(
        roleId,
        menuIds == null ? List.of() : List.copyOf(menuIds),
        UUID.randomUUID().toString(),
        LocalDateTime.now(),
        String.valueOf(roleId));
  }
}
