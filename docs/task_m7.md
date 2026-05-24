# 详细任务清单 — Admin Management Scaffold（M7: 前后端联调与端到端验证）

## 说明

本文档包含 M7 所有可执行任务的详细定义。M7 核心目标是**前后端联调**——修复所有接口不一致问题，让所有页面对接真实 API。

**M7 范围**：
- ✅ 联调环境搭建（后端 + 前端联合启动）
- ✅ 登录/登出接口路径修复
- ✅ LoginResponse / UserProfile 类型对齐
- ✅ 分页字段名 / 查询参数名对齐
- ✅ 所有 CRUD 页面端到端验证
- ✅ OAuth 绑定/解绑接口对齐
- ✅ 修改密码接口对齐
- ✅ 仪表盘对接真实 API
- ✅ 系统配置对接真实 API
- ✅ 前端代码分割优化
- ✅ 关键流程测试

## 任务状态图例

- 📋 待开始 (Backlog)
- 🔵 计划中 (Planned)
- 🟡 进行中 (In Progress)
- 🟢 已完成 (Done)
- ⛔ 阻塞 (Blocked)

## 任务清单

---

### Epic 1: 联调环境搭建（EP-020）

**目标**: 搭建前后端联合开发环境，验证连通性
**关联需求**: US-M7-01
**预计跨度**: Sprint 1 第 1 天

---

#### T-087: 联调环境搭建（启动后端 + 前端，验证代理连通）

| 属性 | 值 |
|------|-----|
| 状态 | 📋 待开始 |
| 优先级 | P0 |
| 估算工时 | 2.5h |
| 风险缓冲 | +0.5h |
| 总工时 | 3h |
| 关联故事 | US-M7-01 |
| Sprint | Sprint 1 |
| 依赖 | 无 |
| 可并行 | ❌ |

**任务描述**:
启动后端 Spring Boot 应用和前端 Vite 开发服务器，验证代理配置正确，前后端可以通信。

**实施步骤**:
1. 确认本地 MySQL 和 Redis 服务运行中
2. 启动后端：`cd backend && mvn spring-boot:run`
3. 验证后端启动成功：`curl http://localhost:8080/api/public/auth/login`（应返回 405 Method Not Allowed，说明路径可达）
4. 启动前端：`cd frontend && npm run dev`
5. 验证代理连通：前端 `http://localhost:3000/api/public/auth/login` 能到达后端
6. 记录启动过程中的任何问题

**验收标准**:
- [ ] 后端 `http://localhost:8080` 启动成功，健康检查通过
- [ ] 前端 `http://localhost:3000` 启动成功，页面可访问
- [ ] Vite 代理 `/api` → `http://localhost:8080` 连通
- [ ] 后端数据库迁移（Flyway）执行成功
- [ ] Redis 连接正常

**技术备注**:
- Vite 配置已存在：`proxy: { '/api': { target: 'http://localhost:8080', changeOrigin: true } }`
- 后端 Spring Boot 端口默认 8080
- 如果 MySQL/Redis 不可用，需先启动 docker-compose 或本地服务

---

### Epic 2: 认证接口对齐（EP-021）

**目标**: 修复登录/登出/Profile 接口的所有不一致问题
**关联需求**: US-M7-01
**预计跨度**: Sprint 1 第 1~2 天

---

#### T-088: 修复登录/登出接口路径（D-001, D-002）

| 属性 | 值 |
|------|-----|
| 状态 | 📋 待开始 |
| 优先级 | P0 |
| 估算工时 | 1.5h |
| 风险缓冲 | +0.5h |
| 总工时 | 2h |
| 关联故事 | US-M7-01 |
| Sprint | Sprint 1 |
| 依赖 | T-087 |
| 可并行 | ✅ 与 T-089 并行 |

**问题描述**:
- 后端登录路径：`POST /api/public/auth/login`
- 前端调用路径：`POST /api/auth/login`（缺失 `/public` 前缀）
- 后端登出路径：`POST /api/public/auth/logout`
- 前端调用路径：`POST /api/auth/logout`

**修复内容**:
修改 `frontend/src/api/auth.ts`：

```typescript
// 登录：'/auth/login' → '/public/auth/login'
export const login = (data: LoginRequest): Promise<LoginResponse> => {
  return post<LoginResponse>('/public/auth/login', data);
};

// 登出：'/auth/logout' → '/public/auth/logout'
export const logout = (): Promise<void> => {
  return post<void>('/public/auth/logout');
};
```

**验收标准**:
- [ ] 前端登录请求路径为 `POST /api/public/auth/login`
- [ ] 前端登出请求路径为 `POST /api/public/auth/logout`
- [ ] 后端能正确接收请求（不再返回 404）
- [ ] 登录成功返回 200 + LoginVO 数据

**子任务**:
- [ ] ST-088-01: 修改 `api/auth.ts` 登录路径 (0.5h)
- [ ] ST-088-02: 修改 `api/auth.ts` 登出路径 (0.5h)
- [ ] ST-088-03: 浏览器验证请求路径正确 (0.5h)

---

#### T-089: 修复 LoginResponse 类型对齐（D-003）

