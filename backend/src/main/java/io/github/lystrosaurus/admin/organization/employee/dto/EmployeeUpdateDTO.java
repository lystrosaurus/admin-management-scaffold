package io.github.lystrosaurus.admin.organization.employee.dto;

import java.time.LocalDate;

/**
 * 员工更新DTO(所有字段可选，支持部分更新)
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
 */
public record EmployeeUpdateDTO(
    String name,
    String preferredName,
    String mobile,
    String email,
    String jobTitle,
    Long primaryOrgId,
    String employmentStatus,
    LocalDate entryDate,
    LocalDate leaveDate) {}
