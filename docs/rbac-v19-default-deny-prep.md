# V19 · default-deny 翻转前 RBAC 权限方案（纠正版）

> 作者：gstack-product-reviewer ｜ 状态：已根据 gstack-security-officer 裁定修订
> 依赖：V3(权限 1-8, ADMIN=1)、V11(9-11)、V14(12-18)、V17(19-20)、V18(21-29)
> 翻转目标：`auth.default-deny.enabled=true` 后，`/api/app/**` 下所有端点必须显式授权，漏标即拒（fail-closed）。

---

## 0. 关键修订（来自安全官裁定）

1. **OAuthBind 纠正**：原方案把 `OAuthBindController.bind` / `unbind` 标为 `@PermitAll`（误判为自改）。安全官据 RBAC 铁律纠正——OAuth 绑定/解绑属高敏感外部身份写操作，**禁止 @PermitAll**。已改为 `@RequiresPermission("auth:external-account:bind" / "auth:external-account:manage")`。
2. **@PermitAll 端点 6 → 4**：仅保留 Profile / SecurityProfile 的 4 个自查/自改端点。`@PermitAll` 仅允许 GET 自身 或 自改（`getCurrentUserId()`），OAuth 绑定明显越界，归为 `@RequiresPermission`。
3. **类级 @RequiresRole 兜底被否决**：源码核实 `PermissionAspect.checkRole` 切点仅为 `@Before("@annotation(requiresRole)")`（方法级），且 `defaultDeny` 仅排除 `!@within(RequiresPermission)`。类级 `@RequiresRole` **能编译但被 aspect 完全忽略，且不能阻断 default-deny**（false safety net）。与安全官一致：**不采用**，改以 fail-closed + 逐方法注解 + 全局覆盖率测试 为唯一安全网。
4. **域名统一**：`AuthExternalAccountController` 与 `OAuthBindController` 统一归入 `auth:external-account:*` 域（对齐安全官裁定命名），不再使用初稿的 `external-account:*`。

---

## 0.1 ⚠️ 与仓库当前 V19 实现的差异（需对齐后再继续）

仓库内已落地的 `V19__seed_zero_annotation_authz.sql`（Task #4，已 completed）与正在进行的注解（Task #5）仍采用**纠正前**方案，与本文及安全官裁定**不一致**，必须对齐：

| 维度 | 仓库当前（V19 文件） | 本文纠正版（安全官裁定） | 处置 |
|------|----------------------|--------------------------|------|
| 外部账号域 | `external-account:*` | `auth:external-account:*` | **必须改**：对齐安全官命名 |
| 解绑动作 | `external-account:unbind`(32) | `auth:external-account:manage`(40) | **必须改**：安全官裁定用 `manage` |
| 组织单元码 | 细粒度 view/create/update/delete(37-40) | 粗粒度 view/manage(34-35) | 建议改：与 `integration:*:manage` 风格统一 |
| 员工码 | 细粒度 view/create/update/delete(41-44) | 粗粒度 view/manage(36-37) | 建议改：同上 |
| 码总数 | 15 个(30-44) | 11 个(30-40) | 取决于 org 粒度决策 |

若保留仓库的细粒度 org 码，则 external-account 域仍须改为 `auth:external-account:*` 且 `unbind`→`manage`，码总数变为 13(30-42)。**注解（Task #5）与测试（Task #6）引用的 code 必须与最终 V19 完全一致**，否则运行时权限码缺失 / 覆盖率测试漂移。

## 1. 实际范围

- 真正零注解控制器：**10 个**（原 brief 列 12 个）。
- 排除：`FileController`（`file:manage` id10）、`OpsController`（`ops:view` id11）——经 Read 源码核实已自带 `@RequiresPermission`，无需改动，亦不纳入 V19。
- 最大现存权限 id = 29 → V19 从 **30** 起。
- 所有 `/app/**` 控制器在 `context-path=/api` 下实际对外路径为 `/api/app/**`，与 `PermissionAspect.isAppPath()` 网关一致。

---

## 2. 交付 1 · 新增权限码（V19, id 30-40, 全部绑定 ADMIN role_id=1）

