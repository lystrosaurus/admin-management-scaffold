# web-reui — Admin Management Scaffold（ReUI 版前端）

基于 **React 19 + TypeScript + Vite 8** 的后台管理 UI，组件体系采用 **ReUI（keenthemes/reui，shadcn 生态 registry 组件库，radix-nova 风格）**，
是 `frontend/`（Ant Design 版）的平行重构版本。后端契约完全不变：`/api/app/**`、统一响应 `{code, message, data}`、`PageResult{items,total,page,size}` 服务端分页。

## 与 frontend/（antd 版）的关系

- 两者**并行运行、互不影响**：本目录 dev 端口 `3100`（strictPort），antd 版在 3000/3001。
- `src/api/`、`src/types/`、`src/utils/token.ts`、`src/stores/authStore.ts` 由 antd 版**原样拷贝**，零重写。
- 页面按 P1→P4 分期迁移，未迁移的路由渲染 `PagePlaceholder` 占位。

## 技术栈

| 类别 | 选型 |
|---|---|
| 框架 | React 19 + TypeScript 6 + Vite 8 |
| 样式 | Tailwind CSS v4（`@tailwindcss/vite`）+ shadcn token 体系（radix-nova） |
| 组件 | shadcn/ui 原生组件 + ReUI 原语（`@reui/*`，copy-and-own） |
| 表格 | TanStack Table v9（服务端分页，`DataTable` 封装） |
| 图表 | Recharts（仪表盘资源分布） |
| 表单 | react-hook-form + zod |
| 路由 / 状态 / 请求 | react-router-dom 7 / zustand 5 / axios |
| 反馈 | sonner（toast） |
| 测试 | Vitest + Testing Library |

## 常用命令

```bash
npm install
npm run dev        # 开发服务器 http://localhost:3100（/api 代理到 8080）
npm run build      # 生产构建（tsc -b && vite build）
npm run test       # 单元测试（Vitest）
npm run test:e2e   # e2e 冒烟（Playwright，需后端 8080 在线；自动复用已运行的 dev server）
npm run preview    # 预览构建产物
```

## 安装 ReUI / shadcn 组件

```bash
# 原生 shadcn 组件（裸名）
npx shadcn@latest add <name>

# ReUI 注册项（@reui 命名空间，registry 配置见 components.json）
npx shadcn@latest add @reui/<name>
```

> 注意：本仓库根目录同时存在 `frontend/`（antd 版），shadcn CLI 的 workspace 探测会因
> `frontend` 是兄弟目录名的字符串前缀而误判。本目录名 `web-reui` 已规避该问题，
> **新增兄弟前端目录时避免以 `frontend` 开头命名**。

## 测试体系

| 层 | 命令 | 覆盖 |
|---|---|---|
| 单元测试 | `npm run test` | api/authStore/hooks 纯逻辑 + 5 个页面渲染冒烟（Vitest + Testing Library，16/16） |
| e2e 冒烟 | `npm run test:e2e` | 真实登录 → 16 个路由逐一渲染（含 pageerror 断言）→ 关键数据链路（Playwright + chromium，27/27） |

e2e 说明：
- `e2e/global-setup.ts` 走真实登录接口拿 JWT 预置 localStorage，落盘 storageState 复用；
- 配置 `reuseExistingServer`，自动复用已在 3100 运行的 dev server（需后端 8080 在线）；
- 结果输出到系统临时目录（`web-reui-playwright-results`），不污染仓库；
- 运行前需 `npx playwright install chromium`（版本不匹配时按提示安装）。

## 部署与切流

| 文件 | 用途 |
|---|---|
| `deploy/nginx.conf` | 生产 nginx：SPA fallback、`/assets/*` immutable 缓存、`/api/` 代理后端（`backend:8080`，透传 Authorization）、gzip、`/healthz` 探活 |
| `Dockerfile` | 多阶段构建：node:22-alpine 构建 → nginx:1.27-alpine 托管（含 HEALTHCHECK） |
| `.dockerignore` | 排除 node_modules/dist/e2e 产物 |
| `deploy/cutover-plan.md` | antd 版 → ReUI 版切流方案：同源替换步骤、蓝绿备选、回滚预案、切换后验证清单 |

```bash
# 容器化构建与运行（后端以 docker network 服务名 backend 可达）
docker build -t admin-web-reui:1.0.0 .
docker run -d --name admin-web -p 80:80 --add-host backend:host-gateway admin-web-reui:1.0.0
```

## 目录结构

