/**
 * 登录请求参数
 */
export interface LoginRequest {
  username: string;
  password: string;
}

/**
 * 用户基本信息（后端 UserVO）
 */
export interface UserVO {
  id: number;
  username: string;
  nickname: string;
  phone?: string;
  email?: string;
  status: string;
  lastLoginAt?: string;
  createdAt: string;
}

/**
 * 登录响应
 */
export interface LoginResponse {
  accessToken: string;
  user: UserVO;
}

/**
 * 菜单视图对象（后端 MenuVO）
 */
export interface MenuVO {
  id: number;
  parentId?: number;
  name: string;
  path?: string;
  component?: string;
  icon?: string;
  sortOrder?: number;
  type: string;
  permissionCode?: string;
  visible?: boolean;
  status: string;
  children?: MenuVO[];
}

/**
 * 用户资料（后端 ProfileVO）
 * 包含用户信息、角色、权限、菜单等
 */
export interface ProfileVO {
  user: UserVO;
  roles: string[];
  permissions: string[];
  menus: MenuVO[];
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
 * OAuth 账户信息（对应后端 ExternalAccountVO）
 */
export interface OAuthAccount {
  id: number;
  providerId: number;
  providerCode: string;
  providerUserId: string;
  userId: number;
  employeeId?: number;
  nickname?: string;
  avatarUrl?: string;
  bindStatus: string;
  lastLoginAt?: string;
  createdAt: string;
  updatedAt?: string;
}

/**
 * 安全资料（对应后端 SecurityProfileVO）
 */
export interface SecurityProfileVO {
  hasPassword: boolean;
  boundAccounts: OAuthAccount[];
}
