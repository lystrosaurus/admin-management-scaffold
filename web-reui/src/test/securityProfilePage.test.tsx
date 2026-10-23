import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import SecurityProfilePage from '@/pages/profile/security/SecurityProfilePage'

/** 渲染冒烟测试：账号安全页（P3 迁移页）在 jsdom 中可正常渲染密码表单与三方绑定列表 */
vi.mock('@/api/auth', () => ({
  changePassword: vi.fn(() => Promise.resolve()),
}))

vi.mock('@/api/oauth', () => ({
  listBoundAccounts: vi.fn(() => Promise.resolve([])),
  unbindAccount: vi.fn(() => Promise.resolve()),
  getAuthorizeUrl: vi.fn(() => Promise.resolve({ url: 'https://example.com', state: 's' })),
}))

describe('SecurityProfilePage', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('renders password form and OAuth provider list', async () => {
    render(
      <MemoryRouter>
        <SecurityProfilePage />
      </MemoryRouter>,
    )

    // 密码表单（卡片标题与提交按钮同名，取 getAllByText）
    expect(screen.getAllByText('修改密码').length).toBeGreaterThanOrEqual(1)
    expect(screen.getByLabelText('当前密码')).toBeDefined()
    expect(screen.getByLabelText('新密码')).toBeDefined()
    expect(screen.getByLabelText('确认新密码')).toBeDefined()

    // 三方绑定（异步加载后出现，均未绑定状态）
    expect(await screen.findByText('飞书')).toBeDefined()
    expect(screen.getByText('企业微信')).toBeDefined()
    expect(screen.getByText('微信')).toBeDefined()
    expect(screen.getAllByText('未绑定').length).toBeGreaterThanOrEqual(1)
  })
})
