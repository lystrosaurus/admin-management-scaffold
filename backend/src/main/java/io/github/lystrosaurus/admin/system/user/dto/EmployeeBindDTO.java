package io.github.lystrosaurus.admin.system.user.dto;

import jakarta.validation.constraints.NotNull;

/**
 * 员工绑定DTO
 *
 * @param employeeId 员工ID
 */
public record EmployeeBindDTO(@NotNull(message = "员工ID不能为空") Long employeeId) {}
