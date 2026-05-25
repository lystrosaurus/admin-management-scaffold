# 代码优化跟进清单

> 本文档记录深度代码审查发现的所有优化项，按优先级排序，便于逐个修复和跟踪。
> 关联审查报告：`review_report.md`（根目录）、`code-review-m8.md`
> 创建日期：2026-05-25

---

## 任务状态图例

- 📋 待开始 (Pending)
- 🟡 进行中 (In Progress)
- 🟢 已完成 (Done)
- ⛔ 阻塞 (Blocked)

---

## 🔴 Critical — 建议优先修复

### OPT-001: 修复 OAuth State 内存存储问题（SEC-002）

| 属性 | 值 |
|------|-----|
| 状态 | 📋 待开始 |
| 优先级 | P0 |
| 严重程度 | Critical |
| 模块 | auth / oauth |
| 关联报告 | review_report.md#SEC-002 |

**文件位置**:
- `backend/src/main/java/io/github/lystrosaurus/admin/auth/oauth/state/OAuthStateService.java`

**问题描述**:
OAuth state 使用 `ConcurrentHashMap` 内存存储，多实例部署时状态不共享，存在 CSRF 风险。

**修复方向**:
1. 注入 `RedisTemplate<String, Object>`
2. 将 state 存储到 Redis，TTL 5 分钟
3. 使用 `opsForValue().getAndDelete()` 验证并消费 state
4. 移除 `ConcurrentHashMap` 和 `ScheduledExecutorService`

**验收标准**:
- [ ] state 存储在 Redis 中，带 5 分钟 TTL
- [ ] `validateAndConsumeState()` 使用原子性的 getAndDelete
- [ ] 移除内存中的 `ConcurrentHashMap`
- [ ] 多实例部署时 state 验证正常

---

### OPT-002: 修复关联表批量插入性能问题（BATCH-001）

| 属性 | 值 |
|------|-----|
| 状态 | 📋 待开始 |
| 优先级 | P0 |
| 严重程度 | Critical |
| 模块 | system / dao |

**文件位置**:
- `backend/src/main/java/io/github/lystrosaurus/admin/system/user/dao/impl/UserDAOImpl.java:113-118`
- `backend/src/main/java/io/github/lystrosaurus/admin/system/role/dao/impl/RoleDAOImpl.java:119-124`
- `backend/src/main/java/io/github/lystrosaurus/admin/system/role/dao/impl/RoleDAOImpl.java:160-165`

**问题描述**:
`assignRoles()` / `assignPermissions()` / `assignMenus()` 使用 `for` 循环逐条 `insert()`，大数据量时产生大量 SQL 往返。

**修复方向**:
1. 使用 MyBatis-Plus `saveBatch()` 批量插入
2. 或手写 `INSERT INTO ... VALUES (...), (...)` SQL
3. 确保中间表实体对应的 Mapper 支持批量插入

**验收标准**:
- [ ] `assignRoles()` 使用批量插入（1 条 SQL）
- [ ] `assignPermissions()` 使用批量插入
- [ ] `assignMenus()` 使用批量插入
- [ ] 现有测试通过，无功能回归

---

### OPT-003: 修复 UserService 绕过 DAO 层问题（ARCH-002）

| 属性 | 值 |
|------|-----|
| 状态 | 📋 待开始 |
| 优先级 | P0 |
| 严重程度 | Critical |
| 模块 | system / user |

**文件位置**:
- `backend/src/main/java/io/github/lystrosaurus/admin/system/user/service/UserService.java:193-206`

**问题描述**:
`employeeExists()` 直接 `new JdbcTemplate(dataSource)` 执行原生 SQL，违反分层架构规范，且每次调用都新建 `JdbcTemplate` 实例。

**修复方向**:
1. 在 `EmployeeDAO` 或 `UserDAO` 中添加 `existsEmployeeById(Long employeeId)` 方法
2. 移除 `UserService` 中的 `DataSource` 和 `JdbcTemplate` 依赖
3. `UserService` 改为调用 DAO 方法

**验收标准**:
- [ ] 移除 `UserService` 中的 `DataSource` 字段
- [ ] 移除 `UserService` 中的 `JdbcTemplate` 使用
- [ ] DAO 层提供 `existsEmployeeById()` 方法
- [ ] ArchUnit 分层验证通过