| 属性 | 值 |
|------|-----|
| 状态 | 📋 待开始 |
| 优先级 | P0 |
| 估算工时 | 2.5h |
| 风险缓冲 | +0.5h |
| 总工时 | 3h |
| 关联故事 | US-M7-01 |
| Sprint | Sprint 1 |
| 依赖 | T-087 |
| 可并行 | ✅ 与 T-088 并行 |

**问题描述**:
- 后端 `LoginVO` 返回：`{ accessToken: String, user: UserVO }`
- 前端 `LoginResponse` 期望：`{ accessToken: string, tokenType: string, expiresIn: number }`
- 后端直接返回用户信息，前端期望的 `tokenType` 和 `expiresIn` 不存在

**修复内容**:

1. 修改 `frontend/src/types/auth.ts`：

```typescript
// 修复前
export interface LoginResponse {
  accessToken: string;
  tokenType: string;
  expiresIn: number;
}

// 修复后
export interface LoginResponse {
  accessToken: string;
  user: LoginUserVO;
}

/** 登录返回的用户信息（对齐后端 UserVO） */
export interface LoginUserVO {
  id: number;
  username: string;
  nickname: string;
  phone?: string;
  email?: string;
  status: string;
  lastLoginAt?: string;
  createdAt: string;
}
```

2. 修改 `frontend/src/stores/authStore.ts` 的 `login` 方法：

```typescript
login: async (username: string, password: string) => {
  set({ loading: true });
  try {
    const response = await authApi.login({ username, password });
    // 保存 token
    setToken(response.accessToken);
    set({
      token: response.accessToken,
      isAuthenticated: true,
    });
    // 登录成功后获取完整用户资料（含角色、权限、菜单）
    await get().fetchProfile();
  } finally {
    set({ loading: false });
  }
},
```

**验收标准**:
- [ ] `LoginResponse` 类型与后端 `LoginVO` 完全匹配
- [ ] 登录成功后 `accessToken` 正确存储到 localStorage
- [ ] 登录成功后自动调用 `fetchProfile()` 获取完整用户信息
- [ ] TypeScript 编译无类型错误

**子任务**:
- [ ] ST-089-01: 修改 `types/auth.ts` LoginResponse 类型 (0.5h)
- [ ] ST-089-02: 新增 `LoginUserVO` 类型定义 (0.5h)
- [ ] ST-089-03: 修改 `stores/authStore.ts` login 方法适配 (1h)
- [ ] ST-089-04: TypeScript 编译验证 (0.5h)

---

#### T-090: 修复 UserProfile / ProfileVO 类型对齐（D-004）

| 属性 | 值 |
|------|-----|
| 状态 | 📋 待开始 |
| 优先级 | P0 |
| 估算工时 | 3.5h |
| 风险缓冲 | +0.5h |
| 总工时 | 4h |
| 关联故事 | US-M7-01 |
| Sprint | Sprint 1 |
| 依赖 | T-089 |
| 可并行 | ❌ |

**问题描述**:
- 后端 `ProfileVO` 返回：`{ user: UserVO, roles: string[], permissions: string[], menus: MenuVO[] }`（嵌套结构）
- 前端 `UserProfile` 期望：`{ id, username, nickname, avatar, email, phone, roles, permissions }`（扁平结构）
- 前端缺少 `menus` 字段（侧边栏菜单需要）

**修复内容**:

1. 修改 `frontend/src/types/auth.ts`：

```typescript
// 修复前
export interface UserProfile {
  id: number;
  username: string;
  nickname: string;
  avatar?: string;
  email?: string;
  phone?: string;
  roles: string[];
  permissions: string[];
}

// 修复后（对齐后端 ProfileVO）
export interface UserProfile {
  user: ProfileUserVO;
  roles: string[];
  permissions: string[];
  menus: MenuVO[];
}

/** Profile 中的用户信息（对齐后端 UserVO） */
export interface ProfileUserVO {
  id: number;
  username: string;
  nickname: string;
  phone?: string;
  email?: string;
  status: string;
  lastLoginAt?: string;
  createdAt: string;
}
```

2. 新增 `frontend/src/types/menu.ts` 中的 `MenuVO` 类型（如不存在）：

```typescript
export interface MenuVO {
  id: number;
  parentId?: number;
  name: string;
  path?: string;
  component?: string;
  icon?: string;
  permission?: string;
  sort: number;
  visible: boolean;
  children?: MenuVO[];
}
```

3. 修改所有引用 `UserProfile` 的地方：
   - `stores/authStore.ts`：适配嵌套结构
   - `layouts/MainLayout.tsx`：从 `profile.menus` 获取菜单
   - 权限检查：从 `profile.permissions` 获取权限列表
   - 用户信息显示：从 `profile.user` 获取用户名等

**影响范围**:
- `frontend/src/stores/authStore.ts`
- `frontend/src/layouts/MainLayout.tsx`
- `frontend/src/routes/AuthGuard.tsx`
- `frontend/src/hooks/useAuth.ts`
- 任何引用 `user.username`、`user.nickname` 等的地方需改为 `user.user.username`

