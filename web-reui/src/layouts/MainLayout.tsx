import { useMemo } from 'react'
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

/** 菜单配置项（与 antd 版 frontend 保持一致，图标换用 lucide-react） */
interface MenuItem {
  key: string
  label: string
  icon: typeof LayoutDashboard
  children?: { key: string; label: string }[]
}

const menuItems: MenuItem[] = [
  { key: '/dashboard', label: '仪表盘', icon: LayoutDashboard },
  { key: '/users', label: '用户管理', icon: Users },
  { key: '/roles', label: '角色管理', icon: KeyRound },
  { key: '/menus', label: '菜单管理', icon: MenuIcon },
  { key: '/employees', label: '员工管理', icon: Users },
  { key: '/org-units', label: '组织管理', icon: Building2 },
  { key: '/settings', label: '系统配置', icon: Settings },
  { key: '/profile/security', label: '账号安全', icon: ShieldCheck },
  { key: '/audit', label: '审计日志', icon: FileSearch },
  { key: '/files', label: '文件管理', icon: FolderOpen },
  { key: '/ops', label: '系统状态', icon: Activity },
  { key: '/dict', label: '数据字典', icon: BookOpen },
  { key: '/config-items', label: '配置项', icon: SlidersHorizontal },
  {
    key: '/integration',
    label: '集成管理',
    icon: Folder,
    children: [
      { key: '/integration/sources', label: '外部身份源' },
      { key: '/integration/principals', label: '外部主体' },
      { key: '/integration/link-candidates', label: '身份匹配' },
    ],
  },
]

/** 路径 → 面包屑标题映射 */
const breadcrumbMap: Record<string, string> = Object.fromEntries(
  menuItems.flatMap((item) => [
    [item.key, item.label],
    ...(item.children?.map((child) => [child.key, child.label] as const) ?? []),
  ]),
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

/**
 * 主布局组件
 * 基于 shadcn sidebar：可折叠侧边栏 + 顶栏（面包屑 / 主题切换 / 用户菜单）+ 内容区。
 */
const MainLayout = () => {
  const navigate = useNavigate()
  const location = useLocation()
  const logout = useAuthStore((s) => s.logout)
  const user = useAuthStore((s) => s.user)

  /** 当前显示的用户名：优先取 user profile，否则取默认值 */
  const username = user?.nickname || user?.username || '管理员'
  const breadcrumbTitle = breadcrumbMap[location.pathname] || '页面'

  /** 当前选中的菜单 key：子菜单路径取顶级分组 */
  const selectedKey = useMemo(() => {
    const parent = menuItems.find((item) => item.children?.some((c) => c.key === location.pathname))
    return parent ? parent.key : location.pathname
  }, [location.pathname])

  const handleLogout = async () => {
    await logout()
    navigate('/login', { replace: true })
  }

  return (
    <SidebarProvider>
      <Sidebar>
        <SidebarHeader>
          <div className="flex h-8 items-center gap-2 px-2 font-semibold">
            <div className="flex size-7 items-center justify-center rounded-md bg-primary text-xs font-bold text-primary-foreground">
              AM
            </div>
            <span className="group-data-[collapsible=icon]:hidden">Admin Management</span>
          </div>
        </SidebarHeader>

        <SidebarContent>
          <SidebarGroup>
            <SidebarGroupLabel>导航</SidebarGroupLabel>
            <SidebarGroupContent>
              <SidebarMenu>
                {menuItems.map((item) => {
                  const Icon = item.icon
                  if (item.children) {
                    const groupOpen = location.pathname.startsWith(item.key)
                    return (
                      <Collapsible asChild key={item.key} defaultOpen={groupOpen} className="group/collapsible">
                        <SidebarMenuItem>
                          <CollapsibleTrigger asChild>
                            <SidebarMenuButton tooltip={item.label}>
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
                        isActive={selectedKey === item.key}
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
