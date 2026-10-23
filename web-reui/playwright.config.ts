import { defineConfig, devices } from '@playwright/test'
import { tmpdir } from 'node:os'
import { resolve } from 'node:path'

/**
 * Playwright e2e 配置
 * - baseURL 指向 web-reui dev server（3100，/api 代理到后端 8080）
 * - 复用已在运行的 dev server（reuseExistingServer）
 * - 登录态由 global-setup 通过 API 登录后落盘，各 spec 复用
 * - 输出目录放系统临时区：避免污染仓库，也避开本机安全删除策略对项目目录的限制
 */
const resultsDir = resolve(tmpdir(), 'web-reui-playwright-results')

export default defineConfig({
  testDir: './e2e',
  outputDir: resultsDir,
  fullyParallel: false,
  workers: 1,
  retries: 0,
  timeout: 30_000,
  expect: { timeout: 10_000 },
  reporter: [['list']],
  globalSetup: './e2e/global-setup.ts',
  use: {
    baseURL: 'http://localhost:3100',
    storageState: './e2e/.auth/state.json',
    trace: 'off',
    screenshot: 'only-on-failure',
  },
  projects: [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] },
    },
  ],
  webServer: {
    command: 'npm run dev',
    url: 'http://localhost:3100',
    reuseExistingServer: true,
    timeout: 120_000,
  },
})
