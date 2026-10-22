package io.github.lystrosaurus.admin.organization.employee.adapter.request;

import io.github.lystrosaurus.admin.organization.employee.application.command.EmployeeUpdateCommand;
import java.time.LocalDate;

/**
 * 员工更新请求（字段与原 EmployeeUpdateDTO 一致）。
 *
 * @param name 员工姓名
 * @param preferredName 常用名/花名
 * @param mobile 手机号
 * @param email 邮箱
 * @param jobTitle 职位
 * @param primaryOrgId 主组织ID
 * @param employmentStatus 在职状态
 * @param entryDate 入职日期
 * @param leaveDate 离职日期
 * @param userId 关联系统用户ID（可空）
 */
public record EmployeeUpdateRequest(
    String name,
    String preferredName,
    String mobile,
    String email,
    String jobTitle,
    Long primaryOrgId,
    String employmentStatus,
    LocalDate entryDate,
    LocalDate leaveDate,
    Long userId) {

  /** 转为应用层命令 */
  public EmployeeUpdateCommand toCommand() {
    return new EmployeeUpdateCommand(
        name, preferredName, mobile, email, jobTitle, primaryOrgId, employmentStatus, entryDate, leaveDate, userId);
  }
}
