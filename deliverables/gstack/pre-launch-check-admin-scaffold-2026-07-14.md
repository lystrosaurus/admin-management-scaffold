# 上线前全检报告 · admin-management-scaffold（代码审查 + 安全审计 + QA测试）

**日期**：2026-07-14
**场景**：上线前检查（代码审查 + 安全审计 + QA测试）
**参与成员**：产品官（gstack-product-reviewer）+ 安全卫士（gstack-security-officer）+ 质量门神（gstack-qa-lead）

---

## 📌 TL;DR（执行摘要）

- **整体结论**：🔴 不通过（NO-GO）。安全官 / 产品官 / QA 三方经交叉核实后**一致 NO-GO**（QA 原"有条件 Go"在实测复核后上调为 No-Go）。
- **阻塞项数量**：4 项 🔴（去重后跨三域）+ 9 项 🟠 高危（含安全域补充修订 F-13/F-14）。
- **最关键发现**：后台敏感接口**仅做登录校验、无服务端授权**（任意已登录用户可删除/禁用管理员、读取全部用户 PII）——该 🔴 由安全域独立发现，产品审查与 QA 的 ArchUnit 均只覆盖分层、未评估授权强制执行。
- **产品本体健康**：构建全绿、认证/RBAC 读取/数据权限/审计集成测试 548 例通过；但发布工程（无部署物、CI 缺数据库、测试腐化）同样阻断。
- **下一步**：先消除 4 项 🔴（服务端授权、默认凭证、登录无限流、无部署物），再修 🟠 后复测。

---

## 🎯 核心结论卡片

| 项目 | 内容 |
|------|------|
| Go / No-Go | 🔴 No-Go（暂缓上线） |
| 严重度分布 | 🔴 4 / 🟠 9 / 🟡 12 / 🟢 多项合规 |
| 关键行动项 | 15 条（P0×6 / P1×8 / P2×1） |
| 建议负责人 | 后端 + 安全 + DevOps（见行动清单） |

---

## 1. 各成员核心结论

### 🔍 产品官（代码审查）
- **核心判断**：架构分层整体合规，旧 `review_report.md` 的 ARCH-001（"Service 直连 MyBatis Mapper"）系**误报**——Service 注入的 `XxxMapper` 全部来自 `..mapstruct..`（MapStruct 转换器，允许），MyBatis-Plus Mapper 只存在于 DAO Impl，ArchUnit `service_should_not_depend_on_mapper` 通过且无误报。已修复项（SEC-001/002、IDX 索引）均验证为真修复。
- **关键建议**：新发现 1×🔴（Flyway V3 以 LOCAL-ONLY 注释"掩护"向生产写入默认 admin/admin123、testuser 同哈希）、3×🟠（登录失败不审计无限流、健康端点被鉴权击穿 k8s 探针、OAuth redirect_uri 无白名单）、8×🟡（MP 查询泄漏到 Service、详情 VO 手工拼装、404→500、文档漂移、`/open` 缺拦截器、V15 provider 默认启用、Sa-Token 策略宽松、无 CORS）。倾向 NO-GO。

### 🛡️ 安全卫士（OWASP+STRIDE 审计）
- **核心判断**：**NO-GO（暂缓上线）**，3 项 🔴 阻塞 + 4 项高危。最严重是**服务端授权缺失（A01）**：`WebMvcConfig` 仅 `checkLogin()`，`PermissionAspect` 的 `@RequiresPermission` 属可选注解、缺省不生效，而 User/Role/Permission/Menu/Employee/OrgUnit/integration/Dashboard 控制器**全部无授权注解**，任意已登录用户可接管管理员与全部 PII。叠加"默认口令种入所有环境"与"登录失败不审计无限流（违红线#5）"形成零凭据接管链。SQL 注入面干净、密码 BCrypt、JWT 密钥≥32 强校验、Token 仅 Header、无密钥落日志。
- **关键建议**：补默认拒绝式授权 + ArchUnit 断言；Flyway 种子与迁移分离、移除默认账户并强改密；登录失败全量审计 + Redis 限流/锁定；修 OAuth redirect_uri 白名单、nonce 校验、并行解绑归属校验、落地操作日志后复测。

