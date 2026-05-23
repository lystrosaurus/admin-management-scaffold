# Sprint 迭代计划 — Admin Management Scaffold（M3: 员工组织 + 数据权限）

## 项目概览

| 属性 | 值 |
|------|-----|
| 项目启动日期 | 2026-05-25（周一） |
| 预计完成日期 | 2026-06-27（周五） |
| Sprint 长度 | 2 周（10 个工作日） |
| 团队规模 | 2 人（双 Agent 并行，worktree 隔离） |
| 总 Sprint 数量 | 3 |
| 总估算工时 | 120h（含 20% 风险缓冲） |

## 团队角色

| 角色 | 人数 | 负责范围 |
|------|------|---------|
| Agent A | 1 | 组织域基础：Employee / OrgUnit / EmployeeOrg 全栈（Entity → DAO → Service → Controller） |
| Agent B | 1 | 数据权限基础设施 + 集成测试 + 用户-员工绑定 |
| 交付负责人 | 1 | Sprint 规划、进度跟踪、风险预警、集成协调 |

## Sprint 总览

| Sprint | 日期范围 | 主题 | 目标 | 完成标准 |
|--------|---------|------|------|---------|
| Sprint 1 | 05-25 ~ 06-06 | 组织域基础 + 数据权限骨架 | 新建 4 张表 + Entity/DTO/VO/MapStruct + @DataScope + Interceptor | Flyway V4 可执行；所有 Entity + MapStruct 通过编译和单元测试；@DataScope 注解 + 拦截器完成并通过单元测试 |
| Sprint 2 | 06-08 ~ 06-19 | CRUD 服务 + 权限集成 | Employee/OrgUnit 全栈 CRUD + 树形查询 + 数据权限注入现有查询 | 所有 Service/DAO/Controller 完成并通过 MVC 测试；数据权限拦截器可注入查询；单元测试全部通过 |
| Sprint 3 | 06-22 ~ 06-27 | 集成验证 + 种子数据 | 用户-员工绑定 + V5 种子数据 + 集成测试 + ArchUnit 扩展 | 集成测试通过；V5 种子数据可执行；ArchUnit 规则覆盖新模块；全量测试回归通过 |

## 详细 Sprint 计划

### Sprint 1: 组织域基础 + 数据权限骨架

**日期**: 2026-05-25 ~ 2026-06-06
**容量**: 60h（2 人 × 30h/Sprint）
**目标**: 完成数据库表创建、所有 Entity/DTO/VO/MapStruct 定义、数据权限注解和拦截器骨架

#### 用户故事

| 故事ID | 标题 | 优先级 | 估算(小时) | 状态 |
|--------|------|--------|-----------|------|
| US-M3-01 | 作为管理员，我能维护员工主数据（姓名、工号、手机号等） | P0 | 4 | 计划中 |
| US-M3-02 | 作为管理员，我能维护组织架构树（部门层级） | P0 | 4 | 计划中 |
| US-M3-03 | 作为系统，能根据角色的数据权限范围自动过滤查询结果 | P0 | 12 | 计划中 |

#### 任务列表

| 任务ID | 任务描述 | 关联故事 | 角色 | 估算(h) | 依赖 | 状态 |
|--------|---------|---------|------|--------|------|------|
| T-030 | V4 Flyway 迁移脚本（hr_employee / org_unit / employee_org / sys_role_org） | US-M3-01, US-M3-02 | Agent A | 3 | - | 计划中 |
| T-031 | Employee Entity + DTO + VO + MapStruct | US-M3-01 | Agent A | 4 | T-030 | 计划中 |
| T-032 | OrgUnit Entity + DTO + VO + MapStruct | US-M3-02 | Agent A | 4 | T-030 | 计划中 |
| T-033 | EmployeeOrg + SysRoleOrg Entity + Mapper | US-M3-01, US-M3-02 | Agent A | 3 | T-030 | 计划中 |
| T-034 | EmployeeDAO + OrgUnitDAO + EmployeeOrgDAO | US-M3-01, US-M3-02 | Agent A | 8 | T-031, T-032, T-033 | 计划中 |
| T-037 | @DataScope 注解 + DataScopeInterceptor | US-M3-03 | Agent B | 12 | T-030 | 计划中 |

#### 风险与阻碍

| 类型 | 描述 | 影响 | 缓解策略 |
|------|------|------|---------|
| 技术风险 | MyBatis-Plus JSqlParser 拦截器改写 SQL 可能与现有分页拦截器冲突 | 高 | T-037 启动时先做 POC 验证两个 InnerInterceptor 共存；优先级：分页 > 数据权限 |
| 依赖风险 | Agent A 的 T-030 必须先完成，否则 Agent B 的 T-037 无法开始 | 中 | T-030 安排在 Sprint 第 1 天完成；Agent B 可先编写注解定义和拦截器框架，不依赖实际表 |

---

### Sprint 2: CRUD 服务 + 权限集成

**日期**: 2026-06-08 ~ 2026-06-19
**容量**: 60h
**目标**: 完成 Employee/OrgUnit 全栈 CRUD + 树形查询 + 数据权限集成到现有查询

