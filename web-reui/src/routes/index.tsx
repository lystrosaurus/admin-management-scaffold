import { lazy, Suspense } from 'react'
import { Routes, Route, Navigate } from 'react-router-dom'
import { Loader2 } from 'lucide-react'
import AuthGuard from './AuthGuard'
import MainLayout from '@/layouts/MainLayout'
import LoginPage from '@/pages/login/LoginPage'
import OAuthCallbackPage from '@/pages/oauth/OAuthCallbackPage'

/** 懒加载页面组件 */
const DashboardPage = lazy(() => import('@/pages/dashboard/DashboardPage'))
const UserListPage = lazy(() => import('@/pages/users/UserListPage'))
const RoleListPage = lazy(() => import('@/pages/roles/RoleListPage'))
const MenuListPage = lazy(() => import('@/pages/menus/MenuListPage'))
const DictPage = lazy(() => import('@/pages/dict/DictPage'))
const ConfigItemPage = lazy(() => import('@/pages/config/ConfigItemPage'))
const EmployeeListPage = lazy(() => import('@/pages/employees/EmployeeListPage'))
const OrgUnitListPage = lazy(() => import('@/pages/org-units/OrgUnitListPage'))
const AuditLogPage = lazy(() => import('@/pages/audit/AuditLogPage'))
const SettingsPage = lazy(() => import('@/pages/settings/SettingsPage'))
const SecurityProfilePage = lazy(() => import('@/pages/profile/security/SecurityProfilePage'))
const FileManagePage = lazy(() => import('@/pages/files/FileManagePage'))
const SystemStatusPage = lazy(() => import('@/pages/ops/SystemStatusPage'))
const SourcePage = lazy(() => import('@/pages/integration/sources/SourcePage'))
const PrincipalPage = lazy(() => import('@/pages/integration/principals/PrincipalPage'))
const LinkCandidatePage = lazy(() => import('@/pages/integration/link-candidates/LinkCandidatePage'))

/** 页面加载占位 */
const PageLoading = () => (
  <div className="flex h-[50vh] items-center justify-center">
    <Loader2 className="size-8 animate-spin text-muted-foreground" />
  </div>
)

/**
 * 应用路由配置
 *
 * 路由结构（与 antd 版 frontend/ 保持一致）：
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
 *   /audit        → 审计日志
 *   /files        → 文件管理
 *   /ops          → 系统状态
 *   /dict         → 数据字典
 *   /config-items → 配置项
 *   /integration/sources       → 外部身份源
 *   /integration/principals    → 外部主体
 *   /integration/link-candidates → 身份匹配
 */
const AppRoutes = () => (
  <Suspense fallback={<PageLoading />}>
    <Routes>
      {/* 公开路由：登录页 */}
      <Route path="/login" element={<LoginPage />} />

      {/* 三方绑定回调（独立路由，OAuth 重定向落地页） */}
      <Route path="/oauth/callback/:provider" element={<OAuthCallbackPage />} />

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
          <Route path="audit" element={<AuditLogPage />} />
          <Route path="files" element={<FileManagePage />} />
          <Route path="ops" element={<SystemStatusPage />} />
          <Route path="dict" element={<DictPage />} />
          <Route path="config-items" element={<ConfigItemPage />} />
          <Route path="integration/sources" element={<SourcePage />} />
          <Route path="integration/principals" element={<PrincipalPage />} />
          <Route path="integration/link-candidates" element={<LinkCandidatePage />} />
        </Route>
      </Route>

      {/* 兜底：未匹配路由重定向到首页 */}
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  </Suspense>
)

export default AppRoutes
