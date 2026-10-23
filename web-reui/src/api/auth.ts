import { post, get, put } from './client';
import type { LoginRequest, LoginResponse, ProfileVO, OAuthAuthorizeUrl, OAuthBindRequest } from '@/types/auth';

/**
 * 用户登录
 */
export const login = (data: LoginRequest): Promise<LoginResponse> => {
  return post<LoginResponse>('/public/auth/login', data);
};

/**
 * 用户登出
 */
export const logout = (): Promise<void> => {
  return post<void>('/public/auth/logout');
};

/**
 * 获取当前用户资料
 */
export const getProfile = (): Promise<ProfileVO> => {
  return get<ProfileVO>('/app/profile');
};

/**
 * 获取 OAuth 授权 URL
 */
export const getAuthorizeUrl = (provider: string): Promise<OAuthAuthorizeUrl> => {
  return get<OAuthAuthorizeUrl>(`/public/oauth/${provider}/authorize`);
};

/**
 * OAuth 绑定账户
 */
export const bindAccount = (provider: string, data: OAuthBindRequest): Promise<void> => {
  return post<void>(`/app/auth/oauth/${provider}/bind`, data);
};

/**
 * OAuth 解绑账户
 */
export const unbindAccount = (provider: string, accountId: number): Promise<void> => {
  return post<void>(`/app/auth/oauth/${provider}/unbind`, { accountId });
};

/**
 * 修改密码
 */
export const changePassword = (data: {
  oldPassword: string;
  newPassword: string;
}): Promise<void> => {
  return put<void>('/app/profile/password', data);
};
