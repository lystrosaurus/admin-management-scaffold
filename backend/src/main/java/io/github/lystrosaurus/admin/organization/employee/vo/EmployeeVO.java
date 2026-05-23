package io.github.lystrosaurus.admin.organization.employee.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 员工VO
 *
 * @param id 主键ID
 * @param employeeNo 员工工号
 * @param name 员工姓名
 * @param preferredName 常用名/花名
 * @param mobile 手机号
 * @param email 邮箱
 * @param jobTitle 职位
 * @param employmentStatus 在职状态
 * @param primaryOrgId 主组织ID
 * @param entryDate 入职日期
 * @param leaveDate 离职日期
 * @param createdAt 创建时间
 */
public record EmployeeVO(
    Long id,
    String employeeNo,
    String name,
    String preferredName,
    String mobile,
    String email,
    String jobTitle,
    String employmentStatus,
    Long primaryOrgId,
    LocalDate entryDate,
    LocalDate leaveDate,
    LocalDateTime createdAt) {}
