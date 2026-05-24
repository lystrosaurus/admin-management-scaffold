import { Navigate, Outlet } from 'react-router-dom';
import { getToken } from '@/utils/token';

/**
 * 路由守卫组件
 * 检查 localStorage 中是否有 token：
 * - 有 token → 渲染子路由
 * - 无 token → 重定向到 /login
 */
const AuthGuard = () => {
  if (!getToken()) {
    return <Navigate to="/login" replace />;
  }

  return <Outlet />;
};

export default AuthGuard;
