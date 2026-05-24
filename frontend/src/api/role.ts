import { get, post, put, del } from './client';
import type { Role, CreateRoleRequest, UpdateRoleRequest, RoleQueryParams } from '@/types/role';

/** 后端响应 → 前端 Role 类型 */
const mapRole = (raw: any): Role => ({
  id: raw.id,
  code: raw.code,
  name: raw.name,
  description: raw.description,
  status: raw.status === 'ENABLED' ? 'ACTIVE' : 'INACTIVE',
  createdAt: raw.createdAt,
  createdBy: raw.createdBy,
  updatedAt: raw.updatedAt,
  updatedBy: raw.updatedBy,
});

/** 前端请求 → 后端请求格式（创建） */
const toBackendCreate = (data: CreateRoleRequest) => ({
  code: data.code,
  name: data.name,
  description: data.description,
});

/** 前端请求 → 后端请求格式（更新） */
const toBackendUpdate = (data: UpdateRoleRequest) => {
  const result: Record<string, unknown> = {};
  if (data.name !== undefined) result.name = data.name;
  if (data.description !== undefined) result.description = data.description;
  if (data.status !== undefined) result.status = data.status === 'ACTIVE' ? 'ENABLED' : 'DISABLED';
  return result;
};

/**
 * 获取角色列表（不分页）
 */
export const listRoles = (params?: RoleQueryParams): Promise<Role[]> => {
  return get<any[]>('/app/roles', { params }).then(items => items.map(mapRole));
};

/**
 * 获取角色详情
 */
export const getRole = (id: number): Promise<Role> => {
  return get<any>(`/app/roles/${id}`).then(mapRole);
};

/**
 * 创建角色
 */
export const createRole = (data: CreateRoleRequest): Promise<Role> => {
  return post<any>('/app/roles', toBackendCreate(data)).then(mapRole);
};

/**
 * 更新角色
 */
export const updateRole = (id: number, data: UpdateRoleRequest): Promise<Role> => {
  return put<any>(`/app/roles/${id}`, toBackendUpdate(data)).then(mapRole);
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
