import { chromium, request } from '@playwright/test'
import { mkdirSync } from 'node:fs'
import { resolve } from 'node:path'

const AUTH_FILE = resolve(import.meta.dirname, '.auth/state.json')

/**
 * 全局登录：调用真实登录接口拿 token 写入 localStorage，落盘 storageState 供各 spec 复用。
 * 种子账号 admin/admin123（与后端约定一致）。
 */
export default async function globalSetup() {
  mkdirSync(resolve(import.meta.dirname, '.auth'), { recursive: true })

  // 1. 走真实登录接口拿 JWT
  const api = await request.newContext({ baseURL: 'http://localhost:3100' })
  const response = await api.post('/api/public/auth/login', {
    data: { username: 'admin', password: 'admin123' },
  })
  if (!response.ok()) {
    throw new Error(`登录接口失败：HTTP ${response.status()}（后端 8080 是否在线？）`)
  }
  const body = await response.json()
  const token: string | undefined = body?.data?.accessToken
  if (!token) {
    throw new Error(`登录响应缺少 data.accessToken：${JSON.stringify(body)}`)
  }
  await api.dispose()

  // 2. 预置 localStorage token，进入应用确认守卫放行，落盘 storageState
  const browser = await chromium.launch()
  const context = await browser.newContext({ baseURL: 'http://localhost:3100' })
  await context.addInitScript((value) => {
    window.localStorage.setItem('admin_token', value as string)
  }, token)

  const page = await context.newPage()
  await page.goto('/')
  // AuthGuard 会把 / 客户端重定向到 /dashboard（SPA 导航，需等待）
  await page.waitForURL(/\/dashboard$/, { timeout: 15_000 })
  await page.context().storageState({ path: AUTH_FILE })
  await browser.close()
}
