package io.github.lystrosaurus.admin.organization.employee.adapter.request;

import io.github.lystrosaurus.admin.organization.employee.application.command.EmployeeCreateCommand;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

/**
 * 员工创建请求（字段与原 EmployeeCreateDTO 一致）。
 *
 * @param employeeNo 员工工号
 * @param name 员工姓名
 * @param mobile 手机号
 * @param email 邮箱
 * @param jobTitle 职位
 * @param primaryOrgId 主组织ID
 * @param entryDate 入职日期
 * @param userId 关联系统用户ID（可空）
 */
public record EmployeeCreateRequest(
    @NotBlank(message = "员工工号不能为空") String employeeNo,
    @NotBlank(message = "员工姓名不能为空") String name,
    String mobile,
    String email,
    String jobTitle,
    Long primaryOrgId,
    LocalDate entryDate,
    Long userId) {

  /** 转为应用层命令 */
  public EmployeeCreateCommand toCommand() {
    return new EmployeeCreateCommand(
        employeeNo, name, mobile, email, jobTitle, primaryOrgId, entryDate, userId);
  }
}