### ✅ 质量门神（QA测试与发布）
- **核心判断（更正后：No-Go）**：原报"有条件 Go"，经安全官代码级实测复核后**上调为 No-Go**。原 H1（RBAC 分配写路径零覆盖）被证实根因为**授权缺失 = 可实操的权限提升漏洞**（`RoleController.assignPermissions:100`/`assignMenus:114` 暴露且整类零 `@RequiresPermission`，任意已登录用户可改写整个 RBAC 模型、为自己授全部权限）；原 H2（"请确认 OAuth 默认禁用"）被推翻为**默认启用 + 明文密钥**（`V15` 三提供方 `enabled=1`、`getClientSecret` 返回密文列明文），填真凭据即激活并叠加 F-05/F-06。产品本体构建/测试仍健康（548 例集成测试通过），但"仅工程阻断"的前提不再成立——存在硬性安全阻断。
- **关键建议**：F-01 分配接口补 `@RequiresPermission` 鉴权后才能上线；OAuth 种子置 `enabled=0` + 修 F-13 明文存储 + 补 F-05/F-06 校验（或彻底禁用入口）；工程阻断（补 `deploy/`、CI 加 MySQL+Redis、修 11 例测试腐化使 `mvn test` 转绿）仍须处理。附带澄清：`UserService.assignRoles` 为死代码（无控制器调用），其 NPE 测试为未接线死路径，优先级低于原暗示；但 `UserServiceTest.employeeDAO` NPE（`bindEmployee`，V16 活跃的用户-员工绑定路径）修复仍成立。

---

## 2. 综合审查发现（去重合并后按严重度排序）

