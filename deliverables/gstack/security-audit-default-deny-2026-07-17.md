# 安全审计产出 · "默认拒绝"授权机制（F-01 整改）

**日期**：2026-07-17
**审计人**：gstack-security-officer（安全官，本人输出）
**对象**：`PermissionAspect` 默认拒绝改造 + `@PermitAll` 逃生舱 + 后续 V18 种子/注解映射
**方法**：STRIDE + OWASP Top 10（A01 Broken Access Control 为重点），全部结论基于源码实测（file:line），置信 ≥ 8/10

---

## 0. 结论（Go / No-Go）

**机制方向：GO（必须做）。本切片合并：条件式 Go（Go-with-conditions）。**

- "默认拒绝"是修复 F-01（A01）的**规范化正确范式**，批准推进。
- 但 `PermissionAspect` 翻转默认拒绝**不能独立合并**。当前所有未注解的 `/app/**` admin 端点在翻转后会统一返回 4001 —— 即"安全但全坏"。必须与 **①全量补 `@RequiresPermission` + ②V18 种子把新增码授予 ADMIN(role_id=1)** 同批交付，否则 admin 自己也全 4001 → 管理后台全断（高危发布事故）。
- 缺任一项配套（见 §7/§8）→ **No-Go**。

---

## 1. 架构核实（已实测，置信 10/10）

| 文件 | 实测结论 |
|---|---|
| `auth/config/WebMvcConfig.java:28` | `SaInterceptor(handle -> StpUtil.checkLogin())` —— **仅认证不授权**；`/app/**` 路径生效，`/public/** /open/**` 排除 |
| `auth/aspect/PermissionAspect.java:31` | `@Before("@annotation(requiresPermission)")` —— **仅对带注解方法触发**，无注解直接放行（F-01 根因） |
| `auth/aspect/PermissionAspect.java:49` | `@RequiresRole` 同理，仅注解触发 |
| `auth/config/StpInterfaceImpl.java:26-37` | 权限从 DB 拉取；`testuser` 无角色 → 0 权限（正确的低权基线） |
| `auth/context/UserContext.java:50-57,65-72` | ⚠️ **`hasPermission()` / `hasRole()` 恒返回 `true`**（`// 暂时返回 true` 桩）。`getPermissions()/getRoles()` 恒返回空。**严禁在默认拒绝切面中使用 `UserContext.hasPermission`**——必须用 `StpUtil.hasPermission`（真实数据源） |

**当前已注解（翻转后安全）控制器**：`RoleController`(7/7)、`FileController`(5/5,`file:manage`)、`OpsController`(2/2,`ops:view`)、`ConfigItemController`(5/5)、`DictController`(5/5)、`LoginLogController`、`OperationLogController`(`audit:log:view`)。

**当前未注解（翻转后默认拒绝）控制器**：`ProfileController`、`SecurityProfileController`、`AuthExternalAccountController`、`OAuthBindController`、`DashboardController`、`IntegrationLinkCandidateController`、`IntegrationPrincipalController`、`IntegrationSourceController`、`EmployeeController`、`OrgUnitController`、`PermissionController`、`MenuController`、`UserController`。

---

## 2. 端点分类与"必须显式放行"清单

### 2.1 必须 `@PermitAll`（仅登录即可，无角色区分 —— 否则破坏正常业务）

这些是**当前登录用户对自身资源**的操作，任何已登录用户都需可达：

| 方法 | 端点 | 来源 | 说明 |
|---|---|---|---|
| GET | `/api/app/profile` | `ProfileController.getProfile` | 当前用户资料（含菜单/角色/权限） |
| PUT | `/api/app/profile/password` | `ProfileController.changePassword` | 改自己密码（用 `getCurrentUserId()`） |
| GET | `/api/app/profile/permissions` | `ProfileController.getPermissions` | 当前用户权限码（前端路由守卫依赖） |
| GET | `/api/app/profile/security` | `SecurityProfileController.getSecurityProfile` | 自身安全信息（密码状态/绑定/登录记录） |
| POST | `/api/app/auth/oauth/{provider}/bind` | `OAuthBindController.bind` | 绑**自己**三方账号（用 `getCurrentUserId()`） |
| DELETE | `/api/app/auth/oauth/{provider}/unbind` | `OAuthBindController.unbind` | 解绑**自己**账号 |
| GET | `/api/app/menus/user` | `MenuController.findUserMenuTree` | 当前用户菜单树（用 `getCurrentUserId()`） |

