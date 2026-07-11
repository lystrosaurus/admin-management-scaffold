import { get } from './client';
import type { PageResult } from '@/types/api';
import type {
  LoginLog,
  OperationLog,
  LoginLogQueryParams,
  OperationLogQueryParams,
} from '@/types/auditLog';

/**
 * 获取登录日志（分页）
 */
export const getLoginLogs = async (
  params?: LoginLogQueryParams,
): Promise<PageResult<LoginLog>> => {
  return get<PageResult<LoginLog>>('/app/audit/login-logs', { params });
};

/**
 * 获取操作日志（分页）
 */
export const getOperationLogs = async (
  params?: OperationLogQueryParams,
): Promise<PageResult<OperationLog>> => {
  return get<PageResult<OperationLog>>('/app/audit/operation-logs', { params });
};
