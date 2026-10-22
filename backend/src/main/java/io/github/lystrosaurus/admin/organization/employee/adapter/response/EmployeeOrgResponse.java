package io.github.lystrosaurus.admin.organization.employee.adapter.response;

import java.time.LocalDate;

/**
 * 员工组织关联响应（字段集合与原 EmployeeOrgVO 完全一致）。
 *
 * @param id 关联ID
 * @param orgId 组织ID
 * @param orgName 组织名称
 * @param isPrimary 是否主组织
 * @param positionName 岗位名称
 * @param startDate 开始日期
 * @param endDate 结束日期
 * @param status 状态
 */
public record EmployeeOrgResponse(
    Long id,
    Long orgId,
    String orgName,
    Integer isPrimary,
    String positionName,
    LocalDate startDate,
    LocalDate endDate,
    String status) {}
