package io.github.lystrosaurus.admin.organization.employee.application.command;

import java.time.LocalDate;

/**
 * 员工创建命令。
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
public record EmployeeCreateCommand(
    String employeeNo,
    String name,
    String mobile,
    String email,
    String jobTitle,
    Long primaryOrgId,
    LocalDate entryDate,
    Long userId) {}
