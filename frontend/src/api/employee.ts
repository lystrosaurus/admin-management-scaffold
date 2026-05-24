import { get, post, put, del } from './client';
import type { PageResult } from '@/types/api';
import type { Employee, CreateEmployeeRequest, UpdateEmployeeRequest, EmployeeQueryParams } from '@/types/employee';

/**
 * 获取员工列表（分页）
 */
export const listEmployees = (params?: EmployeeQueryParams): Promise<PageResult<Employee>> => {
  return get<PageResult<Employee>>('/app/employees', { params });
};

/**
 * 获取员工详情
 */
export const getEmployee = (id: number): Promise<Employee> => {
  return get<Employee>(`/app/employees/${id}`);
};

/**
 * 创建员工
 */
export const createEmployee = (data: CreateEmployeeRequest): Promise<Employee> => {
  return post<Employee>('/app/employees', data);
};

/**
 * 更新员工
 */
export const updateEmployee = (id: number, data: UpdateEmployeeRequest): Promise<Employee> => {
  return put<Employee>(`/app/employees/${id}`, data);
};

/**
 * 删除员工
 */
export const deleteEmployee = (id: number): Promise<void> => {
  return del<void>(`/app/employees/${id}`);
};
