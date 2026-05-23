# Admin Management Scaffold — Agent Guide

> 面向 AI coding agent 的项目速查手册。首次介入请先通读，随后按需查阅。

---

## 项目速览

后台管理系统脚手架，前端 React 19 + TypeScript + Vite + Ant Design 6，后端 Java 21 + Spring Boot 4.0.6（模块化单体）。

| 项 | 值 |
|---|---|
| 仓库结构 | `frontend/`、`backend/`、`deploy/`、`docs/` |
| 后端基础包 | `io.github.lystrosaurus.admin` |
| 前端定位 | 管理后台 UI（Ant Design 6 + CSS variables） |
| 后端定位 | 认证、用户、角色、权限、菜单、组织、员工、审计、文件、运维底座 |

---

## Agent 行为原则

> 源自 Andrej Karpathy 对 LLM 编码问题的观察：模型常在未确认的情况下做错误假设、过度复杂化代码、在无关改动中引入副作用。以下四条原则用于约束这些倾向，优先级高于执行速度。

### 1. 先思后写（Think Before Coding）

**不要假设，不要隐藏困惑，呈现权衡。**

- 不确定时**先提问**，不要猜测
- 存在多种理解时**列出选项**，不要静默选择一种
- 若存在更简单方案，**主动提出**
- 困惑时**停下来**，明确指出不清楚的地方并请求澄清

#### 深度思考工具（强制）

接收任何任务后，**必须先使用 `sequential-thinking` 工具**进行结构化思考，再开始实施。

思考内容应覆盖：
- 任务理解：用户真正想要什么？有无歧义？
- 技术方案：现有代码如何实现？有无相似模式可复用？
- 风险识别：会触动哪些模块？有无边界条件或并发风险？
- 实现步骤：拆分为可验证的小步骤
- 验证方式：如何确认实现正确？

**思考深度按任务复杂度调整**：
- 简单任务（修 typo、单行改动）：1-2 步快速确认理解即可
- 常规任务（新接口、bug 修复）：3-5 步结构化分析
- 复杂任务（跨模块重构、架构决策）：5-10 步深度推演，允许回溯和修正

### 2. 极简优先（Simplicity First）

**最小代码解决问题，不做推测性实现。**

- 不实现超出需求的功能
- 不为一次性代码做抽象
- 不添加未被要求的"灵活性"或"可配置性"
- 不为不可能发生的场景写错误处理
- 200 行能写成 50 行，就重写

> **检验标准**：资深工程师看了会觉得过度设计吗？如果是，简化。
>
> **抽象判定**：重复出现 3 次以上的模式再考虑通用化，禁止过早抽象。

### 3. 精准改动（Surgical Changes）

**只碰必须碰的，只清理自己制造的。**

- 不"改进"与任务无关的相邻代码、注释或格式
- 不重构没有坏掉的代码
- 匹配现有风格，即使你自己做法不同
- 发现无关死代码，**提及即可，不要删除**
- 你的改动产生的无用 import / 变量 / 函数，**必须清理**

> **检验标准**：每一行改动都能追溯到用户的明确请求。

### 4. 目标驱动（Goal-Driven Execution）

**定义成功标准，循环验证直到达成。**

将指令式任务转化为可验证的目标：

| 而不是... | 转化为... |
|---|---|
| "添加校验" | "先写无效输入的测试，再让它们通过" |
| "修复 bug" | "先写复现 bug 的测试，再让它通过" |
| "重构 X" | "确保重构前后测试都通过" |

多步骤任务应给出简要计划：

```
1. [步骤] → 验证：[检查方式]
2. [步骤] → 验证：[检查方式]
```

**对本项目的具体化**：
- 后端变更必须能通过 `mvn test`（如具备本地依赖环境）。
- 前端变更必须能通过 `npm run test`（Vitest）/ 对应 e2e（Playwright）。
- 后端提交前执行格式化；前端保持现有 lint / format 流程。

**失败熔断**：同一问题连续失败 3 次后，必须暂停并重新评估策略，而非盲目重试。

---

## 仓库结构与后端模块

```text
admin-scaffold/
  frontend/    React + Ant Design 后台
  backend/     Spring Boot 4 API 服务
  deploy/      docker-compose、nginx、环境模板
  docs/        设计文档、数据库说明、权限码规范
```

后端模块（建议）：

```text
io.github.lystrosaurus.admin
  config                 Spring Bean、Jackson、Sa-Token、MyBatis-Plus、Web、Redisson
  common                 ApiResponse<T>、ErrorCode、BusinessException、通用常量
  web                    GlobalExceptionHandler、RequestLogFilter、参数校验辅助
  infra                  persistence（BaseEntity、Audit）、redis、storage
  auth                   登录、登出、JWT、当前用户、三方登录、外部账号绑定
  system                 user / role / permission / menu / dict / configitem
  organization           employee / orgunit
  integration            source / principal / identitylink
  audit                  登录日志、操作日志
  file                   文件上传、文件元数据
  ops                    健康检查、内部诊断
```

