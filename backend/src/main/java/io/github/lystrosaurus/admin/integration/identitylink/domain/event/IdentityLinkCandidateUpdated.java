package io.github.lystrosaurus.admin.integration.identitylink.domain.event;

import io.github.lystrosaurus.admin.shared.domain.primitive.DomainEvent;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 身份匹配候选已处理事件。
 *
 * @param candidateId 候选ID
 * @param status 处理后的状态
 * @param handledBy 处理人
 * @param eventId 事件唯一标识
 * @param occurredAt 事件发生时间
 * @param aggregateId 来源聚合标识
 */
public record IdentityLinkCandidateUpdated(
    Long candidateId,
    String status,
    String handledBy,
    String eventId,
    LocalDateTime occurredAt,
    String aggregateId)
    implements DomainEvent {

  /** 创建事件实例 */
  public static IdentityLinkCandidateUpdated of(Long candidateId, String status, String handledBy) {
    return new IdentityLinkCandidateUpdated(
        candidateId,
        status,
        handledBy,
        UUID.randomUUID().toString(),
        LocalDateTime.now(),
        String.valueOf(candidateId));
  }
}
