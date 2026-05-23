# 详细任务清单 — Admin Management Scaffold（M5: 三方登录 + 账号安全）

## 说明

本文档包含 M5 所有可执行任务的详细定义。每个任务都具备：
- 明确的验收标准
- 合理的工时估算
- 清晰的角色定义
- 完整的依赖关系

所有任务遵循项目既有架构：`Controller → Service → DAO → DAO Impl → Mapper`，Entity 用 class + Lombok，DTO/VO 用 record，MapStruct 用 `@Mapper(componentModel = "spring")`。

**M5 范围**：
- ✅ 完整 OAuth 框架（authorize/callback/state 管理）
- ✅ Lark OAuth Client（真实 HTTP 调用，可 mock 测试）
- ✅ WECOM/WECHAT 只定义接口，实现留后续
- ✅ 账号安全页 API
- ❌ 不实现前端

## 任务状态图例

- 📋 待开始 (Backlog)
- 🔵 计划中 (Planned)
- 🟡 进行中 (In Progress)
- 🟢 已完成 (Done)
- ⛔ 阻塞 (Blocked)

## 任务清单

---

### Epic 1: 数据库迁移 + 错误码（EP-005）

**目标**: 创建 M5 所需的 2 张日志表 + 新增 OAuth 相关错误码
**关联需求**: US-M5-01, US-M5-02, US-M5-03
**预计跨度**: Sprint 1（第 1 天）

---

#### T-057: V7 Flyway 迁移脚本（sys_login_log + sys_operation_log）

| 属性 | 值 |
|------|-----|
| 状态 | 📋 待开始 |
| 优先级 | P0 |
| 估算工时 | 1.5h |
| 风险缓冲 | +0.5h |
| 总工时 | 2h |
| 角色 | Agent B |
| 关联故事 | US-M5-01, US-M5-03 |
| Sprint | Sprint 1 |
| 依赖 | 无 |
| 可并行 | ✅ 与 T-058 并行 |

**任务描述**:
创建 `V7__add_login_log_and_operation_log.sql` 迁移脚本，包含以下 2 张表的 DDL：

**sys_login_log**：登录日志表，记录所有登录方式的登录行为。
- 字段：`id`, `user_id`, `login_type`(PASSWORD/OAUTH_LARK/OAUTH_WECOM/OAUTH_WECHAT), `provider_code`, `ip_address`, `user_agent`, `status`(SUCCESS/FAILED), `failure_reason`, `login_at`
- 索引：`idx_user_id`, `idx_login_at`, `idx_status`

**sys_operation_log**：操作日志表，记录关键业务操作。
- 字段：`id`, `user_id`, `operation_type`(BIND_EXTERNAL/UNBIND_EXTERNAL/CHANGE_PASSWORD/...), `target_type`, `target_id`, `detail_json`, `ip_address`, `created_at`
- 索引：`idx_user_id`, `idx_operation_type`, `idx_created_at`

严格遵循项目数据库规范：表名/字段名小写下划线、主键 BIGINT、时间字段 DATETIME、字符集 utf8mb4、排序规则 utf8mb4_0900_ai_ci。日志表不做逻辑删除。

**验收标准**:
- [ ] `V7__add_login_log_and_operation_log.sql` 存在于 `backend/src/main/resources/db/migration/`
- [ ] `sys_login_log` 表结构完整，`login_type` VARCHAR(32)，`status` VARCHAR(32) DEFAULT 'SUCCESS'
- [ ] `sys_operation_log` 表结构完整，`operation_type` VARCHAR(64)
- [ ] 两张表均无 `deleted` 字段（日志不做逻辑删除）
- [ ] Flyway 迁移在本地 MySQL 可成功执行
- [ ] 已有 V1~V6 脚本未被修改

**技术备注**:
- `sys_login_log` 不做逻辑删除，登录日志是审计数据，只增不删
- `sys_operation_log` 同理不做逻辑删除
- `login_type` 使用枚举值：`PASSWORD` / `OAUTH_LARK` / `OAUTH_WECOM` / `OAUTH_WECHAT`
- `provider_code` 可为 NULL（密码登录时为空），OAuth 登录时存 `LARK` / `WECOM` / `WECHAT`

**子任务**:
- [ ] ST-057-01: 编写 `sys_login_log` DDL (0.5h)
- [ ] ST-057-02: 编写 `sys_operation_log` DDL (0.5h)
- [ ] ST-057-03: 本地 Flyway 迁移验证 (0.5h)

---

#### T-058: ErrorCode 新增 OAuth/绑定/安全页错误码

| 属性 | 值 |
|------|-----|
| 状态 | 📋 待开始 |
| 优先级 | P0 |
| 估算工时 | 0.5h |
| 风险缓冲 | +0h |
| 总工时 | 0.5h |
| 角色 | Agent A |
| 关联故事 | US-M5-01, US-M5-02 |
| Sprint | Sprint 1 |
| 依赖 | 无 |
| 可并行 | ✅ 与 T-057 并行 |

**任务描述**:
在 `ErrorCode.java` 中新增以下错误码，覆盖 OAuth 流程、绑定/解绑、安全页的异常场景。

**新增错误码**:

| 错误码 | 数值 | 描述 |
|--------|------|------|
| OAUTH_STATE_INVALID | 9501 | OAuth state 无效或已过期（CSRF 防护） |
| OAUTH_STATE_EXPIRED | 9502 | OAuth state 已过期（超过 5 分钟） |
| OAUTH_CODE_EXCHANGE_FAILED | 9503 | 用授权码换 access_token 失败 |
| OAUTH_USERINFO_FAILED | 9504 | 获取三方用户信息失败 |
| OAUTH_PROVIDER_NOT_FOUND | 9505 | 指定的 OAuth 提供方不存在或未启用 |
| OAUTH_PROVIDER_DISABLED | 9506 | OAuth 提供方已禁用 |
| OAUTH_REDIRECT_URI_INVALID | 9507 | redirect_uri 不在白名单中 |
| BIND_ACCOUNT_ALREADY_EXISTS | 9508 | 该三方账号已被其他用户绑定 |
| UNBIND_LAST_LOGIN_METHOD | 9509 | 无法解绑：这是您唯一的登录方式（无密码且仅剩一个绑定） |
| OAUTH_NONCE_INVALID | 9510 | OAuth nonce 无效或已使用（重放防护） |

同步更新 `ErrorCodeTest` 白名单，确保测试通过。

**验收标准**:
- [ ] ErrorCode.java 包含上述 10 个新错误码
- [ ] 编码数值与现有错误码不冲突
- [ ] ErrorCodeTest 白名单更新，测试通过
- [ ] 编译成功

**技术备注**:
- 错误码数值范围选择 9501~9510，与 M4 的 9xxx 系列保持一致
- `OAUTH_STATE_INVALID` 和 `OAUTH_STATE_EXPIRED` 分开是因为前端需要区分提示（过期可重试，无效需警惕）
- `UNBIND_LAST_LOGIN_METHOD` 需要在前端显示明确的提示信息

