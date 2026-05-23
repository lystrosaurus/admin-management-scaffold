package io.github.lystrosaurus.admin.organization.employee.vo;

import java.time.LocalDate;

/**
 * 员工组织关联VO
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
public record EmployeeOrgVO(
    Long id,
    Long orgId,
    String orgName,
    Integer isPrimary,
    String positionName,
    LocalDate startDate,
    LocalDate endDate,
    String status) {}