| id | code | name(中文) | category(module) | owning controller |
|----|------|-----------|------------------|-------------------|
| 30 | `dashboard:view` | 仪表盘查看 | dashboard | DashboardController |
| 31 | `integration:source:manage` | 外部身份源管理 | integration | IntegrationSourceController |
| 32 | `integration:principal:manage` | 外部主体管理 | integration | IntegrationPrincipalController |
| 33 | `integration:link:manage` | 身份匹配管理 | integration | IntegrationLinkCandidateController |
| 34 | `org:orgunit:view` | 组织单元查看 | org | OrgUnitController |
| 35 | `org:orgunit:manage` | 组织单元管理 | org | OrgUnitController |
| 36 | `org:employee:view` | 员工查看 | org | EmployeeController |
| 37 | `org:employee:manage` | 员工管理 | org | EmployeeController |
| 38 | `auth:external-account:view` | 外部账号查看 | auth | AuthExternalAccountController / OAuthBindController |
| 39 | `auth:external-account:bind` | 外部账号绑定 | auth | AuthExternalAccountController / OAuthBindController |
| 40 | `auth:external-account:manage` | 外部账号管理 | auth | AuthExternalAccountController / OAuthBindController |

复用既有码（不再新增）：`integration:source:view`(16)、`integration:principal:view`(17)、`integration:link:view`(18)。
说明：`dashboard:view` 新建而非复用 `ops:view`(11)，二者语义不同（统计概览 vs 运维审计视图），见风险项 4。

---

## 3. 交付 2 · V19 幂等种子 SQL

```sql
-- V19: default-deny 翻转前补齐 /api/app/** 零注解控制器的权限码
-- 依赖: V3(ADMIN role_id=1, 权限 1-8), V11(9-11), V14(12-18), V17(19-20), V18(21-29)
-- 新增 11 个权限码 (30-40)，全部绑定 ADMIN(role_id=1)
-- 幂等: INSERT IGNORE + 显式 id(30-40) + uk_permission_code / uk_role_permission
-- 注: FileController(file:manage id10)、OpsController(ops:view id11) 已自带注解，不在本迁移范围

INSERT IGNORE INTO sys_permission
  (id, code, name, description, status, type, module, resource, action, sort_order, created_at, updated_at, created_by, updated_by, deleted, version)
VALUES
  (30, 'dashboard:view',              '仪表盘查看',   '查看系统统计概览',           'ENABLED', 'BUTTON', 'dashboard',  'dashboard',       'view',   30, NOW(), NOW(), 'system', 'system', 0, 1),
  (31, 'integration:source:manage',   '外部身份源管理','创建/更新/删除外部身份源',   'ENABLED', 'BUTTON', 'integration','source',          'manage', 31, NOW(), NOW(), 'system', 'system', 0, 1),
  (32, 'integration:principal:manage','外部主体管理', '创建/更新/删除外部主体',     'ENABLED', 'BUTTON', 'integration','principal',       'manage', 32, NOW(), NOW(), 'system', 'system', 0, 1),
  (33, 'integration:link:manage',     '身份匹配管理', '确认/拒绝身份匹配候选',       'ENABLED', 'BUTTON', 'integration','link',            'manage', 33, NOW(), NOW(), 'system', 'system', 0, 1),
  (34, 'org:orgunit:view',            '组织单元查看', '查看组织单元与架构树',       'ENABLED', 'BUTTON', 'org',        'orgunit',         'view',   34, NOW(), NOW(), 'system', 'system', 0, 1),
  (35, 'org:orgunit:manage',          '组织单元管理', '创建/更新/删除组织单元',     'ENABLED', 'BUTTON', 'org',        'orgunit',         'manage', 35, NOW(), NOW(), 'system', 'system', 0, 1),
  (36, 'org:employee:view',           '员工查看',     '查看员工信息',             'ENABLED', 'BUTTON', 'org',        'employee',         'view',   36, NOW(), NOW(), 'system', 'system', 0, 1),
  (37, 'org:employee:manage',         '员工管理',     '创建/更新/删除员工',       'ENABLED', 'BUTTON', 'org',        'employee',         'manage', 37, NOW(), NOW(), 'system', 'system', 0, 1),
  (38, 'auth:external-account:view',  '外部账号查看', '查看三方/外部账号',         'ENABLED', 'BUTTON', 'auth',       'external-account', 'view',   38, NOW(), NOW(), 'system', 'system', 0, 1),
  (39, 'auth:external-account:bind',  '外部账号绑定', '绑定三方/外部账号',         'ENABLED', 'BUTTON', 'auth',       'external-account', 'bind',   39, NOW(), NOW(), 'system', 'system', 0, 1),
  (40, 'auth:external-account:manage','外部账号管理', '解绑/管理三方外部账号',     'ENABLED', 'BUTTON', 'auth',       'external-account', 'manage', 40, NOW(), NOW(), 'system', 'system', 0, 1);

INSERT IGNORE INTO sys_role_permission
  (id, role_id, permission_id, created_at, created_by, updated_at, updated_by, deleted)
VALUES
  (30, 1, 30, NOW(), 'system', NOW(), 'system', 0),
  (31, 1, 31, NOW(), 'system', NOW(), 'system', 0),
  (32, 1, 32, NOW(), 'system', NOW(), 'system', 0),
  (33, 1, 33, NOW(), 'system', NOW(), 'system', 0),
  (34, 1, 34, NOW(), 'system', NOW(), 'system', 0),
  (35, 1, 35, NOW(), 'system', NOW(), 'system', 0),
  (36, 1, 36, NOW(), 'system', NOW(), 'system', 0),
  (37, 1, 37, NOW(), 'system', NOW(), 'system', 0),
  (38, 1, 38, NOW(), 'system', NOW(), 'system', 0),
  (39, 1, 39, NOW(), 'system', NOW(), 'system', 0),
  (40, 1, 40, NOW(), 'system', NOW(), 'system', 0);
```