---

## 🟡 Major — 重要优化项

### OPT-004: 修复前端 api/client.ts 返回类型错误（TYPE-001）

| 属性 | 值 |
|------|-----|
| 状态 | 📋 待开始 |
| 优先级 | P1 |
| 严重程度 | Major |
| 模块 | frontend / api |

**文件位置**:
- `frontend/src/api/client.ts:40`

**问题描述**:
响应拦截器中 `return data.data as AxiosResponse` 将业务数据断言为 `AxiosResponse` 类型，类型与运行时实际值不符。

**修复方向**:
1. 改为 `return data.data as T`
2. 检查 `get/post/put/del` 泛型参数传法，确保 axios 类型正确
3. 验证前端类型检查无报错

**验收标准**:
- [ ] 拦截器返回类型与运行时值一致
- [ ] `npm run build` 无 TS 类型错误
- [ ] API 调用正常（登录、获取列表等）

---

### OPT-005: 修复 DataScopeHelper 重复查询角色问题（PERF-004）

| 属性 | 值 |
|------|-----|
| 状态 | 📋 待开始 |
| 优先级 | P1 |
| 严重程度 | Major |
| 模块 | web / datascope |

**文件位置**:
- `backend/src/main/java/io/github/lystrosaurus/admin/web/datascope/DataScopeHelper.java:49-111`

**问题描述**:
`getAccessibleOrgIds()`、`hasSelfScope()`、`hasAllScope()` 各自独立调用 `getRolesByUserId()`，一次 HTTP 请求会触发 **3 次**相同的角色查询（共 6 条 SQL）。

**修复方向**:
1. 在当前方法调用链内缓存角色列表（方法内局部变量传递）
2. 或将 `getRolesByUserId()` 结果缓存到方法参数中
3. 确保不引入跨请求的状态泄漏

**验收标准**:
- [ ] 一次请求内角色查询仅执行 1 次
- [ ] 不影响多线程安全性
- [ ] 数据权限计算结果正确

---

### OPT-006: 修复 DataScopeHelper 递归查询部门树性能问题（PERF-005）

| 属性 | 值 |
|------|-----|
| 状态 | 📋 待开始 |
| 优先级 | P1 |
| 严重程度 | Major |
| 模块 | web / datascope |

**文件位置**:
- `backend/src/main/java/io/github/lystrosaurus/admin/web/datascope/DataScopeHelper.java:216-224`

**问题描述**:
`collectDescendantIds()` 递归查询数据库，每级子部门都触发一次 `findByParentId()`。部门层级深时产生大量 SQL。

**修复方向**:
1. 方案 A：一次性加载全部部门到内存，构建 `parentId -> children` 映射后递归
2. 方案 B：利用 `org_unit.full_path` 字段，用 `LIKE` 查询一次性获取所有子部门
3. 评估数据量后选择合适方案

**验收标准**:
- [ ] 获取子部门最多执行 2 次 SQL（或更少）
- [ ] 数据权限计算结果正确
- [ ] 大数据量（1000+ 部门）下性能可接受

---

### OPT-007: 修复 OrgUnitService.buildTree() O(n²) 复杂度（PERF-006）

| 属性 | 值 |
|------|-----|
| 状态 | 📋 待开始 |
| 优先级 | P1 |
| 严重程度 | Major |
| 模块 | organization / orgunit |

**文件位置**:
- `backend/src/main/java/io/github/lystrosaurus/admin/organization/orgunit/service/OrgUnitService.java:148-164`

**问题描述**:
每次递归都全量遍历 `allTreeVOs` 列表过滤，构建一棵有 n 个节点的树需要 O(n²) 时间。

**修复方向**:
1. 先用 `Map<Long, List<OrgUnitTreeVO>>` 按 `parentId` 分组
2. 递归时直接从 Map 取子节点，避免全量扫描
3. 参考 `MenuService.findTree()` 的实现方式保持一致

**验收标准**:
- [ ] 树构建时间复杂度降为 O(n)
- [ ] 结果与优化前一致
- [ ] 大数据量下性能显著改善

---

### OPT-008: 移除 MapStruct 冗余 @Mapping 注解（CODE-001）

