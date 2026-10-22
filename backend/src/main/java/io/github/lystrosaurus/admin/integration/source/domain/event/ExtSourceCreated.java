package io.github.lystrosaurus.admin.integration.source.domain.event;

import io.github.lystrosaurus.admin.shared.domain.primitive.DomainEvent;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 外部身份源已创建事件。
 *
 * @param sourceId 外部身份源ID
 * @param code 系统编码
 * @param eventId 事件唯一标识
 * @param occurredAt 事件发生时间
 * @param aggregateId 来源聚合标识
 */
public record ExtSourceCreated(
    Long sourceId, String code, String eventId, LocalDateTime occurredAt, String aggregateId)
    implements DomainEvent {

  /** 创建事件实例 */
  public static ExtSourceCreated of(Long sourceId, String code) {
    return new ExtSourceCreated(
        sourceId, code, UUID.randomUUID().toString(), LocalDateTime.now(), String.valueOf(sourceId));
  }
}
