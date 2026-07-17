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
 * @param userId 关联系统用户ID
 * @param username 关联用户名（由 Service 解析，非持久化字段）
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
    String orgUnitName,
    LocalDate entryDate,
    LocalDate leaveDate,
    LocalDateTime createdAt,
    Long userId,
    String username) {}
