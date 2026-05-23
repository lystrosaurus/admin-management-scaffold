# 详细任务清单 — Admin Management Scaffold（M3: 员工组织 + 数据权限）

## 说明

本文档包含 M3 所有可执行任务的详细定义。每个任务都具备：
- 明确的验收标准
- 合理的工时估算
- 清晰的角色定义
- 完整的依赖关系

所有任务遵循项目既有架构：`Controller → Service → DAO → DAO Impl → Mapper`，Entity 用 class + Lombok，DTO/VO 用 record，MapStruct 用 `@Mapper(componentModel = "spring")`。

## 任务状态图例

- 📋 待开始 (Backlog)
- 🔵 计划中 (Planned)
- 🟡 进行中 (In Progress)
- 🟢 已完成 (Done)
- ⛔ 阻塞 (Blocked)

## 任务清单

---

### Epic 1: 数据库迁移（EP-001）

**目标**: 创建 M3 所需的 4 张新表 + 种子数据
**关联需求**: US-M3-01, US-M3-02, US-M3-03
**预计跨度**: Sprint 1

#### T-030: V4 Flyway 迁移脚本

| 属性 | 值 |
|------|-----|
| 状态 | 📋 |
| 优先级 | P0 |
| 估算工时 | 2.5h |
| 风险缓冲 | +0.5h |
| 总工时 | 3h |
| 角色 | Agent A |
| 关联故事 | US-M3-01, US-M3-02 |
| Sprint | Sprint 1 |
| 依赖 | 无 |

**任务描述**:
创建 `V4__add_employee_org_data_permission.sql` 迁移脚本，包含以下 4 张表的 DDL：
- `hr_employee`: 员工主数据表
- `org_unit`: 组织架构表
- `employee_org`: 员工-组织多对多关联表
- `sys_role_org`: 角色-组织自定义数据权限范围表

严格遵循项目数据库规范：表名/字段名小写下划线、主键 BIGINT、状态字段 VARCHAR(32) 全大写、时间字段 DATETIME、字符集 utf8mb4、排序规则 utf8mb4_0900_ai_ci。每张业务表包含 `created_at / created_by / updated_at / updated_by / deleted / version`（sys_role_org 除外，它不做逻辑删除）。

**验收标准**:
- [ ] `V4__add_employee_org_data_permission.sql` 存在于 `backend/src/main/resources/db/migration/`
- [ ] `hr_employee` 表结构完整，包含 `employee_no` UNIQUE 约束、`employment_status` DEFAULT 'ACTIVE'
- [ ] `org_unit` 表结构完整，包含 `code` UNIQUE 约束、`full_path` VARCHAR(500)、`level` DEFAULT 1
- [ ] `employee_org` 表包含 `uk_employee_org (employee_id, org_id)` UNIQUE 约束
- [ ] `sys_role_org` 表包含 `uk_role_org (role_id, org_id)` UNIQUE 约束
- [ ] Flyway 迁移在本地 MySQL 可成功执行（无语法错误）
- [ ] 已有 V3 种子数据中的 `sys_role.data_scope_type` 值确认为 'ALL' / 'SELF'（V3 已设置）

**技术备注**:
- `hr_employee` 和 `org_unit` 使用 `deleted INT DEFAULT 0`（逻辑删除，配合 `@TableLogic`）
- `sys_role_org` 不做逻辑删除，使用物理删除 + UNIQUE 约束防重复
- `employee_org.status` 用于标记关联是否有效（ACTIVE/INACTIVE），而非员工状态
- `org_unit.full_path` 存储从根到当前节点的路径，如 `/1/5/12/`，用于快速查询子树

**子任务**:
- [ ] ST-030-01: 编写 `hr_employee` DDL (0.5h)
- [ ] ST-030-02: 编写 `org_unit` DDL (0.5h)
- [ ] ST-030-03: 编写 `employee_org` DDL (0.5h)
- [ ] ST-030-04: 编写 `sys_role_org` DDL (0.5h)
- [ ] ST-030-05: 本地 Flyway 迁移验证 (0.5h)

---

### Epic 2: 员工模块（EP-002）

**目标**: 完成 Employee 全栈 CRUD（Entity → DAO → Service → Controller）
**关联需求**: US-M3-01, US-M3-04
**预计跨度**: Sprint 1 ~ Sprint 2

#### T-031: Employee Entity + DTO + VO + MapStruct

| 属性 | 值 |
|------|-----|
| 状态 | 📋 |
| 优先级 | P0 |
| 估算工时 | 3.5h |
| 风险缓冲 | +0.5h |
| 总工时 | 4h |
| 角色 | Agent A |
| 关联故事 | US-M3-01 |
| Sprint | Sprint 1 |
| 依赖 | T-030 |

**任务描述**:
在 `io.github.lystrosaurus.admin.organization.employee` 包下创建完整的数据模型层。

**需创建的文件**:
1. `entity/HrEmployee.java` — 继承 BaseEntity，字段与 hr_employee 表一一对应
2. `dto/EmployeeCreateDTO.java` — record，@NotBlank name, @NotBlank employeeNo，可选字段：mobile, email, jobTitle, entryDate, primaryOrgId
3. `dto/EmployeeUpdateDTO.java` — record，所有字段可选（部分更新语义）
4. `dto/EmployeeQueryDTO.java` — record，查询条件：keyword, employmentStatus, orgId
5. `vo/EmployeeVO.java` — record，基础列表展示字段
6. `vo/EmployeeDetailVO.java` — record，详情展示（含组织信息）
7. `mapstruct/EmployeeMapper.java` — @Mapper(componentModel = "spring")，Entity ↔ VO / DTO 转换

