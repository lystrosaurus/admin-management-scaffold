package io.github.lystrosaurus.admin.organization.employee.domain.model;

import java.time.LocalDate;

/**
 * 员工-组织关联（读模型）。
 *
 * @param id 关联ID
 * @param orgId 组织ID
 * @param isPrimary 是否主组织
 * @param positionName 岗位名称
 * @param startDate 开始日期
 * @param endDate 结束日期
 * @param status 状态
 */
public record EmployeeOrgAssignment(
    Long id,
    Long orgId,
    Integer isPrimary,
    String positionName,
    LocalDate startDate,
    LocalDate endDate,
    String status) {}
