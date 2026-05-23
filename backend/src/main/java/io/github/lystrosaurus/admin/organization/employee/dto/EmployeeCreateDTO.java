package io.github.lystrosaurus.admin.organization.employee.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

/**
 * 员工创建DTO
 *
 * @param employeeNo 员工工号
 * @param name 员工姓名
 * @param mobile 手机号
 * @param email 邮箱
 * @param jobTitle 职位
 * @param primaryOrgId 主组织ID
 * @param entryDate 入职日期
 */
public record EmployeeCreateDTO(
    @NotBlank(message = "员工工号不能为空") String employeeNo,
    @NotBlank(message = "员工姓名不能为空") String name,
    String mobile,
    String email,
    String jobTitle,
    Long primaryOrgId,
    LocalDate entryDate) {}
