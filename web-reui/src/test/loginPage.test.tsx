import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import LoginPage from '@/pages/login/LoginPage'

/** 渲染冒烟测试：验证登录页在 jsdom 中可正常渲染（导入、hooks、表单注册无运行时错误） */
describe('LoginPage', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('renders login form with username and password fields', () => {
    render(
      <MemoryRouter>
        <LoginPage />
      </MemoryRouter>,
    )

    expect(screen.getByLabelText('用户名')).toBeDefined()
    expect(screen.getByLabelText('密码')).toBeDefined()
    expect(screen.getByRole('button', { name: '登录' })).toBeDefined()
    // OAuth 三方登录入口
    expect(screen.getByTitle('飞书')).toBeDefined()
    expect(screen.getByTitle('企业微信')).toBeDefined()
    expect(screen.getByTitle('微信')).toBeDefined()
  })
})
