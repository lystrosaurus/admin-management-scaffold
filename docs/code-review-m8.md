# 全面代码审查报告（M8 启动前）

日期：2026-06-05

## 审查范围

覆盖后端依赖版本、后端架构最佳实践、前端依赖与构建、前后端交互规范、测试策略 5 个维度。

---

## 一、P0 严重问题（必须修复）

### 1. Spring Boot 3.2.5 已 EOL，应升级到 4.0.6

**位置**：`backend/pom.xml`

Spring Boot 4.0.6 已正式 GA 发布。当前 3.2.5 的 OSS 支持已于 2024-11 结束，不再有安全补丁。

升级涉及的联动变更：
- `mybatis-plus-spring-boot3-starter` → `mybatis-plus-spring-boot4-starter`
- `sa-token-spring-boot3-starter` → `sa-token-spring-boot4-starter`
- `redisson-spring-boot-starter` 3.27.0 → 4.4.0（3.x 不兼容 Boot 4）
- Spring Boot 4 使用 Jackson 3.0（包名从 `com.fasterxml.jackson` → `tools.jackson`），需检查自定义 Jackson 配置
- Flyway 移除手动版本号，由 parent 统一管理

### 2. 权限检查未实现 — 始终返回 true

**位置**：`UserContext.java`、`PermissionAspect.java`

`hasPermission()` 和 `hasRole()` 始终返回 `true`，`getCurrentUserRoles()` 返回空列表。任何已登录用户都能访问所有接口。

### 3. 登录失败无限流 — 可被暴力破解

**位置**：`AuthServiceImpl.java`

登录失败没有限制尝试次数。需基于 Redis 实现 IP + 用户名的失败计数和锁定机制。

### 4. TypeScript strict 模式未启用

**位置**：`frontend/tsconfig.app.json`

缺少 `strict: true`，`noImplicitAny`、`strictNullChecks` 等关键类型检查全部缺失。

### 5. any 类型滥用

**位置**：`frontend/src/api/` 下 5 个 API 文件，14+ 处 `any` 使用

所有 `mapXxx(raw: any)` 映射函数缺少后端 VO 类型定义。

---

## 二、P1 重要问题（建议修复）

### 6. UserServiceImpl 绕过 DAO 层

**位置**：`UserServiceImpl.java`

直接注入 `DataSource` + `JdbcTemplate` 执行 SQL，违反分层架构。应用 `EmployeeDAO.existsById()` 替代。

### 7. 登录 IP 硬编码为 "127.0.0.1"

**位置**：`AuthServiceImpl.java`

审计日志无法追踪真实客户端 IP。需从 `HttpServletRequest` 获取（考虑 X-Forwarded-For）。

### 8. JWT 密钥有不安全的默认值

**位置**：`application.yml`

`sa-token.jwt-secret-key` 默认值 `your-jwt-secret-key-at-least-32-chars-long`。应移除默认值，启动时强制要求配置。

### 9. pom.xml 结构问题

- `spring-security-crypto` 重复声明（2 次）
- `mybatis-spring` 显式声明多余（starter 已传递依赖）
- Flyway 版本硬编码为 10.15.0
- 缺少 `dependencyManagement` / BOM 管理
- MapStruct 可从 1.5.5 升级到 1.6.3

### 10. Ant Design 版本不符合设计文档

**位置**：`frontend/package.json`

设计文档要求 Ant Design 6，实际使用 5.29.3。`@ant-design/pro-components` 完全未使用，应移除。

### 11. API 重复代码

`auth.ts` 和 `oauth.ts` 都定义了 `getAuthorizeUrl`、`bindAccount`、`unbindAccount`。应统一到 `oauth.ts`。

### 12. AuthGuard 仅检查 token 存在性

**位置**：`AuthGuard.tsx`

用户可在 localStorage 手动设置任意字符串绕过守卫。应在初始化时调用 `fetchProfile` 验证 token 有效性。

### 13. 审计字段 createdBy/updatedBy 硬编码为 "system"

**位置**：`AuditMetaObjectHandler.java`

应从 Sa-Token 获取当前用户 ID，注入审计字段。

---

