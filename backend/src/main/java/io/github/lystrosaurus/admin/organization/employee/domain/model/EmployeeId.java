package io.github.lystrosaurus.admin.organization.employee.domain.model;

import io.github.lystrosaurus.admin.shared.domain.primitive.Identifier;

/**
 * 员工标识。
 *
 * @param value 标识值
 */
public record EmployeeId(Long value) implements Identifier {

  /** 按值创建员工标识 */
  public static EmployeeId of(Long value) {
    return new EmployeeId(value);
  }
}