**子任务**:
- [ ] ST-058-01: 新增 10 个 OAuth 相关 ErrorCode 枚举值 (0.25h)
- [ ] ST-058-02: 更新 ErrorCodeTest 白名单 (0.25h)

---

### Epic 2: OAuth State 管理（EP-006）

**目标**: 实现 OAuth 授权流程中的 state/nonce 存取和校验，防 CSRF + 重放
**关联需求**: US-M5-01
**预计跨度**: Sprint 1

---

#### T-059: OAuthStateService — state + nonce 存取

| 属性 | 值 |
|------|-----|
| 状态 | 📋 待开始 |
| 优先级 | P0 |
| 估算工时 | 2.5h |
| 风险缓冲 | +0.5h |
| 总工时 | 3h |
| 角色 | Agent A |
| 关联故事 | US-M5-01 |
| Sprint | Sprint 1 |
| 依赖 | T-058 |
| 可并行 | ✅ 与 T-057, T-062, T-063 并行 |

**任务描述**:
在 `io.github.lystrosaurus.admin.auth.oauth` 包下创建 OAuthStateService，负责 OAuth 授权流程中的 state 和 nonce 管理。

**核心设计**：
- 定义 `OAuthStateService` 接口，方法：`generateState()`, `saveState(state, nonce, userId?)`, `validateAndConsumeState(state): StateData`
- 提供两个实现：
  - `RedisOAuthStateService`：使用 RedissonClient，key 格式 `oauth:state:{state}`，TTL 5 分钟
  - `InMemoryOAuthStateService`：使用 ConcurrentHashMap + 定时清理，用于 test/local 环境
- `StateData` record：`state, nonce, userId(nullable), createdAt`
- 校验逻辑：查 Redis → 不存在则抛 `OAUTH_STATE_INVALID` → 存在则删除（单次消费）→ 检查是否过期

**state 生成规则**：
- 使用 `UUID.randomUUID().toString().replace("-", "")` 生成 32 位随机字符串
- nonce 使用 `SecureRandom` 生成 16 字节 hex 字符串

**验收标准**:
- [ ] `OAuthStateService` 接口定义完整（generate/save/validateAndConsume）
- [ ] `RedisOAuthStateService` 实现可编译（依赖 RedissonClient）
- [ ] `InMemoryOAuthStateService` 实现可编译，TTL 使用 `ScheduledExecutorService` 清理过期条目
- [ ] 单元测试覆盖：正常生成/保存/消费、state 不存在、state 已过期、state 重复消费
- [ ] 所有测试通过

**技术备注**:
- 当前项目 Redis/Redisson 在 local/test profile 被 exclude，`InMemoryOAuthStateService` 用于开发和测试
- `@ConditionalOnBean(RedissonClient.class)` 选择 Redis 实现，fallback 到 InMemory
- state 的 TTL 为 5 分钟，与飞书官方推荐一致
- `validateAndConsumeState` 是原子操作：校验 + 删除在同一步完成，防止并发重放

**子任务**:
- [ ] ST-059-01: 定义 OAuthStateService 接口 + StateData record (0.5h)
- [ ] ST-059-02: 实现 InMemoryOAuthStateService (1h)
- [ ] ST-059-03: 实现 RedisOAuthStateService (0.5h)
- [ ] ST-059-04: 单元测试 — 正常流程 + 过期 + 不存在 + 重复消费 (0.5h)

---

### Epic 3: OAuth Client 抽象层 + Lark 实现（EP-007）

**目标**: 定义 OAuth Client 统一接口，实现 Lark OAuth Client（真实 HTTP 调用）
**关联需求**: US-M5-01
**预计跨度**: Sprint 1

---

#### T-060: OAuthClient 接口 + OAuthClientFactory + DTO/VO

| 属性 | 值 |
|------|-----|
| 状态 | 📋 待开始 |
| 优先级 | P0 |
| 估算工时 | 3h |
| 风险缓冲 | +0h |
| 总工时 | 3h |
| 角色 | Agent A |
| 关联故事 | US-M5-01 |
| Sprint | Sprint 1 |
| 依赖 | T-058 |
| 可并行 | ✅ 与 T-059 并行（完成后合流到 T-061） |

**任务描述**:
在 `io.github.lystrosaurus.admin.auth.oauth` 包下创建 OAuth Client 抽象层。

**OAuthClient 接口**:
```java
public interface OAuthClient {
    /** 获取提供方编码（LARK/WECOM/WECHAT） */
    String getProviderCode();

    /** 构建授权 URL（含 state、scope 等参数） */
    String buildAuthorizationUrl(String state, String redirectUri);

    /** 用授权码换 access_token */
    OAuthTokenResponse exchangeCode(String code, String redirectUri);

    /** 用 access_token 获取三方用户信息 */
    OAuthUserInfo getUserInfo(String accessToken);
}
```

**DTO/VO 定义**:

| 类型 | 名称 | 字段 |
|------|------|------|
| record | `OAuthAuthorizeVO` | `authorizeUrl`, `state` |
| record | `OAuthCallbackDTO` | `code`, `state` |
| record | `OAuthTokenResponse` | `accessToken`, `refreshToken`, `expiresIn`, `tokenType` |
| record | `OAuthUserInfo` | `providerUserId`, `nickname`, `avatarUrl`, `identifierJson`（存放 open_id/union_id 等平台特有标识的 JSON） |
| record | `OAuthLoginVO` | `accessToken`(JWT), `user`(UserVO?), `needBind`(boolean), `externalUserIdentity`(String?) |
| record | `OAuthBindDTO` | `code`, `state` |

**OAuthClientFactory**:
- 使用 `Map<String, OAuthClient>` 注入所有 OAuthClient 实现
- `getClient(providerCode): OAuthClient` — 找不到抛 `OAUTH_PROVIDER_NOT_FOUND`

**WecomOAuthClient / WechatOAuthClient 占位**:
- 实现 OAuthClient 接口的所有方法
- 方法体直接 `throw new UnsupportedOperationException("Not implemented yet")`
- 确保 OAuthClientFactory 可以注册它们

**验收标准**:
- [ ] `OAuthClient` 接口定义完整（4 个方法）
- [ ] 6 个 record DTO/VO 定义完成
- [ ] `OAuthClientFactory` 可根据 providerCode 获取对应 Client
- [ ] `WecomOAuthClient` 和 `WechatOAuthClient` 占位实现可编译
- [ ] 编译成功，无错误

**技术备注**:
- `OAuthUserInfo.identifierJson` 存储平台特有标识，格式如 `{"open_id":"xxx","union_id":"yyy"}`
- `OAuthLoginVO.needBind` 为 true 时表示三方账号未绑定本地用户，前端应提示绑定
- `OAuthBindDTO` 用于已登录用户的绑定流程，复用 OAuth 回调的 code/state
- `OAuthClientFactory` 使用 Spring 注入 Map，不需要手动 register