| 属性 | 值 |
|------|-----|
| 状态 | 📋 待开始 |
| 优先级 | P1 |
| 严重程度 | Major |
| 模块 | system / mapstruct |

**文件位置**:
- `backend/src/main/java/io/github/lystrosaurus/admin/system/user/mapstruct/UserMapper.java:22-24`
- `backend/src/main/java/io/github/lystrosaurus/admin/system/role/mapstruct/RoleMapper.java:29`

**问题描述**:
同名字段的 `@Mapping(target = "lastLoginAt", source = "lastLoginAt")` 等注解是多余的，MapStruct 自动映射同名同类型字段。

**修复方向**:
1. 删除所有 `target` 和 `source` 相同的 `@Mapping` 注解
2. 运行 `mvn compile` 验证生成的代码一致
3. 检查其他 Mapper 文件是否有同样问题

**验收标准**:
- [ ] 删除冗余 `@Mapping` 注解
- [ ] `mvn compile` 成功，生成的 impl 代码不变
- [ ] 检查所有 Mapper 文件，无遗漏

---

### OPT-009: 修复 findById() MapStruct 转换后手动重建 VO 问题（CODE-002）

| 属性 | 值 |
|------|-----|
| 状态 | 📋 待开始 |
| 优先级 | P1 |
| 严重程度 | Major |
| 模块 | system / user, system / role |

**文件位置**:
- `backend/src/main/java/io/github/lystrosaurus/admin/system/user/service/UserService.java:82-119`
- `backend/src/main/java/io/github/lystrosaurus/admin/system/role/service/RoleService.java:89-118`

**问题描述**:
先调用 `mapper.toDetailVO(user)` 生成中间 VO，再手动 `new DetailVO(...)` 把所有字段重新赋值一次。MapStruct 的工作被完全浪费。

**修复方向**:
1. 方案 A：扩展 MapStruct，添加包含关联数据的映射方法（如 `@AfterMapping`）
2. 方案 B：直接使用构造函数构建完整 VO，删除中间的 MapStruct 调用
3. 参考 `EmployeeService.findById()` 的良好模式（通过 `buildVO` 组装）

**验收标准**:
- [ ] 消除 MapStruct 转换后立即手动重建 VO 的模式
- [ ] `UserService.findById()` 和 `RoleService.findById()` 修复
- [ ] `mvn test` 通过，功能无回归

---

### OPT-010: 移除手动设置的 createdAt 与自动填充冲突（CODE-003/004）

| 属性 | 值 |
|------|-----|
| 状态 | 📋 待开始 |
| 优先级 | P1 |
| 严重程度 | Major |
| 模块 | integration / identitylink, auth / log |

**文件位置**:
- `backend/src/main/java/io/github/lystrosaurus/admin/integration/identitylink/service/IdentityLinkCandidateService.java:31`
- `backend/src/main/java/io/github/lystrosaurus/admin/auth/log/service/LoginLogService.java:34`
- `backend/src/main/java/io/github/lystrosaurus/admin/auth/log/service/OperationLogService.java:31`

**问题描述**:
手动 `setCreatedAt(LocalDateTime.now())` 与 `BaseEntity` 的 `@TableField(fill = FieldFill.INSERT)` 自动填充冲突，代码冗余。

**修复方向**:
1. 确认 `IdentityLinkCandidate` / `LoginLog` / `OperationLog` 是否继承 `BaseEntity`
2. 若继承，删除手动设置代码，依赖 MyBatis-Plus 自动填充
3. 若不继承，考虑统一继承 `BaseEntity` 或保留手动设置并添加注释说明原因

**验收标准**:
- [ ] 移除与 `@TableField(fill = INSERT)` 重复的手动设置
- [ ] 审计字段仍正确填充
- [ ] 相关测试通过

---

### OPT-011: 修复 AuthService 登录 IP 硬编码问题（CODE-005）

| 属性 | 值 |
|------|-----|
| 状态 | 📋 待开始 |
| 优先级 | P1 |
| 严重程度 | Major |
| 模块 | auth |

**文件位置**:
- `backend/src/main/java/io/github/lystrosaurus/admin/auth/service/AuthService.java:83`

