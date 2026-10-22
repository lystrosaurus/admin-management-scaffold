package io.github.lystrosaurus.admin.system.user.adapter.request;

import io.github.lystrosaurus.admin.system.user.application.command.BindEmployeeCommand;
import jakarta.validation.constraints.NotNull;

/**
 * 员工绑定请求。
 *
 * @param employeeId 员工ID
 */
public record EmployeeBindRequest(@NotNull(message = "员工ID不能为空") Long employeeId) {

  /** 转绑定命令 */
  public BindEmployeeCommand toCommand() {
    return new BindEmployeeCommand(employeeId);
  }
}
