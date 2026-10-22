package io.github.lystrosaurus.admin.system.menu.domain.event;

import io.github.lystrosaurus.admin.shared.domain.primitive.DomainEvent;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 菜单已创建事件。
 *
 * @param menuId 菜单ID
 * @param name 菜单名称
 * @param eventId 事件唯一标识
 * @param occurredAt 事件发生时间
 * @param aggregateId 来源聚合标识
 */
public record MenuCreated(
    Long menuId, String name, String eventId, LocalDateTime occurredAt, String aggregateId)
    implements DomainEvent {

  /** 创建事件实例 */
  public static MenuCreated of(Long menuId, String name) {
    return new MenuCreated(
        menuId, name, UUID.randomUUID().toString(), LocalDateTime.now(), String.valueOf(menuId));
  }
}
