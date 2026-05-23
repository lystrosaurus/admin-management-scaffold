# 后台管理系统脚手架详细设计文档

版本：v1.0

日期：2026-05-23

## 1. 技术基线

| 层 | 方案 |
| --- | --- |
| 前端 | React 19 + TypeScript + Vite + Ant Design 6 |
| 后端 | Java 21 + Spring Boot 4.0.6 |
| ORM | MyBatis-Plus 3.5.16 + mybatis-spring 3.0.5，采用显式 MyBatis-Plus 配置 |
| 安全 | Sa-Token 1.45.0 + Sa-Token JWT；BCrypt 仅使用 `spring-security-crypto` |
| 数据库 | MySQL 8.4 LTS |
| 缓存 | Redis，用于权限缓存、token 版本、登录状态辅助校验 |
| 迁移 | Flyway |
| 测试 | Vitest / Playwright / JUnit 5 / Mockito / Spring MVC Test / ArchUnit；后端集成测试先使用 `test` profile + 本地 MySQL/Redis |
| 部署 | Nginx + Spring Boot Jar + MySQL + Redis，Docker Compose 起步 |

技术基线说明：

- Spring Boot 4 使用 Java 17+，项目默认选择 Java 21。
- MyBatis-Plus 按 Atlas Mountain 验证路径采用显式 `MybatisPlusConfig`，避免依赖尚未充分验证的 Boot 4 starter 自动装配。
- Sa-Token 使用已验证 starter 或手动配置方式接入 Spring Boot 4，并启用 Sa-Token JWT。
- Redisson 作为 Redis 客户端和分布式锁基础设施，后续权限缓存、登录态辅助校验和限流能力都基于它扩展。
- MapStruct 用于 Entity 到 VO 的显式映射，避免 Controller 返回 Entity。
- Spotless 作为 Java 格式化入口，ArchUnit 作为分层约束入口。
- Ant Design 6 默认使用 CSS variables，适合做可主题化后台系统。

## 2. 系统边界

本脚手架解决后台系统的通用底座问题：

- 登录认证
- 用户管理
- 角色管理
- 权限管理
- 菜单管理
- 部门/组织管理
- 员工主数据
- 外部身份映射
- 三方登录
- 操作日志
- 登录日志
- 数据权限
- 文件上传
- 基础配置

第一版不纳入：

- 微服务拆分
- SaaS 多租户
- 工作流引擎
- 低代码页面设计器
- Swagger / Knife4j 等内置接口文档平台
- 复杂 IAM / SSO 中台

## 3. 总体架构

仓库建议结构：

```text
admin-scaffold/
  frontend/    React + Ant Design 后台
  backend/     Spring Boot 4 API 服务
  deploy/      docker-compose、nginx、环境模板
  docs/        设计文档、数据库说明、权限码规范
```

后端采用模块化单体。早期不拆微服务，避免认证传播、网关、分布式事务和部署复杂度提前进入项目。

后端基础包名固定为：

```text
io.github.lystrosaurus.admin
```

## 4. 后端模块划分

```text
io.github.lystrosaurus.admin
  AdminApplication
  config                 Spring Bean、Jackson、Sa-Token、MyBatis-Plus、Web、Redisson 配置
  common                 跨模块通用类型
    response             ApiResponse<T>
    exception            ErrorCode、CommonErrorCode、BusinessException
    support              常量、小型工具、分页对象
  web                    HTTP 运行时能力
    exception            GlobalExceptionHandler
    log                  RequestLogFilter
    validation           参数校验辅助
  infra                  基础设施适配
    persistence          BaseEntity、AuditMetaObjectHandler、分页、数据权限拦截支撑
    redis                Redisson、缓存、分布式锁、tokenVersion/sessionId 校验支撑
    storage              文件存储适配
  auth                   登录、登出、JWT、当前用户上下文、三方登录、外部账号绑定
  system                 后台系统管理域
    user                 用户账号
    role                 角色
    permission           权限
    menu                 菜单
    dict                 字典
    configitem           基础配置项
  organization           组织与员工域
    employee             员工主数据
    orgunit              组织架构
  integration            外部身份源、身份映射、同步任务
    source               外部系统实例
    principal            外部主体与 ID 映射
    identitylink         身份匹配候选与人工处理
  audit                  登录日志、操作日志
  file                   文件上传、文件元数据
  ops                    运维、健康检查、内部诊断入口
```

