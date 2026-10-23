import type { Status } from './api';

/**
 * 员工信息
 */
export interface Employee {
  id: number;
  userId: number;
  username: string;
  name: string;
  employeeNo: string;
  orgUnitId?: number;
  orgUnitName?: string;
  position?: string;
  email?: string;
  phone?: string;
  status: Status;
  createdAt: string;
  createdBy?: number;
  updatedAt: string;
  updatedBy?: number;
}

/**
 * 创建员工请求
 */
export interface CreateEmployeeRequest {
  userId: number;
  name: string;
  employeeNo: string;
  orgUnitId?: number;
  position?: string;
  email?: string;
  phone?: string;
  status?: Status;
}

/**
 * 更新员工请求
 */
export interface UpdateEmployeeRequest {
  name?: string;
  employeeNo?: string;
  orgUnitId?: number;
  position?: string;
  email?: string;
  phone?: string;
  status?: Status;
}

/**
 * 员工查询参数
 * 注意：与后端 EmployeeController 实际参数对齐（keyword 模糊匹配姓名/工号/手机号，
 * employmentStatus 在职状态，orgId 主组织 ID）。antd 版曾传 name/employeeNo/status，
 * 均被后端忽略导致搜索失效，此处按真实契约修正。
 */
export interface EmployeeQueryParams {
  page?: number;
  size?: number;
  /** 关键词（模糊匹配姓名 / 工号 / 手机号） */
  keyword?: string;
  /** 在职状态（ACTIVE / INACTIVE） */
  employmentStatus?: Status;
  /** 主组织 ID */
  orgId?: number;
}