```text
web-reui/
  src/
    api/          按模块划分的请求封装（自 frontend/ 拷贝）
    types/        与后端 DTO/VO 对应的 TypeScript 类型（自 frontend/ 拷贝）
    utils/        token 存取
    stores/       zustand 全局状态（authStore）
    hooks/        usePagedList（服务端分页）/ useClientPagedList（全量+客户端分页）
    components/
      ui/         shadcn 原生组件
      reui/       ReUI 注册项（copy-and-own 代码）
      data-table/ DataTable（TanStack v9 服务端分页/树形表格）+ 共享 features
    layouts/      MainLayout（sidebar 布局）
    routes/       路由表 + AuthGuard
    pages/        业务页面（按迁移进度推进）
    test/         Vitest 测试
```

## 共享基建（页面开发直接复用）

| 模块 | 用途 |
|---|---|
| `hooks/usePagedList` | 服务端分页列表（对齐后端 `PageResult`） |
| `hooks/useClientPagedList` | 全量拉取 + 客户端过滤/分页（后端无分页的域，如角色） |
| `components/data-table/DataTable` | TanStack v9 表格；支持服务端分页与树形（`getSubRows`）两种模式 |
| `components/FormDialog` | RHF 表单弹窗（表单实例由页面创建传入） |
| `components/ConfirmDialog` | 删除等危险操作二次确认 |
| `components/StatusBadge` | 状态徽章（兼容 ACTIVE/INACTIVE 与 ENABLED/DISABLED 口径） |
| `components/PermissionAssignDialog` | 角色权限分配（按 MENU/BUTTON/API 分组勾选） |

新页面开发标准流程：选 `usePagedList` 或 `useClientPagedList` 拉数 → `createColumnHelper<typeof coreTableFeatures, T>()` 建列 → `DataTable` 渲染 → `FormDialog`/`ConfirmDialog` 处理交互。参考实现见 `src/pages/users/UserListPage.tsx`。

## 迁移进度

- [x] P0：脚手架、API/类型/状态层搬迁、登录页、主布局、主题切换、路由守卫
- [x] P1：系统核心域（用户 / 角色 / 菜单 / 字典 / 配置项 全部完成，含角色权限分配弹窗、菜单树形表格）
- [x] P2：组织与审计（员工 / 组织单元 / 审计日志，含 TreeSelectPopover 通用树选择器）
- [x] P3：集成 / 文件 / 运维 / 个人中心 + 仪表盘
  - 外部身份源（SourcePage）：全量拉取 + 名称/编码前端过滤，新增/编辑/删除
  - 外部主体（PrincipalPage）：sourceId/linkStatus 过滤，身份源下拉，映射目标编辑
  - 身份匹配（LinkCandidatePage）：待处理候选确认/拒绝（ConfirmDialog 二次确认）
  - 文件管理（FileManagePage）：服务端分页 + 隐藏 file input 上传 + 带 Bearer 的 fetch 下载
  - 系统状态（SystemStatusPage）：ops info 统计卡 + 详情列表
  - 账号安全（SecurityProfilePage）：修改密码（含确认密码校验）+ 三方账号绑定/解绑
  - 系统配置（SettingsPage）：ops 系统信息 + 本地配置表单 + 身份源管理（integrationSource API）
  - 仪表盘（DashboardPage）：dashboard stats 统计卡 + Recharts 资源分布图 + 最近操作日志（真实数据）
- [x] P4：e2e、部署与切流准备
  - Playwright e2e：登录流程 + 16 路由全渲染（pageerror 断言）+ 关键数据链路，**27/27 通过**
  - `deploy/nginx.conf`：SPA fallback / assets immutable 缓存 / `/api` 代理（透传 Authorization）/ gzip / healthz
  - `Dockerfile`：node:22-alpine 构建 → nginx:1.27-alpine 托管（多阶段 + HEALTHCHECK）
  - `deploy/cutover-plan.md`：同源替换步骤、蓝绿备选、回滚预案、切换后验证清单
  - 修复 e2e 暴露的真实 bug：侧边栏「集成管理」分组缺 `CollapsibleTrigger`，折叠后无法展开

> 已验证：`tsc + vite build` 通过、16/16 单元测试通过（含 4 个 P3 新页面渲染冒烟测试）、27/27 Playwright e2e 通过、
> 经 3100 代理真实登录与 dashboard/ops/files/integration×3/profile/security 全部接口 200 打通，
> 返回结构与类型定义逐一核对一致。构建产物按路由分包（17 个页面 chunk + vendor-dataviz 独立 chunk）。
>
> **已知后端问题（需 Trae 侧排查）**：`GET /api/app/employees` 列表接口在查询命中员工记录时返回 500
> （空结果 200；员工详情 `/api/app/employees/{id}` 正常）。已排除表结构、实体字段、MapStruct 字节码、
> 参数名与权限校验；堆栈需查看 Trae 后端进程控制台（GlobalExceptionHandler 有 log.error）。
> 另外已修正前端员工搜索参数契约：后端实际只认 `keyword`（模糊匹配姓名/工号/手机号）、`employmentStatus`、`orgId`，
> antd 版传的 name/employeeNo/status 均被忽略导致搜索失效。