每个可独立演进的业务功能包优先采用 Atlas Mountain 的固定内部结构：

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

包结构约束：

- 包名全部小写，不使用下划线。
- 按业务域组织包，不按技术层在根目录横向铺开。
- `config` 只做 Spring 组件装配和第三方框架配置，不写业务规则。
- `common` 只放跨模块基础类型，不放具体业务流程。
- `web` 只放 HTTP 运行时能力，如异常处理、请求日志、参数校验扩展。
- `infra` 放基础设施 Adapter，例如 Redis、持久化、文件存储、限流、分布式锁。

分层约束：

```text
Controller -> Service -> DAO -> DAO Impl -> Mapper -> Database
```

- Controller 只处理 HTTP 入参、出参、权限注解和响应包装，只依赖 Service、DTO、VO、`ApiResponse<T>`。
- Controller 禁止依赖 DAO、Mapper、Entity。
- Service 承担业务规则、事务边界和跨 DAO 编排，只依赖 DAO、其他 Service、MapStruct Mapper、common 类型和 infra 服务。
- Service 禁止直接依赖 MyBatis-Plus Mapper。
- Service 默认使用具体类，不为单实现 Service 创建接口；只有存在多实现策略、外部 Adapter 或测试替身价值时才引入接口。
- DAO 接口定义 Service 需要的数据访问语义。
- DAO Impl 是业务层唯一可以依赖 MyBatis-Plus Mapper 的位置。
- Mapper 是 MyBatis-Plus 持久化 Adapter，可配合 XML 承载复杂 SQL。
- Entity 只表达数据库表结构，禁止作为 API 响应返回。
- DTO 表达入参或命令，VO 表达响应；DTO/VO 默认使用 Java `record`。
- Entity 到 VO 的转换优先使用 MapStruct，避免在 Controller 中手工拼装数据库对象。

ArchUnit 必须验证关键规则：

```text
..controller.. 不依赖 ..dao..、..mapper..、..entity..
..service.. 不依赖 ..mapper..
只有 ..dao.impl..、..config..、..mapper.. 可以依赖 ..mapper..
Controller 不返回 Entity
```

### 4.1 统一 API 与异常规范

所有 API 响应统一使用 `ApiResponse<T>`：

```json
{
  "code": "0",
  "message": "success",
  "data": {}
}
```

约束：

- `code = "0"` 表示成功。
- `code != "0"` 表示业务错误或系统错误。
- Jackson 默认不输出 `null` 字段。
- 业务异常统一抛出 `BusinessException`，携带 `ErrorCode`。
- 全局异常由 `web.exception.GlobalExceptionHandler` 转换成 HTTP 状态码和 `ApiResponse<Void>`。

错误码格式：

```text
CATEGORY_NUMBER
```

示例：

```text
COMMON_400
COMMON_401
COMMON_403
COMMON_404
COMMON_409
COMMON_429
COMMON_500
AUTH_401
AUTH_409
PERMISSION_403
```

HTTP 状态映射：

| 场景 | HTTP Status | code |
| --- | --- | --- |
| 成功 | 200 | `0` |
| 参数校验失败 | 400 | `COMMON_400` |
| 未认证 | 401 | `COMMON_401` |
| 无权限 | 403 | `COMMON_403` |
| 资源不存在 | 404 | `COMMON_404` |
| 资源冲突 | 409 | `COMMON_409` |
| 请求过于频繁 | 429 | `COMMON_429` |
| 系统内部错误 | 500 | `COMMON_500` |

