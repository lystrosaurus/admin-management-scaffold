package io.github.lystrosaurus.admin.organization.employee.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 员工详情VO(含组织信息)
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
 * @param orgs 员工组织关联列表
 */
public record EmployeeDetailVO(
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
    LocalDateTime createdAt,
    List<EmployeeOrgVO> orgs) {}
