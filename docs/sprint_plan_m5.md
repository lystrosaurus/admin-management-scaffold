# Sprint 迭代计划 — Admin Management Scaffold（M5: 三方登录 + 账号安全）

## 项目概览

| 属性 | 值 |
|------|-----|
| 项目启动日期 | 2026-05-25（周一） |
| 预计完成日期 | 2026-06-13（周五） |
| Sprint 长度 | 2 周（10 个工作日） |
| 团队规模 | 2 人（双 Agent 并行，worktree 隔离） |
| 总 Sprint 数量 | 2 |
| 总估算工时 | ~80h（含 20% 风险缓冲） |
| 前置条件 | M4 已完成（568 tests），V6 迁移已执行 |

## 团队角色

| 角色 | 人数 | 负责范围 |
|------|------|---------|
| Agent A | 1 | OAuth 基础设施：State 管理、OAuth Client 接口 + Lark 实现、VO/DTO |
| Agent B | 1 | OAuth 服务层 + Controller + 安全页 + 增强绑定 + 日志迁移 |
| 交付负责人 | 1 | Sprint 规划、进度跟踪、风险预警、集成协调 |

## Sprint 总览

| Sprint | 日期范围 | 主题 | 目标 | 完成标准 |
|--------|---------|------|------|---------|
| Sprint 1 | 05-25 ~ 06-06 | OAuth 基础设施 + Client 实现 | DB 迁移 + Redis State 管理 + OAuth Client 接口 + Lark 实现 + ErrorCode + VO/DTO | V7 可执行；RedisStateService 通过单测；LarkOAuthClient 可 mock 调用；编译+现有测试全绿 |
| Sprint 2 | 06-08 ~ 06-13 | OAuth 服务 + Controller + 安全页 | OAuth 流程完整闭环 + 增强绑定/解绑 + 账号安全页 + 集成测试 | authorize→callback 全链路可跑；绑定/解绑含安全检查；安全页返回正确数据；全量测试 ≥ 620 |

## 详细 Sprint 计划

### Sprint 1: OAuth 基础设施 + Client 实现

**日期**: 2026-05-25 ~ 2026-06-06
**容量**: 40h（2 人 × 20h/Sprint，扣除 20% 缓冲）
**目标**: 完成数据库表创建、Redis State 管理、OAuth Client 抽象层 + Lark 实现、所有 DTO/VO

#### 用户故事

| 故事ID | 标题 | 优先级 | 估算(小时) | 状态 |
|--------|------|--------|-----------|------|
| US-M5-01 | 作为用户，我可以通过飞书 OAuth 授权登录后台系统 | P0 | 16 | 计划中 |
| US-M5-02 | 作为已登录用户，我可以绑定/解绑我的飞书账号 | P0 | 8 | 计划中 |
| US-M5-03 | 作为用户，我可以在账号安全页查看绑定状态和登录记录 | P1 | 6 | 计划中 |

#### 任务列表

| 任务ID | 任务描述 | 关联故事 | 角色 | 估算(h) | 依赖 | 可并行 |
|--------|---------|---------|------|--------|------|--------|
| T-057 | V7 Flyway 迁移脚本（sys_login_log + sys_operation_log） | US-M5-01 | Agent B | 2 | - | ✅ |
| T-058 | ErrorCode 新增 OAuth/绑定/安全页错误码 | US-M5-01 | Agent A | 0.5 | - | ✅ |
| T-059 | OAuthStateService — Redis 存取 state + nonce | US-M5-01 | Agent A | 3 | T-058 | |
| T-060 | OAuthClient 接口 + OAuthClientFactory + DTO/VO 定义 | US-M5-01 | Agent A | 3 | T-058 | |
| T-061 | LarkOAuthClient 实现（HTTP 调用飞书 API） | US-M5-01 | Agent A | 6 | T-060 | |
| T-062 | LoginLog Entity + DAO + Service（登录日志写入） | US-M5-01, US-M5-03 | Agent B | 3 | T-057 | |
| T-063 | OperationLog Entity + DAO + Service（操作日志写入） | US-M5-02 | Agent B | 3 | T-057 | |
| T-064 | OAuthClient 单元测试 + LarkOAuthClient 单元测试（Mock HTTP） | US-M5-01 | Agent A | 3 | T-061 | |
| T-065 | LoginLog + OperationLog 单元测试 | US-M5-01 | Agent B | 2 | T-062, T-063 | |

