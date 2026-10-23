import { test, expect } from '@playwright/test'

/**
 * 登录流程 e2e：未登录访问受保护路由被重定向到登录页，登录成功后进入仪表盘。
 */
test.describe('登录流程', () => {
  test('未登录访问受保护路由重定向到登录页', async ({ page }) => {
    // 清掉全局登录态，模拟匿名访问
    await page.context().clearCookies()
    await page.goto('/users')
    await page.evaluate(() => window.localStorage.clear())
    await page.goto('/users')

    await expect(page).toHaveURL(/\/login$/)
    await expect(page.getByLabel('用户名')).toBeVisible()
    await expect(page.getByLabel('密码')).toBeVisible()
  })

  test('使用种子账号登录成功进入仪表盘', async ({ page }) => {
    // 先导航到同源页面再清 localStorage（about:blank 上访问会抛 SecurityError）
    await page.goto('/login')
    await page.evaluate(() => window.localStorage.clear())
    await page.reload()

    await page.getByLabel('用户名').fill('admin')
    await page.getByLabel('密码').fill('admin123')
    await page.getByRole('button', { name: '登录' }).click()

    await expect(page).toHaveURL(/\/dashboard$/, { timeout: 15_000 })
    await expect(page.getByRole('heading', { name: '仪表盘' })).toBeVisible()
    // 侧边栏主导航可见（限定在侧边栏内，避开仪表盘快速入口的同名链接）
    await expect(
      page.locator('[data-slot="sidebar"]').getByRole('link', { name: '用户管理' }),
    ).toBeVisible()
  })
})
