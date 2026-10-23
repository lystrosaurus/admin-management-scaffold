import { test, expect, type Page } from '@playwright/test'

/**
 * 全页面冒烟 e2e：逐一路由验证页面可渲染（MainLayout 面包屑标题 + 无白屏）。
 * 面包屑标题由 MainLayout 的 breadcrumbMap 提供，是每个页面稳定的存在性断言。
 */

/** 路由 → 期望的面包屑标题（与 MainLayout.menuItems 一致） */
const routes: { path: string; breadcrumb: string }[] = [
  { path: '/dashboard', breadcrumb: '仪表盘' },
  { path: '/users', breadcrumb: '用户管理' },
  { path: '/roles', breadcrumb: '角色管理' },
  { path: '/menus', breadcrumb: '菜单管理' },
  { path: '/employees', breadcrumb: '员工管理' },
  { path: '/org-units', breadcrumb: '组织管理' },
  { path: '/audit', breadcrumb: '审计日志' },
  { path: '/dict', breadcrumb: '数据字典' },
  { path: '/config-items', breadcrumb: '配置项' },
  { path: '/files', breadcrumb: '文件管理' },
  { path: '/ops', breadcrumb: '系统状态' },
  { path: '/settings', breadcrumb: '系统配置' },
  { path: '/profile/security', breadcrumb: '账号安全' },
  { path: '/integration/sources', breadcrumb: '外部身份源' },
  { path: '/integration/principals', breadcrumb: '外部主体' },
  { path: '/integration/link-candidates', breadcrumb: '身份匹配' },
]

/** 等待页面主体渲染完成（懒加载 chunk + 首屏数据请求） */
async function waitForPageReady(page: Page) {
  // 内容区 main（MainLayout 的 data-testid；侧边栏 inset 也是一个 <main>，需区分）
  await expect(page.getByTestId('page-content')).toBeVisible()
}

for (const route of routes) {
  test(`页面可渲染：${route.path}`, async ({ page }) => {
    const errors: string[] = []
    page.on('pageerror', (error) => errors.push(error.message))

    await page.goto(route.path)
    await waitForPageReady(page)

    // 面包屑标题证明路由与布局正确挂载
    await expect(page.getByRole('navigation').getByText(route.breadcrumb)).toBeVisible()
    // 主区域有实际内容（非空）
    const mainText = await page.getByTestId('page-content').innerText()
    expect(mainText.trim().length).toBeGreaterThan(0)
    // 无未捕获的运行时错误
    expect(errors).toEqual([])
  })
}
