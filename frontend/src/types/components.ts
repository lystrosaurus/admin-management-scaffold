/**
 * TreeSelect组件数据类型
 */
export interface TreeSelectData {
  value: number;
  label: string;
  children?: TreeSelectData[];
  disabled?: boolean;
}

/**
 * 表格分页参数
 */
export interface TablePagination {
  current: number;
  pageSize: number;
  total: number;
}