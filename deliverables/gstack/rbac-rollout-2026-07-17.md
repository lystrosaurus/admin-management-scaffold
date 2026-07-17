# RBAC 授权铺开 + 默认拒绝机制 — 交付报告

**日期**：2026-07-17
**场景**：全流程交付（产品规划 → 实现 → 安全审计 → QA 验证）
**参与成员**：产品官（gstack-product-reviewer）+ 安全卫士（gstack-security-officer）+ 质量门神（gstack-qa-lead）

---

## 📌 TL;DR（执行摘要）

- 整体结论：**🟢 通过（含收尾修复）** —— RBAC 代码与行为改动 🟢 零回归、已合并（commit `165c5fc`）；原"V18 种子未应用"阻塞项已于 `b7ea790` 修复（测试库 Flyway V2 断裂根因：`TestDatabaseInitializer` 的 `baselineOnMigrate(true)` 缺省基线版本=1 导致 V1 建表脚本被跳过），现 V1–V18 迁移链完整执行，V18 种子经直连库核验已落库。
- 阻塞项数量：**0**（原 V2 断裂独立工单已闭环，见 commit `b7ea790`）。
- 下一步：翻转 `auth.default-deny.enabled=true` 前，仍须确认**目标（生产/预发）库**已应用 V18（测试库已自动落库；生产库遵循本项目手动迁移惯例，见发布检查清单）。

---

## 🎯 核心结论卡片

| 项目 | 内容 |
|------|------|
| Go / No-Go | 🟢 Go（V18 数据验证已闭环，门控仍保持关闭） |
| 严重度分布（RBAC 相关） | 🔴 0 / 🟠 0 / 🟡 0 / 🟢 全绿 |
| 已知局限（非本任务） | 🟢 0（原测试库 Flyway V2 断裂已于 `b7ea790` 修复并验证） |
| 关键行动项 | 3+ 条（见下，翻转前前置项） |
| 建议负责人 | 主理人（翻转决策）/ 运维（目标库手动 V18）/ CI（OOM） |

---

## 1. 各成员核心结论

### 🔍 产品官（产品评审 / 实现）
- 核心判断：四态决策（ALLOW / CHECK_PERMISSION / CHECK_ROLE / DENY）+ 门控开关 + `@PermitAll` 逃生舱 + 分阶段铺开的设计正确，既堵住"无注解放行"根因（F-01），又避免翻转默认拒绝时误伤合法自服务端点与登录。
- 关键建议：权限码不过度增殖——`bindEmployee/unbindEmployee` 复用 `system:user:update`；`@PermitAll` 仅用于 `findUserMenuTree`（当前登录用户只读自身菜单树）。

### 🛡️ 安全卫士（OWASP+STRIDE 审计）
- 核心判断：条件式 Go。终裁"不可在'未铺开注解 + 未补 V18 ADMIN 授予'前单独合并"——本交付两项均已满足（三控制器注解铺开、V18 SQL 已写并绑 ADMIN）。
- 关键建议（已采纳为设计约束）：① `defaultDeny` 必须按 URI 收敛到 `/api/app/**`，不得用 `within(@RestController)` 全量拦截（否则打挂 `/public` 登录）；② `@PermitAll` 仅 METHOD 级、必填 `reason()`、仅限 GET 或自身变更；③ 切面强制走 `StpUtil.hasPermission`，禁用 `UserContext.hasPermission` 恒真后门桩；④ 翻转前清零其余零注解 `/app` 控制器。完整审计见 `deliverables/gstack/security-audit-default-deny-2026-07-17.md`。

### ✅ 质量门神（QA 测试与发布）
- 核心判断：原 🔴（V18 未应用，阻塞数据验证）；但 **RBAC 改动本身 🟢 零回归**——全量 621 用例中 RBAC 相关的 3 个 AuthzTest + 四态决策测试 + ArchUnit + RoleControllerAuthzTest + AuthControllerTest 全部 0 失败。该 🔴 已于 `b7ea790` 闭环（见排障手报告），现 `FlywayMigrationTest 17/17` 全绿且 V18 直连库核验落库。
- 关键建议（原）：V18 未应用根因是测试库 Flyway 链在 V2 中断、66 个全量失败均为 `table doesn't exist` / ErrorCodeTest / UserServiceTest NPE，均与 RBAC 无关。**已采纳**：排障手查明真因为 `TestDatabaseInitializer` 的 `baselineOnMigrate(true)` 未设 `baselineVersion`（默认=1 跳过 V1 建表脚本），非"V1 是空基线"；修复后 V1–V18 完整执行。