## 三、P2 一般问题（可改进）

### 14. 分页参数未验证

**位置**：各 Controller 的分页接口

恶意请求可传入 `size=999999` 导致全表查询。应添加 `@Min(1) @Max(100)` 验证。

### 15. RestTemplate 无超时配置

**位置**：`RestTemplateConfig.java`

外部 API 慢响应可能导致线程长时间阻塞。

### 16. 缺少缓存机制

整个代码库没有 `@Cacheable`。热点数据（用户角色、菜单树、权限码）应缓存到 Redis。

### 17. 登出/刷新接口路径语义不清

登出和刷新 Token 放在 `/public/` 路径下，但需要认证。应移到 `/app/auth/`。

### 18. Refresh Token 使用 GET 方法

有副作用的操作应使用 POST。

### 19. 401 重定向使用 window.location.href

导致整个应用重新加载，应配合 React Router navigate。

### 20. 测试覆盖不足

- 后端：623 tests，但权限检查、登录限流等核心安全逻辑未测试
- 前端：仅 3 个测试文件（10 tests），远低于 80% 覆盖率目标

### 21. 废弃空目录

`frontend/src/pages/` 下有 5 个空目录应清理。

### 22. 登录失败日志表字段注释

`auth_login_log` 表 `login_ip` 字段注释有误（写了"密码加密盐值"）。

---

## 四、做得好的方面

1. **分层架构整体清晰**：Controller → Service → DAO → Mapper 链路完整
2. **事务管理规范**：42 处 `@Transactional(rollbackFor = Exception.class)`
3. **统一响应格式**：`ApiResponse<T>` + `ErrorCode` 枚举
4. **DTO/VO 使用 record**：不可变，安全
5. **Entity 继承 BaseEntity**：审计字段、逻辑删除、乐观锁完备
6. **MapStruct 对象转换**：避免手动映射
7. **Flyway 数据库迁移**：版本化管理
8. **Spotless 代码格式化**：Google Java Format 统一风格
9. **数据权限框架**：DataScope + 注解 + 拦截器
10. **前端路由懒加载**：全部 8 个业务页面使用 React.lazy
11. **Zustand 状态管理**：简洁高效

---

## 五、建议修复路线图

### 阶段 1：基础设施升级（优先级最高）

| 任务 | 说明 | 工作量 |
|------|------|--------|
| Spring Boot 4.0.6 升级 | 含 starter 更换、Redisson 4.4.0、Jackson 3.0 迁移 | 大 |
| pom.xml 清理 | 移除重复依赖、添加 BOM、移除多余显式依赖 | 小 |
| JWT 密钥安全化 | 移除默认值，启动检查 | 小 |
| TypeScript strict 模式 | 启用 + 修复类型错误 | 中 |

### 阶段 2：安全加固

| 任务 | 说明 | 工作量 |
|------|------|--------|
| 权限检查实现 | UserContext 真正查询权限，Redis 缓存 | 大 |
| 登录失败限流 | Redis 计数 + 锁定机制 | 中 |
| 登录 IP 获取 | 从 HttpServletRequest 获取真实 IP | 小 |
| 分页参数验证 | @Min/@Max 注解 | 小 |
| 审计字段修复 | createdBy/updatedBy 从 Sa-Token 获取 | 中 |

### 阶段 3：代码质量

| 任务 | 说明 | 工作量 |
|------|------|--------|
| 消除 any 类型 | 定义后端 VO 类型，替换 mapXxx(raw: any) | 中 |
| API 去重 | 统一 OAuth API 到 oauth.ts | 小 |
| RestTemplate 超时 | 添加连接/读取超时配置 | 小 |
| 分层违规修复 | UserServiceImpl 移除 JdbcTemplate | 小 |
| 前端空目录清理 | 删除 5 个废弃目录 | 小 |

### 阶段 4：性能与测试

| 任务 | 说明 | 工作量 |
|------|------|--------|
| Redis 缓存 | 热点数据缓存 + 失效策略 | 大 |
| 前端测试补充 | API 测试 + 组件测试 | 大 |
| 后端安全测试 | 权限、登录限流等测试 | 中 |
