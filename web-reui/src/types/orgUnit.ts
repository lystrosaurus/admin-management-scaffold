import type { Status } from './api';

/**
 * 组织单元类型
 */
export type OrgUnitType = 'COMPANY' | 'DEPARTMENT' | 'TEAM';

/**
 * 组织单元信息
 */
export interface OrgUnit {
  id: number;
  parentId?: number;
  name: string;
  code: string;
  type: OrgUnitType;
  leaderId?: number;
  leaderName?: string;
  sort: number;
  status: Status;
  children?: OrgUnit[];
  createdAt: string;
  createdBy?: number;
  updatedAt: string;
  updatedBy?: number;
}

/**
 * 创建组织单元请求
 */
export interface CreateOrgUnitRequest {
  parentId?: number;
  name: string;
  code: string;
  type: OrgUnitType;
  leaderId?: number;
  sort?: number;
  status?: Status;
}

/**
 * 更新组织单元请求
 */
export interface UpdateOrgUnitRequest {
  parentId?: number;
  name?: string;
  code?: string;
  type?: OrgUnitType;
  leaderId?: number;
  sort?: number;
  status?: Status;
}

/**
 * 组织单元查询参数
 */
export interface OrgUnitQueryParams {
  name?: string;
  code?: string;
  type?: OrgUnitType;
  status?: Status;
}
