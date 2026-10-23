import { get } from './client';
import type { Permission } from '@/types/role';

/**
 * 获取所有权限列表
 */
export const listPermissions = (): Promise<Permission[]> => {
  return get<Permission[]>('/app/permissions');
};

/**
 * 按角色查询权限
 */
export const getPermissionsByRoleId = (roleId: number): Promise<Permission[]> => {
  return get<Permission[]>(`/app/permissions/role/${roleId}`);
};
