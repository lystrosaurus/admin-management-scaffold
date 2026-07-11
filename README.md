# admin-management-scaffold

一套从 0 到 1 的后台管理系统脚手架：前端 React 19 + TypeScript + Vite + Ant Design，后端 Java 21 + Spring Boot 4 + MyBatis-Plus + Sa-Token + Redisson + Flyway，配套 MySQL 8 与 Redis。目标是提供可直接二次开发的「分层清晰、权限内置、可观测」管理后台基座。

---

## 技术栈

| 层 | 技术 | 版本 |
|---|---|---|
| 前端 | React + TypeScript + Vite | 19 / 6 / 8 |
| 前端 UI | Ant Design + Pro Components + Icons | 5.29 / 2.8 / 6.2 |
| 前端路由/状态 | react-router-dom + zustand | 7 / 5 |
| 后端 | Spring Boot（Web / Validation / Data-Redis / Test） | 4.0.6 |
| 持久层 | MyBatis-Plus | 3.5.16 |
| 认证/权限 | Sa-Token（JWT） | 1.45.0 |
| 分布式协调 | Redisson | 4.4.0 |
| 数据库迁移 | Flyway | 随 Spring Boot |
| 存储 | MySQL 8 / Redis | — |

---

## 仓库结构

```text
admin-management-scaffold/
  frontend/    React + Ant Design 管理后台
  backend/     Spring Boot 4 API 服务（模块化单体）
  deploy/      docker-compose、nginx、环境模板
  docs/        设计文档、数据库说明、权限码规范
```

后端基础包：`io.github.lystrosaurus.admin`。详细的分层约定、模块边界、API 规范见 [`AGENTS.md`](./AGENTS.md)。

---

## 功能模块（后端）

- **auth**：登录/登出/JWT、当前用户、三方登录（WeChat / WeCom）、外部账号绑定
- **system**：用户、角色、权限、菜单、数据字典、配置项
- **organization**：员工、组织单元
- **integration**：外部身份源、主体、身份匹配候选
- **audit**：登录日志、操作日志
- **file**：文件上传与元数据
- **ops**：健康检查、应用信息

前端对应页面：仪表盘、系统（用户/角色/权限/菜单/字典/配置项）、组织（员工/部门）、集成管理、审计、文件、运维。

---

## 快速开始

### 前置依赖

- JDK 21
- Node.js 22+
- 本地 MySQL 8（库名 `admin_scaffold`）+ Redis

### 后端

```bash
cd backend
# 配置数据库连接 / Redis / JWT 密钥（.env 已被 gitignore）
cp .env.example .env   # 若不存在，请参照下列变量自行创建
#   DB_URL / DB_USERNAME / DB_PASSWORD
#   REDIS_HOST / REDIS_PORT / REDIS_DATABASE
#   JWT_SECRET

mvn spring-boot:run
# 默认激活 local 环境（application.yml 中 spring.profiles.active=local）
# 启动后访问 http://localhost:8080/api
```

> 说明：本机若在 Windows 下 `mvn` 启动脚本异常，可用 classpath 直启方式（见 `CLAUDE.md` / 团队记录），不影响生产构建。

 Flyway 会在应用启动时自动执行 `backend/src/main/resources/db/migration` 下的 `V1__* ~ V14__*` 迁移脚本并打 baseline，首次启动即建表 + 写入种子数据（权限、菜单、管理员角色绑定）。

### 前端

```bash
cd frontend
npm install
npm run dev        # 开发服务器（Vite）
npm run build      # 生产构建（tsc -b && vite build）
npm run test       # 单元测试（Vitest）
```

### 默认管理员账号（本地种子）

| 用户名 | 密码 |
|---|---|
| `admin` | `admin123` |

> 仅用于本地 `local` 环境的种子数据，生产请替换为强口令。

---

## 统一 API 约定

- 路径前缀：
  - `/api/public/**` 公开（ping、公开配置、OAuth 入口/回调）
  - `/api/open/**` 机器访问（`X-API-Token`）
  - `/api/app/**` 登录后的管理接口（Sa-Token JWT）
- 统一响应体：

  ```json
  { "code": 200, "message": "操作成功", "data": {}, "timestamp": 0 }
  ```

  `code = 200` 表示成功；其余为业务/系统错误码（数值型，如 `1001` 未登录、`2003` 密码错误、`4001` 权限不足）。错误码定义见 `backend/.../exception/ErrorCode.java`。

---

## 文档导航

- [`AGENTS.md`](./AGENTS.md) — Agent 行为准则、分层与架构红线（**优先阅读**）
- [`backend/README.md`](./backend/README.md) — 后端架构、模块结构、运行与测试
- [`frontend/README.md`](./frontend/README.md) — 前端技术栈、目录结构、脚本
- [`docs/`](./docs/) — 设计文档、迭代计划、代码评审记录