**问题描述**:
`user.setLastLoginIp("127.0.0.1")` 长期硬编码，无法获取真实客户端 IP。注释写"后续可以从 RequestContextHolder 获取"，但长期未改。

**修复方向**:
1. 从 `RequestContextHolder` 获取当前 `HttpServletRequest`
2. 复用 `RequestLogFilter.getClientIp()` 的相同逻辑获取真实 IP
3. 提取 IP 获取逻辑为公共工具类，供 Filter 和 Service 共用

**验收标准**:
- [ ] 登录时记录真实客户端 IP（考虑 X-Forwarded-For）
- [ ] 提取公共 IP 获取工具方法
- [ ] `RequestLogFilter` 和 `AuthService` 共用同一套 IP 获取逻辑

---

### OPT-012: 修复 AuthService 混合构造函数注入模式（INJ-001）

| 属性 | 值 |
|------|-----|
| 状态 | 📋 待开始 |
| 优先级 | P1 |
| 严重程度 | Major |
| 模块 | auth |

**文件位置**:
- `backend/src/main/java/io/github/lystrosaurus/admin/auth/service/AuthService.java:34-46`

**问题描述**:
同时存在 2 参数和 3 参数构造函数，且使用 `@Autowired(required = false)` 在字段上，混合注入模式不规范。

**修复方向**:
1. 统一为单一全参数构造函数
2. 使用 `@RequiredArgsConstructor`
3. 若 `RedisTemplate` 确实可选，考虑用 `Optional<RedisTemplate>` 或单独的条件 Bean 处理

**验收标准**:
- [ ] 移除混合构造函数模式
- [ ] 使用统一的构造器注入
- [ ] 可选依赖（RedisTemplate）处理合理
- [ ] `mvn test` 通过

---

## ⚠️ Minor — 建议优化项

### OPT-013: 统一 ErrorCode 编码体系（CODE-006）

| 属性 | 值 |
|------|-----|
| 状态 | 📋 待开始 |
| 优先级 | P2 |
| 严重程度 | Minor |
| 模块 | common / exception |

**文件位置**:
- `backend/src/main/java/io/github/lystrosaurus/admin/exception/ErrorCode.java`

**问题描述**:
`SYSTEM_SUCCESS(200, ...)` 使用 HTTP 状态码，`AUTH_401(1001, ...)` 使用业务错误码，两种体系混用。前端拦截器判断 `data.code === 200`，但业务异常返回的 code 可能是 1001，语义不一致。

**修复方向**:
1. 统一使用业务错误码体系（如 `SYSTEM_SUCCESS(0, ...)`）
2. 或明确分离：HTTP code 用于通信层，business code 用于业务层
3. 确保前端拦截器判断逻辑同步调整

**验收标准**:
- [ ] ErrorCode 编码体系统一
- [ ] 前端拦截器判断逻辑正确
- [ ] 所有 API 响应格式一致

---

### OPT-014: 移除查询方法上的 @Transactional 注解（CODE-007）

| 属性 | 值 |
|------|-----|
| 状态 | 📋 待开始 |
| 优先级 | P2 |
| 严重程度 | Minor |
| 模块 | 多处 Service |

**文件位置**:
- 多处 Service 的 `findById()`、`findPage()`、`findAll()` 等方法

**问题描述**:
纯查询方法标注 `@Transactional(rollbackFor = Exception.class)`，增加连接持有时间，无实际意义。

**修复方向**:
1. 纯查询方法移除 `@Transactional`
2. 或改为 `@Transactional(readOnly = true)`（若方法内有多表查询需要事务一致性）
3. 批量检查所有 Service 类

**验收标准**:
- [ ] 查询方法无不必要的 `@Transactional`
- [ ] 写操作仍保留事务注解
- [ ] `mvn test` 通过

---

### OPT-015: 统一 Service.create() 使用 MapStruct 转换（CODE-008）

| 属性 | 值 |
|------|-----|
| 状态 | 📋 待开始 |
| 优先级 | P2 |
| 严重程度 | Minor |
| 模块 | system / role |

**文件位置**:
- `backend/src/main/java/io/github/lystrosaurus/admin/system/role/service/RoleService.java:43-52`