### 4.2 后端接口路径规范

接口路径分三类：

| 类别 | 路径前缀 | 认证方式 | 用途 |
| --- | --- | --- | --- |
| 公开入口 | `/api/public/**` | 无 | ping、公开配置、OAuth 授权入口和回调 |
| Open API | `/api/open/**` | `X-API-Token` | 后续外部系统回调或机器访问 |
| 后台应用 | `/api/app/**` | Sa-Token JWT | 登录后的后台管理接口 |

补充约束：

- 登录接口保留为 `POST /api/auth/login`，登出接口为 `POST /api/auth/logout`。
- 当前用户资料使用 `GET /api/app/profile`。
- 绑定、解绑三方账号属于登录后操作，使用 `/api/app/auth/external-accounts/**`。
- 路径使用小写和连字符，不使用驼峰。

### 4.3 数据库、Entity 与 Flyway 规范

数据库命名：

- 表名、字段名使用小写下划线。
- 主键统一为 `BIGINT`，不使用数据库自增。
- 状态字段统一为 `VARCHAR(32)`，取值使用全大写枚举文本。
- 时间字段使用 `DATETIME`。
- 索引命名使用 `idx_表名_字段名` 或 `uk_表名_字段名`。
- 字符集使用 `utf8mb4`，排序规则使用 `utf8mb4_0900_ai_ci`。

每张业务表默认包含：

```text
created_at
created_by
updated_at
updated_by
deleted
```

实现约束：

- 公共审计字段沉入 `infra.persistence.BaseEntity`。
- `created_at`、`created_by`、`updated_at`、`updated_by` 由 `AuditMetaObjectHandler` 填充。
- `deleted` 使用 MyBatis-Plus `@TableLogic`。
- Entity 使用 class，可有限使用 Lombok `@Getter`、`@Setter`。
- DTO/VO 禁止使用 Lombok `@Data`，默认使用 record。

Flyway 规范：

- 脚本位置：`backend/src/main/resources/db/migration/`。
- 命名：`V{version}__{description}.sql`，例如 `V1__init_schema.sql`。
- 描述使用小写下划线。
- 已发布迁移脚本禁止修改，修正历史问题必须新增迁移。
- 本地种子数据必须标注 `LOCAL-ONLY`，生产环境不得依赖本地种子账户。

### 4.4 后端构建、格式化与测试规范

构建约束：

- Maven 单体工程起步，不拆多 Maven module。
- Java 格式化由 Spotless 执行，缩进 2 空格，最大行宽 120。
- 构造器注入优先；禁止字段 `@Autowired`。
- 日志使用 SLF4J，禁止 `System.out.println` 和 `e.printStackTrace()`。

测试分层：

| 类型 | 位置 | 重点 |
| --- | --- | --- |
| 单元测试 | `src/test/java/.../service/`、`.../infra/` | 业务规则、异常分支、纯逻辑 |
| MVC 测试 | `src/test/java/.../web/` 或 controller 对应包 | 参数校验、响应格式、异常映射 |
| 架构测试 | `src/test/java/.../architecture/` | ArchUnit 分层规则 |
| 集成测试 | `src/test/java/.../integration/` | MySQL、Redis、Flyway、登录链路 |

第一版集成测试按 Atlas Mountain 方式使用 `@ActiveProfiles("test")` 和独立测试库；Testcontainers 作为后续可选增强，不作为 M1 阻塞项。

## 5. 核心领域模型

账号、员工、组织、外部身份必须分离：

```text
sys_user       后台登录账号
hr_employee    内部统一员工主数据
org_unit       内部统一组织架构
ext_source     外部系统实例：北森、飞书、企业微信
ext_principal  外部系统中的人或部门
```

设计原则：

