package io.github.lystrosaurus.admin.dashboard.adapter.response;

/**
 * 仪表盘统计数据响应
 *
 * @param userCount 用户总数
 * @param roleCount 角色总数
 * @param employeeCount 员工总数
 * @param orgUnitCount 组织单元总数
 */
public record DashboardResponse(
    long userCount, long roleCount, long employeeCount, long orgUnitCount) {}