**子任务**:
- [ ] ST-060-01: 定义 OAuthClient 接口 (0.5h)
- [ ] ST-060-02: 定义 6 个 DTO/VO record (0.5h)
- [ ] ST-060-03: 实现 OAuthClientFactory (0.5h)
- [ ] ST-060-04: WecomOAuthClient + WechatOAuthClient 占位 (0.5h)
- [ ] ST-060-05: AuthProviderService 添加 getEnabledByCode 方法（如不存在） (0.5h)
- [ ] ST-060-06: 编译验证 (0.5h)

---

#### T-061: LarkOAuthClient 实现

| 属性 | 值 |
|------|-----|
| 状态 | 📋 待开始 |
| 优先级 | P0 |
| 估算工时 | 5h |
| 风险缓冲 | +1h |
| 总工时 | 6h |
| 角色 | Agent A |
| 关联故事 | US-M5-01 |
| Sprint | Sprint 1 |
| 依赖 | T-060 |
| 可并行 | ❌ |

**任务描述**:
在 `io.github.lystrosaurus.admin.auth.oauth.lark` 包下实现 LarkOAuthClient，调用飞书开放平台 OAuth API。

**飞书 OAuth 流程**:
1. **获取 tenant_access_token**（前置步骤，用于获取用户信息）：
   - `POST https://open.feishu.cn/open-apis/auth/v3/tenant_access_token/internal`
   - Body: `{"app_id": "...", "app_secret": "..."}`
   - Response: `{"tenant_access_token": "...", "expire": 7200}`

2. **获取用户信息**：
   - `GET https://open.feishu.cn/open-apis/authen/v1/user_info`
   - Header: `Authorization: Bearer {user_access_token}`
   - Response: `{"open_id": "...", "union_id": "...", "user_id": "...", "name": "...", "avatar_url": "..."}`

3. **user_access_token 换取**：
   - `POST https://open.feishu.cn/open-apis/authen/v1/oidc/access_token`
   - Header: `Authorization: Bearer {app_access_token}`
   - Body: `{"grant_type": "authorization_code", "code": "..."}`

**实现要点**:
- `buildAuthorizationUrl`: 拼接 `https://open.feishu.cn/open-apis/authen/v1/authorize?app_id={clientId}&redirect_uri={redirectUri}&state={state}&scope={scopes}`
- `exchangeCode`: 先获取 app_access_token（用 clientId + clientSecret），再用 code 换 user_access_token
- `getUserInfo`: 用 user_access_token 调用 /authen/v1/user_info
- `getProviderCode`: 返回 `"LARK"`
- `clientSecret` 从 `AuthProvider.clientSecretEncrypted` 获取，需要解密（V1 先直接使用明文，后续加加密）

**从 AuthProvider 获取配置**:
- 通过 `AuthProviderService.getEnabledByCode("LARK")` 获取 clientId、clientSecretEncrypted、redirectUri、scopes
- 构造函数注入 `AuthProviderService` 和 `RestTemplate`（或 `RestClient`）

**验收标准**:
- [ ] `LarkOAuthClient` 实现 `OAuthClient` 接口的全部 4 个方法
- [ ] `buildAuthorizationUrl` 返回正确的飞书授权 URL
- [ ] `exchangeCode` 正确调用飞书 API 换取 token
- [ ] `getUserInfo` 正确解析飞书用户信息并返回 `OAuthUserInfo`
- [ ] 使用 `RestTemplate`（Spring Boot 默认 Bean）进行 HTTP 调用
- [ ] 单元测试覆盖：正常流程、API 异常响应、网络超时、provider 未配置
- [ ] 测试使用 Mockito Mock RestTemplate，不真实调用飞书 API
- [ ] 编译+测试通过

**技术备注**:
- 飞书 API 文档：https://open.feishu.cn/document/server-docs/authentication-management/login-state-management/login-with-accounts
- `tenant_access_token` 有效期 2 小时，可缓存（V1 不缓存，每次都请求；V2 可加 Redis 缓存）
- `clientSecretEncrypted` V1 阶段直接作为明文使用，不做加解密；后续 Sprint 可引入 AES 加密
- HTTP 调用设置 connectTimeout=5s, readTimeout=10s
- 飞书返回的 `user_id` 在不同应用间不通用，应以 `open_id` 或 `union_id` 作为三方用户标识
- `providerUserId` 使用 `open_id`（应用级别唯一标识）

**子任务**:
- [ ] ST-061-01: 实现 buildAuthorizationUrl (0.5h)
- [ ] ST-061-02: 实现 exchangeCode（含 app_access_token 获取） (1.5h)
- [ ] ST-061-03: 实现 getUserInfo (1h)
- [ ] ST-061-04: RestTemplate 配置 + 异常处理 (0.5h)
- [ ] ST-061-05: 单元测试 — Mock RestTemplate，覆盖正常/异常场景 (1.5h)

---

### Epic 4: 登录日志 + 操作日志（EP-008）

**目标**: 实现登录日志和操作日志的 Entity/DAO/Service，供 OAuth 流程和绑定/解绑使用
**关联需求**: US-M5-01, US-M5-02, US-M5-03
**预计跨度**: Sprint 1

---

#### T-062: LoginLog Entity + DAO + Service

| 属性 | 值 |
|------|-----|
| 状态 | 📋 待开始 |
| 优先级 | P0 |
| 估算工时 | 2.5h |
| 风险缓冲 | +0.5h |
| 总工时 | 3h |
| 角色 | Agent B |
| 关联故事 | US-M5-01, US-M5-03 |
| Sprint | Sprint 1 |
| 依赖 | T-057 |
| 可并行 | ✅ 与 T-059, T-060, T-063 并行 |

**任务描述**:
在 `io.github.lystrosaurus.admin.auth.log` 包下创建登录日志全栈。

**包结构**:
```
auth.log/
  entity/LoginLog.java
  dao/LoginLogDAO.java
  dao/impl/LoginLogDAOImpl.java
  mapper/LoginLogMapper.java
  service/LoginLogService.java
  service/impl/LoginLogServiceImpl.java
```

**LoginLog Entity**:
- `id` (BIGINT, ASSIGN_ID)
- `userId` (Long)
- `loginType` (String) — PASSWORD / OAUTH_LARK / OAUTH_WECOM / OAUTH_WECHAT
- `providerCode` (String, nullable)
- `ipAddress` (String)
- `userAgent` (String)
- `status` (String) — SUCCESS / FAILED
- `failureReason` (String, nullable)
- `loginAt` (LocalDateTime)

**LoginLogDAO 方法**:
- `save(LoginLog)`
- `listByUserId(Long userId, int limit)` — 按 loginAt DESC，用于安全页展示最近登录记录

**LoginLogService 方法**:
- `recordLogin(Long userId, String loginType, String providerCode, String ipAddress, String userAgent, boolean success, String failureReason)`
- `getRecentLogins(Long userId, int limit)`