- `sys_user` 可以不绑定员工。
- 一个 `sys_user` 最多绑定一个 `hr_employee`。
- 一个 `hr_employee` 可以关联多个外部系统身份。
- 外部系统 ID 不进入 `sys_user` 主表。
- 数据权限基于内部 `org_unit`，不直接依赖飞书、企微、北森部门。

## 6. 用户表设计

`sys_user` 核心字段：

```text
id
username
password_hash
nickname
avatar_file_id
phone
email
employee_id
status
token_version
last_login_at
last_login_ip
created_at
created_by
updated_at
updated_by
deleted
```

`status` 枚举：

```text
ENABLED
DISABLED
LOCKED
```

约束：

- `username` 唯一。
- `employee_id` 唯一但允许 `NULL`。
- 使用 `deleted` 做逻辑删除。

`employee_id` 可空，用于系统管理员、外包账号、服务账号等非员工账号。

## 7. 员工与组织设计

`hr_employee`：

```text
id
employee_no
name
preferred_name
mobile
email
primary_org_id
job_title
employment_status
entry_date
leave_date
source_type
created_at
updated_at
deleted
```

`employment_status` 枚举：

```text
ACTIVE
RESIGNED
SUSPENDED
```

`org_unit`：

```text
id
parent_id
code
name
full_path
level
manager_employee_id
sort_order
status
source_type
created_at
updated_at
deleted
```

`employee_org` 支持多部门：

```text
id
employee_id
org_id
is_primary
position_name
start_date
end_date
status
```

## 8. 外部身份映射设计

`ext_source`：

```text
id
code
name
source_type
tenant_key
status
priority
config_json
created_at
updated_at
```

`code` 示例：

```text
BEISEN
LARK
WECOM
```

`ext_principal`：

```text
id
source_id
principal_type
external_key
display_name
status
raw_payload_json
last_sync_at
canonical_type
canonical_id
link_status
```

`principal_type` 枚举：

```text
USER
ORG
```

`canonical_type` 枚举：

```text
EMPLOYEE
ORG_UNIT
```

`link_status` 枚举：

```text
UNLINKED
AUTO_LINKED
MANUAL_LINKED
CONFLICT
```

`ext_principal_identifier`：

```text
id
principal_id
id_type
id_value
is_primary
```

`id_type` 示例：

```text
beisen_employee_id
beisen_person_id
lark_open_id
lark_user_id
lark_union_id
wecom_userid
wecom_department_id
wechat_openid
wechat_unionid
```

唯一约束：

```text
unique(source_id, principal_type, external_key)
unique(principal_id, id_type, id_value)
```

该设计支持：

- 同一个员工同时关联北森、飞书、企业微信。
- 同一个飞书员工保存 `open_id`、`user_id`、`union_id`。
- 同一个微信用户保存 `openid` 和 `unionid`。
- 外部系统主键变化时，通过多 ID 映射保留关联能力。

## 9. 身份匹配规则

外部员工同步进来后，按优先级匹配内部员工：

```text
外部 ID 映射精确匹配
北森 employee_id / person_id
工号 employee_no
企业邮箱 email
手机号 mobile
姓名 + 部门路径，进入人工确认
```

匹配结果：

```text
AUTO_LINKED      高置信度自动关联
MANUAL_LINKED    管理员手工确认
CONFLICT         多个候选，等待处理
UNLINKED         未找到候选
```

增加 `identity_link_candidate` 表记录待处理候选：

```text
id
source_principal_id
candidate_type
candidate_id
score
reason
status
created_at
handled_by
handled_at
```

`status` 枚举：

```text
PENDING
CONFIRMED
REJECTED
```

## 10. 权限模型

采用 RBAC + 菜单权限 + 按钮权限 + API 权限分离：

```text
sys_user
sys_role
sys_permission
sys_menu
sys_user_role
sys_role_permission
```

`sys_permission`：

```text
id
code
name
type
module
resource
action
status
sort_order
```

`type` 枚举：

```text
MENU
BUTTON
API
```

权限码规范：

