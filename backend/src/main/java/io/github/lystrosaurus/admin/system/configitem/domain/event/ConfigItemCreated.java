package io.github.lystrosaurus.admin.system.configitem.domain.event;

import io.github.lystrosaurus.admin.shared.domain.primitive.DomainEvent;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 配置项已创建事件。
 *
 * @param configItemId 配置项ID
 * @param configKey 配置键
 * @param eventId 事件唯一标识
 * @param occurredAt 事件发生时间
 * @param aggregateId 来源聚合标识
 */
public record ConfigItemCreated(
    Long configItemId,
    String configKey,
    String eventId,
    LocalDateTime occurredAt,
    String aggregateId)
    implements DomainEvent {

  /** 创建事件实例 */
  public static ConfigItemCreated of(Long configItemId, String configKey) {
    return new ConfigItemCreated(
        configItemId,
        configKey,
        UUID.randomUUID().toString(),
        LocalDateTime.now(),
        String.valueOf(configItemId));
  }
}