**验收标准**:
- [ ] `HrEmployee` 继承 `BaseEntity`，使用 `@Getter`/`@Setter`，无 `@Data`
- [ ] 所有 DTO 使用 `record`，包含合适的 Jakarta Validation 注解
- [ ] 所有 VO 使用 `record`
- [ ] `EmployeeMapper` 使用 `@Mapper(componentModel = "spring")`，包含 `INSTANCE` 字段
- [ ] 编译通过，MapStruct 生成实现类
- [ ] Entity 单元测试验证字段 getter/setter
- [ ] MapStruct 单元测试验证转换正确性

**技术备注**:
- `employment_status` 在 Entity 中为 String 类型，取值 ACTIVE/RESIGNED/SUSPENDED
- `source_type` 默认 MANUAL，后续 M4/M5 扩展
- `entry_date` / `leave_date` 使用 `java.time.LocalDate`
- `primary_org_id` 为 BIGINT，关联 org_unit.id，此处不做外键约束

**子任务**:
- [ ] ST-031-01: HrEmployee Entity (0.5h)
- [ ] ST-031-02: EmployeeCreateDTO + EmployeeUpdateDTO + EmployeeQueryDTO (1h)
- [ ] ST-031-03: EmployeeVO + EmployeeDetailVO (0.5h)
- [ ] ST-031-04: EmployeeMapper MapStruct (0.5h)
- [ ] ST-031-05: Entity + MapStruct 单元测试 (1h)

---

#### T-034-A: EmployeeDAO

| 属性 | 值 |
|------|-----|
| 状态 | 📋 |
| 优先级 | P0 |
| 估算工时 | 3h |
| 风险缓冲 | +0.5h |
| 总工时 | 3.5h |
| 角色 | Agent A |
| 关联故事 | US-M3-01, US-M3-04 |
| Sprint | Sprint 1 |
| 依赖 | T-031, T-033 |

**任务描述**:
创建 `EmployeeDAO` 接口和 `EmployeeDAOImpl` 实现。接口定义 Service 需要的数据访问语义。

**需创建的文件**:
1. `dao/EmployeeDAO.java` — 接口
2. `dao/impl/EmployeeDAOImpl.java` — @Service + @RequiredArgsConstructor
3. `mapper/HrEmployeeMapper.java` — extends BaseMapper<HrEmployee>

**DAO 接口方法**:
```java
HrEmployee findById(Long id);
HrEmployee findByEmployeeNo(String employeeNo);
void save(HrEmployee employee);
void update(HrEmployee employee);
void deleteById(Long id);
List<HrEmployee> findByCondition(String keyword, String employmentStatus, Long orgId, int page, int size);
long countByCondition(String keyword, String employmentStatus, Long orgId);
boolean existsByEmployeeNo(String employeeNo);
boolean existsByEmployeeNoAndIdNot(String employeeNo, Long id);
```

**验收标准**:
- [ ] DAO 接口不依赖 MyBatis-Plus 类型
- [ ] DAOImpl 使用 `LambdaQueryWrapper` 构建查询
- [ ] `findByCondition` 支持 keyword 模糊匹配 name/employee_no/mobile
- [ ] DAOImpl 单元测试覆盖所有方法（Mock Mapper）

**子任务**:
- [ ] ST-034-A01: HrEmployeeMapper (0.5h)
- [ ] ST-034-A02: EmployeeDAO 接口 (0.5h)
- [ ] ST-034-A03: EmployeeDAOImpl 实现 (1h)
- [ ] ST-034-A04: EmployeeDAOImpl 单元测试 (1h)

---

#### T-035-A: EmployeeService

| 属性 | 值 |
|------|-----|
| 状态 | 📋 |
| 优先级 | P0 |
| 估算工时 | 5h |
| 风险缓冲 | +1h |
| 总工时 | 6h |
| 角色 | Agent A |
| 关联故事 | US-M3-04 |
| Sprint | Sprint 2 |
| 依赖 | T-034-A |

**任务描述**:
创建 `EmployeeService` 接口和 `EmployeeServiceImpl`。遵循既有模式：接口 + 实现，写操作加 `@Transactional(rollbackFor = Exception.class)`。

**Service 方法**:
```java
EmployeeVO create(EmployeeCreateDTO dto);
EmployeeVO update(Long id, EmployeeUpdateDTO dto);
void deleteById(Long id);
EmployeeDetailVO findById(Long id);
PageResult<EmployeeVO> findPage(EmployeeQueryDTO dto, int page, int size);
```

**业务规则**:
- 创建时校验 `employee_no` 唯一性
- 删除时校验是否已绑定 `sys_user`（通过 sys_user.employee_id 查询），如已绑定则抛 `EMPLOYEE_HAS_BINDING`
- 更新时支持部分更新语义