```text
system:user:view
system:user:create
system:user:update
system:user:delete
system:user:export
system:role:assign-permission
system:menu:update
```

`sys_menu`：

```text
id
parent_id
title
route_path
route_name
component_path
icon
permission_code
visible
keep_alive
sort_order
status
```

菜单决定前端导航，权限决定能否访问和操作。二者可以关联，但不合并成一张表。

## 11. Sa-Token JWT 方案

使用 Sa-Token JWT，但不把完整权限列表写进 JWT。

JWT 只放：

```text
userId
loginType
tokenVersion
sessionId
issuedAt
expiresAt
```

鉴权流程：

```text
前端携带 Authorization: Bearer <jwt>
Sa-Token 校验 JWT 签名和有效期
后端校验 tokenVersion 与 sessionId
StpInterface 从 Redis / MySQL 获取角色和权限
@SaCheckPermission 校验接口权限
```

权限变更策略：

```text
用户角色变化：清理该用户权限缓存，tokenVersion +1
角色权限变化：清理拥有该角色的用户缓存，相关用户 tokenVersion +1
用户禁用/删除/改密：tokenVersion +1，并强制失效已有 token
```

## 12. 三方登录设计

系统支持多种登录方式，但所有登录方式最终都必须映射到内部后台账号 `sys_user`。三方平台只负责身份认证，不直接决定系统权限。

支持的登录源：

```text
PASSWORD
LARK
WECOM
WECHAT
```

统一原则：

- 三方登录不自动授予权限。
- 三方身份必须绑定 `sys_user`。
- 最终角色和权限仍由 `sys_user` 决定。
- 飞书、企微可辅助匹配员工。
- 个人微信只作为登录凭证，不作为员工主数据来源。

### 12.1 数据模型补充

新增 `auth_provider`：

```text
id
code
name
client_id
client_secret_encrypted
redirect_uri
scopes
enabled
config_json
created_at
updated_at
```

`code` 枚举：

```text
LARK
WECOM
WECHAT
```

新增 `auth_external_account`：

```text
id
provider_id
provider_user_key
user_id
employee_id
identifier_json
nickname
avatar_url
bind_status
last_login_at
created_at
updated_at
```

`bind_status` 枚举：

```text
BOUND
UNBOUND
CONFLICT
```

唯一约束：

```text
unique(provider_id, provider_user_key)
unique(provider_id, user_id)
```

`auth_external_account` 负责登录绑定关系；`ext_principal / ext_principal_identifier` 继续负责外部身份主数据映射。两者可通过外部 ID 互相定位，但职责不合并。

### 12.2 三方登录流程

```text
1. 前端点击飞书/微信/企微登录
2. 调用 GET /api/public/oauth/{provider}/authorize
3. 后端生成授权地址、state、nonce
4. 前端跳转三方授权页
5. 三方平台回调 GET /api/public/oauth/{provider}/callback
6. 后端校验 state
7. 后端用 code 换取 access_token
8. 后端获取三方用户信息
9. 标准化外部身份 ID
10. 查找 auth_external_account
11. 找到已绑定 sys_user 后签发 Sa-Token JWT
12. 返回前端登录成功
```

未绑定账号时：

```text
已登录用户：允许绑定当前 sys_user
未登录用户：拒绝登录，提示先使用账号密码登录后绑定，或联系管理员绑定
```

不建议三方首次登录自动创建后台账号。若业务确实需要，必须通过配置显式开启：

```text
auth.oauth.auto-create-user=false
auth.oauth.auto-bind-by-employee=true
```

### 12.3 飞书登录

飞书登录优先用于企业内部员工身份识别。

身份 ID 优先级：

```text
lark_user_id
lark_union_id
lark_open_id
```

匹配路径：

```text
飞书用户信息
  -> lark_user_id / union_id / open_id
  -> ext_principal_identifier
  -> hr_employee
  -> sys_user
```

如果匹配到员工但员工未绑定后台账号：

