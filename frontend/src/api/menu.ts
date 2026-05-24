import { get, post, put, del } from './client';
import type { Menu, CreateMenuRequest, UpdateMenuRequest, MenuQueryParams } from '@/types/menu';

/**
 * 获取菜单列表（树形结构）
 */
export const listMenus = (params?: MenuQueryParams): Promise<Menu[]> => {
  return get<Menu[]>('/app/menus', { params });
};

/**
 * 获取菜单详情
 */
export const getMenu = (id: number): Promise<Menu> => {
  return get<Menu>(`/app/menus/${id}`);
};

/**
 * 创建菜单
 */
export const createMenu = (data: CreateMenuRequest): Promise<Menu> => {
  return post<Menu>('/app/menus', data);
};

/**
 * 更新菜单
 */
export const updateMenu = (id: number, data: UpdateMenuRequest): Promise<Menu> => {
  return put<Menu>(`/app/menus/${id}`, data);
};

/**
 * 删除菜单
 */
export const deleteMenu = (id: number): Promise<void> => {
  return del<void>(`/app/menus/${id}`);
};
