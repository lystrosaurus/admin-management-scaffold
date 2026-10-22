package io.github.lystrosaurus.admin.integration.source.domain.event;

import io.github.lystrosaurus.admin.shared.domain.primitive.DomainEvent;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 外部身份源已更新事件。
 *
 * @param sourceId 外部身份源ID
 * @param eventId 事件唯一标识
 * @param occurredAt 事件发生时间
 * @param aggregateId 来源聚合标识
 */
public record ExtSourceUpdated(
    Long sourceId, String eventId, LocalDateTime occurredAt, String aggregateId)
    implements DomainEvent {

  /** 创建事件实例 */
  public static ExtSourceUpdated of(Long sourceId) {
    return new ExtSourceUpdated(
        sourceId, UUID.randomUUID().toString(), LocalDateTime.now(), String.valueOf(sourceId));
  }
}
