package io.github.lystrosaurus.admin.system.role.domain.event;

import io.github.lystrosaurus.admin.shared.domain.primitive.DomainEvent;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 角色已创建事件。
 *
 * @param roleId 角色ID
 * @param code 角色编码
 * @param eventId 事件唯一标识
 * @param occurredAt 事件发生时间
 * @param aggregateId 来源聚合标识
 */
public record RoleCreated(
    Long roleId, String code, String eventId, LocalDateTime occurredAt, String aggregateId)
    implements DomainEvent {

  /** 创建事件实例 */
  public static RoleCreated of(Long roleId, String code) {
    return new RoleCreated(
        roleId, code, UUID.randomUUID().toString(), LocalDateTime.now(), String.valueOf(roleId));
  }
}
