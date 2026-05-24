package io.github.lystrosaurus.admin.dashboard.vo;

/**
 * 仪表盘统计数据 VO
 *
 * @param userCount 用户总数
 * @param roleCount 角色总数
 * @param employeeCount 员工总数
 * @param orgUnitCount 组织单元总数
 */
public record DashboardVO(long userCount, long roleCount, long employeeCount, long orgUnitCount) {}