| # | 严重度 | 类别 | 位置 | 问题描述 | 建议 | 来源成员 |
|---|--------|------|------|---------|------|---------|
| 1 | 🔴 | 安全/越权 | `WebMvcConfig.java:28`（拦截器仅 checkLogin）；`auth/aspect/PermissionAspect.java:31-41/49-59`（授权仅可选注解触发）；`RoleController.java:26-27`(类级无注解)/`:100-105`(assignPermissions)/`:114-119`(assignMenus)/`:39/51/64/76/87`(增删改查)；UserController/PermissionController/MenuController/EmployeeController/OrgUnitController/integration×3/DashboardController 同此 | 后台敏感接口仅 `checkLogin()`，无服务端授权（`@RequiresPermission` 可选不生效）。**双重实证（置信 10/10）**：安全官指证 + QA 独立核对——`system/controller/**` 下 `UserController`/`RoleController`/`PermissionController`/`MenuController` 的 `@RequiresPermission` 计数**均为 0**（仅 `DictController`/`ConfigItemController` 有）；`RoleController` 全部 6 个写方法（create/update/deleteById/assignPermissions/assignMenus/findAll）均无注解。**任意已登录低权限用户**即可：给自己角色挂任意权限（提权至管理员）、删除角色、增删用户/权限码/菜单、读全部 PII。F-01 由"RoleController 两个分配接口"扩大为**系统性授权缺失**，影响整个权限基盘 | 为 UserController/RoleController/PermissionController/MenuController 等全部受保护端点补 `@RequiresPermission`（先定 user:manage/role:manage/permission:manage/menu:manage 等权限码，并在 V11/V14 种子授予管理员角色）+ ArchUnit 断言强制注解存在 + 补鉴权集成测试 | 安全卫士(B1) + QA 独立核实 |
| 2 | 🔴 | 安全/凭据 | `V3__seed_data.sql:74-83` | Flyway 以 `-- LOCAL-ONLY` 注释"掩护"向**所有环境**写入默认 `admin/admin123`、`testuser`（同哈希）；注释不生效，违反红线#3 | 新增迁移删除/轮换默认账号、首次登录强制改密、种子移出 Flyway | 安全卫士(B2)+产品官(F-01) |
| 3 | 🔴 | 安全/认证 | `AuthService.java:51-105` | 登录失败不审计、无限流（红线#5）；可暴力破解；仅成功写 LoginLog | 失败分支补审计 + Redis 滑动窗口限流/锁定 | 安全卫士(B3)+产品官(F-02) |
| 4 | 🔴 | 发布/部署 | `deploy/`（空） | 无部署物：目录为空、无 Dockerfile，无法按既定方式上线 | 补齐 `deploy/`（Dockerfile/compose/nginx/env）或明确其他部署方式 | 质量门神(B1) |
| 5 | 🟠 | CI | `.github/workflows/ci.yml` | 后端 CI `mvn verify` 跑需 MySQL+Redis 的集成测试，但无 service 容器/Testcontainers → 必红 | CI 加 MySQL+Redis service 或改 Testcontainers | 质量门神(B2) |
| 6 | 🟠 | 测试 | 11 例失败（测试腐化） | `mvn test` 红：`FlywayMigrationTest`/`ErrorCodeTest`/`Role`·`User` DAO+Service NPE（mock 缺失/`sqlSessionFactory` 未注入），均非产品缺陷 | 修 11 例测试腐化使套件转绿 | 质量门神(B3/M1) |
| 7 | 🟠 | 安全/OAuth | `OAuthService.java:53-72` | OAuth `redirect_uri` 来自请求 `redirectBase` 未白名单校验（红线#4）；authorize 与 exchangeCode 用不同 `redirect_uri` | 维护 `redirectBase` 白名单并校验；统一 `redirect_uri` | 安全卫士(F-05)+产品官(F-04) |
| 8 | 🟠 | 安全/审计 | `OperationLogService.recordOperation` 零调用 | 操作日志从未写入（死代码），违反审计完整性 | 落地操作日志调用（关键写操作） | 安全卫士(F-07) |
| 9 | 🟠 | 安全/绑定 | `AuthExternalAccountController.java:35-75` + `ExternalAccountService.unbind` | 并行外部账号解绑缺归属校验；`bind` 读 `dto.userId()`，可绑他人三方账号 | 强制 `account.userId==currentUser`；`bind` 的 userId 从会话取 | 安全卫士(F-02) |
| 10 | 🟠 | 安全/OAuth | `OAuthStateService` | OAuth `nonce` 仅存储不校验 | 启用 nonce 校验 | 安全卫士(F-06) |
| 11 | 🟠 | 运维/部署 | `OpsController.java:21,35-39` | 健康端点 `/api/app/ops/health` 需登录+权限，击穿 k8s/Docker 探针，Pod 无法就绪 | 健康端点改公开或探活走 actuator | 产品官(F-03) |
| 12 | 🟡 | 安全/JWT | `SaTokenConfig:39-55` | JWT 密钥校验过弱（仅禁一个子串，`.env.example` 占位符可通过）；`UserContext.hasPermission/hasRole` 恒返回 true（潜伏后门桩） | 强化密钥校验；修正 UserContext 桩为真实实现 | 安全卫士(F-08/F-10) |
| 13 | 🟡 | 安全/上传 | `FileService.upload:45-83` | 文件上传无类型/大小白名单 | 加白名单校验 | 安全卫士(F-11) |
| 14 | 🟡 | 安全/日志 | `RequestLogFilter:34-35` | 记录完整 queryString，可能泄露敏感参数 | 脱敏或仅记路径 | 安全卫士(F-12) |
| 15 | 🟡 | 测试/安全 | `RoleService`/`UserService` 分配写路径 | RBAC 角色/权限/菜单分配写路径零有效覆盖（单测 NPE 且无集成测试），权限变更无守护 | 补分配路径集成测试 | 质量门神(H1) |
| 16 | 🟡 | 架构 | `DictService`/`ConfigItemService` | MyBatis-Plus 查询对象（`LambdaQueryWrapper`/`Page`）泄漏到 Service→DAO 边界 | 查询构造下沉 DAO Impl | 产品官(F-05) |
| 17 | 🟡 | 可维护 | `UserService`/`RoleService` detail VO | 详情 VO 手工拼装，易与 Entity 漂移 | 统一 MapStruct | 产品官(F-06) |
| 18 | 🟡 | API契约 | `GlobalExceptionHandler:81-86` | 未知路径返回 500（404→500） | 增加 `NoResourceFoundException`→404 | 产品官(F-07) |
| 19 | 🟡 | 文档 | `AGENTS.md` / `README` / 工作记忆 | 文档漂移：Flyway 实际至 V16（非 V14/V11）、antd 实为 5.29.3（非 6） | 校正文档 | 产品官(F-08)+质量门神(M2) |
| 20 | 🟡 | 安全 | `WebMvcConfig:28-30` | `/open/**` 排除鉴权但无 X-API-Token 拦截器，未来端点默认无认证 | 补 `OpenApiInterceptor` | 产品官(F-09) |
| 21 | 🟡 | 安全/配置 | `V15__seed_oauth_providers.sql` | 占位 `YOUR_*` 密钥且 `enabled=1`，误启用 OAuth 会因占位失败 | 默认 `enabled=0` | 产品官(F-10) |
| 22 | 🟡 | 安全/会话 | `application.yml:33-34` | Sa-Token `is-concurrent:true` / `active-timeout:-1` 偏宽松 | 按合规评估收紧 | 产品官(F-11) |
| 23 | 🟡 | 部署 | 全代码无 `Cors*` | 无 CORS 配置，跨源直连将失败 | 明确 CORS 策略 | 产品官(F-12) |
| 24 | 🟠 | 安全/OAuth | `AuthProviderService.java:89`(入口)/`:94-95`(return 明文)；`AuthProvider.java:38`(字段存明文)；`LarkOAuthClient.java:63`/`WecomOAuthClient.java:61`(取用)；`AuthProviderVO.java:8`(不含该字段，前端未泄露) | OAuth `client_secret` 明文存储（直接返回 `client_secret_encrypted` 列明文，注释自承"V1 直接返回明文"）；DB 泄露即暴露所有厂商密钥（`AuthProviderVO` 不含该字段、未泄露前端） | spring-security-crypto AES/KMS 加密，读取时解密 | 安全卫士(F-13) |
| 25 | 🟠 | 安全/配置 | `V15__seed_oauth_providers.sql:10/14/17/20`(enabled=1)；`AuthProviderService.java:78-85`(getEnabledByCode 仅拦!=1)；`OAuthService.java:54/78/113`(经默认通过)、`:60`(redirectBase 客户端可控无白名单)；`OAuthStateService.java:26-44`(nonce 存而不验) | 三提供方均 `enabled=1`，"功能暂停"非配置强制；`getEnabledByCode` 只拦 `enabled!=1`，填真凭据即激活，激活后带 redirect_uri 未白名单/nonce 未校验风险 | 种子置 `enabled=0`，显式运维开启；并修 F-05 白名单 + F-06 nonce 校验 | 安全卫士(F-14) |

