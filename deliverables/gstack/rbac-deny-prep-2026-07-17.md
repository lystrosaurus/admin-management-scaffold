# RBAC 默认拒绝前置交付报告 (default-deny prep)

**日期**：2026-07-17
**场景**：安全审计 + 代码质量 + 调试复盘（default-deny 翻转前置准备）
**参与成员**：产品官（gstack-product-reviewer）+ 安全官（gstack-security-officer）+ 排障手（gstack-investigator）

---

## 📌 TL;DR（执行摘要）
- 整体结论：🟢 通过（前置准备完成，70 项授权回归测试全绿）
- 阻塞项数量：0（对本次前置任务本身）；`auth.default-deny.enabled=true` 翻转仍为独立门控决策，未自动执行
- Go/No-Go（前置准备）：🟢 Go
- 下一步：等待用户显式签署 default-deny 翻转；两个独立 follow-up（P1 IDOR / P2 测试硬化）待排期

---

## 🎯 核心结论卡片

| 项目 | 内容 |
|------|------|
| Go / No-Go（前置准备） | 🟢 Go |
| default-deny 翻转 | 🔴 门控未签署（需用户显式确认） |
| 严重度分布 | 🔴 0 / 🟠 1 (P1 IDOR) / 🟡 1 (P2 测试) / 🟢 多项 |
| 关键行动项 | 4 条（1 已完成） |
| 建议负责人 | 主理人编排；IDOR/测试硬化交安全官+排障手；翻转交用户签署 |

---

## 1. 各成员核心结论

### 🔍 产品官（产品评审）
- 核心判断：ADMIN（role_id=1）已通过 V14 绑定 `integration:*:view`（16-18），V19 补齐的 30-44 码全部绑定 ADMIN；翻转 default-deny 后 ADMIN 不会被锁死，授权模型自洽。
- 关键建议：拒绝将 V19 改为 11 码粗粒度方案（`unbind→manage`、`org` 粗粒度）——与已签名的安全结论冲突且无安全收益，保持 15 码细粒度。

### 🛡️ 安全官（OWASP+STRIDE 审计）
- 核心判断：OAuthBind `@PermitAll` 违例已整改为 `@RequiresPermission(bind/unbind)` 且强制自 scope（`UserContext.getCurrentUserId()`）；现存 5 个 `@PermitAll` 全部自 scope（Profile/ChangePassword/Permissions/SecurityProfile/Menu 树），无越权面；命名冲突（`org:orgunit→organization:org-unit` 等）已字节级对齐 V19/控制器/测试三方。
- 关键建议：① P1 修复 `AuthExternalAccountController`/`ExternalAccountService` 的 IDOR（service 层缺归属校验，对照 `OAuthService.unbindAccount` L146）；② P2 硬化 `AuthzCoverageTest`（L137-138 stale 注释 + `isForbiddenPermitAll` 未覆盖 `OAuthBindController`）。

### 🔧 排障手（调试与根因）
- 核心判断：主动撤回早前"OAuthBind 仍为 @PermitAll"的争议——初读基于过期快照，重读确认当前磁盘为 `@RequiresPermission`，安全官结论正确；交叉验证全部 5 点一致。
- 关键建议：真实遗留安全工作是 A（IDOR service 层）与 B（测试守门硬化），二者独立于注解翻转，不应阻塞前置交付。

---

## 2. 综合审查发现（去重合并，按严重度排序）

| # | 严重度 | 类别 | 位置 | 问题描述 | 建议 | 来源成员 |
|---|--------|------|------|---------|------|---------|
| 1 | 🟠 P1 | 越权 (IDOR/BOLA) | ExternalAccountService.bind/unbind/getById | bind 用 `dto.userId()` 无归属校验；unbind/getById 无 ownership 检查 | service 层注入 `UserContext.getCurrentUserId()` 并校验 `account.getUserId().equals(userId)`，镜像 `OAuthService` L146 | 安全官 |
| 2 | 🟡 P2 | 测试守门缺口 | AuthzCoverageTest L137-138 / L136-143 | 注释 stale 称 OAuthBind 保持 @PermitAll；`isForbiddenPermitAll` 仅守 AuthExternalAccountController，未守 OAuthBindController | 修正注释 + 扩展守门到 OAuthBindController.bind/unbind | 安全官/排障手 |
| 3 | 🟢 | 命名一致性 | V19 SQL + 10 控制器 + 8 测试 | `auth:`/`organization:`/`system:dashboard:` 字节级对齐 | 已落地并 commit `adc8a63` | 三方共识 |
| 4 | 🟢 | 授权注解覆盖 | 10 个 /app 控制器 | 零注解控制器已补齐 `@RequiresPermission` / `@PermitAll`(自 scope) | 已 commit `bd39816`，70 测试全绿 | 三方共识 |
| 5 | 🔴(门控) | 默认拒绝翻转 | application.yml `auth.default-deny.enabled` | 仍为 false，未翻转（独立决策） | 需用户显式签署 + 翻转前断言 V19 已应用运行库 + ADMIN 冒烟 | 主理人 |

---

## ✅ 行动清单（至少 3 条具体可执行项）

| # | 行动 | 负责方 | 紧急度 | 期望完成 |
|---|------|--------|--------|---------|
| 1 | 提交 V19 权限码重命名（对齐控制器注解） | 主理人 | 已完成 (adc8a63) | 2026-07-17 |
| 2 | P1 IDOR service 层归属校验修复 | 安全官 + 排障手 | P1 | 下个迭代 |
| 3 | P2 AuthzCoverageTest 硬化（注释 + 守门扩展） | 排障手 | P2 | 下个迭代 |
| 4 | 翻转 `auth.default-deny.enabled=true`（门控） | 用户签署 | 阻塞待签 | 签署后执行 |

---

## ⚠️ 待完善 / 已知局限

- default-deny 翻转尚未执行，属独立门控决策，本次不自动翻转。
- 翻转前必须断言：① 运行库已应用 V19（`sys_permission`=44、ADMIN 绑定=44、`flyway_schema_history` 含 V19）；② ADMIN 端到端冒烟（登录→访问各 /app 端点 200）。
- OAuth 三方绑定仍处"暂停"状态（更早决策），与本次 RBAC 授权无关。
- 本环境 Flyway 不自动迁移，V15–V19 均手动应用运行库；CI（seed/集成）需注意此差异。

---

## 📚 成员产出索引

- gstack-product-reviewer（产品官）：V19 码清单定稿 + ADMIN 不会被锁确认 + 拒绝 11 码 churn 提案
- gstack-security-officer（安全官）：最终审计（OAuthBind 合规、5 @PermitAll 自 scope、命名对齐、IDOR P1、测试缺口 P2）
- gstack-investigator（排障手）：撤回争议 + 交叉验证 5 点一致 + 推荐 A/B follow-up

---

> 本报告由软件工坊 AI 协作生成，关键决策请由工程负责人复核。
