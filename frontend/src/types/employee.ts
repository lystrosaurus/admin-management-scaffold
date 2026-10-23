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
 * 员工查询参数（与后端 EmployeeController.findPage 签名一致）
 */
export interface EmployeeQueryParams {
  page?: number;
  size?: number;
  /** 关键词，模糊匹配姓名 / 工号 / 手机号 */
  keyword?: string;
  /** 在职状态：ACTIVE / INACTIVE */
  employmentStatus?: Status;
  /** 主组织 ID */
  orgId?: number;
}
