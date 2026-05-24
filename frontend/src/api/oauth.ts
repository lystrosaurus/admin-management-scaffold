import { get, post, del } from './client';
import type { OAuthAuthorizeUrl, OAuthBindRequest, OAuthAccount } from '@/types/auth';

/**
 * 获取 OAuth 授权 URL
 */
export const getAuthorizeUrl = (provider: string): Promise<OAuthAuthorizeUrl> => {
  return get<OAuthAuthorizeUrl>(`/public/oauth/${provider}/authorize`);
};

/**
 * 绑定 OAuth 账户
 */
export const bindAccount = (provider: string, data: OAuthBindRequest): Promise<void> => {
  return post<void>(`/app/auth/oauth/${provider}/bind`, data);
};

/**
 * 解绑 OAuth 账户
 * 后端使用 Query Param: DELETE /app/auth/oauth/{provider}/unbind?accountId=xxx
 */
export const unbindAccount = (provider: string, accountId: number): Promise<void> => {
  return del<void>(`/app/auth/oauth/${provider}/unbind`, { params: { accountId } });
};

/**
 * 获取已绑定的 OAuth 账户列表
 */
export const listBoundAccounts = (): Promise<OAuthAccount[]> => {
  return get<OAuthAccount[]>('/app/profile/security');
};