**验收标准**:
- [ ] Entity 字段与 V7 迁移脚本一致
- [ ] DAO 接口 + 实现完成
- [ ] Mapper 继承 BaseMapper<LoginLog>
- [ ] Service 接口 + 实现完成
- [ ] `recordLogin` 方法内部创建 Entity 并 save
- [ ] `getRecentLogins` 返回最近 N 条登录记录
- [ ] 编译通过

**技术备注**:
- LoginLog 不做逻辑删除，Entity 无 `deleted` 字段，不继承可能存在的 BaseEntity
- `listByUserId` 使用 MyBatis-Plus 的 LambdaQueryWrapper 按 `loginAt` DESC 排序 + `last(limit)`
- 日志写入不加事务，失败不影响主流程（异步写入可后续优化）

**子任务**:
- [ ] ST-062-01: LoginLog Entity + LoginLogMapper (0.5h)
- [ ] ST-062-02: LoginLogDAO 接口 + LoginLogDAOImpl (1h)
- [ ] ST-062-03: LoginLogService 接口 + LoginLogServiceImpl (1h)

---

#### T-063: OperationLog Entity + DAO + Service

| 属性 | 值 |
|------|-----|
| 状态 | 📋 待开始 |
| 优先级 | P0 |
| 估算工时 | 2.5h |
| 风险缓冲 | +0.5h |
| 总工时 | 3h |
| 角色 | Agent B |
| 关联故事 | US-M5-02 |
| Sprint | Sprint 1 |
| 依赖 | T-057 |
| 可并行 | ✅ 与 T-059, T-060, T-062 并行 |

**任务描述**:
在 `io.github.lystrosaurus.admin.auth.log` 包下创建操作日志全栈（与 T-062 同包）。

**OperationLog Entity**:
- `id` (BIGINT, ASSIGN_ID)
- `userId` (Long)
- `operationType` (String) — BIND_EXTERNAL / UNBIND_EXTERNAL / CHANGE_PASSWORD / ...
- `targetType` (String, nullable) — EXTERNAL_ACCOUNT / USER / ...
- `targetId` (Long, nullable)
- `detailJson` (String, nullable) — 操作详情 JSON
- `ipAddress` (String)
- `createdAt` (LocalDateTime)

**OperationLogDAO 方法**:
- `save(OperationLog)`
- `listByUserId(Long userId, int limit)` — 按 createdAt DESC

**OperationLogService 方法**:
- `recordOperation(Long userId, String operationType, String targetType, Long targetId, String detailJson, String ipAddress)`
- `getRecentOperations(Long userId, int limit)`

**验收标准**:
- [ ] Entity 字段与 V7 迁移脚本一致
- [ ] DAO + Service 完成
- [ ] `recordOperation` 方法正确创建并保存日志
- [ ] 编译通过

**技术备注**:
- OperationLog 不做逻辑删除
- `detailJson` 存储操作详情，如 `{"provider":"LARK","unbind_reason":"user_request"}`
- 日志写入不加事务，失败不影响主流程

**子任务**:
- [ ] ST-063-01: OperationLog Entity + OperationLogMapper (0.5h)
- [ ] ST-063-02: OperationLogDAO 接口 + OperationLogDAOImpl (1h)
- [ ] ST-063-03: OperationLogService 接口 + OperationLogServiceImpl (1h)

---

#### T-064: OAuthClient 单元测试 + LarkOAuthClient 单元测试

| 属性 | 值 |
|------|-----|
| 状态 | 📋 待开始 |
| 优先级 | P0 |
| 估算工时 | 2.5h |
| 风险缓冲 | +0.5h |
| 总工时 | 3h |
| 角色 | Agent A |
| 关联故事 | US-M5-01 |
| Sprint | Sprint 1 |
| 依赖 | T-061 |
| 可并行 | ✅ 与 T-065 并行 |

**任务描述**:
为 OAuthClient 接口和 LarkOAuthClient 编写全面的单元测试。

**测试覆盖**:

| 测试类 | 测试场景 |
|--------|---------|
| OAuthClientFactoryTest | 通过 code 获取 Lark Client ✅ / 通过 code 获取 Wecom Client ✅ / 未知 code 抛 OAUTH_PROVIDER_NOT_FOUND |
| LarkOAuthClientTest | buildAuthorizationUrl 返回正确格式 ✅ / exchangeCode 正常流程（Mock HTTP）/ exchangeCode API 返回错误 ✅ / getUserInfo 正常流程 ✅ / getUserInfo token 无效 / 网络超时 ✅ |

**验收标准**:
- [ ] `OAuthClientFactoryTest` 通过
- [ ] `LarkOAuthClientTest` 覆盖正常 + 异常场景，使用 Mockito Mock RestTemplate
- [ ] 不真实调用飞书 API
- [ ] 测试全部通过

**技术备注**:
- 使用 `@MockBean` 或构造注入 Mockito Mock
- LarkOAuthClient 的 RestTemplate 通过构造函数注入，方便 Mock
- 飞书 API 响应 JSON 可以放在 `src/test/resources/fixtures/lark/` 目录下

**子任务**:
- [ ] ST-064-01: OAuthClientFactoryTest (0.5h)
- [ ] ST-064-02: LarkOAuthClientTest — buildAuthorizationUrl + exchangeCode (1h)
- [ ] ST-064-03: LarkOAuthClientTest — getUserInfo + 异常场景 (1h)

---

#### T-065: LoginLog + OperationLog 单元测试

| 属性 | 值 |
|------|-----|
| 状态 | 📋 待开始 |
| 优先级 | P1 |
| 估算工时 | 1.5h |
| 风险缓冲 | +0.5h |
| 总工时 | 2h |
| 角色 | Agent B |
| 关联故事 | US-M5-01 |
| Sprint | Sprint 1 |
| 依赖 | T-062, T-063 |
| 可并行 | ✅ 与 T-064 并行 |

**任务描述**:
为 LoginLogService 和 OperationLogService 编写单元测试。

**测试覆盖**:

| 测试类 | 测试场景 |
|--------|---------|
| LoginLogServiceImplTest | recordLogin 成功记录 / recordLogin 各字段正确赋值 / getRecentLogins 返回有序列表 / getRecentLogins limit 参数生效 |
| OperationLogServiceImplTest | recordOperation 成功记录 / recordOperation 含 detailJson / getRecentOperations 返回有序列表 |

**验收标准**:
- [ ] 测试覆盖正常 + 边界场景
- [ ] 使用 Mockito Mock DAO 层
- [ ] 测试全部通过

**技术备注**:
- 测试重点是 Service 层逻辑，DAO 层 Mock
- 日志写入失败不影响主流程的场景可在集成测试中验证

---

### Epic 5: OAuth 服务层 + 增强绑定（EP-009）

**目标**: 实现 OAuth 授权/回调/绑定/解绑的核心业务逻辑
**关联需求**: US-M5-01, US-M5-02
**预计跨度**: Sprint 2

