# Sprint 迭代计划 — Admin Management Scaffold（M7: 前后端联调与端到端验证）

## 项目概览

| 属性 | 值 |
|------|-----|
| 项目启动日期 | 2026-05-25（周一） |
| 预计完成日期 | 2026-06-07（周六） |
| Sprint 长度 | 1 周（5 个工作日） |
| 团队规模 | 1 人（Tech Lead 统筹 + 单 Agent 执行） |
| 总 Sprint 数量 | 2 |
| 总估算工时 | 60h（含 20% 风险缓冲） |
| 前置条件 | M6 已完成（前端 8 个页面 + 后端 617 tests），但前后端从未联调 |

## M7 目标

> **核心目标**：让前后端真正跑通，所有页面对接真实 API，消灭 mock 数据，确保端到端流程可用。

### 具体交付标准

1. ✅ 前后端联合启动成功，所有页面可访问
2. ✅ 登录流程端到端跑通（密码登录 + OAuth 登录）
3. ✅ 所有 CRUD 页面对接真实 API，分页/搜索/增删改查正常
4. ✅ 账号安全页面对接真实 API（绑定/解绑/改密/登录记录）
5. ✅ 仪表盘页面从 mock 切换为真实 API
6. ✅ 系统配置页面从 mock 切换为真实 API（如有对应后端接口）
7. ✅ 前端代码分割优化（antd 按需加载、路由懒加载）
8. ✅ 关键流程前端测试覆盖

## 发现的前后端不一致清单

> 以下问题在 M7 规划阶段通过代码审查发现，需在联调过程中逐一修复。

### P0 阻塞级（登录/认证完全不工作）

| 编号 | 问题 | 后端实际 | 前端预期 | 影响 |
|------|------|---------|---------|------|
| D-001 | 登录接口路径 | `POST /public/auth/login` | `POST /auth/login` | 登录完全失败 |
| D-002 | 登出接口路径 | `POST /public/auth/logout` | `POST /auth/logout` | 登出失败 |
| D-003 | LoginResponse 类型不匹配 | `{accessToken, user: UserVO}` | `{accessToken, tokenType, expiresIn}` | 登录成功后无法正确解析 token |
| D-004 | UserProfile 类型不匹配 | `{user: UserVO, roles[], permissions[], menus[]}` (嵌套) | `{id, username, nickname, ...}` (扁平) | 用户信息加载失败 |

### P1 严重级（CRUD 列表页面不工作）

| 编号 | 问题 | 后端实际 | 前端预期 | 影响 |
|------|------|---------|---------|------|
| D-005 | 分页结果字段名不匹配 | `{items, total, page, size}` | `{records, total, current, size}` | 所有列表页无法渲染数据 |
| D-006 | 分页查询参数名不匹配 | `page` + `size` | `current` + `size` | 查询参数错误 |
| D-007 | OAuth 解绑方式不匹配 | `DELETE /unbind?accountId=xxx` (Query) | `POST /unbind` + body `{accountId}` | 解绑失败 |
| D-008 | OAuth 账号列表接口缺失 | 使用 `GET /app/profile/security` | 调用 `GET /app/auth/oauth/accounts` | 账号安全页加载失败 |

### P2 一般级（部分功能受限）

| 编号 | 问题 | 后端实际 | 前端预期 | 影响 |
|------|------|---------|---------|------|
| D-009 | 修改密码 HTTP 方法 | `PUT /app/profile/password` | `POST /app/profile/password` | 改密失败 |
| D-010 | 仪表盘无后端 API | 无统计接口 | mock 数据 | 仪表盘显示假数据 |
| D-011 | 系统配置无后端 API | 无配置接口 | mock + setTimeout | 配置页无法持久化 |
| D-012 | OAuthAccount 类型不匹配 | `ExternalAccountVO` (12 个字段) | `OAuthAccount` (5 个字段) | 账号安全页字段缺失 |
| D-013 | 前端无测试覆盖 | - | - | 质量风险 |
| D-014 | 前端无代码分割 | - | - | chunk 大小警告 |

## Sprint 总览

