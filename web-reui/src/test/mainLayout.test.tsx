import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import { TooltipProvider } from '@/components/ui/tooltip'
import MainLayout from '@/layouts/MainLayout'

/**
 * 渲染冒烟测试：验证主布局在 jsdom 中可正常渲染。
 * 预置 token 以绕过 AuthGuard 的重定向逻辑（此处直接渲染布局本身）。
 * TooltipProvider 与 App.tsx 中的包裹结构保持一致。
 */
describe('MainLayout', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    localStorage.setItem('admin_token', 'test-token')
  })

  it('renders grouped sidebar menu with active group expanded', () => {
    render(
      <TooltipProvider>
        <MemoryRouter initialEntries={['/integration/sources']}>
          <MainLayout />
        </MemoryRouter>
      </TooltipProvider>,
    )

    // 侧边栏按业务域分组（SidebarGroupLabel 渲染）
    expect(screen.getByText('概览')).toBeDefined()
    expect(screen.getByText('系统管理')).toBeDefined()
    expect(screen.getByText('组织人事')).toBeDefined()
    expect(screen.getByText('审计运维')).toBeDefined()
    expect(screen.getByText('集成管理')).toBeDefined()
    expect(screen.getByText('个人')).toBeDefined()

    // 各分组下的叶子菜单项（精简后的标签）
    expect(screen.getAllByText('仪表盘').length).toBeGreaterThanOrEqual(1)
    expect(screen.getByText('用户')).toBeDefined()
    expect(screen.getByText('角色')).toBeDefined()
    expect(screen.getByText('员工')).toBeDefined()
    expect(screen.getByText('组织')).toBeDefined()

    // 集成管理分组在命中子项时默认展开，子菜单可见
    expect(screen.getAllByText('外部身份源').length).toBeGreaterThanOrEqual(1)

    // 退出登录入口（侧边栏底部 + 用户下拉各一处）
    expect(screen.getAllByText('退出登录').length).toBeGreaterThanOrEqual(1)
  })
})
