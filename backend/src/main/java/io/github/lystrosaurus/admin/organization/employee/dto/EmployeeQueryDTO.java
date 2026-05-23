package io.github.lystrosaurus.admin.organization.employee.dto;

/**
 * 员工查询DTO
 *
 * @param keyword 关键词(模糊匹配姓名/工号/手机号)
 * @param employmentStatus 在职状态
 * @param primaryOrgId 主组织ID
 */
public record EmployeeQueryDTO(String keyword, String employmentStatus, Long primaryOrgId) {}
