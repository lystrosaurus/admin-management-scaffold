package io.github.lystrosaurus.admin.integration.principal.domain.event;

import io.github.lystrosaurus.admin.shared.domain.primitive.DomainEvent;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 外部主体已更新事件。
 *
 * @param principalId 外部主体ID
 * @param eventId 事件唯一标识
 * @param occurredAt 事件发生时间
 * @param aggregateId 来源聚合标识
 */
public record ExtPrincipalUpdated(
    Long principalId, String eventId, LocalDateTime occurredAt, String aggregateId)
    implements DomainEvent {

  /** 创建事件实例 */
  public static ExtPrincipalUpdated of(Long principalId) {
    return new ExtPrincipalUpdated(
        principalId, UUID.randomUUID().toString(), LocalDateTime.now(), String.valueOf(principalId));
  }
}