**验收标准**:
- [ ] `UserProfile` 类型与后端 `ProfileVO` 完全匹配
- [ ] `fetchProfile()` 成功获取并存储用户信息、角色、权限、菜单
- [ ] 侧边栏菜单从 `profile.menus` 正确渲染
- [ ] 权限检查从 `profile.permissions` 正确工作
- [ ] 用户名等信息从 `profile.user` 正确显示
- [ ] TypeScript 编译无类型错误

**子任务**:
- [ ] ST-090-01: 修改 `types/auth.ts` UserProfile 类型 (0.5h)
- [ ] ST-090-02: 新增 ProfileUserVO / MenuVO 类型 (0.5h)
- [ ] ST-090-03: 修改 `stores/authStore.ts` 适配嵌套结构 (1h)
- [ ] ST-090-04: 修改 `layouts/MainLayout.tsx` 菜单渲染 (0.5h)
- [ ] ST-090-05: 修改 `hooks/useAuth.ts` 权限检查 (0.5h)
- [ ] ST-090-06: 全局搜索并修复所有 UserProfile 引用 (0.5h)

---

#### T-091: 修复分页结果字段名 + 查询参数名（D-005, D-006）

| 属性 | 值 |
|------|-----|
| 状态 | 📋 待开始 |
| 优先级 | P0 |
| 估算工时 | 3.5h |
| 风险缓冲 | +0.5h |
| 总工时 | 4h |
| 关联故事 | US-M7-02, US-M7-03 |
| Sprint | Sprint 1 |
| 依赖 | T-087 |
| 可并行 | ✅ 与 T-088~T-090 并行 |

**问题描述**:
- 后端 `PageResult`：`{ items: List<T>, total: long, page: int, size: int }`
- 前端 `PageResult`：`{ records: T[], total: number, current: number, size: number }`
- 字段名不匹配：`items` vs `records`，`page` vs `current`
- 查询参数不匹配：后端期望 `page`，前端发送 `current`

**修复内容**:

1. 修改 `frontend/src/types/api.ts`：

```typescript
// 修复前
export interface PageResult<T> {
  records: T[];
  total: number;
  current: number;
  size: number;
}

// 修复后（对齐后端 PageResult）
export interface PageResult<T> {
  items: T[];
  total: number;
  page: number;
  size: number;
}
```

2. 修改所有列表页面的数据读取：
   - `result.records` → `result.items`
   - `result.current` → `result.page`

3. 修改所有 API 查询参数：
   - `current` → `page`（发送给后端的参数名）

**影响范围**（所有列表页面）:
- `frontend/src/api/user.ts`：查询参数 `current` → `page`
- `frontend/src/api/role.ts`：同上
- `frontend/src/api/menu.ts`：同上
- `frontend/src/api/employee.ts`：同上
- `frontend/src/api/orgUnit.ts`：同上
- `frontend/src/pages/users/UserListPage.tsx`：`result.records` → `result.items`
- `frontend/src/pages/roles/RoleListPage.tsx`：同上
- `frontend/src/pages/menus/MenuListPage.tsx`：同上
- `frontend/src/pages/employees/EmployeeListPage.tsx`：同上
- `frontend/src/pages/org-units/OrgUnitListPage.tsx`：同上

**验收标准**:
- [ ] `PageResult` 类型与后端完全匹配
- [ ] 所有列表页面查询参数使用 `page` 而非 `current`
- [ ] 所有列表页面数据读取使用 `items` 而非 `records`
- [ ] TypeScript 编译无类型错误
- [ ] 用户管理列表页可正常加载数据

**子任务**:
- [ ] ST-091-01: 修改 `types/api.ts` PageResult 类型 (0.5h)
- [ ] ST-091-02: 修改所有 API 文件查询参数 (1h)
- [ ] ST-091-03: 修改所有列表页面数据读取 (1h)
- [ ] ST-091-04: TypeScript 编译验证 (0.5h)
- [ ] ST-091-05: 浏览器验证列表页数据加载 (0.5h)

---

### Epic 3: 端到端验证（EP-022）

**目标**: 逐一验证每个页面的端到端功能
**关联需求**: US-M7-01, US-M7-02, US-M7-03, US-M7-04
**预计跨度**: Sprint 1 第 2~5 天

---

#### T-092: 端到端验证：登录 → 获取 Profile → 显示菜单 → 路由守卫

| 属性 | 值 |
|------|-----|
| 状态 | 📋 待开始 |
| 优先级 | P0 |
| 估算工时 | 2.5h |
| 风险缓冲 | +0.5h |
| 总工时 | 3h |
| 关联故事 | US-M7-01 |
| Sprint | Sprint 1 |
| 依赖 | T-088, T-089, T-090 |
| 可并行 | ❌ |

**任务描述**:
验证完整的登录流程：输入用户名密码 → 调用登录 API → 获取 token → 获取 Profile → 渲染侧边栏菜单 → 路由守卫生效。

**验证步骤**:

1. **登录流程**:
   - 打开 `http://localhost:3000/login`
   - 输入用户名和密码
   - 点击登录按钮
   - 验证：请求 `POST /api/public/auth/login` 返回 200
   - 验证：`accessToken` 存储到 localStorage
   - 验证：自动跳转到 `/dashboard`

