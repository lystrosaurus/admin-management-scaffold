import { get, post, put, del } from './client';
import type { PageResult } from '@/types/api';
import type { User, CreateUserRequest, UpdateUserRequest, UserQueryParams } from '@/types/user';

/**
 * 获取用户列表（分页）
 */
export const listUsers = (params?: UserQueryParams): Promise<PageResult<User>> => {
  return get<PageResult<User>>('/app/users', { params });
};

/**
 * 获取用户详情
 */
export const getUser = (id: number): Promise<User> => {
  return get<User>(`/app/users/${id}`);
};

/**
 * 创建用户
 */
export const createUser = (data: CreateUserRequest): Promise<User> => {
  return post<User>('/app/users', data);
};

/**
 * 更新用户
 */
export const updateUser = (id: number, data: UpdateUserRequest): Promise<User> => {
  return put<User>(`/app/users/${id}`, data);
};

/**
 * 删除用户
 */
export const deleteUser = (id: number): Promise<void> => {
  return del<void>(`/app/users/${id}`);
};