约定：permission 与 role_permission 共用 id 30-40（与 V14/V17/V18 一致）；`type=BUTTON`；`sort_order=id`；幂等靠 `INSERT IGNORE` + 显式 id + `uk_permission_code` / `uk_role_permission`。

---

## 4. 交付 3 · 注解映射表（controller.method | 路径(含 /api) | verb | 注解）

> `@PermitAll` 仅 4 个（GET 自身 / 自改）；其余 33 个全部 `@RequiresPermission`。

### ProfileController (`/app/profile`) — 自域，全 @PermitAll
| method | path | verb | annotation |
|--------|------|------|------------|
| getProfile | `/api/app/profile` | GET | `@PermitAll(reason="当前登录用户查看自身资料")` |
| changePassword | `/api/app/profile/password` | PUT | `@PermitAll(reason="修改当前登录用户自身密码")` |
| getPermissions | `/api/app/profile/permissions` | GET | `@PermitAll(reason="当前登录用户查看自身权限码")` |

### SecurityProfileController (`/app/profile`) — 自域，@PermitAll
| method | path | verb | annotation |
|--------|------|------|------------|
| getSecurityProfile | `/api/app/profile/security` | GET | `@PermitAll(reason="当前登录用户查看自身安全信息")` |

### AuthExternalAccountController (`/app/auth/external-accounts`) — 跨用户，全 @RequiresPermission
| method | path | verb | annotation |
|--------|------|------|------------|
| bind | `/api/app/auth/external-accounts/bind` | POST | `@RequiresPermission("auth:external-account:bind")` |
| unbind | `/api/app/auth/external-accounts/{id}` | DELETE | `@RequiresPermission("auth:external-account:manage")` |
| findById | `/api/app/auth/external-accounts/{id}` | GET | `@RequiresPermission("auth:external-account:view")` |
| listByUserId | `/api/app/auth/external-accounts/by-user/{userId}` | GET | `@RequiresPermission("auth:external-account:view")` |
| listByEmployeeId | `/api/app/auth/external-accounts/by-employee/{employeeId}` | GET | `@RequiresPermission("auth:external-account:view")` |

### OAuthBindController (`/app/auth/oauth`) — 高敏感外部身份写，纠正为 @RequiresPermission
| method | path | verb | annotation |
|--------|------|------|------------|
| bind | `/api/app/auth/oauth/{provider}/bind` | POST | `@RequiresPermission("auth:external-account:bind")` |
| unbind | `/api/app/auth/oauth/{provider}/unbind` | DELETE | `@RequiresPermission("auth:external-account:manage")` |