**验收标准**:
- [ ] `EmployeeService` 接口定义完整
- [ ] `EmployeeServiceImpl` 使用 `@Service` + `@RequiredArgsConstructor`
- [ ] 写操作使用 `@Transactional(rollbackFor = Exception.class)`
- [ ] 唯一性校验：创建时 employee_no 重复抛 `EMPLOYEE_NO_DUPLICATE`
- [ ] 删除前检查绑定：已绑定用户抛 `EMPLOYEE_HAS_BINDING`
- [ ] ServiceImpl 单元测试覆盖正常路径 + 异常路径（≥ 8 个测试）

**子任务**:
- [ ] ST-035-A01: EmployeeService 接口定义 (0.5h)
- [ ] ST-035-A02: EmployeeServiceImpl 实现 (2h)
- [ ] ST-035-A03: EmployeeServiceImpl 单元测试 (2.5h)

---

#### T-036-A: EmployeeController

| 属性 | 值 |
|------|-----|
| 状态 | 📋 |
| 优先级 | P0 |
| 估算工时 | 4h |
| 风险缓冲 | +0.5h |
| 总工时 | 4.5h |
| 角色 | Agent A |
| 关联故事 | US-M3-04 |
| Sprint | Sprint 2 |
| 依赖 | T-035-A |

**任务描述**:
创建 `EmployeeController`，路径 `/app/employees`，提供标准 CRUD 端点。

**API 端点**:
```
POST   /app/employees          — 创建员工
GET    /app/employees           — 分页查询（page, size, keyword, employmentStatus, orgId）
GET    /app/employees/{id}      — 员工详情
PUT    /app/employees/{id}      — 更新员工
DELETE /app/employees/{id}      — 删除员工
```

**验收标准**:
- [ ] Controller 仅依赖 `EmployeeService`，不依赖 DAO/Mapper/Entity
- [ ] 使用 `@RestController` + `@RequestMapping("/app/employees")`
- [ ] 所有返回值包装为 `ApiResponse<T>`
- [ ] 参数校验使用 `@Valid`
- [ ] MVC 测试（继承 SaTokenTest）覆盖所有端点（≥ 5 个测试）
- [ ] 测试覆盖正常路径 + 异常路径（资源不存在等）

**子任务**:
- [ ] ST-036-A01: EmployeeController 实现 (1.5h)
- [ ] ST-036-A02: EmployeeControllerTest MVC 测试 (2.5h)

---

### Epic 3: 组织模块（EP-003）

**目标**: 完成 OrgUnit 全栈 CRUD + 树形查询
**关联需求**: US-M3-02, US-M3-05
**预计跨度**: Sprint 1 ~ Sprint 2

#### T-032: OrgUnit Entity + DTO + VO + MapStruct

| 属性 | 值 |
|------|-----|
| 状态 | 📋 |
| 优先级 | P0 |
| 估算工时 | 3.5h |
| 风险缓冲 | +0.5h |
| 总工时 | 4h |
| 角色 | Agent A |
| 关联故事 | US-M3-02 |
| Sprint | Sprint 1 |
| 依赖 | T-030 |

**任务描述**:
在 `io.github.lystrosaurus.admin.organization.orgunit` 包下创建完整的数据模型层。

**需创建的文件**:
1. `entity/OrgUnit.java` — 继承 BaseEntity
2. `dto/OrgUnitCreateDTO.java` — record，@NotBlank code, @NotBlank name, parentId (默认 0), managerEmployeeId, sortOrder
3. `dto/OrgUnitUpdateDTO.java` — record，所有字段可选
4. `dto/OrgUnitQueryDTO.java` — record，查询条件：keyword, status, parentId
5. `vo/OrgUnitVO.java` — record
6. `vo/OrgUnitTreeVO.java` — record，含 `List<OrgUnitTreeVO> children` 字段
7. `mapstruct/OrgUnitMapper.java` — @Mapper(componentModel = "spring")

**验收标准**:
- [ ] `OrgUnit` 继承 `BaseEntity`，字段与 org_unit 表对应
- [ ] `OrgUnitTreeVO` 包含 `children` 字段用于递归树构建
- [ ] 所有 DTO 使用 record + 合适的 Validation 注解
- [ ] MapStruct 映射正确
- [ ] 编译通过，单元测试通过

**技术备注**:
- `parent_id` 默认 0 表示根节点
- `full_path` 由 Service 层在创建/移动时计算，不在 Entity 层处理
- `level` 由 Service 层根据 parent_id 推算
- `status` 取值 ENABLED/DISABLED

**子任务**:
- [ ] ST-032-01: OrgUnit Entity (0.5h)
- [ ] ST-032-02: OrgUnitCreateDTO + UpdateDTO + QueryDTO (1h)
- [ ] ST-032-03: OrgUnitVO + OrgUnitTreeVO (0.5h)
- [ ] ST-032-04: OrgUnitMapper MapStruct (0.5h)
- [ ] ST-032-05: Entity + MapStruct 单元测试 (1h)

---

#### T-033: EmployeeOrg + SysRoleOrg Entity + Mapper

| 属性 | 值 |
|------|-----|
| 状态 | 📋 |
| 优先级 | P0 |
| 估算工时 | 2.5h |
| 风险缓冲 | +0.5h |
| 总工时 | 3h |
| 角色 | Agent A |
| 关联故事 | US-M3-01, US-M3-02 |
| Sprint | Sprint 1 |
| 依赖 | T-030 |

**任务描述**:
创建关联表的 Entity 和 Mapper。

