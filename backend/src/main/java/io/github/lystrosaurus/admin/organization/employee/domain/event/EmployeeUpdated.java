package io.github.lystrosaurus.admin.organization.employee.domain.event;

import io.github.lystrosaurus.admin.shared.domain.primitive.DomainEvent;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 员工已更新事件。
 *
 * @param employeeId 员工ID
 * @param eventId 事件唯一标识
 * @param occurredAt 事件发生时间
 * @param aggregateId 来源聚合标识
 */
public record EmployeeUpdated(
    Long employeeId, String eventId, LocalDateTime occurredAt, String aggregateId)
    implements DomainEvent {

  /** 创建事件实例 */
  public static EmployeeUpdated of(Long employeeId) {
    return new EmployeeUpdated(
        employeeId, UUID.randomUUID().toString(), LocalDateTime.now(), String.valueOf(employeeId));
  }
}