| Sprint | 日期范围 | 主题 | 目标 | 完成标准 |
|--------|---------|------|------|---------|
| Sprint 1 | 05-25 ~ 05-31 | API 对齐 + 认证联调 + CRUD 联调 | 修复所有 P0/P1 不一致，所有页面对接真实 API | 登录/登出正常；所有 CRUD 列表页可正常增删改查；分页/搜索正常 |
| Sprint 2 | 06-01 ~ 06-07 | 功能补全 + 优化 + 测试 | 补全 P2 问题，代码优化，关键测试 | 仪表盘/配置页对接 API；代码分割完成；关键流程测试通过 |

## 详细 Sprint 计划

### Sprint 1: API 对齐 + 认证联调 + CRUD 联调

**日期**: 2026-05-25 ~ 2026-05-31
**容量**: 40h（5 个工作日 × 8h）
**目标**: 修复所有 P0/P1 前后端不一致问题，让登录流程和所有 CRUD 页面对接真实 API

#### 用户故事

| 故事ID | 标题 | 优先级 | 估算(小时) |
|--------|------|--------|-----------|
| US-M7-01 | 作为用户，我可以通过前端页面使用账号密码登录系统 | P0 | 8 |
| US-M7-02 | 作为管理员，我可以通过用户管理页面对真实数据进行增删改查 | P0 | 6 |
| US-M7-03 | 作为管理员，我可以通过角色/菜单/员工/组织管理页面对真实数据进行增删改查 | P0 | 10 |
| US-M7-04 | 作为用户，我可以在账号安全页面查看真实的绑定状态和登录记录 | P1 | 6 |

#### 任务列表

| 任务ID | 任务描述 | 关联故事 | 估算(h) | 依赖 | 并行 |
|--------|---------|---------|--------|------|------|
| T-087 | 联调环境搭建（启动后端 + 前端，验证代理连通） | US-M7-01 | 3 | - | - |
| T-088 | 修复登录/登出接口路径（D-001, D-002） | US-M7-01 | 2 | T-087 | ✅ T-089 |
| T-089 | 修复 LoginResponse 类型对齐（D-003） | US-M7-01 | 3 | T-087 | ✅ T-088 |
| T-090 | 修复 UserProfile / ProfileVO 类型对齐（D-004） | US-M7-01 | 4 | T-089 | - |
| T-091 | 修复分页结果字段名 + 查询参数名（D-005, D-006） | US-M7-02, US-M7-03 | 4 | T-087 | ✅ T-088~T-090 |
| T-092 | 端到端验证：登录 → 获取 profile → 显示菜单 → 路由守卫 | US-M7-01 | 3 | T-088, T-089, T-090 | - |
| T-093 | 端到端验证：用户管理 CRUD（列表/搜索/新增/编辑/删除） | US-M7-02 | 3 | T-091, T-092 | - |
| T-094 | 端到端验证：角色管理 CRUD + 权限分配 | US-M7-03 | 3 | T-091, T-092 | ✅ T-093 |
| T-095 | 端到端验证：菜单管理 CRUD（树形） | US-M7-03 | 2 | T-091, T-092 | ✅ T-093, T-094 |
| T-096 | 端到端验证：员工管理 CRUD | US-M7-03 | 2 | T-091, T-092 | ✅ T-093~T-095 |
| T-097 | 端到端验证：组织管理 CRUD（树形） | US-M7-03 | 2 | T-091, T-092 | ✅ T-093~T-096 |
| T-098 | 修复 OAuth 解绑方式 + 账号列表接口（D-007, D-008, D-012） | US-M7-04 | 4 | T-092 | ✅ T-093~T-097 |
| T-099 | 修复修改密码 HTTP 方法（D-009） | US-M7-04 | 1 | T-092 | ✅ T-098 |
| T-100 | 端到端验证：账号安全页面（绑定/解绑/改密/登录记录） | US-M7-04 | 2 | T-098, T-099 | - |

#### 风险与阻碍

| 类型 | 描述 | 影响 | 缓解策略 |
|------|------|------|---------|
| 集成风险 | 后端启动依赖 MySQL/Redis 环境 | 高 | 确认本地数据库环境就绪，或使用 docker-compose |
| 集成风险 | Sa-Token JWT 配置与前端 token 存储不一致 | 高 | 联调时逐一验证 token 传递链路 |
| 集成风险 | CORS 配置缺失导致前端无法调用后端 | 中 | vite proxy 已配置 `/api` 代理到 `localhost:8080` |