---

## ✅ 行动清单（至少 3 条具体可执行项）

| # | 行动 | 负责方 | 紧急度 | 期望完成 |
|---|------|--------|--------|---------|
| 1 | 落地服务端授权（默认拒绝）：为 `UserController`/`RoleController`/`PermissionController`/`MenuController` 等全部受保护端点补 `@RequiresPermission`（先定 user:manage/role:manage/permission:manage/menu:manage 等权限码，并在 V11/V14 种子授予管理员角色）+ ArchUnit 断言强制注解存在 + 补鉴权集成测试 | 后端 + 安全 | P0 | 上线前 |
| 2 | 新增迁移删除/轮换默认账号（admin/testuser）、首次登录强制改密、种子移出 Flyway | 后端 | P0 | 上线前 |
| 3 | `AuthService.login()` 失败分支补审计 + Redis 失败计数/锁定限流 | 后端 | P0 | 上线前 |
| 4 | 补齐 `deploy/`（Dockerfile/docker-compose/nginx/env）或明确其他部署方式 | 后端 + Ops | P0 | 上线前 |
| 5 | CI 后端 job 加 MySQL+Redis service 或改 Testcontainers | DevOps | P0 | 上线前 |
| 6 | 修 11 例测试腐化使 `mvn test` 转绿 | QA/Dev | P0 | 上线前 |
| 7 | OAuth `redirect_uri` 白名单 + authorize/exchange 统一 + nonce 校验 | 后端 | P1 | 上线前/紧后 |
| 8 | 操作日志落地（关键写操作调用 `recordOperation`） | 后端 | P1 | 上线前 |
| 9 | 健康端点改公开或探活走 actuator | 后端 | P1 | 上线前 |
| 10 | 外部账号解绑补归属校验、`bind` 的 userId 从会话取 | 后端 | P1 | 紧后 |
| 11 | RBAC 分配写路径补集成测试 | Dev | P1 | 紧后 |
| 12 | OAuth `client_secret` 加密存储（spring-security-crypto AES/KMS，读取时解密） | 后端 | P1 | 上线前 |
| 13 | OAuth 种子提供方默认 `enabled=0`（显式运维开启，避免填真凭据即激活） | 后端 | P1 | 上线前 |
| 14 | 为 `RoleController` 分配路径补鉴权集成测试（RBAC 模型篡改面守护） | Dev | P1 | 紧后 |
| 15 | 文档校正（Flyway V16 / antd 5.29.3）+ 其余 🟡 加固项（JWT 校验、UserContext 桩、上传白名单、CORS、Sa-Token 策略、V15 默认禁用） | Dev | P2 | 后续 |

