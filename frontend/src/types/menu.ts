import type { Status } from './api';

/**
 * 菜单类型
 */
export type MenuType = 'DIRECTORY' | 'MENU' | 'BUTTON';

/**
 * 菜单信息
 */
export interface Menu {
  id: number;
  parentId?: number;
  name: string;
  type: MenuType;
  path?: string;
  component?: string;
  icon?: string;
  permission?: string;
  sort: number;
  status: Status;
  children?: Menu[];
  createdAt: string;
  createdBy?: number;
  updatedAt: string;
  updatedBy?: number;
}

/**
 * 创建菜单请求
 */
export interface CreateMenuRequest {
  parentId?: number;
  name: string;
  type: MenuType;
  path?: string;
  component?: string;
  icon?: string;
  permission?: string;
  sort?: number;
  status?: Status;
}

/**
 * 更新菜单请求
 */
export interface UpdateMenuRequest {
  parentId?: number;
  name?: string;
  path?: string;
  component?: string;
  icon?: string;
  permission?: string;
  sort?: number;
  status?: Status;
}

/**
 * 菜单查询参数
 */
export interface MenuQueryParams {
  name?: string;
  type?: MenuType;
  status?: Status;
}
