import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter, Routes, Route } from 'react-router-dom';
import { message } from 'antd';
import OAuthCallbackPage from '@/pages/oauth/OAuthCallbackPage';

// 仅 mock API 层；路由用真实 MemoryRouter，query / navigate 行为更贴近真实 e2e
vi.mock('@/api/oauth', () => ({
  bindAccount: vi.fn(),
}));
import { bindAccount } from '@/api/oauth';

// 受控的 useParams（用于「缺 provider」分支的专项用例）
let mockProvider: string | undefined = 'LARK';
vi.mock('react-router-dom', async (importOriginal) => {
  const actual = await importOriginal<typeof import('react-router-dom')>();
  return {
    ...actual,
    useParams: () => ({ provider: mockProvider }),
  };
});

beforeEach(() => {
  mockProvider = 'LARK';
  vi.spyOn(message, 'success').mockImplementation(() => ({}) as unknown as ReturnType<typeof message.success>);
  vi.spyOn(message, 'error').mockImplementation(() => ({}) as unknown as ReturnType<typeof message.error>);
  vi.mocked(bindAccount).mockReset();
  vi.mocked(bindAccount).mockResolvedValue(undefined);
});

function renderAt(path: string) {
  return render(
    <MemoryRouter initialEntries={[path]}>
      <Routes>
        <Route path="/oauth/callback/:provider" element={<OAuthCallbackPage />} />
        {/* 账号安全页标记：用于断言「跳回了正确的页」而非首页 */}
        <Route path="/profile/security" element={<div>SECURITY-PAGE</div>} />
      </Routes>
    </MemoryRouter>,
  );
}

describe('OAuth 绑定回调流程', () => {
  it('成功：code+state 时调用 bindAccount 并跳回账号安全页', async () => {
    renderAt('/oauth/callback/LARK?code=abc&state=xyz');

    await waitFor(() =>
      expect(bindAccount).toHaveBeenCalledWith('LARK', { code: 'abc', state: 'xyz' }),
    );
    await waitFor(() => expect(screen.getByText('SECURITY-PAGE')).toBeInTheDocument());
    expect(message.success).toHaveBeenCalledWith('三方账号绑定成功');
  });

  it('厂商拒绝授权(error 参数)：透出真实错误并跳安全页，绝不静默回首页', async () => {
    renderAt('/oauth/callback/LARK?error=access_denied&error_description=bad_redirect');

    await waitFor(() => expect(message.error).toHaveBeenCalled());
    expect(bindAccount).not.toHaveBeenCalled();
    expect(screen.getByText('SECURITY-PAGE')).toBeInTheDocument();
    // 错误文案应透出厂商信息，而非「成功」
    expect(message.error).toHaveBeenCalledWith(expect.stringContaining('access_denied'));
  });

  it('回归保护：缺少 code/state 时提示错误并跳安全页（原 bug 会静默跳首页）', async () => {
    renderAt('/oauth/callback/LARK'); // 无 code / state

    await waitFor(() => expect(message.error).toHaveBeenCalled());
    expect(bindAccount).not.toHaveBeenCalled();
    expect(screen.getByText('SECURITY-PAGE')).toBeInTheDocument();
  });

  it('缺少 provider 时提示错误并跳安全页', async () => {
    mockProvider = undefined; // 模拟 useParams 未解析到 provider
    renderAt('/oauth/callback/LARK?code=abc&state=xyz');

    await waitFor(() => expect(message.error).toHaveBeenCalled());
    expect(bindAccount).not.toHaveBeenCalled();
    expect(screen.getByText('SECURITY-PAGE')).toBeInTheDocument();
  });
});
