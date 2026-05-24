import { get, post, put, del } from './client';
import type { PageResult } from '@/types/api';
import type { Role, CreateRoleRequest, UpdateRoleRequest, RoleQueryParams } from '@/types/role';

/**
 * 获取角色列表（分页）
 */
export const listRoles = (params?: RoleQueryParams): Promise<PageResult<Role>> => {
  return get<PageResult<Role>>('/app/roles', { params });
};

/**
 * 获取角色详情
 */
export const getRole = (id: number): Promise<Role> => {
  return get<Role>(`/app/roles/${id}`);
};

/**
 * 创建角色
 */
export const createRole = (data: CreateRoleRequest): Promise<Role> => {
  return post<Role>('/app/roles', data);
};

/**
 * 更新角色
 */
export const updateRole = (id: number, data: UpdateRoleRequest): Promise<Role> => {
  return put<Role>(`/app/roles/${id}`, data);
};

/**
 * 删除角色
 */
export const deleteRole = (id: number): Promise<void> => {
  return del<void>(`/app/roles/${id}`);
};

/**
 * 分配权限给角色
 */
export const assignPermissions = (roleId: number, permissionIds: number[]): Promise<void> => {
  return post<void>(`/app/roles/${roleId}/permissions`, { permissionIds });
};
