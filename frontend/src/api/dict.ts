import { get, post, put, del } from './client';
import type { PageResult } from '@/types/api';
import type { DictVO, DictCreateDTO, DictUpdateDTO, DictQuery } from '@/types/dict';

/** 后端 DictResponse 返回结构 */
interface RawDictVO {
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
 * 后端 DictVO → 前端 DictVO 类型映射
 */
const mapDict = (raw: RawDictVO): DictVO => ({
  id: raw.id,
  dictType: raw.dictType,
  dictLabel: raw.dictLabel,
  dictValue: raw.dictValue,
  sortOrder: raw.sortOrder,
  status: raw.status,
  remark: raw.remark,
  createdAt: raw.createdAt,
  updatedAt: raw.updatedAt,
});

/**
 * 获取数据字典列表（分页）
 */
export const listDict = async (params?: DictQuery): Promise<PageResult<DictVO>> => {
  const result = await get<PageResult<RawDictVO>>('/app/system/dict', { params });
  return { ...result, items: result.items.map(mapDict) };
};

/**
 * 获取数据字典详情
 */
export const getDict = async (id: number): Promise<DictVO> => {
  const raw = await get<RawDictVO>(`/app/system/dict/${id}`);
  return mapDict(raw);
};

/**
 * 创建数据字典
 */
export const createDict = async (data: DictCreateDTO): Promise<DictVO> => {
  const raw = await post<RawDictVO>('/app/system/dict', data);
  return mapDict(raw);
};

/**
 * 更新数据字典
 */
export const updateDict = async (id: number, data: DictUpdateDTO): Promise<DictVO> => {
  const raw = await put<RawDictVO>(`/app/system/dict/${id}`, data);
  return mapDict(raw);
};

/**
 * 删除数据字典
 */
export const deleteDict = (id: number): Promise<void> => {
  return del<void>(`/app/system/dict/${id}`);
};
