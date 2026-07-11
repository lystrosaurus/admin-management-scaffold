# 前端 — admin-management-scaffold

基于 **React 19 + TypeScript + Vite** 的管理后台 UI，配套 **Ant Design 5 + Pro Components** 与 **react-router-dom 7** 路由、**zustand** 状态管理、**axios** 请求层、**Vitest** 单测。

---

## 技术栈

| 类别 | 选型 | 版本 |
|---|---|---|
| 框架 | React + TypeScript + Vite | 19 / 6 / 8 |
| UI 组件 | Ant Design + Pro Components + Icons | 5.29 / 2.8 / 6.2 |
| 路由 | react-router-dom | 7 |
| 状态 | zustand | 5 |
| 请求 | axios + dayjs | 1 / 1 |
| 测试 | Vitest + Testing Library + jsdom | 4 / 16 / 28 |

---

## 脚本

```bash
npm install        # 安装依赖
npm run dev        # 开发服务器（Vite HMR）
npm run build      # 生产构建（tsc -b && vite build）
npm run preview    # 预览构建产物
npm run lint       # ESLint
npm run test       # 单元测试（Vitest，单次）
npm run test:watch# 单元测试（监听模式）
```

> 要求 Node.js 22+。构建前请确保后端 `http://localhost:8080/api` 可达（开发代理在 `vite.config` 中配置）。

---

## 目录结构

```text
frontend/
  src/
    api/          按模块划分的请求封装（auth / system / dict / config / integration / audit / file / ops ...）
    types/        与后端 DTO/VO 对应的 TypeScript 类型
    pages/        业务页面（dashboard / system / organization / integration / audit / files / ops ...）
    layouts/      布局组件（如 MainLayout 侧边菜单 + 顶栏）
    routes/       路由表（与菜单、权限码联动）
    components/   复用组件
    store/        zustand 全局状态（用户、权限、主题等）
    utils/        工具函数（请求拦截、token 存取等）
    assets/       静态资源
```

约定：

- `api/*` 只负责请求与类型转换，不写业务逻辑。
- `pages/*` 通过 `api/*` 取数，通过 `store/*` 读取登录用户与权限。
- 路由与菜单由 `routes/` 统一声明，并与后端 `sys_menu` / 权限码保持一致。

---

## 路由与权限

- 路由前缀约定与后端对齐：`/api/app/**` 为登录后受保护资源。
- 页面访问受登录态（Sa-Token JWT，存于前端 storage）保护；按钮级权限依据后端返回的权限码做显隐控制。
- 新增页面时：在 `routes/index.tsx` 与 `layouts/MainLayout.tsx` 中登记路由与菜单，并确认后端已配置对应权限与 `sys_menu` 种子。

---

## 请求层

- 统一通过 `axios` 实例发起请求，自动附带 JWT、统一处理 `code !== 200` 的业务错误提示。
- 响应结构：`{ code, message, data, timestamp }`，`code === 200` 视为成功。
- 典型调用：

  ```ts
  import { getDictList } from '@/api/dict';
  const { data } = await getDictList();
  ```

---

## 设计令牌（Design Tokens）

- 主题基于 Ant Design 5 的 `ConfigProvider` + CSS 变量，支持浅色主题。
- 颜色、圆角、间距遵循 Ant Design 设计语言；图标统一使用 `@ant-design/icons`。
- 若需定制品牌色，请在 `ConfigProvider` 的 `theme.token` 中调整，避免散落硬编码。

---

## 测试

- 测试框架 Vitest（jsdom 环境），配合 `@testing-library/react`。
- 组件/工具测试位于对应 `*.test.ts(x)`；运行 `npm run test`。
