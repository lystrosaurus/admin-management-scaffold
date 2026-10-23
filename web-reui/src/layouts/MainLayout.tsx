import { useNavigate, useLocation, Outlet, Link } from 'react-router-dom'
import { useTheme } from 'next-themes'
import {
  Activity,
  BookOpen,
  Building2,
  ChevronRight,
  FileSearch,
  Folder,
  FolderOpen,
  IdCard,
  KeyRound,
  LayoutDashboard,
  LogOut,
  Menu as MenuIcon,
  Moon,
  Settings,
  ShieldCheck,
  SlidersHorizontal,
  Sun,
  User as UserIcon,
  Users,
} from 'lucide-react'
import {
  Collapsible,
  CollapsibleContent,
  CollapsibleTrigger,
} from '@/components/ui/collapsible'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarGroup,
  SidebarGroupContent,
  SidebarGroupLabel,
  SidebarHeader,
  SidebarInset,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
  SidebarMenuSub,
  SidebarMenuSubButton,
  SidebarMenuSubItem,
  SidebarProvider,
  SidebarTrigger,
} from '@/components/ui/sidebar'
import { Avatar, AvatarFallback } from '@/components/ui/avatar'
import { Separator } from '@/components/ui/separator'
import { Breadcrumb, BreadcrumbItem, BreadcrumbLink, BreadcrumbList, BreadcrumbPage } from '@/components/ui/breadcrumb'
import { Button } from '@/components/ui/button'
import { useAuthStore } from '@/stores/authStore'

/** 单个菜单条目（叶子节点） */
interface LeafItem {
  key: string
  label: string
  icon: typeof LayoutDashboard
}

/** 可折叠分组菜单条目 */
interface BranchItem {
  key: string
  label: string
  icon: typeof LayoutDashboard
  children: { key: string; label: string }[]
}

type MenuEntry = LeafItem | BranchItem

/** 侧边栏分组（按业务域） */
interface MenuGroup {
  label: string
  items: MenuEntry[]
}

/** 菜单配置：按业务域分 6 组 */
const menuGroups: MenuGroup[] = [
  {
    label: '概览',
    items: [{ key: '/dashboard', label: '仪表盘', icon: LayoutDashboard }],
  },
  {
    label: '系统管理',
    items: [
      { key: '/users', label: '用户', icon: Users },
      { key: '/roles', label: '角色', icon: KeyRound },
      { key: '/menus', label: '菜单', icon: MenuIcon },
      { key: '/dict', label: '字典', icon: BookOpen },
      { key: '/config-items', label: '配置项', icon: SlidersHorizontal },
    ],
  },
  {
    label: '组织人事',
    items: [
      { key: '/employees', label: '员工', icon: IdCard },
      { key: '/org-units', label: '组织', icon: Building2 },
    ],
  },
  {
    label: '审计运维',
    items: [
      { key: '/audit', label: '审计日志', icon: FileSearch },
      { key: '/files', label: '文件管理', icon: FolderOpen },
      { key: '/ops', label: '系统状态', icon: Activity },
    ],
  },
  {
    label: '集成管理',
    items: [
      {
        key: '/integration',
        label: '外部身份',
        icon: Folder,
        children: [
          { key: '/integration/sources', label: '外部身份源' },
          { key: '/integration/principals', label: '外部主体' },
          { key: '/integration/link-candidates', label: '身份匹配' },
        ],
      },
    ],
  },
  {
    label: '个人',
    items: [
      { key: '/profile/security', label: '账号安全', icon: ShieldCheck },
      { key: '/settings', label: '偏好设置', icon: Settings },
    ],
  },
]

/** 所有叶子条目平铺，用于面包屑映射与父项命中判断 */
const allLeafEntries: LeafItem[] = menuGroups.flatMap((group) =>
  group.items.flatMap((item) => {
    if ('children' in item) {
      return item.children.map<LeafItem>((child) => ({
        key: child.key,
        label: child.label,
        icon: item.icon,
      }))
    }
    return [item]
  }),
)

/** 路径 → 面包屑标题映射 */
const breadcrumbMap: Record<string, string> = Object.fromEntries(
  allLeafEntries.map((item) => [item.key, item.label]),
)

/** 亮/暗主题切换按钮 */
const ThemeToggle = () => {
  const { resolvedTheme, setTheme } = useTheme()
  const isDark = resolvedTheme === 'dark'

  return (
    <Button
      variant="ghost"
      size="icon"
      title={isDark ? '切换到亮色模式' : '切换到暗色模式'}
      onClick={() => setTheme(isDark ? 'light' : 'dark')}
    >
      {isDark ? <Sun className="size-4" /> : <Moon className="size-4" />}
    </Button>
  )
}

/** 判断菜单条目当前是否应高亮：叶子直接匹配；分支匹配自身或其任一子项 */
function isItemActive(item: MenuEntry, pathname: string): boolean {
  if ('children' in item) {
    if (item.key === pathname) return true
    return item.children.some((c) => c.key === pathname)
  }
  return item.key === pathname
}

/**
 * 主布局组件
 * 基于 shadcn sidebar：inset 模式 + icon 折叠 + 按业务域分组。
 * 折叠后保留图标条（collapsible="icon"），而不是 offcanvas 完全消失。
 */
