import { describe, it, expect, vi, beforeEach, beforeAll } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter, Routes, Route } from 'react-router-dom';
import { message } from 'antd';
import LoginPage from '@/pages/login/LoginPage';

// 只 mock 登录动作，路由与表单保持真实实现
const { mockLogin } = vi.hoisted(() => ({ mockLogin: vi.fn() }));

vi.mock('@/stores/authStore', () => ({
  useAuthStore: (selector: (state: { login: typeof mockLogin }) => unknown) =>
    selector({ login: mockLogin }),
}));

beforeAll(() => {
  // jsdom 未实现 matchMedia，antd 的响应式观察器会调用它
  window.matchMedia = ((query: string) => ({
    matches: false,
    media: query,
    onchange: null,
    addListener: () => {},
    removeListener: () => {},
    addEventListener: () => {},
    removeEventListener: () => {},
    dispatchEvent: () => false,
  })) as unknown as typeof window.matchMedia;
});

beforeEach(() => {
  mockLogin.mockReset();
  mockLogin.mockResolvedValue(undefined);
  vi.spyOn(message, 'success').mockImplementation(() => ({}) as never);
  vi.spyOn(message, 'error').mockImplementation(() => ({}) as never);
});

function renderLogin() {
  return render(
    <MemoryRouter initialEntries={['/login']}>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/dashboard" element={<div>DASHBOARD-PAGE</div>} />
      </Routes>
    </MemoryRouter>,
  );
}

describe('登录页表单提交', () => {
  it('在密码框按回车应提交登录并跳转仪表盘', async () => {
    const user = userEvent.setup();
    renderLogin();

    await user.type(screen.getByPlaceholderText('用户名'), 'admin');
    await user.type(screen.getByPlaceholderText('密码'), 'admin123');
    await user.keyboard('{Enter}');

    await waitFor(() => expect(mockLogin).toHaveBeenCalledWith('admin', 'admin123'));
    expect(mockLogin).toHaveBeenCalledTimes(1);
    await waitFor(() => expect(screen.getByText('DASHBOARD-PAGE')).toBeInTheDocument());
  });

  it('点击登录按钮仍可提交，且只提交一次', async () => {
    const user = userEvent.setup();
    renderLogin();

    await user.type(screen.getByPlaceholderText('用户名'), 'admin');
    await user.type(screen.getByPlaceholderText('密码'), 'admin123');
    // antd 会在两个中文字符间插入空格，因此用正则匹配
    await user.click(screen.getByRole('button', { name: /登\s*录/ }));

    await waitFor(() => expect(mockLogin).toHaveBeenCalledWith('admin', 'admin123'));
    expect(mockLogin).toHaveBeenCalledTimes(1);
  });
});