---

### Sprint 2: 功能补全 + 优化 + 测试

**日期**: 2026-06-01 ~ 2026-06-07
**容量**: 20h（5 个工作日，部分时间用于 Sprint 1 收尾）
**目标**: 补全 P2 问题，完成代码优化和关键测试

#### 用户故事

| 故事ID | 标题 | 优先级 | 估算(小时) |
|--------|------|--------|-----------|
| US-M7-05 | 作为管理员，我可以在仪表盘看到真实的系统统计数据 | P2 | 6 |
| US-M7-06 | 作为管理员，我可以在系统配置页面管理 OAuth Provider | P1 | 6 |
| US-M7-07 | 作为开发者，我希望前端加载速度优化，无 chunk 大小警告 | P1 | 4 |
| US-M7-08 | 作为开发者，我希望关键流程有测试覆盖 | P2 | 6 |

#### 任务列表

| 任务ID | 任务描述 | 关联故事 | 估算(h) | 依赖 | 并行 |
|--------|---------|---------|--------|------|------|
| T-101 | 后端新增仪表盘统计 API（用户/员工/组织/今日登录数） | US-M7-05 | 4 | T-092 | ✅ T-103 |
| T-102 | 前端仪表盘对接真实 API，替换 mock 数据 | US-M7-05 | 2 | T-101 | - |
| T-103 | 后端新增系统配置 API（OAuth Provider CRUD）或对齐现有接口 | US-M7-06 | 4 | T-092 | ✅ T-101 |
| T-104 | 前端系统配置页面对接真实 API | US-M7-06 | 2 | T-103 | - |
| T-105 | 前端代码分割优化（路由懒加载 + antd 按需加载 + chunk 优化） | US-M7-07 | 4 | T-092 | ✅ T-101~T-104 |
| T-106 | 前端关键流程测试（登录流程 + 用户 CRUD 流程） | US-M7-08 | 6 | T-092 | ✅ T-105 |

#### 风险与阻碍

| 类型 | 描述 | 影响 | 缓解策略 |
|------|------|------|---------|
| 需求风险 | 仪表盘统计 API 需求不明确 | 中 | MVP：只返回总数统计，不做复杂查询 |
| 技术风险 | antd 5 按需加载配置复杂度 | 低 | 使用 babel-plugin-import 或 unplugin 自动导入 |
| 时间风险 | Sprint 1 可能溢出到 Sprint 2 | 中 | Sprint 2 任务按优先级排序，P2 可裁剪 |

## 里程碑

| 里程碑 | 日期 | 交付物 | 验收标准 |
|--------|------|--------|---------|
| M7.1: 认证联调通过 | 05-25 | 登录/登出/Profile 端到端跑通 | 用户可通过前端登录，看到正确的个人信息和菜单 |
| M7.2: CRUD 联调通过 | 05-28 | 所有 CRUD 页面对接真实 API | 用户/角色/菜单/员工/组织管理页面增删改查正常 |
| M7.3: 功能补全完成 | 06-03 | 仪表盘/配置页对接 API；代码优化 | 全部页面使用真实数据；前端无 chunk 警告 |
| M7.4: M7 完成 | 06-07 | 测试通过；全部联调完成 | 关键流程测试覆盖 ≥ 60%；所有页面端到端可用 |

## 关键依赖关系

```
T-087 (环境搭建)
  ├─→ T-088 (修复登录路径) ──┬──→ T-090 (修复 Profile 类型) ──→ T-092 (登录 E2E 验证)
  │                          │                                      │
  ├─→ T-089 (修复 LoginResp) ┘                                      │
  │                                                                  │
  └─→ T-091 (修复分页字段) ─────────────────────────────────────────→ T-093~T-097 (CRUD E2E)
                                                                      │
  T-092 ──→ T-098 (修复 OAuth) ──→ T-099 (修复改密) ──→ T-100 (账号安全 E2E)
                                                                      │
  T-092 ──→ T-101 (仪表盘 API) ──→ T-102 (仪表盘对接)
  T-092 ──→ T-103 (配置 API) ──→ T-104 (配置对接)
  T-092 ──→ T-105 (代码优化)
  T-092 ──→ T-106 (前端测试)
```