### DashboardController (`/app/dashboard`)
| method | path | verb | annotation |
|--------|------|------|------------|
| getStats | `/api/app/dashboard/stats` | GET | `@RequiresPermission("dashboard:view")` |

### IntegrationSourceController (`/app/integration/sources`)
| method | path | verb | annotation |
|--------|------|------|------------|
| create | `/api/app/integration/sources` | POST | `@RequiresPermission("integration:source:manage")` |
| findById | `/api/app/integration/sources/{id}` | GET | `@RequiresPermission("integration:source:view")` |
| update | `/api/app/integration/sources/{id}` | PUT | `@RequiresPermission("integration:source:manage")` |
| deleteById | `/api/app/integration/sources/{id}` | DELETE | `@RequiresPermission("integration:source:manage")` |
| list | `/api/app/integration/sources` | GET | `@RequiresPermission("integration:source:view")` |

### IntegrationPrincipalController (`/app/integration/principals`)
| method | path | verb | annotation |
|--------|------|------|------------|
| create | `/api/app/integration/principals` | POST | `@RequiresPermission("integration:principal:manage")` |
| findById | `/api/app/integration/principals/{id}` | GET | `@RequiresPermission("integration:principal:view")` |
| update | `/api/app/integration/principals/{id}` | PUT | `@RequiresPermission("integration:principal:manage")` |
| deleteById | `/api/app/integration/principals/{id}` | DELETE | `@RequiresPermission("integration:principal:manage")` |
| list | `/api/app/integration/principals` | GET | `@RequiresPermission("integration:principal:view")` |

### IntegrationLinkCandidateController (`/app/integration/link-candidates`)
| method | path | verb | annotation |
|--------|------|------|------------|
| listPending | `/api/app/integration/link-candidates/pending` | GET | `@RequiresPermission("integration:link:view")` |
| listByPrincipalId | `/api/app/integration/link-candidates/by-principal/{principalId}` | GET | `@RequiresPermission("integration:link:view")` |
| confirm | `/api/app/integration/link-candidates/{id}/confirm` | POST | `@RequiresPermission("integration:link:manage")` |
| reject | `/api/app/integration/link-candidates/{id}/reject` | POST | `@RequiresPermission("integration:link:manage")` |

### OrgUnitController (`/app/org-units`)
| method | path | verb | annotation |
|--------|------|------|------------|
| create | `/api/app/org-units` | POST | `@RequiresPermission("org:orgunit:manage")` |
| findById | `/api/app/org-units/{id}` | GET | `@RequiresPermission("org:orgunit:view")` |
| update | `/api/app/org-units/{id}` | PUT | `@RequiresPermission("org:orgunit:manage")` |
| deleteById | `/api/app/org-units/{id}` | DELETE | `@RequiresPermission("org:orgunit:manage")` |
| findPage | `/api/app/org-units` | GET | `@RequiresPermission("org:orgunit:view")` |
| findTree | `/api/app/org-units/tree` | GET | `@RequiresPermission("org:orgunit:view")` |

### EmployeeController (`/app/employees`)
| method | path | verb | annotation |
|--------|------|------|------------|
| create | `/api/app/employees` | POST | `@RequiresPermission("org:employee:manage")` |
| findById | `/api/app/employees/{id}` | GET | `@RequiresPermission("org:employee:view")` |
| update | `/api/app/employees/{id}` | PUT | `@RequiresPermission("org:employee:manage")` |
| deleteById | `/api/app/employees/{id}` | DELETE | `@RequiresPermission("org:employee:manage")` |
| findPage | `/api/app/employees` | GET | `@RequiresPermission("org:employee:view")` |

---

## 5. 交付 4 · 实施顺序 + 类级 @RequiresRole 兜底评估

