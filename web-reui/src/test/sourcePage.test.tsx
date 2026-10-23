import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import SourcePage from '@/pages/integration/sources/SourcePage'

/** 渲染冒烟测试：外部身份源页（P3 迁移页）在 jsdom 中可正常渲染并展示数据 */
vi.mock('@/api/integration', () => ({
  listSources: vi.fn(() =>
    Promise.resolve([
      {
        id: 1,
        code: 'LARK',
        name: '飞书',
        sourceType: 'IM',
        tenantKey: 'tenant-a',
        priority: 10,
        status: 'ENABLED',
        configJson: '{}',
        createdAt: '2026-10-01T10:00:00',
        updatedAt: '2026-10-01T10:00:00',
      },
    ]),
  ),
  createSource: vi.fn(),
  updateSource: vi.fn(),
  deleteSource: vi.fn(),
}))

describe('SourcePage', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('renders toolbar and loads source rows', async () => {
    render(
      <MemoryRouter>
        <SourcePage />
      </MemoryRouter>,
    )

    // 工具栏与表头（“系统编码”同时出现在搜索标签与表头，取 getAllByText）
    expect(screen.getByText('新增身份源')).toBeDefined()
    expect(screen.getAllByText('系统编码').length).toBeGreaterThanOrEqual(1)
    expect(screen.getAllByText('来源类型').length).toBeGreaterThanOrEqual(1)

    // 异步加载出的数据行
    expect(await screen.findByText('飞书')).toBeDefined()
    expect(screen.getByText('LARK')).toBeDefined()
  })
})