**需创建的文件**:
1. `organization/employee/entity/EmployeeOrg.java` — 继承 BaseEntity，字段：employeeId, orgId, isPrimary (Integer, 0/1), positionName, startDate, endDate, status
2. `organization/employee/mapper/EmployeeOrgMapper.java` — extends BaseMapper<EmployeeOrg>
3. `system/role/entity/SysRoleOrg.java` — 无继承 BaseEntity（物理删除），字段：id, roleId, orgId, createdAt, createdBy
4. `system/role/mapper/SysRoleOrgMapper.java` — extends BaseMapper<SysRoleOrg>

**验收标准**:
- [ ] `EmployeeOrg` 继承 `BaseEntity`，`isPrimary` 使用 Integer 类型（0/1）
- [ ] `SysRoleOrg` 不继承 BaseEntity，手动定义 id + createdAt + createdBy 字段
- [ ] Mapper 编译通过
- [ ] Entity 单元测试通过

**技术备注**:
- `EmployeeOrg.startDate` / `endDate` 使用 `LocalDate`
- `SysRoleOrg` 不做逻辑删除，直接物理删除
- `SysRoleOrg` 不需要 version 乐观锁

**子任务**:
- [ ] ST-033-01: EmployeeOrg Entity + Mapper (1h)
- [ ] ST-033-02: SysRoleOrg Entity + Mapper (0.5h)
- [ ] ST-033-03: 单元测试 (1h)

---

#### T-034-B: OrgUnitDAO + EmployeeOrgDAO

| 属性 | 值 |
|------|-----|
| 状态 | 📋 |
| 优先级 | P0 |
| 估算工时 | 4h |
| 风险缓冲 | +0.5h |
| 总工时 | 4.5h |
| 角色 | Agent A |
| 关联故事 | US-M3-02, US-M3-05 |
| Sprint | Sprint 1 |
| 依赖 | T-032, T-033 |

**任务描述**:
创建 OrgUnitDAO 和 EmployeeOrgDAO 接口及实现。

**OrgUnitDAO 方法**:
```java
OrgUnit findById(Long id);
OrgUnit findByCode(String code);
void save(OrgUnit orgUnit);
void update(OrgUnit orgUnit);
void deleteById(Long id);
List<OrgUnit> findByCondition(String keyword, String status, Long parentId, int page, int size);
long countByCondition(String keyword, String status, Long parentId);
List<OrgUnit> findAll();
List<OrgUnit> findByParentId(Long parentId);
List<OrgUnit> findByIds(List<Long> ids);
boolean existsByCode(String code);
boolean existsByCodeAndIdNot(String code, Long id);
boolean hasChildren(Long id);
```

**EmployeeOrgDAO 方法**:
```java
void save(EmployeeOrg employeeOrg);
void deleteByEmployeeIdAndOrgId(Long employeeId, Long orgId);
void deleteByEmployeeId(Long employeeId);
List<EmployeeOrg> findByEmployeeId(Long employeeId);
List<EmployeeOrg> findByOrgId(Long orgId);
List<Long> findEmployeeIdsByOrgId(Long orgId);
List<Long> findOrgIdsByEmployeeId(Long employeeId);
```

**验收标准**:
- [ ] 两个 DAO 接口不依赖 MyBatis-Plus 类型
- [ ] DAOImpl 使用 `LambdaQueryWrapper`
- [ ] `findByCondition` 支持 keyword 模糊匹配 name/code
- [ ] `findAll` 返回全部未删除组织（用于树构建）
- [ ] 单元测试覆盖所有方法

**子任务**:
- [ ] ST-034-B01: OrgUnitDAO 接口 + OrgUnitDAOImpl (2h)
- [ ] ST-034-B02: EmployeeOrgDAO 接口 + EmployeeOrgDAOImpl (1h)
- [ ] ST-034-B03: 单元测试 (1.5h)

---

#### T-035-B: OrgUnitService（含树形查询）

| 属性 | 值 |
|------|-----|
| 状态 | 📋 |
| 优先级 | P0 |
| 估算工时 | 7h |
| 风险缓冲 | +1h |
| 总工时 | 8h |
| 角色 | Agent A |
| 关联故事 | US-M3-05 |
| Sprint | Sprint 2 |
| 依赖 | T-034-B |

**任务描述**:
创建 OrgUnitService，实现 CRUD + 树形查询。这是 M3 中业务逻辑最复杂的服务。

**Service 方法**:
```java
OrgUnitVO create(OrgUnitCreateDTO dto);
OrgUnitVO update(Long id, OrgUnitUpdateDTO dto);
void deleteById(Long id);
OrgUnitVO findById(Long id);
List<OrgUnitVO> findAll();
List<OrgUnitTreeVO> findTree();
PageResult<OrgUnitVO> findPage(OrgUnitQueryDTO dto, int page, int size);
```

**业务规则**:
- 创建时校验 `code` 唯一性
- 创建时根据 parentId 计算 `level` 和 `full_path`（parent.full_path + parent.id + "/"）
- 更新 parentId 时校验不能形成循环引用（新父节点不能是自己的子孙）
- 更新 parentId 时重新计算 level 和 full_path，并级联更新所有子孙节点
- 删除时校验：不能有子节点（`hasChildren`），不能有关联员工（`findByOrgId`）
- `findTree()` 使用应用层递归：先查全部 OrgUnit，再构建树（parentId=0 为根）

