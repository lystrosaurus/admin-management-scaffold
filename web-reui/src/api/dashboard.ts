import { get } from './client';

/**
 * 仪表盘统计数据（对齐后端 DashboardVO）
 */
export interface DashboardStats {
  userCount: number;
  roleCount: number;
  employeeCount: number;
  orgUnitCount: number;
}

/**
 * 获取仪表盘统计数据
 */
export const getDashboardStats = (): Promise<DashboardStats> => {
  return get<DashboardStats>('/app/dashboard/stats');
};