> 注：`/api/app/menus/tree`（全量菜单树）与 `/api/app/dashboard/stats`（系统统计）**不是**自服务端点，属 admin 资源，**不得** `@PermitAll`（见 §2.2）。

### 2.2 必须 `@RequiresPermission`（admin 资源，**绝不** `@PermitAll`）

| 控制器 | 路径 | 建议权限码（遵循 `module:resource:action` 规范） |
|---|---|---|
| `UserController` | `/app/users/**` | `system:user:view/create/update/delete` |
| `PermissionController` | `/app/permissions/**` | `system:permission:view/manage` |
| `MenuController`（除 `/menus/user`） | `/app/menus/**` | `system:menu:view/manage`；仅 `findUserMenuTree` 为 §2.1 |
| `EmployeeController` | `/app/employees/**` | `hr:employee:view/manage` |
| `OrgUnitController` | `/app/org-units/**` | `hr:org:view/manage` |
| `IntegrationSourceController` | `/app/integration/sources/**` | `integration:source:view/manage` |
| `IntegrationPrincipalController` | `/app/integration/principals/**` | `integration:principal:view/manage` |
| `IntegrationLinkCandidateController` | `/app/integration/link-candidates/**` | `integration:candidate:view/manage`（`confirm/reject` 更敏感） |
| `AuthExternalAccountController` | `/app/auth/external-accounts/**` | `system:external-account:view`（**且需修 IDOR**，见 §4） |
| `DashboardController` | `/app/dashboard/stats` | `system:dashboard:view` |

---

## 3. `@PermitAll` 逃生舱安全风险与约束

**风险**
- **滥用/误用（最高频）**：开发者为"过 CI / 解阻塞"给敏感端点打 `@PermitAll` → 重开 F-01（认证即授权）。
- **类级 `@PermitAll` 灾难**：若允许类级，整个控制器变"登录即放行"。
- **IDOR 残留**：`@PermitAll` 端点若接受 `userId/employeeId` 路径/体参数读他人数据 → 越权读（如 `AuthExternalAccountController`）。
- **审计缺失**：`@PermitAll` 的 bind/unbind 仍需写审计日志（**红线 #5**：绑解绑必审计）。
- **与默认拒绝互斥实现 bug**：优先级错配可致**全拒（DoS 正常用户）**或**全放（沦陷）**。

**安全约束（建议写入实现规格 + 评审清单）**
1. `@PermitAll` **仅方法级允许**；类级 `@PermitAll` 默认禁止，如确需须安全评审登记。
2. 带 **`reason` 属性**强制理由：`@PermitAll(reason = "self-service: current user only")`，理由入每次发布评审。
3. 维护 **`@PermitAll` 端点注册表（allowlist）**；ArchUnit/集成测试断言"实标 `@PermitAll` 集合 == 允许清单"，**多出即构建失败**。
4. `@PermitAll` 端点**硬性约束**：方法体必须且仅用 `UserContext.getCurrentUserId()` 取主体，不得接受可指定他人资源的路径/体参数（防 IDOR）。
5. 命名明确为"已登录免角色"，**不可**命名为 `@AnonymousAccess`（避免被误解为绕过登录拦截）。
6. 切面优先级：**方法注解 > 类注解**；`@PermitAll` 与 `@RequiresPermission` 互斥，方法级 `@PermitAll` 覆盖类级 `@RequiresPermission`。
7. `@PermitAll` 端点**必须仍在 `/app/**` 登录拦截之下**（认证不可省）。

---

## 4. 应受控却被"默认放行"的端点（翻转前必补注解）

- 即 §1 未注解集合（§2.2 全部）。翻转默认拒绝后它们变拒绝（**安全态**），但若被懒标 `@PermitAll` 则仍失控 —— 这是最大落地风险。
- **重点 IDOR 面**（预检报告 F-02 已记，需在本机制一并收口）：
  - `AuthExternalAccountController.findByUserId(userId)` / `listByEmployeeId(employeeId)` / `findById(id)` —— 跨用户读他人三方账号；
  - `AuthExternalAccountController.bind(dto)` 读 `dto.userId()` —— 可绑他人三方账号；
  - `UserController.changePassword(@PathVariable id, ...)` —— 可改**他人**密码。
  - 这些**必须 admin 化且补归属校验**，绝不能 `@PermitAll`。

