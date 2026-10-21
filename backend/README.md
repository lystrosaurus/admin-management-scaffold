# 后端 — admin-management-scaffold

基于 **Spring Boot 4.0.6（Java 21）** 的模块化单体管理后台 API 服务。职责覆盖认证授权、系统管理（用户/角色/权限/菜单/字典/配置项）、组织、集成身份、审计、文件与运维底座。

---

## 技术栈

| 类别 | 选型 | 版本 |
|---|---|---|
| 框架 | Spring Boot（web / validation / data-redis / test / webmvc-test） | 4.0.6 |
| 持久层 | MyBatis-Plus | 3.5.16 |
| 认证授权 | Sa-Token（JWT、注解式权限 `@RequiresPermission`） | 1.45.0 |
| 分布式协调 | Redisson（Redis 客户端） | 4.4.0 |
| 数据库迁移 | Flyway（自动迁移 + baseline） | 随 Spring Boot |
| 存储 | MySQL 8 + Redis | — |

> 安全红线：仅允许 `spring-security-crypto` 做 BCrypt，**不引入 Spring Security Web 栈**；日志/响应中禁止输出密码、Token、密钥、三方 `access_token`；三方登录必须校验 `state` / `nonce` / `redirect_uri` 白名单。

---

## 分层架构

```text
Controller -> Service -> DAO -> DAO Impl -> Mapper -> Database
```

硬性约束（由 ArchUnit 测试强制校验）：

- Controller 只依赖 Service / DTO / VO / `ApiResponse<T>`，**禁止**依赖 DAO、Mapper、Entity。
- Service **禁止**直接依赖 MyBatis-Plus Mapper。
- 只有 `..dao.impl..`、`..config..`、`..mapper..` 可以依赖 Mapper。
- Entity **禁止**作为 API 响应返回，必须映射为 VO（优先 MapStruct）。
- 功能模块内部固定结构：`controller / service / dao / dao.impl / mapper / entity / dto / vo / mapstruct`。

---

## 模块与包结构

基础包：`io.github.lystrosaurus.admin`

```text
config         Spring Bean、Jackson、Sa-Token、MyBatis-Plus、Web、Redisson、RedisTemplate
common         ApiResponse<T>、ErrorCode、BusinessException、通用常量
web            GlobalExceptionHandler、RequestLogFilter、参数校验辅助
infra          persistence（BaseEntity、Audit）、redis、storage
auth           登录/登出/JWT、当前用户、三方登录、外部账号绑定
system         user / role / permission / menu / dict / configitem
organization   employee / orgunit
integration    source / principal / identitylink
audit          登录日志、操作日志
file           文件上传、文件元数据
ops            健康检查、应用信息
```

---

## 认证与接口路径

| 类别 | 路径前缀 | 认证方式 | 用途 |
|---|---|---|---|
| 公开入口 | `/api/public/**` | 无 | ping、公开配置、OAuth 入口与回调 |
| Open API | `/api/open/**` | `X-API-Token` | 外部回调 / 机器访问 |
| 后台应用 | `/api/app/**` | Sa-Token JWT | 登录后的管理接口 |

常用端点：

- 登录：`POST /api/public/auth/login`
- 登出：`POST /api/public/auth/logout`
- 当前用户：`GET /api/app/profile`
- 健康检查：`GET /api/app/ops/health`（需 `ops:view` 权限）
- 应用信息：`GET /api/app/ops/info`（需 `ops:view` 权限）

路径约定：全小写 + 连字符，不使用驼峰。

---

## 统一响应与错误码

```json
{ "code": 200, "message": "操作成功", "data": {}, "timestamp": 0 }
```

- `code = 200` 成功；其余为错误（**数值型**）。
- 业务异常统一使用 `BusinessException` + `ErrorCode`，由 `GlobalExceptionHandler`（`@RestControllerAdvice`）输出标准格式。
- Sa-Token 异常已映射：`NotLoginException` → `401 / AUTH_401(1001)`；`NotPermissionException` / `NotRoleException` → `403 / PERMISSION_DENIED(4001)`。
- `ErrorCode` 命名采用 `CATEGORY_NUMBER`（如 `AUTH_401`、`USER_INVALID_PASSWORD`），但实际 `code` 字段为数值（如 `1001`、`2003`）。

---

## 数据库与 Flyway

- 表名/字段名小写下划线；主键 `BIGINT`；状态字段 `VARCHAR(32)` 全大写；时间字段 `DATETIME`；字符集 `utf8mb4`。
- 每张业务表默认包含：`created_at`、`created_by`、`updated_at`、`updated_by`、`deleted`（审计字段由 `AuditMetaObjectHandler` 填充，`deleted` 配合 `@TableLogic`）。
- 迁移脚本：`backend/src/main/resources/db/migration/`，命名 `V{version}__{description}.sql`。
- **禁止修改已发布脚本**；修正历史必须新增迁移。本地种子数据需标注 `LOCAL-ONLY`。
- 当前迁移范围：`V1 ~ V14`（含建表、索引、权限/菜单种子、字典/配置项/集成模块表与种子）。

---

## 运行

```bash
# 1. 准备 .env（已被 gitignore，切勿提交真实密钥）
#    DB_URL / DB_USERNAME / DB_PASSWORD
#    REDIS_HOST / REDIS_PORT / REDIS_DATABASE
#    JWT_SECRET（>= 32 字符）

# 2. 启动（默认 local 环境）
mvn spring-boot:run
# 应用上下文路径 /api，默认端口 8080
```

> Windows 环境下若 `mvn` 启动脚本异常，可改用 classpath 直启：
> `java -cp "C:/path/to/maven/boot/plexus-classworlds-x.jar" -Dclassworlds.conf=... -Dmaven.home=... -Dmaven.multiModuleProjectDirectory=<backend 目录> org.codehaus.classworlds.Launcher spring-boot:run`

---

## 测试

| 类型 | 位置 | 重点 |
|---|---|---|
| 单元测试 | `src/test/java/.../service/`、`.../infra/` | 业务规则、异常分支、纯逻辑 |
| MVC 测试 | `src/test/java/.../web/` | 参数校验、响应格式、异常映射 |
| 架构测试 | `src/test/java/.../architecture/` | ArchUnit 分层规则 |
| 集成测试 | `src/test/java/.../integration/` | MySQL、Redis、Flyway、登录链路（`@ActiveProfiles("test")`） |

常用命令：

```bash
mvn test                 # 运行全部测试
mvn spotless:apply      # 提交前格式化
```

---

## 配置

- `application.yml`：全局配置，`spring.profiles.active: local` 为默认激活环境。
- `application-local.yml`：本地数据源 / Redis / Flyway 开关（读取 `.env` 中的环境变量）。
- 提交规范：`.env` 已被 gitignore，生产凭据严禁硬编码。
