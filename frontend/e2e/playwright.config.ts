import { defineConfig, devices } from '@playwright/test';

/**
 * Playwright 端到端配置。
 * 前置（本环境离线无法安装浏览器，联网后执行）：
 *   npm i -D @playwright/test && npx playwright install
 * 运行：npx playwright test
 */
export default defineConfig({
  testDir: './e2e',
  timeout: 30000,
  expect: { timeout: 10000 },
  use: {
    baseURL: 'http://localhost:3001',
    headless: true,
  },
  webServer: {
    command: 'npm run dev',
    url: 'http://localhost:3001',
    reuseExistingServer: true,
    timeout: 120000,
  },
  projects: [{ name: 'chromium', use: { ...devices['Desktop Chrome'] } }],
});
