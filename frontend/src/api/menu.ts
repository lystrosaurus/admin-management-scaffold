import { get, post, put, del } from './client';
import type { Menu, CreateMenuRequest, UpdateMenuRequest, MenuQueryParams } from '@/types/menu';

/** 后端菜单类型: 1=目录, 2=菜单, 3=按钮 */
const typeMap: Record<number, 'DIRECTORY' | 'MENU' | 'BUTTON'> = {
  1: 'DIRECTORY',
  2: 'MENU',
  3: 'BUTTON',
};

/** 前端菜单类型 → 后端数字 */
const reverseTypeMap: Record<string, number> = {
  DIRECTORY: 1,
  MENU: 2,
  BUTTON: 3,
};

/** 后端 MenuResponse 返回结构 */
interface RawMenuVO {
  id: number;
  parentId?: number;
  name: string;
  path?: string;
  component?: string;
  icon?: string;
  sortOrder?: number;
  type: number;
  permissionCode?: string;
  visible?: number;
  status: number;
  children?: RawMenuVO[];
  createdAt: string;
  createdBy?: number;
  updatedAt: string;
  updatedBy?: number;
}

/** 后端响应 → 前端 Menu 类型 */
const mapMenu = (raw: RawMenuVO): Menu => ({
  id: raw.id,
  parentId: raw.parentId,
  name: raw.name,
  path: raw.path,
  component: raw.component,
  icon: raw.icon,
  sort: raw.sortOrder ?? 0,
  type: typeMap[raw.type] ?? 'DIRECTORY',
  permission: raw.permissionCode,
  status: raw.status === 1 ? 'ACTIVE' : 'INACTIVE',
  createdAt: raw.createdAt,
  createdBy: raw.createdBy,
  updatedAt: raw.updatedAt,
  updatedBy: raw.updatedBy,
  children: raw.children?.map(mapMenu),
});

/** 前端请求 → 后端请求格式 */
const toBackendMenu = (data: CreateMenuRequest | UpdateMenuRequest) => {
  const { sort, permission, type, status, ...rest } = data as CreateMenuRequest & UpdateMenuRequest;
  return {
    ...rest,
    ...(sort !== undefined && { sortOrder: sort }),
    ...(permission !== undefined && { permissionCode: permission }),
    ...(type !== undefined && { type: reverseTypeMap[type] ?? 1 }),
    ...(status !== undefined && { status: status === 'ACTIVE' ? 1 : 0 }),
  };
};

/**
 * 获取菜单列表（树形结构）
 */
export const listMenus = (params?: MenuQueryParams): Promise<Menu[]> => {
  return get<RawMenuVO[]>('/app/menus/tree', { params }).then(items => items.map(mapMenu));
};

/**
 * 获取菜单详情
 */
export const getMenu = (id: number): Promise<Menu> => {
  return get<RawMenuVO>(`/app/menus/${id}`).then(mapMenu);
};

/**
 * 创建菜单
 */
export const createMenu = (data: CreateMenuRequest): Promise<Menu> => {
  return post<RawMenuVO>('/app/menus', toBackendMenu(data)).then(mapMenu);
};

/**
 * 更新菜单
 */
export const updateMenu = (id: number, data: UpdateMenuRequest): Promise<Menu> => {
  return put<RawMenuVO>(`/app/menus/${id}`, toBackendMenu(data)).then(mapMenu);
};

/**
 * 删除菜单
 */
export const deleteMenu = (id: number): Promise<void> => {
  return del<void>(`/app/menus/${id}`);
};
