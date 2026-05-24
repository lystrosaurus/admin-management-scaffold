import type { Status } from './api';

/**
 * 角色信息
 */
export interface Role {
  id: number;
  code: string;
  name: string;
  description?: string;
  status: Status;
  createdAt: string;
  createdBy?: number;
  updatedAt: string;
  updatedBy?: number;
}

/**
 * 创建角色请求
 */
export interface CreateRoleRequest {
  code: string;
  name: string;
  description?: string;
  status?: Status;
  permissionIds?: number[];
}

/**
 * 更新角色请求
 */
export interface UpdateRoleRequest {
  name?: string;
  description?: string;
  status?: Status;
  permissionIds?: number[];
}

/**
 * 角色查询参数
 */
export interface RoleQueryParams {
  current?: number;
  size?: number;
  code?: string;
  name?: string;
  status?: Status;
}

/**
 * 角色权限分配请求
 */
export interface AssignPermissionsRequest {
  permissionIds: number[];
}

/**
 * 权限信息
 */
export interface Permission {
  id: number;
  code: string;
  name: string;
  description?: string;
  type: 'MENU' | 'BUTTON' | 'API';
  status: Status;
}
