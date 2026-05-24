import { get, post, put, del } from './client';
import type { OrgUnit, CreateOrgUnitRequest, UpdateOrgUnitRequest, OrgUnitQueryParams, OrgUnitType } from '@/types/orgUnit';

/** 根据后端 level 推断组织类型: 1=公司, 2=部门, 3+=团队 */
const inferType = (level?: number): OrgUnitType => {
  if (!level || level <= 1) return 'COMPANY';
  if (level === 2) return 'DEPARTMENT';
  return 'TEAM';
};

/** 后端响应 → 前端 OrgUnit 类型（适用于 TreeVO 和普通 VO） */
const mapOrgUnit = (raw: any): OrgUnit => ({
  id: raw.id,
  parentId: raw.parentId,
  name: raw.name,
  code: raw.code,
  type: inferType(raw.level),
  leaderId: raw.managerEmployeeId,
  leaderName: raw.leaderName,
  sort: raw.sortOrder ?? 0,
  status: raw.status === 'ENABLED' ? 'ACTIVE' : 'INACTIVE',
  createdAt: raw.createdAt,
  createdBy: raw.createdBy,
  updatedAt: raw.updatedAt,
  updatedBy: raw.updatedBy,
  children: raw.children?.map(mapOrgUnit),
});

/** 前端请求 → 后端请求格式（创建） */
const toBackendCreate = (data: CreateOrgUnitRequest) => ({
  code: data.code,
  name: data.name,
  parentId: data.parentId,
  managerEmployeeId: data.leaderId,
  sortOrder: data.sort,
});

/** 前端请求 → 后端请求格式（更新） */
const toBackendUpdate = (data: UpdateOrgUnitRequest) => {
  const result: Record<string, unknown> = {};
  if (data.name !== undefined) result.name = data.name;
  if (data.parentId !== undefined) result.parentId = data.parentId;
  if (data.leaderId !== undefined) result.managerEmployeeId = data.leaderId;
  if (data.sort !== undefined) result.sortOrder = data.sort;
  if (data.status !== undefined) result.status = data.status === 'ACTIVE' ? 'ENABLED' : 'DISABLED';
  return result;
};

/**
 * 获取组织单元列表（树形结构）
 * 调用后端 /tree 接口获取树形数据
 */
export const listOrgUnits = (params?: OrgUnitQueryParams): Promise<OrgUnit[]> => {
  return get<any[]>('/app/org-units/tree', { params }).then(items => items.map(mapOrgUnit));
};

/**
 * 获取组织单元详情
 */
export const getOrgUnit = (id: number): Promise<OrgUnit> => {
  return get<any>(`/app/org-units/${id}`).then(mapOrgUnit);
};

/**
 * 创建组织单元
 */
export const createOrgUnit = (data: CreateOrgUnitRequest): Promise<OrgUnit> => {
  return post<any>('/app/org-units', toBackendCreate(data)).then(mapOrgUnit);
};

/**
 * 更新组织单元
 */
export const updateOrgUnit = (id: number, data: UpdateOrgUnitRequest): Promise<OrgUnit> => {
  return put<any>(`/app/org-units/${id}`, toBackendUpdate(data)).then(mapOrgUnit);
};

/**
 * 删除组织单元
 */
export const deleteOrgUnit = (id: number): Promise<void> => {
  return del<void>(`/app/org-units/${id}`);
};
