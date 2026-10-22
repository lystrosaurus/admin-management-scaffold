package io.github.lystrosaurus.admin.system.user.domain.event;

import io.github.lystrosaurus.admin.shared.domain.primitive.DomainEvent;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 用户已禁用事件。
 *
 * @param userId 用户ID
 * @param eventId 事件唯一标识
 * @param occurredAt 事件发生时间
 * @param aggregateId 来源聚合标识
 */
public record UserDisabled(
    Long userId, String eventId, LocalDateTime occurredAt, String aggregateId)
    implements DomainEvent {

  /** 创建事件实例 */
  public static UserDisabled of(Long userId) {
    return new UserDisabled(
        userId, UUID.randomUUID().toString(), LocalDateTime.now(), String.valueOf(userId));
  }
}