const MainLayout = () => {
  const navigate = useNavigate()
  const location = useLocation()
  const logout = useAuthStore((s) => s.logout)
  const user = useAuthStore((s) => s.user)

  const username = user?.nickname || user?.username || '管理员'
  const breadcrumbTitle = breadcrumbMap[location.pathname] || '页面'

  const handleLogout = async () => {
    await logout()
    navigate('/login', { replace: true })
  }

  return (
    <SidebarProvider>
      {/* inset 模式 + icon 折叠：折叠后保留图标 strip，是管理后台的标配交互 */}
      <Sidebar variant="inset" collapsible="icon">
        <SidebarHeader>
          <div className="flex h-8 items-center gap-2 px-2 font-semibold">
            <div className="flex size-7 items-center justify-center rounded-md bg-primary text-xs font-bold text-primary-foreground">
              AM
            </div>
            <span className="group-data-[collapsible=icon]:hidden">Admin Management</span>
          </div>
        </SidebarHeader>

        <SidebarContent>
          {menuGroups.map((group) => (
            <SidebarGroup key={group.label}>
              <SidebarGroupLabel>{group.label}</SidebarGroupLabel>
              <SidebarGroupContent>
                <SidebarMenu>
                  {group.items.map((item) => {
                    const Icon = item.icon
                    const active = isItemActive(item, location.pathname)

                    if ('children' in item) {
                      const groupOpen = location.pathname.startsWith(item.key) || item.children.some((c) => c.key === location.pathname)
                      return (
                        <Collapsible asChild key={item.key} defaultOpen={groupOpen} className="group/collapsible">
                          <SidebarMenuItem>
                            <CollapsibleTrigger asChild>
                              <SidebarMenuButton tooltip={item.label} isActive={active}>
                                <Icon />
                                <span>{item.label}</span>
                                <ChevronRight className="ml-auto size-4 transition-transform duration-200 group-data-[state=open]/collapsible:rotate-90" />
                              </SidebarMenuButton>
                            </CollapsibleTrigger>
                            <CollapsibleContent>
                              <SidebarMenuSub>
                                {item.children.map((child) => (
                                  <SidebarMenuSubItem key={child.key}>
                                    <SidebarMenuSubButton asChild isActive={location.pathname === child.key}>
                                      <Link to={child.key}>
                                        <span>{child.label}</span>
                                      </Link>
                                    </SidebarMenuSubButton>
                                  </SidebarMenuSubItem>
                                ))}
                              </SidebarMenuSub>
                            </CollapsibleContent>
                          </SidebarMenuItem>
                        </Collapsible>
                      )
                    }

                    return (
                      <SidebarMenuItem key={item.key}>
                        <SidebarMenuButton
                          asChild
                          isActive={active}
                          tooltip={item.label}
                        >
                          <Link to={item.key}>
                            <Icon />
                            <span>{item.label}</span>
                          </Link>
                        </SidebarMenuButton>
                      </SidebarMenuItem>
                    )
                  })}
                </SidebarMenu>
              </SidebarGroupContent>
            </SidebarGroup>
          ))}
        </SidebarContent>

        <SidebarFooter>
          <SidebarMenu>
            <SidebarMenuItem>
              <SidebarMenuButton onClick={handleLogout} tooltip="退出登录">
                <LogOut />
                <span>退出登录</span>
              </SidebarMenuButton>
            </SidebarMenuItem>
          </SidebarMenu>
        </SidebarFooter>
      </Sidebar>

      <SidebarInset>
        <header className="sticky top-0 z-10 flex h-14 shrink-0 items-center gap-2 border-b bg-background px-4">
          <SidebarTrigger />
          <Separator orientation="vertical" className="mr-2 h-4" />
          <Breadcrumb>
            <BreadcrumbList>
              <BreadcrumbItem>
                <BreadcrumbLink asChild>
                  <Link to="/">首页</Link>
                </BreadcrumbLink>
              </BreadcrumbItem>
              <BreadcrumbItem>
                <BreadcrumbPage>{breadcrumbTitle}</BreadcrumbPage>
              </BreadcrumbItem>
            </BreadcrumbList>
          </Breadcrumb>

          <div className="ml-auto flex items-center gap-2">
            <ThemeToggle />
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <Button variant="ghost" className="flex items-center gap-2 px-2">
                  <Avatar className="size-7">
                    <AvatarFallback>
                      <UserIcon className="size-4" />
                    </AvatarFallback>
                  </Avatar>
                  <span className="text-sm font-medium">{username}</span>
                </Button>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end" className="w-48">
                <DropdownMenuLabel>{username}</DropdownMenuLabel>
                <DropdownMenuSeparator />
                <DropdownMenuItem asChild>
                  <Link to="/profile/security">
                    <ShieldCheck className="size-4" />
                    账号安全
                  </Link>
                </DropdownMenuItem>
                <DropdownMenuSeparator />
                <DropdownMenuItem variant="destructive" onClick={handleLogout}>
                  <LogOut className="size-4" />
                  退出登录
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>
          </div>
        </header>

        <main data-testid="page-content" className="flex-1 p-4 md:p-6">
          <Outlet />
        </main>
      </SidebarInset>
    </SidebarProvider>
  )
}

export default MainLayout
