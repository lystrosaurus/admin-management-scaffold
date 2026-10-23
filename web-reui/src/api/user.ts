import { get, post, put, del } from './client';
import type { PageResult } from '@/types/api';
import type { User, CreateUserRequest, UpdateUserRequest, UserQueryParams } from '@/types/user';

/**
 * 后端 UserVO → 前端 User 类型映射
 * status: 'ENABLED' → 'ACTIVE', 'DISABLED' → 'INACTIVE'
 */
const mapUser = (raw: any): User => ({
  id: raw.id,
  username: raw.username,
  nickname: raw.nickname,
  email: raw.email,
  phone: raw.phone,
  avatar: raw.avatar,
  status: raw.status === 'ENABLED' ? 'ACTIVE' : 'INACTIVE',
  lastLoginAt: raw.lastLoginAt,
  createdAt: raw.createdAt,
  createdBy: raw.createdBy,
  updatedAt: raw.updatedAt,
  updatedBy: raw.updatedBy,
});

/**
 * 前端 status → 后端 status 映射
 */
const toBackendStatus = (status?: string): string | undefined =>
  status === 'ACTIVE' ? 'ENABLED' : status === 'INACTIVE' ? 'DISABLED' : status;

/**
 * 获取用户列表（分页）
 */
export const listUsers = async (params?: UserQueryParams): Promise<PageResult<User>> => {
  const result = await get<PageResult<any>>('/app/users', { params });
  return { ...result, items: result.items.map(mapUser) };
};

/**
 * 获取用户详情
 */
export const getUser = async (id: number): Promise<User> => {
  const raw = await get<any>(`/app/users/${id}`);
  return mapUser(raw);
};

/**
 * 创建用户
 */
export const createUser = async (data: CreateUserRequest): Promise<User> => {
  const raw = await post<any>('/app/users', {
    ...data,
    status: toBackendStatus(data.status),
  });
  return mapUser(raw);
};

/**
 * 更新用户
 */
export const updateUser = async (id: number, data: UpdateUserRequest): Promise<User> => {
  const raw = await put<any>(`/app/users/${id}`, {
    ...data,
    status: toBackendStatus(data.status),
  });
  return mapUser(raw);
};

/**
 * 删除用户
 */
export const deleteUser = (id: number): Promise<void> => {
  return del<void>(`/app/users/${id}`);
};