### 实施顺序
1. **落 V19 迁移**：应用种子 SQL，本地与 CI 验证 Flyway 幂等（`INSERT IGNORE` 重复执行无副作用）。
2. **注解 4 个自域端点**：Profile/SecurityProfile 的 getProfile、changePassword、getPermissions、getSecurityProfile 标 `@PermitAll(reason=...)`。
3. **注解其余 33 个端点**：按上表逐方法加 `@RequiresPermission(code)`。建议**仅方法级**，不依赖类级继承。
4. **禁止类级 @RequiresRole**：10 个控制器均不加类级 `@RequiresRole`（见下评估，确认为 no-op）。
5. **加全局覆盖率测试**（见交付 5）并设为 CI 硬门禁，翻转前必须全绿。
6. **翻转开关**：仅在 (a) 迁移绿 (b) 10 个控制器全部注解 (c) 覆盖率测试绿 之后，将 `auth.default-deny.enabled=true`。
7. **灰度**：先 staging 翻转 → 冒烟验证 ADMIN 可访问全部 37 端点 → 再 prod；保留回滚（`enabled=false`）。

### 类级 @RequiresRole 兜底评估（结论：否决）
- 源码核实：`RequiresRole` `@Target` 含 `TYPE`（编译通过），但 `PermissionAspect.checkRole` 切点为 `@Before("@annotation(requiresRole)")`——**仅方法级**，无 `@within`。
- `defaultDeny` 仅排除 `!@within(RequiresPermission)`（不含 RequiresRole）。
- 推论：类级 `@RequiresRole` 是 **no-op**——被 aspect 完全忽略，且**不能**阻断 default-deny。把它当兜底会造成"已加保护"的假象，实际漏标方法仍被拒（但也无从区分"故意@PermitAll"与"漏标"）。
- 决策（与安全官一致）：**不采用**。安全网 = fail-closed（漏标即拒，安全）+ 逐方法显式注解 + 覆盖率测试在部署前捕获漏标。

---

## 6. 交付 5 · 覆盖率测试（CI 硬门禁）+ 风险/待决

### 覆盖率测试断言（翻转前必须全绿）
- **A. 全覆盖**：10 个控制器中每个 public 方法必须且只能带 `@PermitAll` 或 `@RequiresPermission` 之一，无未注解方法。
- **B. 无类级兜底**：断言 10 个控制器**零**类级 `@RequiresRole`；且每个受保护方法自身携带注解，而非靠继承。
- **C. 高敏感写禁 @PermitAll**：断言 `OAuthBindController.bind`、`OAuthBindController.unbind`、`AuthExternalAccountController.bind`、`AuthExternalAccountController.unbind` 均为 `@RequiresPermission`，**绝不**为 `@PermitAll`。（安全官补充断言）
- **D. 码表一致性**：注解中引用的 `@RequiresPermission` code 集合 == V19 种子 code 集合（无悬空码、无种子码未被引用），防漂移。

### 风险 / 待决（需主理人拍板）
1. **brief 前提误差**：FileController / OpsController 已自带注解，已排除。无动作。
2. **OAuthBind 已纠正**：见 §0。已落地。
3. **类级 @RequiresRole no-op**：已否决，见 §5。
4. **dashboard:view vs 复用 ops:view**：新建 `dashboard:view`(30)。若你倾向复用 `ops:view`(11)，需同步改映射与种子。
5. **org: 域 vs system:org:***：采用 `org:`。若想并入 system 域请告知，影响 code 命名与菜单 permission 关联。
6. **integration:*:manage 粗粒度**：写操作合并为单一 `manage` 码，最小化码量。若需 create/update/delete 分权，需拆为 3 码/资源。
7. **迁移 id 协调**：V19 占用 sys_permission / sys_role_permission 的 30-40。确保同期无其它迁移抢占该区间。
8. **灰度与回滚**：翻转后非 ADMIN 角色将失去 dashboard/org/integration/external-account 访问（预期收紧）。确认当前无业务角色需要这些端点；prod 保留 `enabled=false` 回滚开关。

### 补充安全注记
- 4 个 `@PermitAll` 端点翻转后对所有**已登录用户**开放（不止 ADMIN），这是自服务预期。其安全性依赖方法体确实使用 `getCurrentUserId()` 且**不接收目标 userId 参数**——已逐一核实 4 个方法均满足（无跨用户入参）。
- `auth:external-account:*` 三个码当前仅绑定 ADMIN；后续若需对非 ADMIN 开放绑定自服，应单独评估（不在此 V19 范围）。