---

#### T-066: OAuthService — authorize + callback + bind + unbind

| 属性 | 值 |
|------|-----|
| 状态 | 📋 待开始 |
| 优先级 | P0 |
| 估算工时 | 8h |
| 风险缓冲 | +2h |
| 总工时 | 10h |
| 角色 | Agent A |
| 关联故事 | US-M5-01, US-M5-02 |
| Sprint | Sprint 2 |
| 依赖 | T-059, T-060, T-061 |
| 可并行 | ✅ 与 T-067 并行 |

**任务描述**:
在 `io.github.lystrosaurus.admin.auth.oauth` 包下创建 OAuthService，实现完整的 OAuth 业务流程。

**包结构**:
```
auth.oauth/
  service/OAuthService.java
  service/impl/OAuthServiceImpl.java
```

**OAuthService 接口方法**:
```java
/** 未登录用户：三方登录 */
OAuthAuthorizeVO authorize(String providerCode);

/** 未登录用户：OAuth 回调 → 登录或提示绑定 */
OAuthLoginVO handleCallback(String providerCode, OAuthCallbackDTO dto);

/** 已登录用户：绑定三方账号 */
ExternalAccountVO bindAccount(Long userId, String providerCode, OAuthBindDTO dto);

/** 已登录用户：解绑三方账号 */
void unbindAccount(Long userId, String providerCode, Long accountId);
```

**authorize 流程**:
1. 根据 `providerCode` 获取 `OAuthClient`（通过 Factory）
2. 根据 `providerCode` 获取 `AuthProvider` 配置，检查 `enabled` 状态
3. 生成 state + nonce，存入 `OAuthStateService`
4. 调用 `client.buildAuthorizationUrl(state, redirectUri)`
5. 返回 `OAuthAuthorizeVO(authorizeUrl, state)`

**handleCallback 流程**:
1. 校验 `state`：调用 `OAuthStateService.validateAndConsumeState(state)`
2. 获取 `OAuthClient`，用 `code` 换 `access_token`（`client.exchangeCode`）
3. 用 `access_token` 获取三方用户信息（`client.getUserInfo`）
4. 在 `auth_external_account` 表查找 `providerId + providerUserId`：
   - **已绑定**（bindStatus=BOUND）→ `StpUtil.login(userId)` → 更新 `lastLoginAt` → 记录登录日志（SUCCESS）→ 返回 `OAuthLoginVO(token, user, needBind=false)`
   - **未绑定** → 记录登录日志（FAILED, reason=NEED_BIND）→ 返回 `OAuthLoginVO(null, null, needBind=true, externalUserIdentity)`
5. 任何异常记录登录日志（FAILED）→ 抛出对应 ErrorCode

**bindAccount 流程**:
1. 获取 `OAuthClient`，用 `code` 换 `access_token` → 获取三方用户信息
2. 检查 `providerId + providerUserId` 是否已被绑定（任何人）→ 是则抛 `BIND_ACCOUNT_ALREADY_EXISTS`
3. 检查 `providerId + userId` 是否已绑定 → 是则抛 `EXTERNAL_ACCOUNT_ALREADY_BOUND`
4. 创建 `AuthExternalAccount`，bindStatus=BOUND
5. 记录操作日志（BIND_EXTERNAL）
6. 返回 `ExternalAccountVO`

**unbindAccount 流程**:
1. 查找 `AuthExternalAccount`，验证属于当前用户
2. 检查解绑安全约束（委托给 T-067 的增强逻辑）
3. 更新 bindStatus=UNBOUND
4. 记录操作日志（UNBIND_EXTERNAL）
5. 记录登录日志（供审计）

**验收标准**:
- [ ] `OAuthService` 接口定义完整（4 个方法）
- [ ] `OAuthServiceImpl` 实现全部 4 个方法
- [ ] `authorize` 正确生成 state 并返回授权 URL
- [ ] `handleCallback` 正确执行 OAuth 流程：state 校验 → code 换 token → 获取用户信息 → 查找绑定 → 登录/提示绑定
- [ ] `bindAccount` 正确执行绑定，含重复检查
- [ ] `unbindAccount` 正确执行解绑，含安全约束检查
- [ ] 所有 OAuth 流程异常都有对应日志记录
- [ ] 三方 access_token 不写入日志、不返回前端
- [ ] 编译通过

**技术备注**:
- `OAuthServiceImpl` 依赖：`OAuthStateService`, `OAuthClientFactory`, `AuthProviderService`, `ExternalAccountService`, `ExternalAccountDAO`, `LoginLogService`, `OperationLogService`, `UserService`(查用户信息)
- `StpUtil.login(userId)` 复用现有 Sa-Token 登录机制，与密码登录走同一条路径
- `clientSecretEncrypted` V1 阶段作为明文使用
- 三方 access_token 只在 Service 内部使用，不写入日志、不返回给前端
- `handleCallback` 中未绑定场景的 `externalUserIdentity` 用于前端展示提示信息，格式如 `"飞书用户：张三"`

**子任务**:
- [ ] ST-066-01: OAuthService 接口定义 (0.5h)
- [ ] ST-066-02: authorize 方法实现 (1h)
- [ ] ST-066-03: handleCallback 方法实现（核心流程，最复杂） (3h)
- [ ] ST-066-04: bindAccount 方法实现 (1.5h)
- [ ] ST-066-05: unbindAccount 方法实现（含安全约束调用） (1h)
- [ ] ST-066-06: 日志记录集成（LoginLog + OperationLog） (1h)

---

#### T-067: ExternalAccountService 增强 — 最后登录方式检查

| 属性 | 值 |
|------|-----|
| 状态 | 📋 待开始 |
| 优先级 | P0 |
| 估算工时 | 2.5h |
| 风险缓冲 | +0.5h |
| 总工时 | 3h |
| 角色 | Agent B |
| 关联故事 | US-M5-02 |
| Sprint | Sprint 2 |
| 依赖 | T-062 |
| 可并行 | ✅ 与 T-066 并行 |

**任务描述**:
增强 `ExternalAccountServiceImpl` 的 `unbind` 方法，增加最后登录方式安全检查。

**新增逻辑**:
```java
// unbind 前检查
private void checkUnbindSafety(Long userId) {
    // 1. 检查用户是否有密码
    SysUser user = userDAO.findById(userId);
    boolean hasPassword = user.getPasswordHash() != null && !user.getPasswordHash().isBlank();

    // 2. 统计当前用户的有效绑定数（bindStatus=BOUND）
    List<AuthExternalAccount> boundAccounts = accountDAO.listByUserId(userId);
    long activeBoundCount = boundAccounts.stream()
        .filter(a -> "BOUND".equals(a.getBindStatus()))
        .count();

    // 3. 如果无密码且只剩 1 个绑定，拒绝解绑
    if (!hasPassword && activeBoundCount <= 1) {
        throw new BusinessException(ErrorCode.UNBIND_LAST_LOGIN_METHOD);
    }
}
```

