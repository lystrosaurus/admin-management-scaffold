package io.github.lystrosaurus.admin.organization.employee.domain.model;

import io.github.lystrosaurus.admin.shared.domain.exception.DomainErrorCode;

/** 员工上下文错误码：数值与旧 ErrorCode 保持一致。 */
public enum EmployeeErrorCode implements DomainErrorCode {
  EMPLOYEE_NOT_FOUND(7001, "员工不存在"),
  EMPLOYEE_ALREADY_EXISTS(7002, "员工已存在");

  private final int code;
  private final String message;

  EmployeeErrorCode(int code, String message) {
    this.code = code;
    this.message = message;
  }

  @Override
  public int code() {
    return code;
  }

  @Override
  public String message() {
    return message;
  }
}
