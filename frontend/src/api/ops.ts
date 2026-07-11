import { get } from './client';
import type { AppInfo } from '@/types/ops';

/**
 * 获取应用信息（系统状态）
 */
export const getAppInfo = async (): Promise<AppInfo> => {
  return get<AppInfo>('/app/ops/info');
};