**验收标准**:
- [ ] Service 接口定义完整
- [ ] `findTree()` 返回 `List<OrgUnitTreeVO>`（递归树结构）
- [ ] 创建时自动计算 `level` 和 `full_path`
- [ ] 循环引用检测正常工作
- [ ] 删除时校验子节点和员工关联
- [ ] ServiceImpl 单元测试覆盖所有方法 + 异常路径（≥ 10 个测试）

**技术备注**:
- 树构建在应用层完成，不在 SQL 中递归（兼容性好，数据量小时性能足够）
- `full_path` 格式：`/1/5/12/`，以 `/` 开头和结尾，便于 `LIKE '/1/5/%'` 查询子树
- 循环引用检测：从目标节点向上遍历 parent_id 链，检查是否回到自身

**子任务**:
- [ ] ST-035-B01: OrgUnitService 接口定义 (0.5h)
- [ ] ST-035-B02: OrgUnitServiceImpl — CRUD 实现 (2.5h)
- [ ] ST-035-B03: OrgUnitServiceImpl — 树形查询 + full_path 计算 (1.5h)
- [ ] ST-035-B04: OrgUnitServiceImpl — 循环引用检测 + 删除校验 (1h)
- [ ] ST-035-B05: OrgUnitServiceImpl 单元测试 (2.5h)

---

#### T-036-B: OrgUnitController

| 属性 | 值 |
|------|-----|
| 状态 | 📋 |
| 优先级 | P0 |
| 估算工时 | 4h |
| 风险缓冲 | +0.5h |
| 总工时 | 4.5h |
| 角色 | Agent A |
| 关联故事 | US-M3-05 |
| Sprint | Sprint 2 |
| 依赖 | T-035-B |

**任务描述**:
创建 OrgUnitController，路径 `/app/org-units`。

**API 端点**:
```
POST   /app/org-units          — 创建组织单元
GET    /app/org-units           — 分页查询（page, size, keyword, status, parentId）
GET    /app/org-units/tree      — 获取组织架构树
GET    /app/org-units/{id}      — 组织单元详情
PUT    /app/org-units/{id}      — 更新组织单元
DELETE /app/org-units/{id}      — 删除组织单元
```

**验收标准**:
- [ ] Controller 仅依赖 OrgUnitService
- [ ] 路径 `/app/org-units`（小写连字符）
- [ ] `/app/org-units/tree` 返回 `ApiResponse<List<OrgUnitTreeVO>>`
- [ ] MVC 测试覆盖所有端点（≥ 6 个测试）
- [ ] 测试覆盖正常路径 + 异常路径

**子任务**:
- [ ] ST-036-B01: OrgUnitController 实现 (1.5h)
- [ ] ST-036-B02: OrgUnitControllerTest MVC 测试 (2.5h)

---

### Epic 4: 数据权限基础设施（EP-004）

**目标**: 实现 @DataScope 注解 + MyBatis-Plus InnerInterceptor 自动追加数据权限 WHERE 条件
**关联需求**: US-M3-03, US-M3-06
**预计跨度**: Sprint 1 ~ Sprint 2

#### T-037: @DataScope 注解 + DataScopeInterceptor

| 属性 | 值 |
|------|-----|
| 状态 | 📋 |
| 优先级 | P0 |
| 估算工时 | 10h |
| 风险缓冲 | +2h |
| 总工时 | 12h |
| 角色 | Agent B |
| 关联故事 | US-M3-03 |
| Sprint | Sprint 1 |
| 依赖 | T-030（表结构，用于理解字段） |

**任务描述**:
实现数据权限的核心基础设施。这是 M3 技术难度最高的任务。

**需创建的文件**:
1. `infra/persistence/datascope/DataScope.java` — 注解定义
2. `infra/persistence/datascope/DataScopeType.java` — 枚举：ALL, ORG_TREE, ORG_ONLY, SELF, CUSTOM
3. `infra/persistence/datascope/DataScopeInterceptor.java` — implements InnerInterceptor
4. `infra/persistence/datascope/DataScopeContextHolder.java` — ThreadLocal 上下文（可选）
5. `infra/persistence/datascope/DataScopeHelper.java` — 辅助类，负责 SQL 改写逻辑

**@DataScope 注解设计**:
```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataScope {
    /** 主表别名，默认为空则使用原表名 */
    String tableAlias() default "";
    /** org_id 字段名，默认 "org_id" */
    String orgIdColumn() default "org_id";
    /** created_by 字段名（SELF 模式用），默认 "created_by" */
    String createdByColumn() default "created_by";
}
```

**拦截器逻辑**:
```
1. beforeQuery 拦截被 @DataScope 标注的方法
2. 获取当前用户 ID（StpUtil.getLoginIdAsLong()）
3. 查询用户所有角色，获取 data_scope_type 集合
4. 合并逻辑：
   - 有 ALL → 不追加条件
   - ORG_TREE → 查询 org_unit 表获取子树 ID 集合（WHERE org_id IN (子树IDs)）
   - ORG_ONLY → 查询 employee_org 获取直接关联 org_id（WHERE org_id IN (直接IDs)）
   - SELF → WHERE created_by = currentUser
   - CUSTOM → 查询 sys_role_org 获取配置的 org_id（WHERE org_id IN (配置IDs)）
   - 多个角色合并：ORG_TREE/ORG_ONLY/CUSTOM 的 org_id 取并集，SELF 独立追加
5. 使用 JSqlParser 在原 SQL 的 WHERE 子句中追加 AND 条件
```