**排查方法（给产品官/QA）**
1. **静态扫描**：收集所有 `@RestController` 的 `@RequestMapping` + 方法 `@GetMapping/@PostMapping/...` 路径；与带 `@RequiresPermission`/`@PermitAll` 的方法比对；差集 = 未受控端点。
2. **ArchUnit 默认拒绝断言**（比翻转拦截器更稳的护栏）：对 `/app/**` 下每个 `@RestController` 的每个 public 方法，断言存在 `@RequiresPermission` 或 `@PermitAll`。无注解即编译失败。
3. **集成测试**：用 `testuser`（0 权限）打每个 `/app/**` 端点，断言**除 §2.1 白名单外全部 4001**，admin 对 §2.2 全部 200。

---

## 5. V18 种子 / 注解映射安全复核

- **必须 `INSERT IGNORE` 新增权限码**（idempotent，禁改 V1–V17），并**把新增码授予 ADMIN(role_id=1)**（及任何默认角色）。否则：
  - ADMIN 缺失新码 → admin 自己也 4001 → **管理后台全断**（高危发布事故）；
  - 注解字符串必须与 `sys_permission.code` **完全一致**（`system:user:view` 等），错配 = 全员拒绝。
- **禁止"ADMIN 通配/超级绕过"**：不要在切面写 `role==ADMIN 直接放行`（等同 `UserContext.hasPermission=true` 后门）。应**显式授予**所有新码给 ADMIN。
- `testuser`（USER 角色，0 权限）在默认拒绝下只能打 §2.1 `@PermitAll` 端点 —— 正好作 QA 低权负向基线，**保留**。
- **数据层授权缺口（残余，登记不 blocking）**：USER 角色 `data_scope_type='SELF'` 但控制器未实现 `WHERE created_by=currentUser`，故即便授权正确，普通用户读员工/组织仍无数据范围隔离（A01 数据层纵深）。建议默认拒绝之上单独立项。
- **审计落地**：`OperationLogService.recordOperation` 当前死代码（预检 F-07）；默认拒绝不自动补审计。务必在 bind/unbind、关键写操作落地 `recordOperation`（**红线 #5**：绑解绑必审计）。

---

## 6. OWASP / STRIDE 映射

| 维度 | 判定 |
|---|---|
| **A01 Broken Access Control** | 默认拒绝 = canonical 修复；`@PermitAll` 是唯一残余向量 → 用注册表+测试约束。F-01 由"默认放行"转"默认拒绝"彻底消除 |
| **A04 Insecure Design** | 注解治理需 ArchUnit + 种子同批，否则回归 |
| **A05 Security Misconfiguration** | 当前 `/app/**` 默认放行即配置错误，翻转即修 |
| **A09 Logging & Monitoring** | 拒绝事件未审计 → 建议记录 `PERMISSION_DENIED`（安全事件） |
| **STRIDE·Spoofing** | 登录拦截 + tokenVersion 已覆盖 |
| **STRIDE·Repudiation** | 拒绝/绑解绑需审计（红线 #5） |
| **STRIDE·Information Disclosure** | 默认拒绝收敛面；`@PermitAll` 仅返自身数据 |
| **STRIDE·DoS** | 切面优先级错配致全拒 → 必须有测试守护 |
| **STRIDE·EoP** | 默认拒绝防低权触达 admin；`@PermitAll` 是唯一提权面 |

---

## 7. 残余风险与优先级

**P0（阻塞合并）**
- 翻转默认拒绝必须与"全量补 `@RequiresPermission` + V18 授予 ADMIN"同批；CI 加 `AdminControllerAuthzTest`（admin 200 / testuser 4001）防回归。
- 切面实现：将 `@Before("@annotation(...)")` 改为**覆盖全部控制器方法**的 pointcut，逻辑按"方法 `@PermitAll` → 放行 / 方法或类 `@RequiresPermission` → 校验 / 其他 → 拒绝"，**方法级优先**；用 `StpUtil.hasPermission`（真实源），**禁止 `UserContext.hasPermission`（恒 true 桩，`UserContext.java:50-57`）**。