**关键路径**: T-087 → T-088/T-089 → T-090 → T-092 → T-093~T-097 → T-100 → T-105 → T-106（约 40h）

## API 对齐修复方案汇总

### D-001/D-002: 登录/登出路径修复

**方案**: 修改前端 `api/auth.ts`

```typescript
// 修复前
export const login = (data: LoginRequest): Promise<LoginResponse> => {
  return post<LoginResponse>('/auth/login', data);
};

// 修复后
export const login = (data: LoginRequest): Promise<LoginResponse> => {
  return post<LoginResponse>('/public/auth/login', data);
};
```

### D-003: LoginResponse 类型对齐

**方案**: 修改前端 `types/auth.ts` 的 `LoginResponse`，对齐后端 `LoginVO`

```typescript
// 修复前
export interface LoginResponse {
  accessToken: string;
  tokenType: string;
  expiresIn: number;
}

// 修复后（对齐后端 LoginVO）
export interface LoginResponse {
  accessToken: string;
  user: UserProfile;  // 后端直接返回 UserVO
}
```

**影响范围**: `authStore.ts` 的 `login` 方法需要适配

### D-004: UserProfile 类型对齐

**方案**: 修改前端 `types/auth.ts` 的 `UserProfile`，对齐后端 `ProfileVO`

```typescript
// 修复前：扁平结构
export interface UserProfile {
  id: number;
  username: string;
  nickname: string;
  // ...
  roles: string[];
  permissions: string[];
}

// 修复后：对齐后端 ProfileVO 嵌套结构
export interface UserProfile {
  user: UserVO;       // 嵌套的用户信息
  roles: string[];
  permissions: string[];
  menus: MenuVO[];
}
```

**影响范围**: `authStore.ts`、`MainLayout.tsx`（菜单渲染）、所有权限检查逻辑

### D-005/D-006: 分页字段名对齐

**方案**: 修改前端 `types/api.ts` 的 `PageResult`，对齐后端 `PageResult`

```typescript
// 修复前
export interface PageResult<T> {
  records: T[];
  total: number;
  current: number;
  size: number;
}

// 修复后（对齐后端）
export interface PageResult<T> {
  items: T[];
  total: number;
  page: number;
  size: number;
}
```

**影响范围**: 所有列表页面（用户/角色/菜单/员工/组织），需修改数据读取字段名

### D-007: OAuth 解绑方式修复

**方案**: 修改前端 `api/oauth.ts`

```typescript
// 修复前
export const unbindAccount = (provider: string, accountId: number): Promise<void> => {
  return post<void>(`/app/auth/oauth/${provider}/unbind`, { accountId });
};

// 修复后（对齐后端 DELETE + Query 参数）
export const unbindAccount = (provider: string, accountId: number): Promise<void> => {
  return del<void>(`/app/auth/oauth/${provider}/unbind`, { params: { accountId } });
};
```

### D-008: OAuth 账号列表接口对齐

**方案**: 前端改用 `GET /app/profile/security` 获取绑定账号列表

```typescript
// 修复前
export const listBoundAccounts = (): Promise<OAuthAccount[]> => {
  return get<OAuthAccount[]>('/app/auth/oauth/accounts');
};

// 修复后
export const getSecurityProfile = (): Promise<SecurityProfileVO> => {
  return get<SecurityProfileVO>('/app/profile/security');
};
```

### D-009: 修改密码 HTTP 方法修复

**方案**: 修改前端 `api/auth.ts`

```typescript
// 修复前
export const changePassword = (data: {...}): Promise<void> => {
  return post<void>('/app/profile/password', data);
};

// 修复后
export const changePassword = (data: {...}): Promise<void> => {
  return put<void>('/app/profile/password', data);
};
```

## 变更日志

| 版本 | 日期 | 变更内容 | 变更人 |
|------|------|---------|--------|
| v1.0 | 2026-05-24 | M7 初始版本 | tech-lead |
