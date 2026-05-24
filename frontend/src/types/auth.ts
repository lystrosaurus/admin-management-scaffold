/**
 * 登录请求参数
 */
export interface LoginRequest {
  username: string;
  password: string;
}

/**
 * 登录响应
 */
export interface LoginResponse {
  accessToken: string;
  tokenType: string;
  expiresIn: number;
}

/**
 * 用户资料
 */
export interface UserProfile {
  id: number;
  username: string;
  nickname: string;
  avatar?: string;
  email?: string;
  phone?: string;
  roles: string[];
  permissions: string[];
}

/**
 * OAuth 授权 URL 响应
 */
export interface OAuthAuthorizeUrl {
  url: string;
  state: string;
}

/**
 * OAuth 绑定请求
 */
export interface OAuthBindRequest {
  code: string;
  state: string;
}

/**
 * OAuth 账户信息
 */
export interface OAuthAccount {
  id: number;
  provider: string;
  providerUserId: string;
  providerUsername: string;
  boundAt: string;
}