**问题描述**:
`RoleService.create()` 手动 `new SysRole()` 并逐个 set 字段，但 `AuthProviderService` / `ExtSourceService` 等都使用 MapStruct `toEntity()`，风格不一致。

**修复方向**:
1. 在 `RoleMapper` 中添加 `toEntity(RoleCreateDTO dto)` 方法
2. 修改 `RoleService.create()` 使用 MapStruct 转换
3. 检查其他 Service 是否有同样不一致问题

**验收标准**:
- [ ] `RoleService.create()` 使用 MapStruct
- [ ] 与其他 Service 风格一致
- [ ] `mvn test` 通过

---

### OPT-016: 补充 sys_role_menu.role_id 缺失索引（MISS-001）

| 属性 | 值 |
|------|-----|
| 状态 | 📋 待开始 |
| 优先级 | P2 |
| 严重程度 | Minor |
| 模块 | db / migration |

**文件位置**:
- `backend/src/main/resources/db/migration/V8__add_missing_indexes.sql`

**问题描述**:
V8 脚本已添加 `sys_user_role.user_id` 和 `sys_role_permission.role_id` 索引，但遗漏了 `sys_role_menu.role_id` 索引（report 中提到的 IDX-003）。

**修复方向**:
1. 在 V8 中追加：`ALTER TABLE sys_role_menu ADD INDEX idx_role_id (role_id);`
2. 或新建 V9 迁移脚本（如果 V8 已在某些环境执行过）
3. 确认 Flyway 版本策略

**验收标准**:
- [ ] `sys_role_menu` 表有 `role_id` 索引
- [ ] Flyway 迁移正常执行
- [ ] 查询性能有改善

---

## 任务统计

### 按状态

| 状态 | 数量 |
|------|------|
| 📋 待开始 | 16 |
| 🟡 进行中 | 0 |
| 🟢 已完成 | 0 |
| ⛔ 阻塞 | 0 |

### 按严重程度

| 严重程度 | 数量 | 编号 |
|----------|------|------|
| 🔴 Critical | 3 | OPT-001 ~ OPT-003 |
| 🟡 Major | 9 | OPT-004 ~ OPT-012 |
| ⚠️ Minor | 4 | OPT-013 ~ OPT-016 |

### 按模块

| 模块 | 任务数 |
|------|--------|
| auth / oauth | 4 (OPT-001, OPT-011, OPT-012, OPT-010) |
| system / dao | 3 (OPT-002, OPT-008, OPT-009) |
| system / user | 2 (OPT-003, OPT-009) |
| web / datascope | 2 (OPT-005, OPT-006) |
| organization / orgunit | 1 (OPT-007) |
| frontend / api | 1 (OPT-004) |
| integration / identitylink | 1 (OPT-010) |
| common / exception | 1 (OPT-013) |
| system / role | 1 (OPT-015) |
| db / migration | 1 (OPT-016) |

---

## 修复建议顺序

### 第一波：安全 + 架构（Critical）

1. **OPT-001** — OAuth State 内存存储（安全漏洞）
2. **OPT-002** — 关联表批量插入（性能）
3. **OPT-003** — UserService 绕过 DAO（架构违规）

### 第二波：性能优化（Major）

4. **OPT-005** — DataScopeHelper 重复查询
5. **OPT-006** — DataScopeHelper 递归查部门树
6. **OPT-007** — OrgUnitService O(n²) 树构建
7. **OPT-002** — 批量插入（继续完善）

### 第三波：代码质量（Major + Minor）

8. **OPT-004** — 前端 api/client.ts 类型修复
9. **OPT-008** — MapStruct 冗余注解
10. **OPT-009** — findById 重建 VO 浪费
11. **OPT-010** — createdAt 自动填充冲突
12. **OPT-011** — 登录 IP 硬编码
13. **OPT-012** — 混合构造注入
14. **OPT-013** — ErrorCode 体系统一
15. **OPT-014** — 查询方法 @Transactional
16. **OPT-015** — MapStruct 使用一致
17. **OPT-016** — 补充缺失索引

---

## 版本历史

| 版本 | 日期 | 修改内容 | 修改人 |
|------|------|---------|--------|
| v1.0 | 2026-05-25 | 初始版本：深度代码审查后的 16 项优化跟进清单 | code-reviewer |
