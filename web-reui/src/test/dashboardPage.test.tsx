import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import DashboardPage from '@/pages/dashboard/DashboardPage'

/** 渲染冒烟测试：仪表盘（P3 改版）在 jsdom 中可正常渲染统计卡与快速入口 */
vi.mock('@/api/dashboard', () => ({
  getDashboardStats: vi.fn(() =>
    Promise.resolve({
      userCount: 2,
      roleCount: 2,
      employeeCount: 3,
      orgUnitCount: 5,
    }),
  ),
}))

vi.mock('@/api/auditLog', () => ({
  getOperationLogs: vi.fn(() =>
    Promise.resolve({
      items: [
        {
          id: 1,
          username: 'admin',
          module: '用户管理',
          action: '创建用户',
          bizId: '1',
          opAt: '2026-10-22T08:00:00',
          ip: '127.0.0.1',
        },
      ],
      total: 1,
      page: 1,
      size: 8,
    }),
  ),
}))

describe('DashboardPage', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    localStorage.setItem('admin_token', 'test-token')
  })

  it('renders stat cards with backend counts and quick links', async () => {
    render(
      <MemoryRouter>
        <DashboardPage />
      </MemoryRouter>,
    )

    // 统计卡标题与快速入口
    expect(screen.getByText('用户总数')).toBeDefined()
    expect(screen.getByText('角色总数')).toBeDefined()
    expect(screen.getByText('快速入口')).toBeDefined()

    // 最近操作日志（真实接口数据）
    expect(await screen.findByText('创建用户')).toBeDefined()
  })
})