**MyBatis-Plus 配置更新**:
- 在 `MyBatisPlusConfig` 中注册 `DataScopeInterceptor`，优先级高于 `PaginationInnerInterceptor`

**验收标准**:
- [ ] `@DataScope` 注解定义完整，支持 tableAlias / orgIdColumn / createdByColumn 配置
- [ ] `DataScopeType` 枚举包含 5 种类型
- [ ] `DataScopeInterceptor` 实现 `InnerInterceptor`，在 `beforeQuery` 中拦截
- [ ] ALL 模式：不追加任何条件
- [ ] ORG_TREE 模式：追加 `AND org_id IN (...)` 条件
- [ ] ORG_ONLY 模式：追加 `AND org_id IN (...)` 条件（仅直接关联）
- [ ] SELF 模式：追加 `AND created_by = 'xxx'` 条件
- [ ] CUSTOM 模式：追加 `AND org_id IN (...)` 条件（从 sys_role_org 查询）
- [ ] 多角色合并：取并集
- [ ] 未标注 @DataScope 的方法不受影响
- [ ] 与 PaginationInnerInterceptor 共存无冲突
- [ ] 单元测试覆盖所有 5 种 data_scope_type（≥ 8 个测试）

**技术备注**:
- 使用 `JSqlParser` 解析和改写 SQL（MyBatis-Plus 已依赖 `mybatis-plus-jsqlparser`）
- 拦截器需要从 MyBatis 的 MappedStatement 中获取方法上的 @DataScope 注解
- 角色信息可通过 DAO 查询，或通过缓存获取（V1 先直接查询，后续可优化）
- org_id 集合获取需要查询 org_unit 表（ORG_TREE 需要递归），可复用 OrgUnitDAO
- 拦截器中不能直接注入 Spring Bean，需要通过 `ApplicationContextAware` 或手动获取

**子任务**:
- [ ] ST-037-01: @DataScope 注解定义 + DataScopeType 枚举 (1h)
- [ ] ST-037-02: DataScopeInterceptor 框架（拦截 + 注解获取） (2h)
- [ ] ST-037-03: SQL 改写逻辑（JSqlParser 追加 WHERE 条件） (3h)
- [ ] ST-037-04: 5 种 data_scope_type 的 org_id 集合获取逻辑 (2h)
- [ ] ST-037-05: 多角色合并逻辑 (1h)
- [ ] ST-037-06: MyBatisPlusConfig 注册 + 优先级配置 (0.5h)
- [ ] ST-037-07: 单元测试（Mock 场景验证 SQL 改写） (2.5h)

---

#### T-038: 数据权限集成到现有查询

| 属性 | 值 |
|------|-----|
| 状态 | 📋 |
| 优先级 | P0 |
| 估算工时 | 8h |
| 风险缓冲 | +2h |
| 总工时 | 10h |
| 角色 | Agent B |
| 关联故事 | US-M3-06 |
| Sprint | Sprint 2 |
| 依赖 | T-037, T-035-A（需要 EmployeeDAO 方法来标注） |

**任务描述**:
将 @DataScope 注解集成到实际的查询方法中。

**集成点**:
1. `EmployeeDAO.findByCondition()` — 添加 `@DataScope(orgIdColumn = "primary_org_id")`（员工按主组织过滤）
2. `EmployeeOrgDAO.findByOrgId()` — 需要额外处理（员工-组织关联表的 org_id 过滤）
3. 新增 `RoleDAO.findDataScopeTypeByUserId(Long userId)` — 查询用户所有角色的 data_scope_type
4. 新增 `RoleDAO.findCustomOrgIds(Long userId)` — 查询 CUSTOM 角色配置的 org_id 列表
5. 新增 `OrgUnitDAO.findDescendantIds(Long orgId)` — 查询组织单元的所有子孙 ID（ORG_TREE 用）

**RoleDAO 新增方法**:
```java
List<String> findDataScopeTypesByUserId(Long userId);
List<Long> findCustomOrgIdsByUserId(Long userId);
```

**验收标准**:
- [ ] `@DataScope` 标注在 EmployeeDAO 的查询方法上
- [ ] 拦截器能正确获取注解并改写 SQL
- [ ] RoleDAO 新增数据权限相关查询方法
- [ ] OrgUnitDAO 新增 `findDescendantIds` 方法
- [ ] M2 全量回归测试通过（383 tests 不减少）
- [ ] 新增 Service 层数据权限相关单元测试（≥ 4 个测试）

**技术备注**:
- `findDescendantIds` 先用应用层实现：查全部 org_unit → 构建树 → 收集子孙 ID
- 数据权限查询结果需要缓存（避免每次查询都走 DB），V1 先不缓存，后续在 Redis 层优化
- @DataScope 标注在 DAO 层方法上（而非 Service），因为拦截器操作的是 SQL

**子任务**:
- [ ] ST-038-01: RoleDAO 新增数据权限查询方法 + 实现 (2h)
- [ ] ST-038-02: OrgUnitDAO 新增 findDescendantIds (1.5h)
- [ ] ST-038-03: EmployeeDAO 标注 @DataScope (0.5h)
- [ ] ST-038-04: 集成验证 + M2 回归测试 (3h)
- [ ] ST-038-05: 数据权限 Service 层单元测试 (3h)

---

### Epic 5: 用户-员工绑定（EP-005）

