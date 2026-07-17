# F-01 修复交付报告 · RoleController 服务端授权（最小切片）

**日期**：2026-07-17
**场景**：安全审计整改（F-01 根因修复）+ 独立 QA 验证
**参与成员**：安全卫士（验证机制 + 修复规格）→ 产品官（按规格落地代码）→ 质量门神（独立验证；因专用 QA 代理临时不可用，由 general-purpose 代理承接 + 主理人复核测试输出）

---

## 📌 TL;DR（执行摘要）
- **整体结论**：🟢 本切片修复完成且验证通过（RoleController 全方法已强制服务端授权）
- 修复内容：为 `RoleController` 7 个方法补齐 `@RequiresPermission`，新增 V17 种子迁移（2 个赋值权限码绑定 ADMIN），新增授权回归测试与作用域 ArchUnit 规则
- 独立验证：定向 133 用例全绿（RoleControllerAuthzTest 9/9、14 个 SaTokenTest 子类 106/0、ArchitectureTest 18/18）；**授权切面确证生效**（user 请求被拒为 HTTP 200 + code 4001，admin 正常 200）
- ⚠️ **范围边界**：本次仅为 F-01 的**最小切片**（只覆盖 `RoleController`）。`UserController`/`PermissionController`/`MenuController` 等其余核心 RBAC 控制器仍无服务端授权，默认拒绝机制亦未实施，需后续工单收口。

---

## 🎯 核心结论卡片

| 项目 | 内容 |
|------|------|
| Go / No-Go（本切片） | 🟢 Go（可合入，待提交审阅） |
| 严重度分布（本切片） | 修复项 5 处 / 验证全绿 / 0 回归 |
| 关键行动项 | 5 条（本切片收尾 2 + 后续 3） |
| 建议负责人 | 后端 + 安全 |

---

## 1. 各成员核心结论

### 🛡️ 安全卫士（验证机制 + 修复规格）
- 核心判断：根因为 `WebMvcConfig` 的 SaInterceptor 仅 `checkLogin()`（只认证不授权），`PermissionAspect` 用 `@Before("@annotation(requiresPermission)")` **仅对带注解的方法触发**，无注解方法直接放行；`RoleController` 7 个方法零注解 → 任意已登录用户可改写 RBAC 模型。
- 关键建议：本切片**不动** `PermissionAspect`/`WebMvcConfig`，仅凭 RoleController 加方法级 `@RequiresPermission` 走既有切面；默认拒绝机制作为后续独立工单（需与全量补注解同批）。纠正早期草稿三处：版本号用 V17（非 V16）、拒绝语义是 HTTP 200+code 4001（非 403）、权限码改为"复用 4 个既有细粒度码 + 新增 2 个赋值码"。

### 🔍 产品官（按规格实现）
- 核心判断：按规格落地 5 处改动，编译与 3 个相关测试类自验证全绿，未触碰 `PermissionAspect`/`WebMvcConfig`，未提交 git。
- 关键建议：实测 `ObjectMapper` 需用 `tools.jackson`（Spring Boot 4 + Sa-Token Jackson 3），ArchUnit 1.2.1 下 `haveOnlyMethodsThat` 不可用，已按 RoleController 全限定名精确匹配重写规则避免误伤同包测试类。

### ✅ 质量门神（独立验证）
- 核心判断：定向验证证明授权真实生效（非"测试存在即有效"），且无回归。14 个 `SaTokenTest` 子类全过，确认 `hasPermission→true` 桩未致任何子类"假放行"；V17 Flyway 迁移实跑成功。
- 关键建议：本切片可安全合入；后续须补 User/Permission/Menu 三个控制器的 `@RequiresPermission` 以完成 F-01 全量收口，并落地默认拒绝机制。

---

## 交付清单（代码变更 + 测试覆盖 + 发布检查 + 回滚预案）

### 代码变更（5 处，均未提交 git）
| 文件 | 改动 |
|---|---|
| `system/role/controller/RoleController.java` | 新增 import + 7 个方法级 `@RequiresPermission`：`create→system:role:create`、`findById/findAll→system:role:view`、`update→system:role:update`、`deleteById→system:role:delete`、`assignPermissions→system:role:assign-permission`、`assignMenus→system:role:assign-menu` |
| `db/migration/V17__seed_role_assign_permissions.sql`（新建） | 幂等 `INSERT IGNORE` 新增 `system:role:assign-permission`(id=19)、`system:role:assign-menu`(id=20) 并绑定 ADMIN(role id=1)；严禁改 V1–V16 |
| `test/SaTokenTest.java` | 新增桩 `stpUtilMock.when(() -> StpUtil.hasPermission(anyString())).thenReturn(true);`（防继承类回归） |
| `system/role/controller/RoleControllerAuthzTest.java`（新建） | 9 用例，admin→200 / user→code 4001（HTTP 200） |
| `arch/ArchitectureTest.java` | 新增 role 作用域规则（RoleController 全部方法须带 `@RequiresPermission`） |