2. **Profile 加载**:
   - 登录成功后，验证请求 `GET /api/app/profile` 返回 200
   - 验证：用户信息正确显示在顶栏
   - 验证：角色和权限信息正确存储

3. **菜单渲染**:
   - 验证：侧边栏菜单从 `profile.menus` 渲染
   - 验证：菜单项显示正确（名称、图标、路由）
   - 验证：无权限的菜单不显示

4. **路由守卫**:
   - 未登录时访问 `/dashboard`，验证重定向到 `/login`
   - 登录后访问 `/login`，验证重定向到 `/dashboard`
   - 访问无权限的页面，验证显示 403 或重定向

5. **登出流程**:
   - 点击登出按钮
   - 验证：请求 `POST /api/public/auth/logout` 返回 200
   - 验证：localStorage 中 token 清除
   - 验证：重定向到 `/login`

**验收标准**:
- [ ] 登录成功，token 正确存储
- [ ] Profile 正确加载，用户信息显示
- [ ] 侧边栏菜单正确渲染
- [ ] 路由守卫生效
- [ ] 登出成功，状态清除

**子任务**:
- [ ] ST-092-01: 验证登录流程 (0.5h)
- [ ] ST-092-02: 验证 Profile 加载 (0.5h)
- [ ] ST-092-03: 验证菜单渲染 (0.5h)
- [ ] ST-092-04: 验证路由守卫 (0.5h)
- [ ] ST-092-05: 验证登出流程 (0.5h)

---

#### T-093: 端到端验证：用户管理 CRUD（列表/搜索/新增/编辑/删除）

| 属性 | 值 |
|------|-----|
| 状态 | 📋 待开始 |
| 优先级 | P0 |
| 估算工时 | 2.5h |
| 风险缓冲 | +0.5h |
| 总工时 | 3h |
| 关联故事 | US-M7-02 |
| Sprint | Sprint 1 |
| 依赖 | T-091, T-092 |
| 可并行 | ✅ 与 T-094~T-097 并行 |

**任务描述**:
验证用户管理页面的所有 CRUD 功能对接真实 API。

**验证步骤**:

1. **列表加载**:
   - 访问 `/users`
   - 验证：请求 `GET /api/app/users?page=1&size=10` 返回 200
   - 验证：表格正确渲染用户列表
   - 验证：分页信息正确（总数、当前页）

2. **搜索**:
   - 输入用户名搜索条件
   - 点击搜索
   - 验证：请求包含搜索参数
   - 验证：列表正确过滤

3. **新增用户**:
   - 点击"新增用户"
   - 填写表单（用户名、昵称、密码、邮箱、手机、状态）
   - 提交
   - 验证：请求 `POST /api/app/users` 返回 200
   - 验证：列表刷新，新用户出现

4. **编辑用户**:
   - 点击某用户的"编辑"
   - 修改昵称
   - 提交
   - 验证：请求 `PUT /api/app/users/{id}` 返回 200
   - 验证：列表刷新，修改生效

5. **删除用户**:
   - 点击某用户的"删除"
   - 确认删除
   - 验证：请求 `DELETE /api/app/users/{id}` 返回 200
   - 验证：列表刷新，用户消失

**验收标准**:
- [ ] 用户列表正确加载
- [ ] 搜索功能正常
- [ ] 新增用户成功
- [ ] 编辑用户成功
- [ ] 删除用户成功
- [ ] 分页切换正常

**子任务**:
- [ ] ST-093-01: 验证用户列表加载 (0.5h)
- [ ] ST-093-02: 验证搜索功能 (0.5h)
- [ ] ST-093-03: 验证新增用户 (0.5h)
- [ ] ST-093-04: 验证编辑用户 (0.5h)
- [ ] ST-093-05: 验证删除用户 (0.5h)

---

#### T-094: 端到端验证：角色管理 CRUD + 权限分配

| 属性 | 值 |
|------|-----|
| 状态 | 📋 待开始 |
| 优先级 | P0 |
| 估算工时 | 2.5h |
| 风险缓冲 | +0.5h |
| 总工时 | 3h |
| 关联故事 | US-M7-03 |
| Sprint | Sprint 1 |
| 依赖 | T-091, T-092 |
| 可并行 | ✅ 与 T-093, T-095~T-097 并行 |

**任务描述**:
验证角色管理页面的 CRUD 功能和权限分配功能。

**验证步骤**:

1. **列表加载**: 访问 `/roles`，验证角色列表正确加载
2. **新增角色**: 创建角色（名称、编码、状态、描述）
3. **编辑角色**: 修改角色信息
4. **删除角色**: 删除角色
5. **权限分配**: 为角色分配权限（如有对应 UI）
6. **菜单分配**: 为角色分配菜单（如有对应 UI）

**验收标准**:
- [ ] 角色列表正确加载
- [ ] 新增/编辑/删除角色成功
- [ ] 分页和搜索正常
- [ ] 权限分配功能正常（如有 UI）

---

#### T-095: 端到端验证：菜单管理 CRUD（树形）