**目标**: 实现 sys_user 与 hr_employee 的绑定/解绑
**关联需求**: US-M3-07
**预计跨度**: Sprint 3

#### T-039: 用户-员工绑定 API

| 属性 | 值 |
|------|-----|
| 状态 | 📋 |
| 优先级 | P1 |
| 估算工时 | 5h |
| 风险缓冲 | +1h |
| 总工时 | 6h |
| 角色 | Agent A |
| 关联故事 | US-M3-07 |
| Sprint | Sprint 3 |
| 依赖 | T-035-A（需要 EmployeeService 查找员工） |

**任务描述**:
扩展现有 UserController 和 ProfileController，支持用户-员工绑定。

**新增/修改的 API 端点**:

1. **UserController 新增**:
```
PUT /app/users/{id}/employee — 绑定员工（body: { employeeId: Long }）
DELETE /app/users/{id}/employee — 解绑员工
```

2. **ProfileController 扩展**:
```
GET /app/profile — 扩展返回 employee 信息（员工姓名、工号、主组织）
```

**DTO**:
```java
// system/user/dto/UserBindEmployeeDTO.java
public record UserBindEmployeeDTO(@NotNull Long employeeId) {}
```

**业务规则**:
- 绑定时校验：employee 存在且未删除；employee 未绑定其他用户（employee_id 唯一约束）
- 绑定时更新：`sys_user.employee_id = employeeId`
- 解绑时更新：`sys_user.employee_id = null`
- ProfileVO 扩展：新增 `EmployeeBriefVO` 字段（name, employeeNo, primaryOrgName）

**需修改的文件**:
- `UserService.java` + `UserServiceImpl.java` — 新增 `bindEmployee` / `unbindEmployee` 方法
- `UserController.java` — 新增 PUT/DELETE 端点
- `ProfileController.java` — 扩展 getProfile 返回员工信息
- `ProfileVO.java` — 新增 employeeBrief 字段
- `UserDAO.java` + `UserDAOImpl.java` — 如需新增查询方法

**验收标准**:
- [ ] `PUT /app/users/{id}/employee` 正确绑定员工
- [ ] `DELETE /app/users/{id}/employee` 正确解绑
- [ ] 绑定时校验 employee 存在性
- [ ] 绑定时校验 employee 未被其他用户绑定
- [ ] `GET /app/profile` 返回员工简要信息（已绑定时）
- [ ] `GET /app/profile` 员工字段为 null（未绑定时）
- [ ] MVC 测试覆盖绑定/解绑/Profile 扩展（≥ 4 个测试）

**子任务**:
- [ ] ST-039-01: UserBindEmployeeDTO + EmployeeBriefVO (0.5h)
- [ ] ST-039-02: UserService.bindEmployee / unbindEmployee (1.5h)
- [ ] ST-039-03: UserController 新增端点 (1h)
- [ ] ST-039-04: ProfileController/ProfileVO 扩展 (1h)
- [ ] ST-039-05: MVC 测试 (2h)

---

### Epic 6: 集成验证与质量保障（EP-006）

**目标**: 种子数据、集成测试、ArchUnit 扩展、全量回归
**关联需求**: US-M3-08
**预计跨度**: Sprint 3

#### T-040: V5 种子数据 + 集成测试

| 属性 | 值 |
|------|-----|
| 状态 | 📋 |
| 优先级 | P0 |
| 估算工时 | 8h |
| 风险缓冲 | +2h |
| 总工时 | 10h |
| 角色 | Agent B |
| 关联故事 | US-M3-08 |
| Sprint | Sprint 3 |
| 依赖 | T-036-A, T-036-B, T-038 |

**任务描述**:
创建 V5 种子数据迁移脚本和集成测试。

**V5 种子数据内容**:
```
-- LOCAL-ONLY 标注

1. 组织架构种子数据：
   - 总公司 (id=100, parent_id=0, code=HQ)
     - 技术部 (id=101, parent_id=100, code=TECH)
       - 前端组 (id=103, parent_id=101, code=FE)
       - 后端组 (id=104, parent_id=101, code=BE)
     - 产品部 (id=102, parent_id=100, code=PRODUCT)
     - 运营部 (id=105, parent_id=100, code=OPS)

2. 员工种子数据：
   - 张三 (id=201, employee_no=EMP001, 主组织=技术部)
   - 李四 (id=202, employee_no=EMP002, 主组织=产品部)
   - 王五 (id=203, employee_no=EMP003, 主组织=运营部)

3. 员工-组织关联：
   - 张三 → 技术部 (is_primary=1) + 前端组 (is_primary=0)
   - 李四 → 产品部 (is_primary=1)
   - 王五 → 运营部 (is_primary=1)

4. 绑定测试用户到员工：
   - admin (id=1) → 张三 (id=201)

5. 数据权限种子数据：
   - 技术部经理角色 (id=3, code=TECH_MANAGER, data_scope_type=ORG_TREE)
   - 技术部经理角色关联组织：技术部(101)
   - testuser (id=2) 分配 TECH_MANAGER 角色

6. 新增权限码：
   - organization:employee:view/create/update/delete
   - organization:orgunit:view/create/update/delete
```

**集成测试**:
1. `EmployeeCrudIntegrationTest` — 完整 CRUD 流程
2. `OrgUnitCrudIntegrationTest` — 完整 CRUD + 树形查询
3. `DataPermissionIntegrationTest` — 数据权限过滤验证
4. `UserEmployeeBindingIntegrationTest` — 绑定/解绑流程

