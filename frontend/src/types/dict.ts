/**
 * 数据字典信息
 */
export interface DictVO {
  id: number;
  dictType: string;
  dictLabel: string;
  dictValue: string;
  sortOrder: number;
  status: string;
  remark?: string;
  createdAt?: string;
  updatedAt?: string;
}

/**
 * 创建数据字典请求
 */
export interface DictCreateDTO {
  dictType: string;
  dictLabel: string;
  dictValue: string;
  sortOrder?: number;
  status?: string;
  remark?: string;
}

/**
 * 更新数据字典请求
 */
export interface DictUpdateDTO {
  dictType?: string;
  dictLabel?: string;
  dictValue?: string;
  sortOrder?: number;
  status?: string;
  remark?: string;
}

/**
 * 数据字典查询参数
 */
export interface DictQuery {
  page?: number;
  size?: number;
  dictType?: string;
}
