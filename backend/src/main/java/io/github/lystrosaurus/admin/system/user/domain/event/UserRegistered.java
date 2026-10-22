package io.github.lystrosaurus.admin.system.user.domain.event;

import io.github.lystrosaurus.admin.shared.domain.primitive.DomainEvent;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 用户已注册事件。
 *
 * @param userId 用户ID
 * @param username 用户名
 * @param eventId 事件唯一标识
 * @param occurredAt 事件发生时间
 * @param aggregateId 来源聚合标识
 */
public record UserRegistered(
    Long userId, String username, String eventId, LocalDateTime occurredAt, String aggregateId)
    implements DomainEvent {

  /** 创建事件实例 */
  public static UserRegistered of(Long userId, String username) {
    return new UserRegistered(
        userId,
        username,
        UUID.randomUUID().toString(),
        LocalDateTime.now(),
        String.valueOf(userId));
  }
}
