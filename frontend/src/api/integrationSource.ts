import { get, post, put, del } from './client';
import type {
  IntegrationSource,
  CreateIntegrationSourceRequest,
  UpdateIntegrationSourceRequest,
} from '@/types/integrationSource';

/**
 * 获取所有外部身份源
 */
export const listIntegrationSources = (): Promise<IntegrationSource[]> => {
  return get<IntegrationSource[]>('/app/integration/sources');
};

/**
 * 获取外部身份源详情
 */
export const getIntegrationSource = (id: number): Promise<IntegrationSource> => {
  return get<IntegrationSource>(`/app/integration/sources/${id}`);
};

/**
 * 创建外部身份源
 */
export const createIntegrationSource = (
  data: CreateIntegrationSourceRequest,
): Promise<IntegrationSource> => {
  return post<IntegrationSource>('/app/integration/sources', data);
};

/**
 * 更新外部身份源
 */
export const updateIntegrationSource = (
  id: number,
  data: UpdateIntegrationSourceRequest,
): Promise<IntegrationSource> => {
  return put<IntegrationSource>(`/app/integration/sources/${id}`, data);
};

/**
 * 删除外部身份源
 */
export const deleteIntegrationSource = (id: number): Promise<void> => {
  return del<void>(`/app/integration/sources/${id}`);
};
