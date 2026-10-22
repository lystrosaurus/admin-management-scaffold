package io.github.lystrosaurus.admin.integration.principal.domain.event;

import io.github.lystrosaurus.admin.shared.domain.primitive.DomainEvent;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 外部主体已创建事件。
 *
 * @param principalId 外部主体ID
 * @param sourceId 外部系统ID
 * @param externalKey 外部系统主键
 * @param eventId 事件唯一标识
 * @param occurredAt 事件发生时间
 * @param aggregateId 来源聚合标识
 */
public record ExtPrincipalCreated(
    Long principalId,
    Long sourceId,
    String externalKey,
    String eventId,
    LocalDateTime occurredAt,
    String aggregateId)
    implements DomainEvent {

  /** 创建事件实例 */
  public static ExtPrincipalCreated of(Long principalId, Long sourceId, String externalKey) {
    return new ExtPrincipalCreated(
        principalId,
        sourceId,
        externalKey,
        UUID.randomUUID().toString(),
        LocalDateTime.now(),
        String.valueOf(principalId));
  }
}