**新增 `countActiveBindsByUserId` 方法到 DAO 层**:
- `AuthExternalAccountDAO.countActiveBindsByUserId(Long userId): long`
- 实现：`SELECT COUNT(*) FROM auth_external_account WHERE user_id = ? AND bind_status = 'BOUND' AND deleted = 0`

**验收标准**:
- [ ] `unbind` 方法在解绑前执行安全检查
- [ ] 用户无密码 + 仅剩 1 个绑定时，抛 `UNBIND_LAST_LOGIN_METHOD`
- [ ] 用户有密码时，可以解绑所有三方账号
- [ ] 用户有多个绑定时，可以解绑（即使无密码）
- [ ] `countActiveBindsByUserId` DAO 方法可正确计数
- [ ] 单元测试覆盖：无密码+1绑定（拒绝）/ 有密码+1绑定（允许）/ 无密码+2绑定（允许）/ 有密码+0绑定（允许）
- [ ] 测试全部通过

**技术备注**:
- 需要注入 `UserDAO` 到 `ExternalAccountServiceImpl`，检查 `passwordHash` 字段
- `passwordHash` 为空表示用户未设置密码（如通过 OAuth 创建的用户）
- 安全检查逻辑抽取为 `private` 方法，`unbind` 和 `OAuthService.unbindAccount` 都可调用

**子任务**:
- [ ] ST-067-01: AuthExternalAccountDAO 新增 countActiveBindsByUserId (0.5h)
- [ ] ST-067-02: ExternalAccountServiceImpl 增强 unbind + checkUnbindSafety (1h)
- [ ] ST-067-03: 单元测试 — 4 种解绑场景 (1h)

---

### Epic 6: OAuth Controller + 安全页（EP-010）

**目标**: 实现 OAuth 授权/回调/绑定/解绑的 API 端点 + 账号安全页
**关联需求**: US-M5-01, US-M5-02, US-M5-03
**预计跨度**: Sprint 2

---

#### T-068: OAuthController — /public/oauth/{provider}/authorize + callback

| 属性 | 值 |
|------|-----|
| 状态 | 📋 待开始 |
| 优先级 | P0 |
| 估算工时 | 2.5h |
| 风险缓冲 | +0.5h |
| 总工时 | 3h |
| 角色 | Agent B |
| 关联故事 | US-M5-01 |
| Sprint | Sprint 2 |
| 依赖 | T-066 |
| 可并行 | ❌ |

**任务描述**:
在 `io.github.lystrosaurus.admin.auth.oauth.controller` 包下创建 OAuthController，提供公开的 OAuth 授权和回调端点。

**端点定义**:

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | `/api/public/oauth/{provider}/authorize` | 生成授权 URL |
| GET | `/api/public/oauth/{provider}/callback` | OAuth 回调处理 |

**authorize 端点**:
- 入参：`@PathVariable String provider`
- 调用 `OAuthService.authorize(provider)`
- 返回 `ApiResponse<OAuthAuthorizeVO>`

**callback 端点**:
- 入参：`@PathVariable String provider`, `@RequestParam String code`, `@RequestParam String state`
- 构造 `OAuthCallbackDTO(code, state)`
- 调用 `OAuthService.handleCallback(provider, dto)`
- 返回 `ApiResponse<OAuthLoginVO>`
- `OAuthLoginVO` 包含 `needBind` 标志，前端据此决定跳转

**验收标准**:
- [ ] 两个端点路径正确：`/api/public/oauth/{provider}/authorize` 和 `/api/public/oauth/{provider}/callback`
- [ ] 路径遵循小写 + 连字符规范
- [ ] 无 Sa-Token 认证（在 `/api/public/**` 路径下）
- [ ] 统一响应格式 `ApiResponse<T>`
- [ ] MVC 测试覆盖：正常授权、正常回调、provider 不存在、state 无效
- [ ] 测试全部通过

**技术备注**:
- `/api/public/**` 路径在 SaTokenConfig 中已配置为免认证
- callback 端点的 `code` 和 `state` 是飞书重定向时携带的 query 参数
- 不做参数校验注解（`code` 和 `state` 为空时在 Service 层处理）

**子任务**:
- [ ] ST-068-01: OAuthController 实现（2 个端点） (1h)
- [ ] ST-068-02: MVC 测试 — MockMvc + Mock OAuthService (1.5h)

---

#### T-069: OAuthBindController — /app/auth/oauth/{provider}/bind + unbind

| 属性 | 值 |
|------|-----|
| 状态 | 📋 待开始 |
| 优先级 | P0 |
| 估算工时 | 2.5h |
| 风险缓冲 | +0.5h |
| 总工时 | 3h |
| 角色 | Agent B |
| 关联故事 | US-M5-02 |
| Sprint | Sprint 2 |
| 依赖 | T-066, T-067 |
| 可并行 | ✅ 与 T-068 部分并行（T-066 完成后可开始） |

**任务描述**:
在 `io.github.lystrosaurus.admin.auth.oauth.controller` 包下创建 OAuthBindController（或合并到 OAuthController），提供已登录用户的绑定/解绑端点。

**端点定义**:

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | `/api/app/auth/oauth/{provider}/bind` | 已登录用户绑定三方账号 |
| DELETE | `/api/app/auth/oauth/{provider}/unbind` | 已登录用户解绑三方账号 |

**bind 端点**:
- 入参：`@PathVariable String provider`, `@RequestBody OAuthBindDTO dto`（含 code, state）
- 获取当前用户 ID：`UserContext.getCurrentUserId()`
- 调用 `OAuthService.bindAccount(userId, provider, dto)`
- 返回 `ApiResponse<ExternalAccountVO>`

**unbind 端点**:
- 入参：`@PathVariable String provider`, `@RequestParam Long accountId`
- 获取当前用户 ID
- 调用 `OAuthService.unbindAccount(userId, provider, accountId)`
- 返回 `ApiResponse<Void>`

**验收标准**:
- [ ] 两个端点路径正确
- [ ] 路径遵循小写 + 连字符规范
- [ ] 需要 Sa-Token JWT 认证（在 `/api/app/**` 路径下）
- [ ] MVC 测试覆盖：正常绑定、重复绑定（抛异常）、正常解绑、最后登录方式解绑（抛异常）
- [ ] 测试全部通过

**技术备注**:
- 绑定/解绑都需要记录操作日志（在 Service 层实现，Controller 不直接操作日志）
- 解绑使用 `accountId` 而不是 `provider`，因为一个用户可能绑定同一 provider 的多个账号（理论上）

**子任务**:
- [ ] ST-069-01: OAuthBindController 实现（2 个端点） (1h)
- [ ] ST-069-02: MVC 测试 (1.5h)

---

#### T-070: SecurityProfileController — GET /app/profile/security

