package io.github.lystrosaurus.admin.integration.identitylink.domain.event;

import io.github.lystrosaurus.admin.shared.domain.primitive.DomainEvent;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 身份匹配候选已创建事件。
 *
 * @param candidateId 候选ID
 * @param sourcePrincipalId 外部主体ID
 * @param eventId 事件唯一标识
 * @param occurredAt 事件发生时间
 * @param aggregateId 来源聚合标识
 */
public record IdentityLinkCandidateCreated(
    Long candidateId,
    Long sourcePrincipalId,
    String eventId,
    LocalDateTime occurredAt,
    String aggregateId)
    implements DomainEvent {

  /** 创建事件实例 */
  public static IdentityLinkCandidateCreated of(Long candidateId, Long sourcePrincipalId) {
    return new IdentityLinkCandidateCreated(
        candidateId,
        sourcePrincipalId,
        UUID.randomUUID().toString(),
        LocalDateTime.now(),
        String.valueOf(candidateId));
  }
}
