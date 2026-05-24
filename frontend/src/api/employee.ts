import { get, post, put, del } from './client';
import type { PageResult } from '@/types/api';
import type { Employee, CreateEmployeeRequest, UpdateEmployeeRequest, EmployeeQueryParams } from '@/types/employee';

/**
 * 后端 EmployeeVO → 前端 Employee 类型映射
 * 字段映射: mobile→phone, jobTitle→position, employmentStatus→status, primaryOrgId→orgUnitId
 */
const mapEmployee = (raw: any): Employee => ({
  id: raw.id,
  userId: raw.userId,
  username: raw.username,
  name: raw.name,
  employeeNo: raw.employeeNo,
  orgUnitId: raw.primaryOrgId,
  orgUnitName: raw.orgUnitName,
  position: raw.jobTitle,
  email: raw.email,
  phone: raw.mobile,
  status: raw.employmentStatus === 'ACTIVE' ? 'ACTIVE' : 'INACTIVE',
  createdAt: raw.createdAt,
  createdBy: raw.createdBy,
  updatedAt: raw.updatedAt,
  updatedBy: raw.updatedBy,
});

/**
 * 前端 CreateEmployeeRequest → 后端请求体映射
 */
const toBackendCreatePayload = (data: CreateEmployeeRequest) => ({
  userId: data.userId,
  name: data.name,
  employeeNo: data.employeeNo,
  primaryOrgId: data.orgUnitId,
  jobTitle: data.position,
  email: data.email,
  mobile: data.phone,
  employmentStatus: data.status,
});

/**
 * 前端 UpdateEmployeeRequest → 后端请求体映射
 */
const toBackendUpdatePayload = (data: UpdateEmployeeRequest) => ({
  name: data.name,
  employeeNo: data.employeeNo,
  primaryOrgId: data.orgUnitId,
  jobTitle: data.position,
  email: data.email,
  mobile: data.phone,
  employmentStatus: data.status,
});

/**
 * 获取员工列表（分页）
 */
export const listEmployees = async (params?: EmployeeQueryParams): Promise<PageResult<Employee>> => {
  const result = await get<PageResult<any>>('/app/employees', { params });
  return { ...result, items: result.items.map(mapEmployee) };
};

/**
 * 获取员工详情
 */
export const getEmployee = async (id: number): Promise<Employee> => {
  const raw = await get<any>(`/app/employees/${id}`);
  return mapEmployee(raw);
};

/**
 * 创建员工
 */
export const createEmployee = async (data: CreateEmployeeRequest): Promise<Employee> => {
  const raw = await post<any>('/app/employees', toBackendCreatePayload(data));
  return mapEmployee(raw);
};

/**
 * 更新员工
 */
export const updateEmployee = async (id: number, data: UpdateEmployeeRequest): Promise<Employee> => {
  const raw = await put<any>(`/app/employees/${id}`, toBackendUpdatePayload(data));
  return mapEmployee(raw);
};

/**
 * 删除员工
 */
export const deleteEmployee = (id: number): Promise<void> => {
  return del<void>(`/app/employees/${id}`);
};