| 属性 | 值 |
|------|-----|
| 状态 | 📋 待开始 |
| 优先级 | P1 |
| 估算工时 | 1.5h |
| 风险缓冲 | +0.5h |
| 总工时 | 2h |
| 角色 | Agent B |
| 关联故事 | US-M5-03 |
| Sprint | Sprint 2 |
| 依赖 | T-062, T-067 |
| 可并行 | ✅ 与 T-068, T-069 并行 |

**任务描述**:
在现有 `ProfileController` 中新增 `/security` 端点，或创建独立的 `SecurityProfileController`。

**端点定义**:

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | `/api/app/profile/security` | 获取账号安全信息 |

**返回 VO**:
```java
public record SecurityProfileVO(
    List<ExternalAccountVO> boundAccounts,    // 已绑定的三方账号列表
    List<LoginLogVO> recentLogins,            // 最近登录记录（最多 10 条）
    boolean hasPassword                        // 用户是否设置了密码
) {}
```

```java
public record LoginLogVO(
    Long id,
    String loginType,
    String providerCode,
    String ipAddress,
    String status,
    String failureReason,
    LocalDateTime loginAt
) {}
```

**实现逻辑**:
1. 获取当前用户 ID
2. 查询 `ExternalAccountService.listByUserId(userId)` — 获取已绑定账号
3. 查询 `LoginLogService.getRecentLogins(userId, 10)` — 获取最近 10 条登录记录
4. 查询 `UserService.findById(userId)` — 判断 `passwordHash` 是否为空
5. 组装 `SecurityProfileVO` 返回

**验收标准**:
- [ ] `GET /api/app/profile/security` 端点可访问
- [ ] 返回已绑定的三方账号列表（providerCode, nickname, bindStatus 等）
- [ ] 返回最近登录记录（loginType, ipAddress, status, loginAt）
- [ ] 返回 `hasPassword` 标志
- [ ] MVC 测试覆盖：有绑定+有登录记录、无绑定+无登录记录
- [ ] 测试全部通过

**技术备注**:
- 复用现有 `ExternalAccountService.listByUserId`
- `LoginLogVO` 需要新建（在 `auth.log.vo` 或 `auth.vo` 包下）
- `hasPassword` 判断：`user.getPasswordHash() != null && !user.getPasswordHash().isBlank()`

**子任务**:
- [ ] ST-070-01: SecurityProfileVO + LoginLogVO 定义 (0.5h)
- [ ] ST-070-02: SecurityProfileController 实现 (0.5h)
- [ ] ST-070-03: MVC 测试 (0.5h)

---

### Epic 7: 测试 + 集成验证（EP-011）

**目标**: 全面测试覆盖、集成测试、ArchUnit 约束、全量回归
**关联需求**: US-M5-01, US-M5-02, US-M5-03
**预计跨度**: Sprint 2

---

#### T-071: OAuthService 单元测试 + OAuthController MVC 测试

| 属性 | 值 |
|------|-----|
| 状态 | 📋 待开始 |
| 优先级 | P0 |
| 估算工时 | 4h |
| 风险缓冲 | +1h |
| 总工时 | 5h |
| 角色 | Agent A |
| 关联故事 | US-M5-01 |
| Sprint | Sprint 2 |
| 依赖 | T-066, T-068 |
| 可并行 | ✅ 与 T-072 并行 |

**任务描述**:
为 OAuthService 和 OAuthController 编写全面的单元测试和 MVC 测试。

**OAuthServiceImplTest（单元测试）**:

| 测试场景 | 预期结果 |
|---------|---------|
| authorize 正常流程 | 返回 authorizeUrl + state |
| authorize provider 不存在 | 抛 OAUTH_PROVIDER_NOT_FOUND |
| authorize provider 已禁用 | 抛 OAUTH_PROVIDER_DISABLED |
| handleCallback 正常（已绑定用户） | 返回 JWT + needBind=false |
| handleCallback 正常（未绑定用户） | 返回 needBind=true + externalUserIdentity |
| handleCallback state 无效 | 抛 OAUTH_STATE_INVALID |
| handleCallback code 换 token 失败 | 抛 OAUTH_CODE_EXCHANGE_FAILED |
| handleCallback 获取用户信息失败 | 抛 OAUTH_USERINFO_FAILED |
| bindAccount 正常 | 返回 ExternalAccountVO |
| bindAccount 三方账号已被他人绑定 | 抛 BIND_ACCOUNT_ALREADY_EXISTS |
| bindAccount 当前用户已绑定该 provider | 抛 EXTERNAL_ACCOUNT_ALREADY_BOUND |
| unbindAccount 正常 | 成功解绑 |
| unbindAccount 最后登录方式 | 抛 UNBIND_LAST_LOGIN_METHOD |

**OAuthControllerTest（MVC 测试）**:

| 测试场景 | HTTP | 路径 | 预期 |
|---------|------|------|------|
| 正常授权 | GET | /api/public/oauth/lark/authorize | 200 + authorizeUrl |
| provider 不存在 | GET | /api/public/oauth/unknown/authorize | 错误码 |
| 正常回调 | GET | /api/public/oauth/lark/callback?code=xxx&state=yyy | 200 + OAuthLoginVO |
| state 无效回调 | GET | /api/public/oauth/lark/callback?code=xxx&state=invalid | 错误码 |

**验收标准**:
- [ ] `OAuthServiceImplTest` 覆盖 ≥ 13 个测试场景
- [ ] `OAuthControllerTest` 覆盖 ≥ 4 个测试场景
- [ ] 所有 Mock 依赖使用 Mockito
- [ ] 测试全部通过

**技术备注**:
- OAuthServiceImpl 依赖较多，使用 `@Mock` + `@InjectMocks`
- handleCallback 测试需要 Mock OAuthClientFactory → OAuthClient → OAuthStateService 整条链路
- MVC 测试中 OAuthService 使用 `@MockBean`

**子任务**:
- [ ] ST-071-01: OAuthServiceImplTest — authorize 场景 (1h)
- [ ] ST-071-02: OAuthServiceImplTest — handleCallback 场景（最复杂） (1.5h)
- [ ] ST-071-03: OAuthServiceImplTest — bindAccount + unbindAccount 场景 (1h)
- [ ] ST-071-04: OAuthControllerTest — MVC 测试 (1.5h)

---

#### T-072: 增强绑定/解绑 + 安全页单元测试 + MVC 测试

| 属性 | 值 |
|------|-----|
| 状态 | 📋 待开始 |
| 优先级 | P0 |
| 估算工时 | 3h |
| 风险缓冲 | +1h |
| 总工时 | 4h |
| 角色 | Agent B |
| 关联故事 | US-M5-02, US-M5-03 |
| Sprint | Sprint 2 |
| 依赖 | T-069, T-070 |
| 可并行 | ✅ 与 T-071 并行 |

**任务描述**:
为增强的绑定/解绑逻辑和安全页编写测试。

**OAuthBindControllerTest（MVC 测试）**:

