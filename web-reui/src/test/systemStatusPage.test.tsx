import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import SystemStatusPage from '@/pages/ops/SystemStatusPage'

/** 渲染冒烟测试：系统状态页（P3 迁移页）在 jsdom 中可正常渲染并展示后端信息 */
vi.mock('@/api/ops', () => ({
  getAppInfo: vi.fn(() =>
    Promise.resolve({
      name: 'admin-management-scaffold',
      version: '1.0.0',
      javaVersion: '21.0.5',
      status: 'UP',
      osName: 'Windows 11',
      osArch: 'amd64',
      profile: 'local',
      startTime: '2026-10-21T16:59:32.533',
    }),
  ),
}))

describe('SystemStatusPage', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('renders stats cards and app info from backend', async () => {
    render(
      <MemoryRouter>
        <SystemStatusPage />
      </MemoryRouter>,
    )

    // 异步加载出的后端数据（应用名同时出现在统计卡与详情行，取 findAllByText）
    expect(screen.getByText('系统状态')).toBeDefined()
    expect((await screen.findAllByText('admin-management-scaffold')).length).toBeGreaterThanOrEqual(1)
    expect(screen.getAllByText('1.0.0').length).toBeGreaterThanOrEqual(1)
    expect(screen.getAllByText('UP').length).toBeGreaterThanOrEqual(1)
  })
})