#### 风险与阻碍

| 类型 | 描述 | 影响 | 缓解策略 |
|------|------|------|---------|
| 技术风险 | 项目当前 Redis/Redisson 在 local/test profile 被 exclude，需评估是否启用或使用内存实现 | 高 | T-059 设计为接口 + 双实现（RedisImpl + InMemoryImpl），测试和开发环境用 InMemory；生产用 Redis |
| 依赖风险 | 飞书 OAuth API 文档变更或接口限制 | 低 | T-061 使用接口隔离，HTTP 调用可 Mock；飞书 API 相对稳定 |
| 集成风险 | V7 迁移与现有 Flyway 脚本冲突 | 低 | 遵循 V7 命名规范，不修改已有脚本 |

---

### Sprint 2: OAuth 服务 + Controller + 安全页

**日期**: 2026-06-08 ~ 2026-06-13
**容量**: 40h（2 人 × 20h/Sprint）
**目标**: 完成 OAuth 授权/回调全流程、增强绑定/解绑、账号安全页、全部测试

#### 用户故事

| 故事ID | 标题 | 优先级 | 估算(小时) | 状态 |
|--------|------|--------|-----------|------|
| US-M5-01 | OAuth 授权回调 → 登录/JWT 返回 | P0 | 12 | 计划中 |
| US-M5-02 | 已登录用户 OAuth 绑定/解绑（含安全检查） | P0 | 8 | 计划中 |
| US-M5-03 | 账号安全页 API | P1 | 4 | 计划中 |

#### 任务列表

| 任务ID | 任务描述 | 关联故事 | 角色 | 估算(h) | 依赖 | 可并行 |
|--------|---------|---------|------|--------|------|--------|
| T-066 | OAuthService — authorize + callback + bind + unbind 业务逻辑 | US-M5-01, US-M5-02 | Agent A | 10 | T-059, T-060, T-061 | |
| T-067 | ExternalAccountService 增强 — 最后登录方式检查 + 解绑安全约束 | US-M5-02 | Agent B | 3 | T-062 | ✅ |
| T-068 | OAuthController — /public/oauth/{provider}/authorize + callback | US-M5-01 | Agent B | 3 | T-066 | |
| T-069 | OAuthBindController — /app/auth/oauth/{provider}/bind + unbind | US-M5-02 | Agent B | 3 | T-066, T-067 | |
| T-070 | SecurityProfileController — GET /app/profile/security | US-M5-03 | Agent B | 2 | T-062, T-067 | ✅ |
| T-071 | OAuthService 单元测试 + OAuthController MVC 测试 | US-M5-01 | Agent A | 5 | T-066, T-068 | |
| T-072 | 增强绑定/解绑 + 安全页单元测试 + MVC 测试 | US-M5-02, US-M5-03 | Agent B | 4 | T-069, T-070 | |
| T-073 | 集成测试 + ArchUnit auth.oauth 包约束 + 全量回归 | US-M5-01 | Agent B | 4 | T-071, T-072 | |

#### 风险与阻碍

| 类型 | 描述 | 影响 | 缓解策略 |
|------|------|------|---------|
| 安全风险 | state/nonce 未校验导致 CSRF 重放攻击 | 高 | T-059 强制单次消费（用完即删），T-066 callback 先校验 state 再处理业务 |
| 业务风险 | 用户无密码且仅剩一个登录方式时解绑导致无法登录 | 高 | T-067 在 unbind 前检查：如果用户无密码且绑定账号数 ≤ 1，拒绝解绑并抛出异常 |
| 集成风险 | OAuth 登录流程与现有 Sa-Token 会话管理冲突 | 中 | T-066 直接复用 StpUtil.login(userId)，与密码登录走同一条 Sa-Token 路径 |
| 测试风险 | 飞书 API 不可控，集成测试需 Mock | 中 | T-064/T-071 使用 MockWebServer 或 Mockito Mock OAuthClient |

---

## 里程碑