#### 用户故事

| 故事ID | 标题 | 优先级 | 估算(小时) | 状态 |
|--------|------|--------|-----------|------|
| US-M3-04 | 作为管理员，我能通过 API 创建、编辑、删除、查询员工 | P0 | 10 | 计划中 |
| US-M3-05 | 作为管理员，我能通过 API 管理组织架构树（增删改 + 树形查询） | P0 | 12 | 计划中 |
| US-M3-06 | 作为系统，数据权限拦截器能自动注入到标注了 @DataScope 的查询中 | P0 | 10 | 计划中 |

#### 任务列表

| 任务ID | 任务描述 | 关联故事 | 角色 | 估算(h) | 依赖 | 状态 |
|--------|---------|---------|------|--------|------|------|
| T-035 | EmployeeService + OrgUnitService（CRUD + 树形） | US-M3-04, US-M3-05 | Agent A | 14 | T-034 | 计划中 |
| T-036 | EmployeeController + OrgUnitController | US-M3-04, US-M3-05 | Agent A | 10 | T-035 | 计划中 |
| T-038 | 数据权限集成到现有查询（@DataScope 标注 + 缓存策略） | US-M3-06 | Agent B | 10 | T-037, T-035 | 计划中 |

#### 风险与阻碍

| 类型 | 描述 | 影响 | 缓解策略 |
|------|------|------|---------|
| 技术风险 | 组织树递归查询（获取子节点集合）在 MySQL 中需要 CTE 或应用层递归 | 中 | 优先使用应用层递归（Java List 操作），避免复杂 SQL；数据量大时后续可优化为 CTE |
| 集成风险 | 数据权限拦截器修改 SQL 后可能影响现有 M2 测试 | 高 | @DataScope 仅标注新查询方法；先跑 M2 全量回归测试确认无影响 |

---

### Sprint 3: 集成验证 + 种子数据

**日期**: 2026-06-22 ~ 2026-06-27
**容量**: 30h（轻量 Sprint，5 个工作日）
**目标**: 用户-员工绑定、种子数据、集成测试、ArchUnit 扩展、全量回归

#### 用户故事

| 故事ID | 标题 | 优先级 | 估算(小时) | 状态 |
|--------|------|--------|-----------|------|
| US-M3-07 | 作为管理员，我能将后台用户绑定到员工记录 | P1 | 6 | 计划中 |
| US-M3-08 | 作为开发者，我希望有完整的种子数据和集成测试覆盖新功能 | P0 | 14 | 计划中 |

#### 任务列表

| 任务ID | 任务描述 | 关联故事 | 角色 | 估算(h) | 依赖 | 状态 |
|--------|---------|---------|------|--------|------|------|
| T-039 | 用户-员工绑定 API（UserController 扩展 + ProfileController 扩展） | US-M3-07 | Agent A | 6 | T-035 | 计划中 |
| T-040 | V5 种子数据 + 集成测试（员工/组织 CRUD + 数据权限验证） | US-M3-08 | Agent B | 10 | T-036, T-038 | 计划中 |
| T-041 | ArchUnit 规则扩展 + Controller MVC 测试 + 全量回归 | US-M3-08 | Agent B | 8 | T-036, T-038 | 计划中 |

#### 风险与阻碍

| 类型 | 描述 | 影响 | 缓解策略 |
|------|------|------|---------|
| 集成风险 | 数据权限集成后全量回归可能出现意外失败 | 中 | 预留 1 天缓冲用于修复回归问题；优先修复 P0 测试 |
| 质量风险 | Sprint 3 时间较短（5 天），可能来不及完成所有测试 | 中 | 如时间不足，T-041 的 ArchUnit 扩展可降级为 P1，推迟到 M4 |

---

## 里程碑

| 里程碑 | 日期 | 交付物 | 验收标准 |
|--------|------|--------|---------|
| M3.1: 数据层就绪 | 2026-06-06 | V4 迁移脚本、4 个 Entity、8 个 DTO/VO、4 个 MapStruct、3 个 DAO 接口 + 实现、@DataScope + Interceptor | Flyway 迁移可执行；所有 Entity/DTO/VO/MapStruct 编译通过；DAO 单元测试通过；DataScopeInterceptor 单元测试通过 |
| M3.2: CRUD + 权限就绪 | 2026-06-19 | EmployeeService/OrgUnitService + Controller + 数据权限集成 | 所有 API 端点可通过 MockMvc 测试；数据权限拦截器在标注方法上生效；M2 全量回归通过（383 tests） |
| M3.3: M3 完成 | 2026-06-27 | 用户-员工绑定、V5 种子数据、集成测试、ArchUnit 扩展 | 集成测试通过；V5 种子数据可执行；总测试数 ≥ 450；M2 测试无回归（383 tests 不减少） |

## 风险登记册

