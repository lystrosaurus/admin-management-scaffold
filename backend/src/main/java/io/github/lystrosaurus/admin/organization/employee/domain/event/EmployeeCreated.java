package io.github.lystrosaurus.admin.organization.employee.domain.event;

import io.github.lystrosaurus.admin.shared.domain.primitive.DomainEvent;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 员工已创建事件。
 *
 * @param employeeId 员工ID
 * @param employeeNo 员工工号
 * @param eventId 事件唯一标识
 * @param occurredAt 事件发生时间
 * @param aggregateId 来源聚合标识
 */
public record EmployeeCreated(
    Long employeeId,
    String employeeNo,
    String eventId,
    LocalDateTime occurredAt,
    String aggregateId)
    implements DomainEvent {

  /** 创建事件实例 */
  public static EmployeeCreated of(Long employeeId, String employeeNo) {
    return new EmployeeCreated(
        employeeId,
        employeeNo,
        UUID.randomUUID().toString(),
        LocalDateTime.now(),
        String.valueOf(employeeId));
  }
}