功能模块内部固定结构：

```text
controller
service
dao
  impl
mapper
entity
dto
vo
mapstruct
```

---

## 架构规则

```text
Controller -> Service -> DAO -> DAO Impl -> Mapper -> Database
```

- Controller 只依赖 Service、DTO、VO、`ApiResponse<T>`，禁止依赖 DAO、Mapper、Entity。
- Service 禁止直接依赖 MyBatis-Plus Mapper。
- DAO Impl 是业务层唯一可以依赖 Mapper 的位置。
- Entity 禁止作为 API 响应返回；必须映射为 VO（MapStruct 优先）。
- DTO/VO 默认 record；Entity 使用 class。

ArchUnit 必须验证：

```text
..controller.. 不依赖 ..dao..、..mapper..、..entity..
..service.. 不依赖 ..mapper..
只有 ..dao.impl..、..config..、..mapper.. 可以依赖 ..mapper..
Controller 不返回 Entity
```

---

## 认证与接口路径

| 类别 | 路径前缀 | 认证方式 | 用途 |
|---|---|---|---|
| 公开入口 | `/api/public/**` | 无 | ping、公开配置、OAuth 入口与回调 |
| Open API | `/api/open/**` | `X-API-Token` | 外部回调 / 机器访问 |
| 后台应用 | `/api/app/**` | Sa-Token JWT | 登录后的管理接口 |

补充：
- 登录：`POST /api/auth/login`
- 登出：`POST /api/auth/logout`
- 当前用户资料：`GET /api/app/profile`
- 路径小写 + 连字符，不使用驼峰。

---

## API 与错误码

统一响应：

```json
{
  "code": "0",
  "message": "success",
  "data": {}
}
```

- `code = "0"` 表示成功。
- `code != "0"` 表示业务或系统错误。
- 业务异常统一使用 `BusinessException` + `ErrorCode`，全局异常处理器输出标准格式。

错误码格式：`CATEGORY_NUMBER`

常见示例：
`COMMON_400`、`COMMON_401`、`COMMON_403`、`COMMON_404`、`COMMON_409`、`COMMON_429`、`COMMON_500`、`AUTH_401`、`AUTH_409`、`PERMISSION_403`。

---

## 数据库与 Flyway

- 表名/字段名小写下划线；主键 `BIGINT`；状态字段 `VARCHAR(32)` 全大写；时间字段 `DATETIME`。
- 字符集 `utf8mb4`，排序 `utf8mb4_0900_ai_ci`。
- 每张业务表默认包含：`created_at`、`created_by`、`updated_at`、`updated_by`、`deleted`。
- 审计字段由 `AuditMetaObjectHandler` 填充；`deleted` 配合 `@TableLogic`。

Flyway：
- 脚本位置：`backend/src/main/resources/db/migration/`
- 命名：`V{version}__{description}.sql`
- **禁止修改已发布脚本**；修正历史必须新增迁移。
- 本地种子数据必须标注 `LOCAL-ONLY`。

---

## 测试策略（后端）

| 类型 | 位置 | 重点 |
|---|---|---|
| 单元测试 | `src/test/java/.../service/`、`.../infra/` | 业务规则、异常分支、纯逻辑 |
| MVC 测试 | `src/test/java/.../web/` | 参数校验、响应格式、异常映射 |
| 架构测试 | `src/test/java/.../architecture/` | ArchUnit 分层规则 |
| 集成测试 | `src/test/java/.../integration/` | MySQL、Redis、Flyway、登录链路 |

第一版集成测试：`@ActiveProfiles("test")` + 本地 MySQL/Redis，不强制 Testcontainers。

---

## 常用命令

```bash
# 后端
cd backend
mvn test
mvn spotless:apply

# 前端
cd frontend
npm install
npm run dev
npm run test
```

---

## 安全红线

1. 禁止引入 Spring Security Web 栈（仅允许使用 `spring-security-crypto` 做 BCrypt）。
2. 禁止在日志、响应中输出密码、Token、密钥、三方 access_token。
3. 禁止硬编码生产环境凭据。
4. 三方登录必须校验 `state`、`nonce`、`redirect_uri` 白名单。
5. 绑定/解绑、登录失败必须写审计日志；多次失败要限流。

---

## Agent 行动清单

1. **任何任务先使用 sequential-thinking** — 先思考再动手，复杂度决定思考深度。
2. **新增模块先镜像标准结构**（controller/service/dao/dao.impl/mapper/entity/dto/vo/mapstruct）。
3. **DTO/VO 用 record，Entity 用 class**；Entity 禁止作为 API 响应。
4. **Service 禁止绕过 DAO 调用 Mapper**。
5. **后端变更先跑测试**；具备格式化流程时，提交前先格式化。
6. **前端变更先跑对应测试/构建**；涉及 UI 改动尽量附上验证手段（截图/测试）。
7. **涉及权限、菜单、角色变更**：检查缓存失效与 tokenVersion 策略是否满足设计。
8. **涉及数据库变更**：优先 Flyway 迁移，不手动改已发布脚本。