**验收标准**:
- [ ] `V5__seed_employee_org_data.sql` 包含完整的种子数据
- [ ] 标注 `LOCAL-ONLY`
- [ ] `EmployeeCrudIntegrationTest` 覆盖创建/查询/更新/删除/分页（≥ 5 个测试）
- [ ] `OrgUnitCrudIntegrationTest` 覆盖 CRUD + 树查询 + 循环引用检测（≥ 5 个测试）
- [ ] `DataPermissionIntegrationTest` 覆盖 ALL/ORG_TREE/ORG_ONLY/SELF/CUSTOM（≥ 5 个测试）
- [ ] `UserEmployeeBindingIntegrationTest` 覆盖绑定/解绑/重复绑定（≥ 3 个测试）
- [ ] 所有集成测试继承 `IntegrationTest` 基类
- [ ] 集成测试使用 `@Transactional` 自动回滚

**子任务**:
- [ ] ST-040-01: V5 种子数据脚本 (1.5h)
- [ ] ST-040-02: EmployeeCrudIntegrationTest (2h)
- [ ] ST-040-03: OrgUnitCrudIntegrationTest (2h)
- [ ] ST-040-04: DataPermissionIntegrationTest (3h)
- [ ] ST-040-05: UserEmployeeBindingIntegrationTest (1.5h)

---

#### T-041: ArchUnit 规则扩展 + Controller 测试 + 全量回归

| 属性 | 值 |
|------|-----|
| 状态 | 📋 |
| 优先级 | P0 |
| 估算工时 | 6h |
| 风险缓冲 | +2h |
| 总工时 | 8h |
| 角色 | Agent B |
| 关联故事 | US-M3-08 |
| Sprint | Sprint 3 |
| 依赖 | T-036-A, T-036-B, T-038 |

**任务描述**:
扩展 ArchUnit 架构测试覆盖新模块，验证全量测试回归。

**ArchUnit 新增规则**:
```
1. organization 包下的 Controller 不依赖 DAO/Mapper/Entity
2. organization 包下的 Service 不依赖 Mapper
3. infra.persistence.datascope 包不依赖业务 Service（只依赖 DAO）
4. 新 Entity 继承 BaseEntity
5. 新 DTO/VO 使用 record
```

**集成测试基类更新**:
- `IntegrationTest` 的 static 初始化块需要新增 `employee_org`, `hr_employee`, `org_unit`, `sys_role_org` 的 DROP TABLE

**验收标准**:
- [ ] ArchUnit 测试覆盖 organization 包的分层约束（≥ 3 个新测试）
- [ ] ArchUnit 测试覆盖 datascope 包的依赖约束（≥ 1 个测试）
- [ ] `IntegrationTest` 基类的清理逻辑包含新表
- [ ] `mvn test` 全量通过，测试总数 ≥ 450
- [ ] M2 原有 383 个测试无回归
- [ ] 新模块测试（单元 + MVC + 集成）≥ 67 个

**子任务**:
- [ ] ST-041-01: ArchUnit 规则扩展 (2h)
- [ ] ST-041-02: IntegrationTest 基类更新 (0.5h)
- [ ] ST-041-03: 全量回归测试 + 修复 (3.5h)
- [ ] ST-041-04: 测试报告整理 (2h)

---

## 任务统计

### 按状态

| 状态 | 数量 | 总工时 |
|------|------|--------|
| 📋 待开始 | 12 | 120h |
| 🔵 计划中 | 0 | 0h |
| 🟡 进行中 | 0 | 0h |
| 🟢 已完成 | 0 | 0h |
| ⛔ 阻塞 | 0 | 0h |

### 按角色

| 角色 | 任务数 | 总工时 | 占比 |
|------|--------|--------|------|
| Agent A | 7 | 57h | 47.5% |
| Agent B | 5 | 63h | 52.5% |

### 按优先级

| 优先级 | 任务数 | 总工时 |
|--------|--------|--------|
| P0 | 11 | 114h |
| P1 | 1 | 6h |

### 按 Sprint

| Sprint | 任务数 | 总工时 |
|--------|--------|--------|
| Sprint 1 | 6 | 48h |
| Sprint 2 | 3 | 39h |
| Sprint 3 | 3 | 33h |

### 预估测试数量

| 测试类型 | 来源 | 预估数量 |
|---------|------|---------|
| Entity 单元测试 | T-031, T-032, T-033 | ~12 |
| MapStruct 单元测试 | T-031, T-032 | ~4 |
| DAO 单元测试 | T-034-A, T-034-B | ~20 |
| Service 单元测试 | T-035-A, T-035-B, T-038 | ~28 |
| Controller MVC 测试 | T-036-A, T-036-B, T-039 | ~15 |
| DataScope 单元测试 | T-037 | ~8 |
| 集成测试 | T-040 | ~18 |
| ArchUnit 测试 | T-041 | ~4 |
| **合计新增** | | **~109** |
| M2 现有测试 | | 383 |
| **M3 完成后总计** | | **≥ 450** |

## 版本历史

| 版本 | 日期 | 修改内容 | 修改人 |
|------|------|---------|--------|
| v1.0 | 2026-05-23 | 初始版本 | delivery-lead |