### 测试覆盖（定向验证，本机 MySQL80 + Redis 在线）
- `RoleControllerAuthzTest`：**9/9 通过**；日志确认 user 请求 `POST /app/roles`、`DELETE /app/roles/1`、`POST /app/roles/1/permissions`、`POST /app/roles/1/menus` 均被拒为 `status=200 + code=4001`
- 14 个 `SaTokenTest` 子类（含 `RoleControllerTest`）：**106/0 通过**
- `ArchitectureTest`：**18/18 通过**
- 合计定向 **133 用例全绿**，BUILD SUCCESS

### 发布检查清单
- [x] 编译通过（`compile` + `test-compile`）
- [x] 授权生效（AuthzTest 佐证）
- [x] 无回归（14 子类 + ArchTest）
- [x] V17 Flyway 干净执行
- [ ] **提交 git 供审阅**（当前改动仍在工作区，未提交）
- [ ] 在预发/生产库执行 V17 迁移前做全量备份

### 回滚预案
- **代码回滚**：`git revert` / `git checkout` 本次 5 处改动即可恢复（无逻辑/配置副作用）。
- **数据回滚**：V17 为**增量**（新增 2 权限码 + 2 行角色关联），向后回滚需手工反向：`DELETE FROM sys_role_permission WHERE id IN (19,20); DELETE FROM sys_permission WHERE id IN (19,20); DELETE FROM flyway_schema_history WHERE version='17';`；或恢复升级前数据库备份（推荐）。
- **无需**回滚 Redis/配置（本次未改）。

---

## ✅ 行动清单

| # | 行动 | 负责方 | 紧急度 | 期望完成 |
|---|------|--------|--------|---------|
| 1 | 提交本切片改动（git commit）供 Code Review | 后端 | P0 | 本周 |
| 2 | **F-01 全量收口**：为 `UserController`/`PermissionController`/`MenuController` 补 `@RequiresPermission`（先定 user:manage/permission:manage/menu:manage 码 + 种子授予），并铺开 ArchUnit 作用域规则 | 后端 + 安全 | P0 | 下一工单 |
| 3 | **默认拒绝机制**：`PermissionAspect` 改为 deny-by-default（无注解方法一律拒绝）+ 全量控制器补注解 + 启用全局 ArchUnit，分批上线避免回归 | 后端 + 安全 | P1 | 后续 |
| 4 | 修复预检报告 B3 的 11 例测试腐化使 `mvn test` 全绿 | QA/Dev | P1 | 后续 |
| 5 | 上线前完成其余 P0 阻塞（移除默认账户强改密、登录失败审计+限流、补 deploy/ 与 CI 数据库 service） | 多角色 | P0 | 上线前 |

---

## ⚠️ 待完善 / 已知局限

- **仅 RoleController 切片**：F-01 的实质范围是"全部后台敏感接口系统性缺授权"。`UserController`/`PermissionController`/`MenuController`（及 Employee/OrgUnit/Integration/Dashboard 等）仍零注解，仍为可实操越权面，需按行动 #2/#3 收口。
- **默认拒绝未实施**：当前无注解的 `/app/**` 方法仍"默认放行"，本切片仅因 RoleController 已加注解而受控；真正的纵深防御（deny-by-default）属后续系统性工单。
- **未提交 git**：所有改动在工作区，待人工审阅后提交。
- **未跑全量 `mvn test` 对比基线**：定向验证已覆盖授权生效 + 桩回归 + 架构规则，但完整 559 跑批（基线 11 例腐化）未在本机复跑；合入前建议 CI 跑全量确认无新增失败。

---

## 📚 成员产出索引

- 安全卫士（规格与机制验证）：对话内产出，关键结论已并入上文
- 产品官（实现）：对话内产出，改动文件见"交付清单"
- 质量门神（独立验证）：专用 QA 代理临时不可用，由 general-purpose 代理承接 + 主理人复核 Maven 测试输出，结论为 133 用例全绿、无回归

---

> 本报告由软件工坊 AI 协作生成，关键决策请由工程负责人复核。
