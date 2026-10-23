import { get, post, put, del } from './client';
import type { PageResult } from '@/types/api';
import type {
  ConfigItemVO,
  ConfigItemCreateDTO,
  ConfigItemUpdateDTO,
  ConfigItemQuery,
} from '@/types/config';

/** 后端 ConfigItemResponse 返回结构 */
interface RawConfigItemVO {
  id: number;
  configKey: string;
  configValue: string;
  configType: string;
  description?: string;
  status: string;
  createdAt?: string;
  updatedAt?: string;
}

/**
 * 后端 ConfigItemVO → 前端 ConfigItemVO 类型映射
 */
const mapConfigItem = (raw: RawConfigItemVO): ConfigItemVO => ({
  id: raw.id,
  configKey: raw.configKey,
  configValue: raw.configValue,
  configType: raw.configType,
  description: raw.description,
  status: raw.status,
  createdAt: raw.createdAt,
  updatedAt: raw.updatedAt,
});

/**
 * 获取配置项列表（分页）
 */
export const listConfig = async (params?: ConfigItemQuery): Promise<PageResult<ConfigItemVO>> => {
  const result = await get<PageResult<RawConfigItemVO>>('/app/system/config-item', { params });
  return { ...result, items: result.items.map(mapConfigItem) };
};

/**
 * 获取配置项详情
 */
export const getConfig = async (id: number): Promise<ConfigItemVO> => {
  const raw = await get<RawConfigItemVO>(`/app/system/config-item/${id}`);
  return mapConfigItem(raw);
};

/**
 * 创建配置项
 */
export const createConfig = async (data: ConfigItemCreateDTO): Promise<ConfigItemVO> => {
  const raw = await post<RawConfigItemVO>('/app/system/config-item', data);
  return mapConfigItem(raw);
};

/**
 * 更新配置项
 */
export const updateConfig = async (id: number, data: ConfigItemUpdateDTO): Promise<ConfigItemVO> => {
  const raw = await put<RawConfigItemVO>(`/app/system/config-item/${id}`, data);
  return mapConfigItem(raw);
};

/**
 * 删除配置项
 */
export const deleteConfig = (id: number): Promise<void> => {
  return del<void>(`/app/system/config-item/${id}`);
};