**P1**
- `@PermitAll` 注册表 + ArchUnit 允许清单断言 + `reason` 属性。
- `AuthExternalAccountController` 修 IDOR（listByUserId/listByEmployeeId/findById 改 admin 且/或限定当前用户；bind 的 userId 取自会话）。
- 落地 `OperationLog`（绑解绑 + 拒绝事件审计）。

**P2**
- 数据层 scope 隔离（SELF/ALL 落地到查询）。

---

## 8. 终裁与配套交付清单

**Go（条件式）**：默认拒绝机制本身批准推进；但本切片（task #3）不可在"未铺开注解 + 未补 V18 ADMIN 授予"前单独合并。

**配套交付（缺任一项 → No-Go）**
1. 本报告 §2.1 的 `@PermitAll` **允许清单**（硬编码入 ArchUnit 断言）；
2. V18 种子（新增码 `INSERT IGNORE` + 授予 ADMIN(role_id=1)）；
3. ArchUnit **默认拒绝断言**（每个 `/app/**` 控制器方法必带 `@RequiresPermission` 或 `@PermitAll`）；
4. `testuser` 负向回归测试（除白名单外全 4001，admin 对 §2.2 全 200）；
5. `AuthExternalAccountController` **IDOR 修复**（§4）。

---

## 9. 修订 / 与产品官设计对齐（2026-07-17 收尾）

产品官提出"属性门控 `auth.default-deny.enabled`（本轮默认 false）+ `@PermitAll` 逃生舱 + 分阶段逐控制器启用，最后翻开关"。**安全官确认该方案与本次审计口径一致，并补充以下实测修正与强化约束。**

### 9.1 实测修正：爆炸半径清单更正（置信 10/10）
对全部 `*Controller.java` 的 `@RequiresPermission` 计数量化后确认：

| 控制器 | @RequiresPermission 计数 | 是否需改 |
|---|---|---|
| `ConfigItemController` | 5/5 | **否**（已全注解，不在爆炸半径） |
| `DictController` | 5/5 | **否**（已全注解） |
| `LoginLogController` | 1/1 | **否**（已注解） |
| `OperationLogController` | 1/1 | **否**（已注解） |
| `FileController` | 5/5 | 否 |
| `OpsController` | 2/2 | 否 |
| `RoleController` | 7/7 | 否（V17 切片） |

> 产品官原清单将 `LoginLog/OperationLog/ConfigItem/Dict` 列为"无注解"属**误判**——它们早已全注解，翻转默认拒绝**不受影响**，请勿在其上做无谓改动以免引入回归。

**真实零注解集合（13 个，翻转后默认拒绝）**：
`AuthController`(/public/auth)、`OAuthController`(/public/oauth)、`ProfileController`、`SecurityProfileController`、`AuthExternalAccountController`、`OAuthBindController`、`DashboardController`、`IntegrationSourceController`、`IntegrationLinkCandidateController`、`IntegrationPrincipalController`、`OrgUnitController`、`PermissionController`、`UserController`、`EmployeeController`、`MenuController`。

### 9.2 关键设计修正：`/public` 与 `/open` 必须被切面排除（非 `@PermitAll`）
- PermissionAspect 是 AOP，**路径无关**，对所有 `@RestController` 生效。而 SaInterceptor 仅拦 `/app/**`、排除 `/public` `/open`。一旦默认拒绝全局开启：
  - **`AuthController`(/public/auth：login/logout/refresh) 会直接变 4001 → 登录链路整体挂掉**（这是真实 #1 爆炸半径，产品官判断正确）。
- **修正建议（优于类级 `@PermitAll`）**：将默认拒绝切面**按路径收敛到 `/app/**`**（切面内读 `HttpServletRequest` 路径，非 `/app` 直接放行 / 或 pointcut 结合路径判断），使 `/public` 与 `/open` 根本不在默认拒绝作用域内。
  - 理由：`@PermitAll` 语义是"**已登录、免角色**"，而 login/logout/refresh 是**登录前**端点，对其标注 `@PermitAll` 语义错误（甚至可能因"要求已登录"反而阻断登录）。路径排除最干净，且登录端点无需任何注解。
  - 若坚持用标注逃生舱，请改用专用 `@PublicEndpoint`/`@Bypass` 排除标记，不要重载 `@PermitAll` 语义。