| 测试场景 | HTTP | 路径 | 预期 |
|---------|------|------|------|
| 正常绑定 | POST | /api/app/auth/oauth/lark/bind | 200 + ExternalAccountVO |
| 重复绑定 | POST | /api/app/auth/oauth/lark/bind | 错误码 9508 |
| 正常解绑 | DELETE | /api/app/auth/oauth/lark/unbind?accountId=1 | 200 |
| 最后登录方式解绑 | DELETE | /api/app/auth/oauth/lark/unbind?accountId=1 | 错误码 9509 |

**SecurityProfileControllerTest（MVC 测试）**:

| 测试场景 | HTTP | 路径 | 预期 |
|---------|------|------|------|
| 有绑定 + 有登录记录 | GET | /api/app/profile/security | 200 + 完整数据 |
| 无绑定 + 无登录记录 | GET | /api/app/profile/security | 200 + 空列表 |
| 未认证访问 | GET | /api/app/profile/security | 401 |

**验收标准**:
- [ ] MVC 测试覆盖 ≥ 7 个场景
- [ ] 测试全部通过

**技术备注**:
- 安全页测试需要 Mock ExternalAccountService、LoginLogService、UserService
- 未认证访问测试验证 Sa-Token 拦截正确

---

#### T-073: 集成测试 + ArchUnit 约束 + 全量回归

| 属性 | 值 |
|------|-----|
| 状态 | 📋 待开始 |
| 优先级 | P0 |
| 估算工时 | 3h |
| 风险缓冲 | +1h |
| 总工时 | 4h |
| 角色 | Agent B |
| 关联故事 | US-M5-01 |
| Sprint | Sprint 2 |
| 依赖 | T-071, T-072 |
| 可并行 | ❌ |

**任务描述**:
集成测试验证 M5 新增模块的端到端流程，ArchUnit 约束验证分层规则，全量回归确保无破坏。

**集成测试（auth.oauth 包）**:
- OAuthStateService 集成测试（InMemory 实现）：生成 → 保存 → 消费 → 重复消费失败
- OAuthClientFactory 集成测试：获取 Lark Client ✅ / 获取未知 Client 失败
- ExternalAccountService 增强集成测试：解绑安全约束验证

**ArchUnit 约束扩展**:
- `auth.oauth.controller` 不依赖 `dao`、`mapper`、`entity`
- `auth.oauth.service` 不依赖 `mapper`
- `auth.log.controller` 不依赖 `dao`、`mapper`、`entity`（如有 Controller）

**全量回归**:
- 运行 `mvn test`，确认全部测试通过
- M4 现有 568 tests 不减少
- M5 新增测试 ≥ 52（预估）
- 总测试数 ≥ 620

**验收标准**:
- [ ] 集成测试通过
- [ ] ArchUnit 约则通过
- [ ] `mvn test` 全部通过
- [ ] 总测试数 ≥ 620
- [ ] M4 测试无回归（568 tests 不减少）

**技术备注**:
- 集成测试使用 `@ActiveProfiles("test")` + 本地 MySQL
- ArchUnit 依赖可选（如项目未引入则跳过）
- 全量回归是 Sprint 2 最后一步，阻塞 M5 里程碑确认

---

## 任务统计

### 按状态

| 状态 | 数量 | 总工时 |
|------|------|--------|
| 📋 待开始 | 17 | 76h |

### 按角色

| 角色 | 任务数 | 总工时 | 占比 |
|------|--------|--------|------|
| Agent A | 8 | 38h | 50% |
| Agent B | 9 | 38h | 50% |

### 按优先级

| 优先级 | 任务数 | 总工时 |
|--------|--------|--------|
| P0 | 15 | 71h |
| P1 | 2 | 5h |

### 按 Sprint

| Sprint | 任务数 | 总工时 | 关键产出 |
|--------|--------|--------|---------|
| Sprint 1 | 9 | 30.5h | V7 迁移 + ErrorCode + OAuthStateService + OAuthClient 接口 + Lark 实现 + 日志全栈 |
| Sprint 2 | 8 | 45.5h | OAuthService + Controller + 增强绑定 + 安全页 + 全部测试 |

### 测试估算

| 测试类型 | 来源 | 预估数量 |
|---------|------|---------|
| OAuthStateService 单元测试 | T-059 | ~6 |
| LarkOAuthClient 单元测试 | T-064 | ~8 |
| OAuthClientFactory 单元测试 | T-064 | ~3 |
| LoginLog + OperationLog 单元测试 | T-065 | ~8 |
| ExternalAccountService 增强测试 | T-067 | ~4 |
| OAuthService 单元测试 | T-071 | ~13 |
| OAuthController MVC 测试 | T-068, T-071 | ~4 |
| OAuthBindController MVC 测试 | T-069, T-072 | ~4 |
| SecurityProfileController MVC 测试 | T-070, T-072 | ~3 |
| 集成测试 | T-073 | ~5 |
| ArchUnit 测试 | T-073 | ~2 |
| **合计新增** | | **~60** |
| M4 现有测试 | | 568 |
| **M5 完成后总计** | | **≥ 620** |

---

## 并行开发策略

### Sprint 1 并行方案

```
Agent A (worktree-a)                    Agent B (worktree-b)
─────────────────────                   ─────────────────────
T-058 ErrorCode (0.5h) ─┐              T-057 V7 迁移 (2h) ─┐
                         ↓                                   ↓
T-059 OAuthStateService (3h)    ←并行→  T-062 LoginLog (3h)
T-060 OAuthClient 接口 (3h)    ←并行→  T-063 OperationLog (3h)
                         ↓                                   ↓
T-061 LarkOAuthClient (6h)              T-065 日志测试 (2h)
         ↓
T-064 Client 测试 (3h)
```

**冲突风险**：低。Agent A 工作在 `auth.oauth` 包，Agent B 工作在 `auth.log` 包 + 迁移脚本。唯一交集是 ErrorCode.java，T-058 由 Agent A 先完成，Agent B 不修改。

### Sprint 2 并行方案

```
Agent A (worktree-a)                    Agent B (worktree-b)
─────────────────────                   ─────────────────────
T-066 OAuthService (10h)       ←并行→  T-067 增强绑定 (3h)
         ↓                                      ↓
T-071 OAuth 测试 (5h)          ←并行→  T-068 OAuth Controller (3h)
                                        T-069 Bind Controller (3h)
                                        T-070 安全页 (2h)
                                                 ↓
                                        T-072 测试 (4h)
                                                 ↓
                                        T-073 集成+回归 (4h)
```

**冲突风险**：中。T-066（Agent A）完成后 Agent B 才能开始 T-068/T-069（Controller 依赖 Service）。合并时注意 `auth.oauth` 包下的文件不冲突。

---

## 版本历史

| 版本 | 日期 | 修改内容 | 修改人 |
|------|------|---------|--------|
| v1.0 | 2026-05-23 | M5 初始版本 | delivery-lead |