---

## ⚠️ 待完善 / 已知局限

- **授权阻断未经交叉验证**：安全卫士指出的"后台接口无服务端授权"为 🔴 阻断，但产品官的代码审查与 QA 的 ArchUnit 测试**仅覆盖分层、未评估授权注解的强制执行**——该项是安全域独立发现、静态可证（置信 9/10），建议作为最高优先处理，但需后端确认 `PermissionAspect` 实际拦截行为。
- **本机环境**：Maven/Vite 已用项目既定 workaround；QA 集成测试依赖本机 MySQL(root/root)+Redis(6379) 通过 548 例，CI 缺该基础设施。
- **测试腐化非产品缺陷**：11 例失败均为断言过期/mock 缺失/NPE，不反映业务代码错误，但作为发布门禁不可接受。
- **OAuth 绑定暂停但入口活跃**：真实环境实测绑定失败、功能暂停、代码保留；`/public/oauth` 入口与回调仍活跃，故 OAuth 相关安全项（#7/#9/#10/#21）仍计入。
- **安全域补充修订（2026-07-14 收尾）**：安全官经与 QA 交叉核实后新增 F-13（OAuth `client_secret` 明文存储）、F-14（OAuth 种子提供方默认 `enabled=1`，"暂停"非配置强制）；并将 F-01 由"理论越权"实证为"可实操的 RBAC 模型篡改"（`RoleController.assignPermissions/assignMenus` 暴露且零授权，置信升至 10/10）。本汇编报告已同步上述条目（发现表 #1/#24/#25、行动项 #12/#13/#14）。QA 的 11 例测试腐化结论不受影响，安全注入/A02 等判定源自代码审阅。
- **SQL 注入 / 依赖 / 日志泄密面良好**：全量 MyBatis-Plus 参数化、无 `${}`、无命令执行/反序列化；建议 CI 接 Dependency-Check（A06）。

---

## 📚 成员产出索引

- gstack-product-reviewer（产品官）原始产出：全量代码静态审查（review skill，7 子审查员），含 ARCH-001 误报核实、F-01~F-12 明细。
- gstack-security-officer（安全卫士）原始产出：配套详报 `C:/workspace/security_audit_report_admin_scaffold.md`（含第八章 F-01/F-13/F-14 精确 file:line 与 RoleController 分配路径"护栏+守护"补丁：V16 权限码迁移 SQL、RoleControllerAuthzTest MockMvc 负向/正向 JUnit 骨架）；OWASP Top 10 + STRIDE 威胁建模，F-01~F-14 明细，结论 NO-GO（3 阻塞 P0 + 6 项 P1）。
- gstack-qa-lead（质量门神）原始产出：QA 评估报告（本机 Maven/Vite workaround 实测），测试执行结果表、Issue 清单（B1~B3/H1~H2/M1~M3/L1~L3）、发布检查清单、回滚预案，结论有条件 Go。

---

> 本报告由软件工坊 AI 协作生成，关键决策请由工程负责人复核。
