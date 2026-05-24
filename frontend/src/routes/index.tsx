import { Routes, Route, Navigate } from 'react-router-dom';
import AuthGuard from './AuthGuard';
import MainLayout from '@/layouts/MainLayout';
import LoginPage from '@/pages/login/LoginPage';
import DashboardPage from '@/pages/dashboard/DashboardPage';
import UserListPage from '@/pages/users/UserListPage';
import RoleListPage from '@/pages/roles/RoleListPage';
import MenuListPage from '@/pages/menus/MenuListPage';
import EmployeeListPage from '@/pages/employees/EmployeeListPage';
import OrgUnitListPage from '@/pages/org-units/OrgUnitListPage';
import SettingsPage from '@/pages/settings/SettingsPage';
import SecurityProfilePage from '@/pages/profile/SecurityProfilePage';

/**
 * 应用路由配置
 *
 * 路由结构：
 * /login          → 登录页（无需认证）
 * /               → 主布局（需要认证）
 *   /dashboard    → 仪表盘
 *   /users        → 用户管理
 *   /roles        → 角色管理
 *   /menus        → 菜单管理
 *   /employees    → 员工管理
 *   /org-units    → 组织管理
 *   /settings     → 系统配置
 *   /profile/security → 账号安全
 */
const AppRoutes = () => (
  <Routes>
    {/* 公开路由：登录页 */}
    <Route path="/login" element={<LoginPage />} />

    {/* 受保护路由：需要认证 */}
    <Route element={<AuthGuard />}>
      <Route path="/" element={<MainLayout />}>
        {/* 首页重定向到仪表盘 */}
        <Route index element={<Navigate to="/dashboard" replace />} />
        <Route path="dashboard" element={<DashboardPage />} />
        <Route path="users" element={<UserListPage />} />
        <Route path="roles" element={<RoleListPage />} />
        <Route path="menus" element={<MenuListPage />} />
        <Route path="employees" element={<EmployeeListPage />} />
        <Route path="org-units" element={<OrgUnitListPage />} />
        <Route path="settings" element={<SettingsPage />} />
        <Route path="profile/security" element={<SecurityProfilePage />} />
      </Route>
    </Route>

    {/* 兜底：未匹配路由重定向到首页 */}
    <Route path="*" element={<Navigate to="/" replace />} />
  </Routes>
);

export default AppRoutes;