| 属性 | 值 |
|------|-----|
| 状态 | 📋 待开始 |
| 优先级 | P0 |
| 估算工时 | 1.5h |
| 风险缓冲 | +0.5h |
| 总工时 | 2h |
| 关联故事 | US-M7-03 |
| Sprint | Sprint 1 |
| 依赖 | T-091, T-092 |
| 可并行 | ✅ 与 T-093, T-094, T-096, T-097 并行 |

**任务描述**:
验证菜单管理页面的树形 CRUD 功能。

**验证步骤**:
1. **树形列表加载**: 访问 `/menus`，验证菜单树正确渲染
2. **新增菜单**: 创建菜单（名称、路径、图标、排序）
3. **编辑菜单**: 修改菜单信息
4. **删除菜单**: 删除菜单
5. **树形展开/折叠**: 验证树形交互正常

**验收标准**:
- [ ] 菜单树正确加载和渲染
- [ ] 新增/编辑/删除菜单成功
- [ ] 树形层级关系正确
- [ ] 排序功能正常

---

#### T-096: 端到端验证：员工管理 CRUD

| 属性 | 值 |
|------|-----|
| 状态 | 📋 待开始 |
| 优先级 | P0 |
| 估算工时 | 1.5h |
| 风险缓冲 | +0.5h |
| 总工时 | 2h |
| 关联故事 | US-M7-03 |
| Sprint | Sprint 1 |
| 依赖 | T-091, T-092 |
| 可并行 | ✅ 与 T-093~T-095, T-097 并行 |

**任务描述**:
验证员工管理页面的 CRUD 功能。

**验证步骤**:
1. **列表加载**: 访问 `/employees`，验证员工列表正确加载
2. **新增员工**: 创建员工（姓名、工号、手机、邮箱、职位）
3. **编辑员工**: 修改员工信息
4. **删除员工**: 删除员工

**验收标准**:
- [ ] 员工列表正确加载
- [ ] 新增/编辑/删除员工成功
- [ ] 搜索和分页正常

---

#### T-097: 端到端验证：组织管理 CRUD（树形）

| 属性 | 值 |
|------|-----|
| 状态 | 📋 待开始 |
| 优先级 | P0 |
| 估算工时 | 1.5h |
| 风险缓冲 | +0.5h |
| 总工时 | 2h |
| 关联故事 | US-M7-03 |
| Sprint | Sprint 1 |
| 依赖 | T-091, T-092 |
| 可并行 | ✅ 与 T-093~T-096 并行 |

**任务描述**:
验证组织管理页面的树形 CRUD 功能。

**验证步骤**:
1. **树形列表加载**: 访问 `/org-units`，验证组织树正确渲染
2. **新增组织**: 创建组织（名称、编码、上级组织）
3. **编辑组织**: 修改组织信息
4. **删除组织**: 删除组织

**验收标准**:
- [ ] 组织树正确加载和渲染
- [ ] 新增/编辑/删除组织成功
- [ ] 树形层级关系正确

---

### Epic 4: OAuth + 安全接口对齐（EP-023）

**目标**: 修复 OAuth 和账号安全相关的接口不一致问题
**关联需求**: US-M7-04
**预计跨度**: Sprint 1 第 4~5 天

---

#### T-098: 修复 OAuth 解绑方式 + 账号列表接口（D-007, D-008, D-012）

| 属性 | 值 |
|------|-----|
| 状态 | 📋 待开始 |
| 优先级 | P1 |
| 估算工时 | 3.5h |
| 风险缓冲 | +0.5h |
| 总工时 | 4h |
| 关联故事 | US-M7-04 |
| Sprint | Sprint 1 |
| 依赖 | T-092 |
| 可并行 | ✅ 与 T-093~T-097 并行 |

**问题描述**:

**D-007**: OAuth 解绑方式不匹配
- 后端：`DELETE /app/auth/oauth/{provider}/unbind?accountId=xxx`（DELETE + Query 参数）
- 前端：`POST /app/auth/oauth/{provider}/unbind` + body `{ accountId }`（POST + Body）

**D-008**: OAuth 账号列表接口不匹配
- 后端：通过 `GET /app/profile/security` 返回 `SecurityProfileVO` 包含 `boundAccounts`
- 前端：调用 `GET /app/auth/oauth/accounts`（不存在的接口）

**D-012**: OAuthAccount 类型不匹配
- 后端 `ExternalAccountVO`：12 个字段（id, providerId, providerCode, providerUserId, userId, employeeId, nickname, avatarUrl, bindStatus, lastLoginAt, createdAt, updatedAt）
- 前端 `OAuthAccount`：5 个字段（id, provider, providerUserId, providerUsername, boundAt）

**修复内容**:

1. 修改 `frontend/src/api/oauth.ts`：

```typescript
// 修复解绑方式：POST → DELETE，body → query params
export const unbindAccount = (provider: string, accountId: number): Promise<void> => {
  return del<void>(`/app/auth/oauth/${provider}/unbind`, { params: { accountId } });
};

// 修复账号列表：改用 security profile 接口
export const getSecurityProfile = (): Promise<SecurityProfileVO> => {
  return get<SecurityProfileVO>('/app/profile/security');
};
```

2. 新增 `frontend/src/types/security.ts`：