```text
默认拒绝登录
可进入管理员待处理列表
管理员确认后创建或绑定 sys_user
```

### 12.4 企业微信登录

企业微信登录适合企业内部员工登录，优先级高于个人微信。

身份 ID 优先级：

```text
wecom_userid
wecom_open_userid
wecom_unionid
```

匹配路径：

```text
企微用户信息
  -> wecom_userid
  -> ext_principal_identifier
  -> hr_employee
  -> sys_user
```

企业微信组织关系只用于外部映射和辅助匹配，不直接参与系统数据权限计算。数据权限仍然基于内部 `org_unit`。

### 12.5 个人微信登录

个人微信登录只作为一种便捷登录凭证，不作为员工身份来源。

身份 ID 优先级：

```text
wechat_unionid
wechat_openid
```

限制：

```text
不通过微信昵称匹配员工
不通过头像匹配员工
不通过微信 openid 自动创建员工
不自动授予后台权限
```

个人微信账号必须由用户主动绑定，或由管理员绑定到某个 `sys_user` 后才能登录。

### 12.6 绑定与解绑

绑定接口：

```text
POST /api/app/auth/external-accounts/{provider}/bind
```

解绑接口：

```text
DELETE /api/app/auth/external-accounts/{provider}/unbind
```

绑定前要求：

```text
当前用户已登录
校验 state
必要时要求输入密码或二次验证
目标三方账号未被其他 sys_user 绑定
```

解绑限制：

```text
如果用户没有密码，且只剩一个登录方式，不允许解绑
解绑管理员账号的三方登录必须记录操作日志
```

三方登录成功后，统一进入内部登录签发流程：

```text
OAuth user -> sys_user -> StpUtil.login(userId) -> Sa-Token JWT
```

不要把三方平台 access_token、refresh_token、完整用户信息写入 JWT。

### 12.7 三方登录安全要求

必须实现：

```text
state 防 CSRF
nonce 防重放
redirect_uri 白名单校验
client_secret 加密存储
三方 access_token 不下发前端
三方 token 不写日志
绑定/解绑写操作日志
登录失败写登录日志
多次失败做限流
```

回调地址建议：

```text
/api/public/oauth/lark/callback
/api/public/oauth/wecom/callback
/api/public/oauth/wechat/callback
```

前端展示路径：

```text
/login
/account/security
/system/external-account-bindings
```

## 13. 数据权限设计

角色表增加：

```text
data_scope_type:
  ALL
  ORG_TREE
  ORG_ONLY
  SELF
  CUSTOM
```

`sys_role_org` 保存自定义组织范围：

```text
role_id
org_id
```

多个角色合并时取并集：

```text
有 ALL 则全部数据
ORG_TREE / ORG_ONLY / CUSTOM 合并组织 ID 集合
SELF 追加 created_by_user_id = 当前用户
```

业务表建议统一保留：

```text
created_by_user_id
created_by_employee_id
org_id
```

数据权限通过 MyBatis-Plus 拦截器或显式查询片段实现。第一版只对标注了 `@DataScope` 的查询启用，避免误伤系统配置、权限、菜单等基础表。

## 14. 外部系统同步

统一连接器接口：

```text
IdentitySourceConnector
  syncOrganizations(sourceId)
  syncEmployees(sourceId)
  getUser(sourceId, externalId)
```

实现：

```text
BeisenIdentityConnector
LarkIdentityConnector
WecomIdentityConnector
```

同步流程：

```text
拉取外部组织/员工
写入 ext_principal 和 ext_principal_identifier
执行匹配规则
自动关联高置信度身份
冲突进入待处理队列
更新员工、组织、映射关系
清理权限和数据权限缓存
写入同步日志
```

北森作为员工和组织主数据优先来源；飞书和企业微信作为协同身份来源。没有北森时，允许飞书/企微创建 `EXTERNAL_ONLY` 员工，后续再与北森数据合并。

## 15. 前端设计

前端目录：