---

## 2. 综合审查发现（去重合并后按严重度排序）

| # | 严重度 | 类别 | 位置 | 问题描述 | 建议 | 来源成员 |
|---|--------|------|------|---------|------|---------|
| 1 | 🟢 | 数据/迁移（已修复） | `TestDatabaseInitializer` | **原**：测试库 Flyway 链 V2 断裂、`sys_permission` 等核心表从未创建、V3–V18 全未执行。**真因**：`baselineOnMigrate(true)` 未设 `baselineVersion`（默认=1）导致 V1 建表脚本被跳过，V2 对不存在的表 ALTER 失败。**已修复**（`b7ea790`）：补 `.baselineVersion("0")` 使 V1+ 全执行，V18 种子经直连库核验落库 | 已闭环，无需后续动作 | 排障手 |
| 2 | 🟢 | 测试/CI（环境性） | 全量 `mvn test` | 每测试类重建 Spring 上下文 + 大量 `@MockitoBean` 致 OOM 静默崩溃（首次跑无 BUILD 标记） | CI 加 `-Xmx4g` 或收敛 `@MockitoBean` 复用上下文 | 质量门神 |
| 3 | 🟢 | 质量（已修复） | `TestDatabaseInitializer` | `flyway.migrate()` 异常被 try/catch 吞掉，迁移故障无法在 CI 暴露。**已修复**（`b7ea790`）：改为 fail-fast 抛 RuntimeException | 已闭环 | 排障手 |
| 4 | 🟢 | 安全（后续工单） | 零注解 `/app` 控制器 | Profile/SecurityProfile/AuthExternalAccount/OAuthBind/Dashboard/Integration×3/OrgUnit/Employee 仍零授权注解 | 翻转 default-deny 前须清零（后续工单） | 安全卫士 |

> RBAC 本次改动：**无 🔴/🟠 级发现**，针对性测试全绿、全量重跑中 RBAC 相关用例 0 失败。

---

## 交付清单（代码变更 + 测试覆盖 + 发布检查 + 回滚预案）

**代码变更（commit `165c5fc`，11 files / +952 −10）**
- `auth/aspect/PermissionAspect.java`：四态决策 `decidePolicy()` + 门控 `auth.default-deny.enabled`（默认 false）+ 路径收敛 `isAppPath()`→`/api/app/**` + 强制 `StpUtil.hasPermission`。
- `auth/annotation/PermitAll.java`（新增）：METHOD 级逃生舱，必填 `reason()`。
- `system/user|permission|menu/controller/*Controller.java`：补齐 `@RequiresPermission`（User 8 / Permission 5 / Menu 5 + 1 `@PermitAll`）。
- `db/migration/V18__seed_user_permission_menu_authz.sql`（新增）：sys_permission 21-29 全绑 ADMIN(role_id=1)，幂等 INSERT IGNORE。

**测试覆盖**
- 新增：UserControllerAuthzTest(15) / PermissionControllerAuthzTest(11) / MenuControllerAuthzTest(13) / PermissionAspectDecisionTest(9) / ArchUnit per-controller 守卫(3 条) + 全局 @Disabled 守卫。
- 验证预期：admin→`$.code 200`；无权限用户→`$.code 4001`；`findUserMenuTree` 两者均 200；登录 `/public` 端点放行。

**发布检查清单**
- [x] 门控 `auth.default-deny.enabled=false`（默认关闭，维持现状放行）
- [x] 三控制器注解铺开 + V18 SQL 写就绑 ADMIN
- [x] RBAC 相关测试全绿、无回归
- [ ] **翻转前必做**：目标库手动应用 V18（sys_permission 21-29 + ADMIN 绑定），参照本项目 V8–V14 手动迁移惯例
- [ ] **翻转前必做**：清零其余零注解 `/app` 控制器