### 9.3 门控 + 分阶段：安全官确认一致
- `auth.default-deny.enabled` 默认 **false** 本论 → 维持现状（默认放行），**无回归**，正确。
- 切面实现须：`enabled=false` 时完全不拦截（保留既有默认放行语义）；`true` 时仅对 `/app/**` 生效。
- **翻转开关前的硬门槛**：新增全局 ArchUnit 断言——**每个 `/app/**` 控制器 public 方法必须带 `@RequiresPermission` 或 `@PermitAll`**；CI 红即阻断翻开关。分阶段逐控制器补齐 + 每控制器 ArchUnit 守卫 + 最后翻开关，方向批准。

### 9.4 两条硬性约束再确认
1. **翻转前全量注解覆盖**：✅ 与 9.3 门控一致。注意当前仅 RoleController/File/Ops/ConfigItem/Dict/LoginLog/OperationLog 已覆盖；其余 13 个零注解控制器在翻开关前必须全部补 `@RequiresPermission` 或纳入 `@PermitAll` 白名单，否则翻开关即"安全但全坏"。
2. **`@PermitAll` 仅限只读/当前用户端点，禁用于任何特权变更**：✅ 强化为硬规则——
   - 允许：`GET` 或**仅作用于 `getCurrentUserId()` 的自身变更**（`PUT /profile/password`、`POST /auth/oauth/{p}/bind`、`DELETE /auth/oauth/{p}/unbind`）。
   - **禁止**：任何触及共享/跨用户/RBAC 状态的 `POST/PUT/DELETE`（如 `create-user`、`delete-role`、`assign-permission`、`create-employee`）绝不可 `@PermitAll`。
   - 本报告 §2.1 的 7 个白名单端点全部符合；其余均须 `@RequiresPermission`。

### 9.5 本轮范围提示
若本论仅动"3 控制器(UserController/PermissionController/MenuController) + 切面 + V18"且保持 `enabled=false`，属**安全无回归**；但剩余零注解 `/app` 控制器（Employee/OrgUnit/Integration×3/Dashboard/AuthExternalAccount/Profile 系）仍处默认放行（F-01 持续），须纳入后续阶段，且**翻开关前必须清零**（否则全 4001）。V18 仍须把新增码授予 ADMIN(role_id=1)（§5）。

### 9.6 对齐闭环确认（2026-07-17 收尾）
产品官已书面确认吸收 §9.1–§9.4 全部修正，设计口径与审计一致：
- 移除 ConfigItem/Dict/LoginLog/OperationLog 出"翻转前须补注解"清单（实测已全注解）；
- 切面按路径收敛 `/app`（读 `RequestContextHolder` requestURI，非 `/app` 直接 return，`/public`/`/open` 永不在作用域，登录端点零注解）；撤销 AuthController 类级 `@PermitAll`；
- 全局 ArchUnit 守卫 + `@PermitAll` 仅 GET/自身变更硬规则落地。
- 本论唯一 `@PermitAll` = `MenuController.findUserMenuTree`（GET+当前用户+只读），符合白名单。

**安全官追加两点跨阶段提醒（写入评审清单，防后续阶段踩坑）**：
1. `findUserMenuTree` 须标**方法级 `@PermitAll`** 以覆盖 MenuController 的**类级 `@RequiresPermission`**（方法注解 > 类注解，§3 约束#6）。若仅标类级而漏方法级，该端点会继承 admin 权限要求，正常用户菜单树打不开。
2. 后续阶段补齐 Profile/SecurityProfile/OAuthBind 等控制器时，其当前用户端点须标 **`@PermitAll`（非 `@RequiresPermission`）**，否则正常用户功能断裂。其中 `PUT /profile/password`、`POST /auth/oauth/{p}/bind`、`DELETE /auth/oauth/{p}/unbind` 为**自身变更（仅用 getCurrentUserId()）**，属硬规则允许的 POST/PUT/DELETE 例外，可标 `@PermitAll`；但 `AuthExternalAccountController` 的 `findByUserId/listByEmployeeId/findById/bind(dto.userId)` 是**跨用户读写（IDOR）**，必须 `@RequiresPermission` + 归属校验，绝不可 `@PermitAll`。

**终裁维持：Go（条件式）**。设计侧已无安全阻塞；翻开关前须满足 §7/§8 全部配套。
