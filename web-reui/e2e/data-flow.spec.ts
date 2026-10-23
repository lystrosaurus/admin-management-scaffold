import { test, expect } from '@playwright/test'

/**
 * 关键数据链路 e2e：验证真实后端数据经 3100 代理流入各页面。
 */

test.describe('仪表盘', () => {
  test('统计卡展示后端真实计数', async ({ page }) => {
    await page.goto('/dashboard')
    // 四张统计卡标题
    for (const label of ['用户总数', '角色总数', '员工总数', '组织总数']) {
      await expect(page.getByText(label, { exact: true })).toBeVisible()
    }
    // 资源分布图表（Recharts）渲染出 SVG
    await expect(page.locator('.recharts-wrapper').first()).toBeVisible()
  })
})

test.describe('用户管理', () => {
  test('表格展示种子用户 admin', async ({ page }) => {
    await page.goto('/users')
    await expect(page.getByRole('button', { name: '新增用户' })).toBeVisible()
    // 种子账号 admin 应在首屏数据中
    await expect(page.getByRole('cell', { name: 'admin' }).first()).toBeVisible()
  })

  test('按用户名搜索可过滤结果', async ({ page }) => {
    await page.goto('/users')
    await page.getByPlaceholder('用户名').fill('admin')
    await page.getByRole('button', { name: '搜索' }).click()
    await expect(page.getByRole('cell', { name: 'admin' }).first()).toBeVisible()
    await expect(page.getByText('共 1 条')).toBeVisible()
  })
})

test.describe('系统状态', () => {
  test('展示后端应用信息（UP）', async ({ page }) => {
    await page.goto('/ops')
    await expect(page.getByText('admin-management-scaffold').first()).toBeVisible()
    await expect(page.getByText('UP').first()).toBeVisible()
  })
})

test.describe('集成管理', () => {
  test('外部身份源页可加载（空列表也正常）', async ({ page }) => {
    await page.goto('/integration/sources')
    await expect(page.getByRole('button', { name: '新增身份源' })).toBeVisible()
  })

  test('外部主体页展示筛选栏与表格', async ({ page }) => {
    await page.goto('/integration/principals')
    await expect(page.getByRole('button', { name: '新增外部主体' })).toBeVisible()
    // 关联状态筛选下拉
    await expect(page.getByText('关联状态').first()).toBeVisible()
  })

  test('身份匹配页可加载', async ({ page }) => {
    await page.goto('/integration/link-candidates')
    await expect(page.getByRole('button', { name: '刷新' })).toBeVisible()
  })
})

test.describe('账号安全', () => {
  test('修改密码表单与三方绑定列表可见', async ({ page }) => {
    await page.goto('/profile/security')
    await expect(page.getByLabel('当前密码')).toBeVisible()
    await expect(page.getByLabel('新密码', { exact: true })).toBeVisible()
    // 三个 OAuth 提供商（异步加载后出现；exact 避免匹配到“未绑定飞书账户”描述文案）
    await expect(page.getByText('飞书', { exact: true })).toBeVisible()
    await expect(page.getByText('企业微信', { exact: true })).toBeVisible()
    await expect(page.getByText('微信', { exact: true })).toBeVisible()
  })
})

test.describe('侧边栏导航', () => {
  test('展开外部身份分组并跳转外部身份源', async ({ page }) => {
    await page.goto('/dashboard')
    // 折叠模式下侧边栏分组默认收起，先点开触发器（button），再点子菜单链接
    await page.getByRole('button', { name: '外部身份' }).click()
    await page.getByRole('link', { name: '外部身份源' }).click()
    await expect(page).toHaveURL(/\/integration\/sources$/)
    await expect(page.getByRole('button', { name: '新增身份源' })).toBeVisible()
  })
})
