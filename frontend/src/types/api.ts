/**
 * 通用 API 响应类型
 */
export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

/**
 * 分页查询参数
 */
export interface PageParams {
  current?: number;
  size?: number;
  [key: string]: unknown;
}

/**
 * 分页结果
 */
export interface PageResult<T> {
  records: T[];
  total: number;
  current: number;
  size: number;
}

/**
 * 通用 ID 参数
 */
export interface IdParam {
  id: number;
}

/**
 * 通用状态枚举
 */
export type Status = 'ACTIVE' | 'INACTIVE';

/**
 * 通用排序参数
 */
export interface SortParams {
  field?: string;
  order?: 'ascend' | 'descend';
}