| 里程碑 | 日期 | 交付物 | 验收标准 |
|--------|------|--------|---------|
| M5.1: OAuth 基础就绪 | 2026-06-06 | V7 迁移、OAuthStateService、OAuthClient + Lark 实现、LoginLog/OperationLog | V7 可执行；OAuthStateService 单测通过；LarkOAuthClient 可 mock 调用；编译+全量测试 ≥ 568（不回归） |
| M5.2: M5 完成 | 2026-06-13 | OAuth 全流程 + 增强绑定/解绑 + 安全页 + 全部测试 | authorize→callback 全链路可跑；绑定/解绑含安全检查；安全页返回正确数据；总测试 ≥ 620；M4 测试无回归 |

## 风险登记册

| 风险ID | 风险描述 | 概率 | 影响 | 等级 | 缓解策略 | 负责人 |
|--------|---------|------|------|------|---------|--------|
| R-001 | 项目 Redis 在 local/test 被 exclude，OAuthStateService 需要替代方案 | 高 | 高 | 高 | 设计接口 + 双实现（Redis/InMemory），测试用 InMemory | Agent A |
| R-002 | 飞书 OAuth API 变更或不可用 | 低 | 中 | 低 | 接口隔离 + Mock 测试；文档链接固定 | Agent A |
| R-003 | OAuth state 泄露或重放 | 低 | 高 | 中 | state/nonce 单次消费（Redis DEL after read）+ 5min TTL | Agent A |
| R-004 | 解绑后用户无法登录（无密码 + 最后登录方式） | 中 | 高 | 高 | Service 层强制检查，拒绝解绑并返回明确错误码 | Agent B |
| R-005 | V7 迁移与现有脚本冲突 | 低 | 低 | 低 | 遵循 Flyway 版本递增，不修改已有脚本 | Agent B |
| R-006 | Sprint 2 时间短（5 天），测试不足 | 中 | 中 | 中 | 优先完成核心流程测试（OAuth authorize/callback）；安全页测试可降级 | 交付负责人 |

## 关键依赖关系

```
T-057 (V7 迁移) ──┬──→ T-062 (LoginLog) ──→ T-065 (日志测试) ──→ T-067 (增强绑定)
                   └──→ T-063 (OperationLog) ↗                       ↓
                                                               T-069 (OAuthBind Controller)
T-058 (ErrorCode) ──┬──→ T-059 (StateService) ──→ T-066 (OAuthService)
                    └──→ T-060 (Client接口)  ──→ T-061 (Lark实现) ↗  ↓
                                                               T-068 (OAuth Controller)
                                                                      ↓
T-066 + T-068 ──→ T-071 (OAuth 测试)
T-069 + T-070 ──→ T-072 (绑定+安全页测试)
T-071 + T-072 ──→ T-073 (集成测试 + 全量回归)
```

**关键路径**: T-058 → T-060 → T-061 → T-066 → T-068 → T-071 → T-073（约 24h）

## 新增 API 端点规划

### OAuth 授权 `/public/oauth`

| 方法 | 路径 | 描述 | 认证 |
|------|------|------|------|
| GET | `/api/public/oauth/{provider}/authorize` | 生成三方授权 URL（含 state） | 无 |
| GET | `/api/public/oauth/{provider}/callback` | OAuth 回调（校验 state → 换 token → 登录/提示绑定） | 无 |

### OAuth 绑定 `/app/auth/oauth`

| 方法 | 路径 | 描述 | 认证 |
|------|------|------|------|
| POST | `/api/app/auth/oauth/{provider}/bind` | 已登录用户通过 OAuth 流程绑定三方账号 | Sa-Token JWT |
| DELETE | `/api/app/auth/oauth/{provider}/unbind` | 解绑三方账号（含最后登录方式检查） | Sa-Token JWT |

### 账号安全页 `/app/profile`

| 方法 | 路径 | 描述 | 认证 |
|------|------|------|------|
| GET | `/api/app/profile/security` | 返回已绑定三方账号 + 最近登录记录 | Sa-Token JWT |

## 变更日志

| 版本 | 日期 | 变更内容 | 变更人 |
|------|------|---------|--------|
| v1.0 | 2026-05-23 | M5 初始版本 | delivery-lead |