```typescript
/** 账号安全信息（对齐后端 SecurityProfileVO） */
export interface SecurityProfileVO {
  hasPassword: boolean;
  boundAccounts: ExternalAccountVO[];
  recentLogins: LoginLogVO[];
}

/** 三方账号信息（对齐后端 ExternalAccountVO） */
export interface ExternalAccountVO {
  id: number;
  providerId: number;
  providerCode: string;
  providerUserId: string;
  userId: number;
  employeeId?: number;
  nickname?: string;
  avatarUrl?: string;
  bindStatus: string;
  lastLoginAt?: string;
  createdAt: string;
  updatedAt: string;
}

/** 登录日志（对齐后端 LoginLogVO） */
export interface LoginLogVO {
  id: number;
  userId: number;
  loginType: string;
  loginIp?: string;
  userAgent?: string;
  loginStatus: string;
  loginAt: string;
}
```

3. 修改 `frontend/src/pages/profile/SecurityProfilePage.tsx`：
   - 调用 `getSecurityProfile()` 替代 `listBoundAccounts()`
   - 适配 `ExternalAccountVO` 字段名

**验收标准**:
- [ ] OAuth 解绑使用 DELETE 方法 + Query 参数
- [ ] 账号安全页通过 `GET /app/profile/security` 获取数据
- [ ] `ExternalAccountVO` 类型与后端匹配
- [ ] `LoginLogVO` 类型与后端匹配
- [ ] 账号安全页正确显示绑定账号和登录记录

**子任务**:
- [ ] ST-098-01: 修改 `api/oauth.ts` 解绑方式 (0.5h)
- [ ] ST-098-02: 修改 `api/oauth.ts` 账号列表接口 (0.5h)
- [ ] ST-098-03: 新增 `types/security.ts` 类型定义 (1h)
- [ ] ST-098-04: 修改 SecurityProfilePage 适配新类型 (1h)
- [ ] ST-098-05: 浏览器验证账号安全页 (0.5h)

---

#### T-099: 修复修改密码 HTTP 方法（D-009）

| 属性 | 值 |
|------|-----|
| 状态 | 📋 待开始 |
| 优先级 | P2 |
| 估算工时 | 0.5h |
| 风险缓冲 | +0.5h |
| 总工时 | 1h |
| 关联故事 | US-M7-04 |
| Sprint | Sprint 1 |
| 依赖 | T-092 |
| 可并行 | ✅ 与 T-098 并行 |

**问题描述**:
- 后端：`PUT /app/profile/password`
- 前端：`POST /app/profile/password`

**修复内容**:
修改 `frontend/src/api/auth.ts`：

```typescript
// 修复前
export const changePassword = (data: {
  currentPassword: string;
  newPassword: string;
}): Promise<void> => {
  return post<void>('/app/profile/password', data);
};

// 修复后
export const changePassword = (data: {
  currentPassword: string;
  newPassword: string;
}): Promise<void> => {
  return put<void>('/app/profile/password', data);
};
```

**验收标准**:
- [ ] 修改密码使用 PUT 方法
- [ ] 修改密码功能正常工作

**子任务**:
- [ ] ST-099-01: 修改 `api/auth.ts` changePassword 方法 (0.25h)
- [ ] ST-099-02: 浏览器验证修改密码 (0.25h)

---

#### T-100: 端到端验证：账号安全页面（绑定/解绑/改密/登录记录）

| 属性 | 值 |
|------|-----|
| 状态 | 📋 待开始 |
| 优先级 | P1 |
| 估算工时 | 1.5h |
| 风险缓冲 | +0.5h |
| 总工时 | 2h |
| 关联故事 | US-M7-04 |
| Sprint | Sprint 1 |
| 依赖 | T-098, T-099 |
| 可并行 | ❌ |

**任务描述**:
验证账号安全页面的所有功能。

**验证步骤**:
1. **安全信息加载**: 访问 `/profile/security`，验证安全信息正确加载
2. **绑定账号显示**: 验证已绑定的三方账号列表正确显示
3. **修改密码**: 输入旧密码和新密码，验证修改成功
4. **登录记录**: 验证最近登录记录正确显示

**验收标准**:
- [ ] 账号安全页正确加载
- [ ] 绑定账号列表正确显示
- [ ] 修改密码功能正常
- [ ] 登录记录正确显示

---

### Epic 5: 仪表盘 + 系统配置对接（EP-024）

**目标**: 将仪表盘和系统配置页面从 mock 数据切换为真实 API
**关联需求**: US-M7-05, US-M7-06
**预计跨度**: Sprint 2

---

#### T-101: 后端新增仪表盘统计 API

| 属性 | 值 |
|------|-----|
| 状态 | 📋 待开始 |
| 优先级 | P2 |
| 估算工时 | 3.5h |
| 风险缓冲 | +0.5h |
| 总工时 | 4h |
| 关联故事 | US-M7-05 |
| Sprint | Sprint 2 |
| 依赖 | T-092 |
| 可并行 | ✅ 与 T-103 并行 |

**任务描述**:
在后端新增仪表盘统计 API，返回系统概览数据。

**API 设计**:

```
GET /api/app/dashboard/stats
```