| 风险ID | 风险描述 | 概率 | 影响 | 等级 | 缓解策略 | 负责人 |
|--------|---------|------|------|------|---------|--------|
| R-001 | MyBatis-Plus JSqlParser 拦截器与分页拦截器冲突 | 中 | 高 | 高 | Sprint 1 第 1 天做 POC 验证；配置拦截器优先级：DataScope(先) → Pagination(后) | Agent B |
| R-002 | 数据权限改写 SQL 破坏现有 M2 测试 | 中 | 高 | 高 | @DataScope 仅标注新方法；每个 Sprint 结束跑 M2 全量回归 | Agent B |
| R-003 | org_unit 递归树查询性能问题 | 低 | 中 | 低 | V1 使用应用层递归；数据量 > 1000 时考虑 CTE 或物化路径 | Agent A |
| R-004 | Sprint 3 时间窗口短（5 天），测试不足 | 中 | 中 | 中 | T-041 ArchUnit 扩展可降级；优先保障集成测试覆盖 | 交付负责人 |
| R-005 | employee_org 多部门逻辑与 primary_org_id 一致性 | 低 | 中 | 低 | Service 层强制约束：设置 is_primary=1 时自动同步 primary_org_id | Agent A |
| R-006 | sys_role_org 无逻辑删除，清理逻辑需单独处理 | 低 | 低 | 低 | 使用物理删除 + 唯一约束防止重复；角色删除时级联清理 | Agent B |

## 关键依赖关系

```
T-030 (V4 迁移) ──┬──→ T-031 (Employee Entity) ──→ T-034 (DAO) ──→ T-035 (Service) ──→ T-036 (Controller)
                   ├──→ T-032 (OrgUnit Entity)  ──→ T-034 (DAO) ──→ T-035 (Service) ──→ T-036 (Controller)
                   ├──→ T-033 (关联 Entity)      ──→ T-034 (DAO)
                   └──→ T-037 (@DataScope)       ──→ T-038 (权限集成) ──────────────────→ T-040 (集成测试)
                                                                         T-036 (Controller) ──→ T-039 (绑定 API)
                                                                                              → T-041 (ArchUnit)
```

**关键路径**: T-030 → T-031/T-032 → T-034 → T-035 → T-036 → T-040/T-041（约 39h）

## 新增错误码规划

| 错误码 | 数值 | 描述 |
|--------|------|------|
| EMPLOYEE_NOT_FOUND | 7001 | 员工不存在 |
| EMPLOYEE_ALREADY_EXISTS | 7002 | 员工已存在（工号重复） |
| EMPLOYEE_NO_DUPLICATE | 7003 | 工号重复 |
| EMPLOYEE_HAS_BINDING | 7004 | 员工已绑定用户，无法删除 |
| ORG_UNIT_NOT_FOUND | 8001 | 组织单元不存在 |
| ORG_UNIT_ALREADY_EXISTS | 8002 | 组织单元已存在（编码重复） |
| ORG_UNIT_HAS_CHILDREN | 8003 | 组织单元存在子节点，无法删除 |
| ORG_UNIT_HAS_EMPLOYEES | 8004 | 组织单元下有员工，无法删除 |
| ORG_UNIT_CIRCULAR_REF | 8005 | 组织单元父节点形成循环引用 |
| DATA_SCOPE_NO_ORG | 9001 | 数据权限配置错误：CUSTOM 角色未配置组织范围 |

## 新增 API 端点规划

### 员工管理 `/app/employees`

| 方法 | 路径 | 描述 | 权限码 |
|------|------|------|--------|
| POST | `/app/employees` | 创建员工 | organization:employee:create |
| GET | `/app/employees` | 分页查询员工 | organization:employee:view |
| GET | `/app/employees/{id}` | 员工详情 | organization:employee:view |
| PUT | `/app/employees/{id}` | 更新员工 | organization:employee:update |
| DELETE | `/app/employees/{id}` | 删除员工 | organization:employee:delete |

### 组织管理 `/app/org-units`

| 方法 | 路径 | 描述 | 权限码 |
|------|------|------|--------|
| POST | `/app/org-units` | 创建组织单元 | organization:orgunit:create |
| GET | `/app/org-units` | 查询组织列表（支持 tree 参数） | organization:orgunit:view |
| GET | `/app/org-units/{id}` | 组织单元详情 | organization:orgunit:view |
| PUT | `/app/org-units/{id}` | 更新组织单元 | organization:orgunit:update |
| DELETE | `/app/org-units/{id}` | 删除组织单元 | organization:orgunit:delete |
| GET | `/app/org-units/tree` | 获取组织架构树 | organization:orgunit:view |

### 用户-员工绑定（扩展现有端点）

| 方法 | 路径 | 描述 | 权限码 |
|------|------|------|--------|
| PUT | `/app/users/{id}/employee` | 绑定/解绑员工 | system:user:update |
| GET | `/app/profile` | 扩展返回绑定员工信息 | （已有） |

## 变更日志

| 版本 | 日期 | 变更内容 | 变更人 |
|------|------|---------|--------|
| v1.0 | 2026-05-23 | 初始版本：M3 Sprint 计划 | delivery-lead |