```text
src/
  app
  layouts
  routes
  shared
  modules/auth
  modules/system/user
  modules/system/role
  modules/system/menu
  modules/organization/employee
  modules/organization/org-unit
  modules/integration/source
  modules/integration/identity-link
```

登录后调用 `/api/app/profile` 获取：

```text
用户信息
角色码
权限码
菜单树
绑定员工信息
主组织信息
```

前端权限只做体验控制：

```text
动态菜单
路由守卫
按钮显隐
```

后端 `@SaCheckPermission` 是最终权限边界。

## 16. 关键页面

权限与账号：

```text
登录页
账号安全页
用户管理
角色管理
权限管理
菜单管理
数据权限配置
三方账号绑定管理
```

员工与组织：

```text
员工管理
组织架构
员工详情
员工外部身份
```

外部集成：

```text
外部身份源配置
身份匹配待处理
同步日志
外部账号绑定查询
绑定冲突处理
```

用户管理页重点展示：

```text
用户名
昵称
绑定员工
主组织
角色
账号状态
最后登录时间
```

员工管理页重点展示：

```text
姓名
工号
主组织
手机号
邮箱
在职状态
绑定后台账号
已关联外部身份：北森 / 飞书 / 企微
```

账号安全页展示：

```text
修改密码
已绑定飞书
已绑定企业微信
已绑定微信
绑定 / 解绑
最近登录记录
```

## 17. 交付阶段

```text
M1：项目骨架、`io.github.lystrosaurus.admin` 基础包、登录、Sa-Token JWT、统一响应、统一异常、MyBatis-Plus、Flyway、Spotless、ArchUnit
M2：用户、角色、权限、菜单、动态路由、按钮权限
M3：员工、组织、用户绑定员工、数据权限
M4：外部身份源、映射表、身份匹配待处理
M5：三方登录、账号绑定、账号安全页
M6：北森/飞书/企微同步适配器、同步日志、缓存失效
M7：测试、部署、操作日志、安全加固
```

## 18. 设计决策

定稿决策：

```text
后端基础包固定为 io.github.lystrosaurus.admin
后端采用 Controller -> Service -> DAO -> DAO Impl -> Mapper 分层
Service 默认不为单实现创建接口
DTO/VO 使用 record，Entity 不作为 API 响应
统一响应为 ApiResponse<T>，统一异常为 BusinessException + ErrorCode
用户和员工分离
权限和菜单分离
JWT 不承载权限全集
外部系统 ID 统一进映射表
内部 org_unit 是唯一数据权限基准
支持用户名密码、飞书、企业微信、个人微信登录
三方登录必须绑定 sys_user 后才能进入后台
第一版不做多租户
第一版不集成 Swagger/Knife4j
```

实施风险：

```text
Sa-Token JWT 无状态能力和权限立即失效天然冲突，需要 tokenVersion + Redis 辅助
外部身份匹配不能完全自动化，必须有人工确认页面
数据权限拦截器不能全局粗暴启用，需要白名单和显式标注
飞书、企微、北森字段差异较大，连接器要隔离原始数据模型
个人微信不代表企业员工，不能作为员工主数据来源
MyBatis-Plus 与 Sa-Token 在 Spring Boot 4 上需优先采用已验证依赖组合，避免未验证 starter 自动装配带来的启动风险
```

## 19. 参考链接

- Spring Boot System Requirements: https://docs.spring.io/spring-boot/system-requirements.html
- MyBatis-Plus Getting Started: https://baomidou.com/en/getting-started/
- Sa-Token Documentation: https://sa-token.cc/doc.html
- Ant Design 6 Migration: https://ant.design/docs/react/migration-v6/
- Atlas Mountain Coding Standards: `C:\project\atlas-mountain\CODING_STANDARDS.md`
- Atlas Mountain Dependency Decisions: `C:\project\atlas-mountain\docs\superpowers\specs\2026-05-09-atlas-mountain-dependency-decisions.md`