**响应结构**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "userCount": 128,
    "roleCount": 12,
    "employeeCount": 96,
    "orgUnitCount": 8,
    "todayLoginCount": 23
  }
}
```

**实现步骤**:
1. 新增 `DashboardVO` record
2. 新增 `DashboardController`
3. 新增 `DashboardService` + `DashboardServiceImpl`
4. 各 DAO 提供 count 方法
5. 编写单元测试

**验收标准**:
- [ ] `GET /api/app/dashboard/stats` 返回正确的统计数据
- [ ] 单元测试通过
- [ ] 需要认证才能访问

**子任务**:
- [ ] ST-101-01: 新增 DashboardVO (0.5h)
- [ ] ST-101-02: 新增 DashboardController (0.5h)
- [ ] ST-101-03: 新增 DashboardService + Impl (1h)
- [ ] ST-101-04: 各 DAO 提供 count 方法 (0.5h)
- [ ] ST-101-05: 编写单元测试 (1h)

---

#### T-102: 前端仪表盘对接真实 API

| 属性 | 值 |
|------|-----|
| 状态 | 📋 待开始 |
| 优先级 | P2 |
| 估算工时 | 1.5h |
| 风险缓冲 | +0.5h |
| 总工时 | 2h |
| 关联故事 | US-M7-05 |
| Sprint | Sprint 2 |
| 依赖 | T-101 |
| 可并行 | ❌ |

**任务描述**:
修改仪表盘页面，从 mock 数据切换为调用真实 API。

**修改内容**:
1. 新增 `frontend/src/api/dashboard.ts`
2. 新增 `DashboardStats` 类型
3. 修改 `DashboardPage.tsx`：useEffect 中调用 API 获取统计数据
4. 添加 loading 状态

**验收标准**:
- [ ] 仪表盘统计数据从后端 API 获取
- [ ] 统计卡片显示真实数据
- [ ] 加载状态正确显示
- [ ] 错误处理正确

---

#### T-103: 后端新增 OAuth Provider 管理 API

| 属性 | 值 |
|------|-----|
| 状态 | 📋 待开始 |
| 优先级 | P1 |
| 估算工时 | 3.5h |
| 风险缓冲 | +0.5h |
| 总工时 | 4h |
| 关联故事 | US-M7-06 |
| Sprint | Sprint 2 |
| 依赖 | T-092 |
| 可并行 | ✅ 与 T-101 并行 |

**任务描述**:
检查后端是否已有 OAuth Provider 管理 API（`IntegrationSourceController`），如有则对齐前端接口；如无则新增。

**现有后端接口**:
- `IntegrationSourceController` 在 `/app/integration/sources`
- 需要确认是否与前端期望的 OAuth Provider CRUD 一致

**验收标准**:
- [ ] OAuth Provider CRUD API 可用
- [ ] 前端接口路径和参数与后端一致
- [ ] 单元测试通过

---

#### T-104: 前端系统配置页面对接真实 API

| 属性 | 值 |
|------|-----|
| 状态 | 📋 待开始 |
| 优先级 | P1 |
| 估算工时 | 1.5h |
| 风险缓冲 | +0.5h |
| 总工时 | 2h |
| 关联故事 | US-M7-06 |
| Sprint | Sprint 2 |
| 依赖 | T-103 |
| 可并行 | ❌ |

**任务描述**:
修改系统配置页面，从 mock 数据切换为调用真实 API。

**修改内容**:
1. 修改 `SettingsPage.tsx`：替换 mock 数据为 API 调用
2. OAuth Provider 列表从后端获取
3. 保存配置调用后端 API

**验收标准**:
- [ ] 系统配置页面使用真实 API
- [ ] OAuth Provider 列表正确加载
- [ ] 保存配置功能正常

---

### Epic 6: 前端优化与测试（EP-025）

**目标**: 优化前端性能，补充关键测试
**关联需求**: US-M7-07, US-M7-08
**预计跨度**: Sprint 2

---

#### T-105: 前端代码分割优化（路由懒加载 + antd 按需加载 + chunk 优化）

| 属性 | 值 |
|------|-----|
| 状态 | 📋 待开始 |
| 优先级 | P1 |
| 估算工时 | 3.5h |
| 风险缓冲 | +0.5h |
| 总工时 | 4h |
| 关联故事 | US-M7-07 |
| Sprint | Sprint 2 |
| 依赖 | T-092 |
| 可并行 | ✅ 与 T-101~T-104 并行 |

**任务描述**:
优化前端构建产物大小，消除 chunk 大小警告。

**优化内容**:

1. **路由懒加载**（React.lazy + Suspense）:

```typescript
// routes/index.tsx
import { lazy, Suspense } from 'react';
import LoadingSpinner from '@/components/LoadingSpinner';

const DashboardPage = lazy(() => import('@/pages/dashboard/DashboardPage'));
const UserListPage = lazy(() => import('@/pages/users/UserListPage'));
const RoleListPage = lazy(() => import('@/pages/roles/RoleListPage'));
// ... 其他页面

