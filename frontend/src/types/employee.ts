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
 */
export interface EmployeeQueryParams {
  current?: number;
  size?: number;
  name?: string;
  employeeNo?: string;
  orgUnitId?: number;
  position?: string;
  status?: Status;
}
