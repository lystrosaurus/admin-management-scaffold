package io.github.lystrosaurus.admin.system.user.domain.event;

import io.github.lystrosaurus.admin.shared.domain.primitive.DomainEvent;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 用户已解绑员工事件。
 *
 * @param userId 用户ID
 * @param employeeId 原绑定的员工ID
 * @param eventId 事件唯一标识
 * @param occurredAt 事件发生时间
 * @param aggregateId 来源聚合标识
 */
public record UserEmployeeUnbound(
    Long userId, Long employeeId, String eventId, LocalDateTime occurredAt, String aggregateId)
    implements DomainEvent {

  /** 创建事件实例 */
  public static UserEmployeeUnbound of(Long userId, Long employeeId) {
    return new UserEmployeeUnbound(
        userId,
        employeeId,
        UUID.randomUUID().toString(),
        LocalDateTime.now(),
        String.valueOf(userId));
  }
}