// 路由中使用 Suspense 包裹
<Route path="dashboard" element={
  <Suspense fallback={<LoadingSpinner />}>
    <DashboardPage />
  </Suspense>
} />
```

2. **antd 按需加载**（已内置，确认 Vite 配置）:
   - antd 5 默认支持 tree-shaking
   - 确认 `import { Button } from 'antd'` 而非 `import antd from 'antd'`
   - 使用 `@ant-design/icons` 按需导入

3. **Vite 构建优化**:

```typescript
// vite.config.ts
export default defineConfig({
  build: {
    rollupOptions: {
      output: {
        manualChunks: {
          'vendor-react': ['react', 'react-dom', 'react-router-dom'],
          'vendor-antd': ['antd', '@ant-design/icons', '@ant-design/pro-components'],
          'vendor-utils': ['axios', 'zustand', 'dayjs'],
        },
      },
    },
    chunkSizeWarningLimit: 500, // 调整警告阈值（可选）
  },
});
```

4. **Gzip 压缩**（可选）:
   - 安装 `vite-plugin-compression`
   - 配置 Gzip 预压缩

**验收标准**:
- [ ] 所有页面组件使用 React.lazy 懒加载
- [ ] `npm run build` 无 chunk 大小警告
- [ ] 构建产物合理分块（vendor-react, vendor-antd, 页面组件）
- [ ] 首屏加载时间改善
- [ ] `npm run build` 成功

**子任务**:
- [ ] ST-105-01: 实现路由懒加载 (1h)
- [ ] ST-105-02: 配置 Vite 构建分块 (1h)
- [ ] ST-105-03: 验证 antd 按需加载 (0.5h)
- [ ] ST-105-04: 构建产物分析和验证 (1h)

---

#### T-106: 前端关键流程测试（登录流程 + 用户 CRUD 流程）

| 属性 | 值 |
|------|-----|
| 状态 | 📋 待开始 |
| 优先级 | P2 |
| 估算工时 | 5h |
| 风险缓冲 | +1h |
| 总工时 | 6h |
| 关联故事 | US-M7-08 |
| Sprint | Sprint 2 |
| 依赖 | T-092 |
| 可并行 | ✅ 与 T-105 并行 |

**任务描述**:
为关键流程编写前端测试。

**测试框架**:
- Vitest（与 Vite 集成更好）
- React Testing Library
- MSW（Mock Service Worker）用于 API mock

**测试范围**:

1. **登录流程测试**:
   - 登录表单渲染
   - 表单验证（空用户名、空密码）
   - 登录成功跳转
   - 登录失败提示

2. **用户 CRUD 测试**:
   - 用户列表渲染
   - 搜索功能
   - 新增用户表单
   - 编辑用户表单
   - 删除确认

3. **认证状态测试**:
   - authStore login/logout
   - token 存储/清除
   - 权限检查

**验收标准**:
- [ ] 测试框架配置完成（Vitest + RTL）
- [ ] 登录流程测试通过
- [ ] 用户 CRUD 测试通过
- [ ] 认证状态测试通过
- [ ] `npm run test` 全部通过

**子任务**:
- [ ] ST-106-01: 配置 Vitest + RTL 测试环境 (1h)
- [ ] ST-106-02: 编写登录流程测试 (1.5h)
- [ ] ST-106-03: 编写用户 CRUD 测试 (1.5h)
- [ ] ST-106-04: 编写认证状态测试 (1h)

---

## 任务统计

### 按状态

| 状态 | 数量 | 总工时 |
|------|------|--------|
| 📋 待开始 | 18 | 62h |

### 按 Sprint

| Sprint | 任务数 | 总工时 | 核心目标 |
|--------|--------|--------|---------|
| Sprint 1 | 14 | 42h | API 对齐 + 认证联调 + CRUD 联调 |
| Sprint 2 | 4 | 16h | 功能补全 + 优化 + 测试 |

### 按优先级

| 优先级 | 任务数 | 总工时 |
|--------|--------|--------|
| P0 | 9 | 28h |
| P1 | 5 | 16h |
| P2 | 4 | 14h |

### 按 Epic

| Epic | 任务数 | 总工时 |
|------|--------|--------|
| EP-020: 联调环境搭建 | 1 | 3h |
| EP-021: 认证接口对齐 | 4 | 14h |
| EP-022: 端到端验证 | 7 | 18h |
| EP-023: OAuth + 安全接口对齐 | 3 | 7h |
| EP-024: 仪表盘 + 配置对接 | 4 | 12h |
| EP-025: 前端优化与测试 | 2 | 10h |

## 关键路径

```
T-087 (环境搭建, 3h)
  → T-088 (登录路径, 2h) + T-089 (LoginResp, 3h) + T-091 (分页字段, 4h)  [并行]
  → T-090 (Profile 类型, 4h)
  → T-092 (登录 E2E, 3h)
  → T-093~T-097 (CRUD E2E, 12h)  [并行]
  → T-098~T-100 (OAuth E2E, 7h)
  → T-101~T-104 (功能补全, 12h)  [并行]
  → T-105 (代码优化, 4h)
  → T-106 (前端测试, 6h)
```

**关键路径总工时**: 3 + 2 + 4 + 3 + 12 + 7 + 4 + 6 = 41h

## 版本历史

| 版本 | 日期 | 修改内容 | 修改人 |
|------|------|---------|--------|
| v1.0 | 2026-05-24 | M7 初始版本 | tech-lead |