**回滚预案**
- 代码回滚：`git revert 165c5fc`（仅影响授权切面与注解，不影响数据）。
- 数据回滚：V18 为幂等 INSERT IGNORE，如需撤销可 `DELETE FROM sys_role_permission WHERE role_id=1 AND permission_id BETWEEN 21 AND 29; DELETE FROM sys_permission WHERE id BETWEEN 21 AND 29;`（仅当已应用时）。
- 门控回滚：将 `auth.default-deny.enabled` 置回 `false` 即恢复全放行。

---

## ✅ 行动清单

| # | 行动 | 负责方 | 紧急度 | 期望完成 |
|---|------|--------|--------|---------|
| 1 | ~~修复测试库 Flyway 迁移链 V2 断裂~~ **✅ 已完成**（commit `b7ea790`：补 `.baselineVersion("0")` + fail-fast，V18 直连库核验落库） | 排障手 | ✅ 完成 | — |
| 2 | 翻转 `auth.default-deny.enabled=true` 前，确认**目标（生产/预发）库**已应用 V18（sys_permission 21-29 + ADMIN 绑定）；测试库已自动落库，生产库遵循手动迁移惯例 | 主理人 / 运维 | P0（翻转前） | 翻转前 |
| 3 | CI 全量 `mvn test` 加 `-Xmx4g` 或收敛 `@MockitoBean` 复用上下文，避免 OOM 误判 | 质量门神 / CI | P2 | 下个迭代 |
| 4 | ~~`TestDatabaseInitializer.flyway.migrate()` 失败时抛错~~ **✅ 已完成**（随 `b7ea790` fail-fast 改造一并落地） | 排障手 | ✅ 完成 | — |
| 5 | 翻转前清零其余零注解 `/app` 控制器（Profile/SecurityProfile/AuthExternalAccount/OAuthBind/Dashboard/Integration×3/OrgUnit/Employee） | 后续工单 | P1（翻转前） | 翻转前 |

---

## ⚠️ 待完善 / 已知局限

- **V18 数据已实测落库（测试库）**：原测试库 Flyway 链 V2 断裂已于 `b7ea790` 修复——根因是 `TestDatabaseInitializer` 的 `baselineOnMigrate(true)` 未设 `baselineVersion`（默认=1 跳过 V1 建表脚本），非"V1 是空基线"。修复后 V1–V18 完整执行，直连 `admin_scaffold_test` 核验 `sys_permission` 21–29 与 `sys_role_permission(role_id=1)` 绑定均就位。**生产/预发库**仍须按手动迁移惯例落 V18（翻转前必做，行动项 #2）。
- **全量测试 66 失败均为既有缺陷**（V2 断裂→`table doesn't exist`、ErrorCodeTest 类别格式、UserServiceTest NPE），非 RBAC 回归；其中 V2 断裂与"迁移异常被吞"已随 `b7ea790` 修复。
- **仍有零注解 `/app` 控制器**：翻转默认拒绝前须清零（行动项 #5）。
- `auth.default-deny.enabled` 当前保持 `false`：即本次交付**不改变任何运行时放行行为**，仅新增"可选择性开启的 fail-closed 能力"。

---

## 📚 成员产出索引

- 产品官（gstack-product-reviewer）原始产出：四态+门控+逃生舱实现计划（已落地为代码，见 commit `165c5fc`）
- 安全卫士（gstack-security-officer）原始产出：`deliverables/gstack/security-audit-default-deny-2026-07-17.md`（含 §9 修订节）
- 质量门神（gstack-qa-lead）原始产出：独立验证报告（定向 `backend/qa_targeted.log`、全量重跑 `backend/qa_full2.log`、首次崩溃 `backend/qa_full.log`）；结论 🔴(V18 未应用, 既有) + RBAC 零回归
- 排障手（gstack-investigator）原始产出：V2 断裂根因报告 + 修复（`b7ea790`：`.baselineVersion("0")` + fail-fast）；直连库核验 V18 落库；`FlywayMigrationTest 17/17` 全绿。结论 🟢 修复有效

---

> 本报告由软件工坊 AI 协作生成，关键决策请由工程负责人复核。
