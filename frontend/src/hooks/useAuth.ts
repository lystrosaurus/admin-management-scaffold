import { useAuthStore } from '@/stores/authStore';

/**
 * 认证相关 Hook
 * 封装 authStore 的常用操作
 */
export const useAuth = () => {
  const {
    token,
    user,
    roles,
    permissions,
    isAuthenticated,
    loading,
    login,
    logout,
    fetchProfile,
    clearAuth,
  } = useAuthStore();

  return {
    /** JWT Token */
    token,
    /** 用户基本信息 */
    user,
    /** 是否已认证 */
    isAuthenticated,
    /** 加载状态 */
    loading,
    /** 登录 */
    login,
    /** 登出 */
    logout,
    /** 刷新用户资料 */
    refreshProfile: fetchProfile,
    /** 清除认证状态 */
    clearAuth,
    /** 用户角色列表 */
    roles,
    /** 用户权限列表 */
    permissions,
  };
};
