import type { Status } from './api';

/**
 * 用户信息
 */
export interface User {
  id: number;
  username: string;
  nickname: string;
  email?: string;
  phone?: string;
  avatar?: string;
  status: Status;
  lastLoginAt?: string;
  createdAt: string;
  createdBy?: number;
  updatedAt: string;
  updatedBy?: number;
}

/**
 * 创建用户请求
 */
export interface CreateUserRequest {
  username: string;
  nickname: string;
  password: string;
  email?: string;
  phone?: string;
  status?: Status;
  roleIds?: number[];
}

/**
 * 更新用户请求
 */
export interface UpdateUserRequest {
  nickname?: string;
  email?: string;
  phone?: string;
  avatar?: string;
  status?: Status;
  roleIds?: number[];
}

/**
 * 用户查询参数
 */
export interface UserQueryParams {
  page?: number;
  size?: number;
  username?: string;
  nickname?: string;
  status?: Status;
  email?: string;
  phone?: string;
}
