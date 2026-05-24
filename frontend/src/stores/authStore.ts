import { create } from 'zustand';
import type { UserProfile } from '@/types/auth';
import { getToken, setToken, removeToken } from '@/utils/token';
import * as authApi from '@/api/auth';

/**
 * 认证状态接口
 */
interface AuthState {
  /** JWT Token */
  token: string | null;
  /** 用户信息 */
  user: UserProfile | null;
  /** 是否已认证 */
  isAuthenticated: boolean;
  /** 加载状态 */
  loading: boolean;

  /** 登录 */
  login: (username: string, password: string) => Promise<void>;
  /** 登出 */
  logout: () => Promise<void>;
  /** 获取用户资料 */
  fetchProfile: () => Promise<void>;
  /** 设置 Token */
  setToken: (token: string) => void;
  /** 清除认证状态 */
  clearAuth: () => void;
}

/**
 * 认证状态管理
 */
export const useAuthStore = create<AuthState>((set, get) => ({
  // 初始状态：从 localStorage 恢复 token
  token: getToken(),
  user: null,
  isAuthenticated: !!getToken(),
  loading: false,

  /**
   * 登录
   */
  login: async (username: string, password: string) => {
    set({ loading: true });
    try {
      const response = await authApi.login({ username, password });
      // 保存 token
      setToken(response.accessToken);
      set({
        token: response.accessToken,
        isAuthenticated: true,
      });
      // 登录成功后获取用户资料
      await get().fetchProfile();
    } finally {
      set({ loading: false });
    }
  },

  /**
   * 登出
   */
  logout: async () => {
    try {
      await authApi.logout();
    } finally {
      // 无论接口是否成功，都清除本地状态
      get().clearAuth();
    }
  },

  /**
   * 获取用户资料
   */
  fetchProfile: async () => {
    set({ loading: true });
    try {
      const user = await authApi.getProfile();
      set({ user });
    } finally {
      set({ loading: false });
    }
  },

  /**
   * 设置 Token
   */
  setToken: (token: string) => {
    setToken(token);
    set({ token, isAuthenticated: true });
  },

  /**
   * 清除认证状态
   */
  clearAuth: () => {
    removeToken();
    set({
      token: null,
      user: null,
      isAuthenticated: false,
    });
  },
}));
