import { get, post, put, del } from './client';
import type { OrgUnit, CreateOrgUnitRequest, UpdateOrgUnitRequest, OrgUnitQueryParams } from '@/types/orgUnit';

/**
 * 获取组织单元列表（树形结构）
 */
export const listOrgUnits = (params?: OrgUnitQueryParams): Promise<OrgUnit[]> => {
  return get<OrgUnit[]>('/app/org-units', { params });
};

/**
 * 获取组织单元详情
 */
export const getOrgUnit = (id: number): Promise<OrgUnit> => {
  return get<OrgUnit>(`/app/org-units/${id}`);
};

/**
 * 创建组织单元
 */
export const createOrgUnit = (data: CreateOrgUnitRequest): Promise<OrgUnit> => {
  return post<OrgUnit>('/app/org-units', data);
};

/**
 * 更新组织单元
 */
export const updateOrgUnit = (id: number, data: UpdateOrgUnitRequest): Promise<OrgUnit> => {
  return put<OrgUnit>(`/app/org-units/${id}`, data);
};

/**
 * 删除组织单元
 */
export const deleteOrgUnit = (id: number): Promise<void> => {
  return del<void>(`/app/org-units/${id}`);
};
