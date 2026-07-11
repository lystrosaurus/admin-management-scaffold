import { test, expect } from '@playwright/test';

/**
 * OAuth 绑定 e2e（飞书为例）。
 *
 * 前置：
 *  1) 后端已在 8080 运行，前端 dev 在 3001（见 playwright.config.ts 的 webServer）。
 *  2) 数据库 auth_provider 表 LARK 行已配置飞书 appId/secret，
 *     redirect_uri = http://localhost:3001/oauth/callback/LARK。
 *  3) 准备一个本地管理员账号，替换下方 ADMIN_USERNAME / ADMIN_PASSWORD。
 *
 * 说明：用 page.route 拦截飞书授权端点，模拟「授权后回跳前端回调页」，
 * 因此无需真实飞书凭证 / 外网也能跑通 e2e 骨架，重点验证前端回调解析与绑定链路。
 */
const ADMIN_USERNAME = 'admin';
const ADMIN_PASSWORD = 'admin123';

test('飞书绑定：点击绑定 → 授权回调 → 绑定成功（不跳首页）', async ({ page }) => {
  // 拦截飞书授权端点，模拟用户授权后带着 code+state 回跳前端回调页
  await page.route('**/open.feishu.cn/**', (route) => {
    const url = new URL(route.request().url());
    const redirect = url.searchParams.get('redirect_uri') ?? 'http://localhost:3001/oauth/callback/LARK';
    const state = url.searchParams.get('state') ?? 'mock-state';
    void route.fulfill({
      status: 302,
      headers: { location: `${redirect}?code=MOCK_CODE&state=${state}` },
    });
  });

  // 登录
  await page.goto('/login');
  await page.fill('input#username', ADMIN_USERNAME);
  await page.fill('input#password', ADMIN_PASSWORD);
  await page.click('button[type=submit]');
  await page.waitForLoadState('networkidle').catch(() => {});

  // 进入账号安全页并点击飞书绑定
  await page.goto('/profile/security');
  await page.getByRole('button', { name: /绑定|飞书/ }).first().click();

  // 应回跳账号安全页并提示绑定成功（而非静默跳回首页）
  await page.waitForURL('**/profile/security**');
  await expect(page.getByText(/绑定成功|已绑定/)).toBeVisible();
});
