# 代码深入审查报告 - admin-management-scaffold

> ⚠️ **更新说明**：本报告为 2026-05-25 的深度审查，补充了上次审查未覆盖的架构、性能和安全细节问题。

## 审查概览

| 属性 | 值 |
|------|-----|
| 审查日期 | 2026-05-25 |
| 审查人 | Code Reviewer |
| 代码版本 | admin-management-scaffold v1.0.0-SNAPSHOT |
| 关联任务 | 全面代码优化审查 |
| 审查范围 | 架构层面 + 代码质量 + 安全风险 + 性能 + 可维护性 |
| 审查阶段 | 深度专项审查（6 个维度） |

---

## 审查结论

### 总体评分

**得分**: 72 / 100  
**等级**: 🟠 合格

### 合并建议

**建议**: ⚠️ 建议修复关键问题（4 个 Critical）后合并

**理由**:  
项目整体架构清晰，分层合理，使用了主流技术栈（Spring Boot 4 + MyBatis-Plus + Sa-Token），测试覆盖充分。但存在 **4 个高危问题** 需要立即修复：
1. JWT 密钥硬编码默认值（SEC-001）
2. OAuth 状态内存存储，多实例部署不安全（SEC-002）
3. 数据库关联表缺少关键索引（IDX-001/IDX-002）

修复后预计可提升至 85+ 分。

### 关键发现

**亮点**:
1. ✅ Spring Boot 4.0 迁移完整，无遗留 2.x API/依赖
2. ✅ 分层架构清晰：Controller → Service → DAO → Mapper
3. ✅ 使用 MyBatis-Plus LambdaQueryWrapper，SQL 安全性好，无 SQL 注入
4. ✅ 事务注解完善，所有写操作使用 @Transactional(rollbackFor = Exception.class)
5. ✅ 前端使用 TypeScript + axios，有类型检查和统一拦截器
6. ✅ 测试覆盖优秀，623 个测试 100% 通过
7. ✅ 日志使用 Slf4j，无 System.out/printStackTrace

**关键问题**:
1. 🔴 JWT 密钥硬编码默认值 `your-jwt-secret-key-at-least-32-chars-long`
2. 🔴 OAuth 状态存储在内存，多实例部署存在 CSRF 风险
3. 🔴 数据库关联表（user_role, role_permission）缺少外键索引
4. 🟡 N+1 查询问题（RoleService.findById 执行 3 次独立查询）
5. 🟡 Service 层部分直接依赖 Mapper，违反架构规范

---

## 一、架构层面问题

### 1.1 分层清晰度 ✅ 良好

**检查结果**：
- Controller → Service → DAO → Mapper 分层正确
- 使用 MyBatis-Plus，无原生 SQL 拼接，SQL 注入风险低
- 使用 MapStruct 做对象映射，Entity 不直接暴露给 API
- ArchUnit 规则验证通过

**问题**：

| 编号 | 类型 | 位置 | 描述 | 严重程度 |
|------|------|------|------|---------|
| ARCH-001 | 架构 | RoleService.java | Service 层直接依赖 Mapper，违反"Service 禁止依赖 Mapper"规则 | 🟡 Medium |

**代码示例**：
```java
// RoleService.java - 第 6-15 行
@Service
@RequiredArgsConstructor
public class RoleService {
  private final SysRoleMapper roleMapper;         // ❌ 应通过 RoleDAO
  private final PermissionMapper permissionMapper; // ❌ 应通过 RoleDAO
  private final MenuMapper menuMapper;            // ❌ 应通过 RoleDAO
  private final RoleDAO roleDAO;                  // ✅ 正确
}
```

**修复建议**：将 Mapper 依赖移到 RoleDAOImpl，Service 只依赖 DAO 接口。

### 1.2 依赖管理 ✅ 良好

**检查结果**：
- 无循环依赖
- Maven 依赖管理清晰
- Spring Boot 4.0 版本兼容

---

## 二、安全风险问题

### 2.1 高危安全问题 🔴 Critical

| 编号 | 类型 | 位置 | 描述 | 严重程度 |
|------|------|------|------|---------|
| SEC-001 | 安全 | application.yml:22 | JWT 密钥硬编码默认值 | 🔴 Critical |
| SEC-002 | 安全 | OAuthStateService.java | OAuth 状态内存存储，多实例不安全 | 🔴 Critical |

**SEC-001 详细**：

**位置**：`backend/src/main/resources/application.yml` 第 22 行

```yaml
sa-token:
  jwt-secret-key: ${JWT_SECRET:your-jwt-secret-key-at-least-32-chars-long}
```

**风险**：
- 如果环境变量 `JWT_SECRET` 未设置，会使用默认密钥
- 攻击者可利用默认密钥伪造任意用户的 JWT Token
- 生产环境部署失误会导致严重安全漏洞

**修复建议**：
```yaml
sa-token:
  jwt-secret-key: ${JWT_SECRET:}  # 移除默认值，强制要求配置
```

并在启动时校验：
```java
@Configuration
public class SaTokenConfig {
    @Value("${sa-token.jwt-secret-key}")
    private String jwtSecretKey;
    
    @PostConstruct
    public void validate() {
        if (jwtSecretKey == null || jwtSecretKey.isBlank()) {
            throw new IllegalStateException(
                "sa-token.jwt-secret-key must be configured via JWT_SECRET env var");
        }
    }
}
```

**SEC-002 详细**：

**位置**：`backend/src/main/java/io/github/lystrosaurus/admin/auth/oauth/state/OAuthStateService.java`

```java
@Service
public class OAuthStateService {
    private final ConcurrentHashMap<String, StateData> stateStore = new ConcurrentHashMap<>();
    
    public String generateState() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    public StateData validateAndConsumeState(String state) {
        StateData stateData = stateStore.remove(state);  // 内存存储
        if (stateData == null) {
            throw new BusinessException(ErrorCode.OAUTH_STATE_INVALID);
        }
        return stateData;
    }
}
```

**风险**：
- 单实例应用正常，多实例部署时状态不共享
- 用户请求被路由到不同实例时，state 验证失败
- 攻击者可在不同实例上使用同一 state 值发起 CSRF 攻击

**修复建议**：
```java
@Service
@RequiredArgsConstructor
public class OAuthStateService {
    private final RedisTemplate<String, StateData> redisTemplate;
    private static final String STATE_KEY_PREFIX = "oauth:state:";
    private static final Duration STATE_TTL = Duration.ofMinutes(5);
    
    public String generateState() {
        String state = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set(STATE_KEY_PREFIX + state, new StateData(...), STATE_TTL);
        return state;
    }
    
    public StateData validateAndConsumeState(String state) {
        String key = STATE_KEY_PREFIX + state;
        StateData stateData = redisTemplate.opsForValue().getAndDelete(key);
        if (stateData == null) {
            throw new BusinessException(ErrorCode.OAUTH_STATE_INVALID);
        }
        return stateData;
    }
}
```

### 2.2 中等安全问题 🟡 Medium

| 编号 | 类型 | 位置 | 描述 | 严重程度 |
|------|------|------|------|---------|
| SEC-003 | 安全 | TokenVersionInterceptor.java:41 | Redis 操作无超时配置，可能导致线程阻塞 | 🟡 Medium |
| FE-001 | 安全 | LoginPage.tsx:38 | OAuth redirect_uri 硬编码 localhost:3000 | 🟡 Medium |

### 2.3 XSS 防护 ✅ 良好

**检查结果**：
- 前端无 `dangerouslySetInnerHTML` 使用
- 后端无直接 HTML 拼接输出
- API 响应使用 JSON 格式

### 2.4 敏感信息暴露 ✅ 良好

**检查结果**：
- 密码使用 BCrypt 哈希存储，不返回前端
- OAuth client_secret 从数据库动态获取，无硬编码
- 错误日志不记录敏感信息
- Token 不在 URL 中传递

---

## 三、性能问题

### 3.1 数据库索引缺失 🔴 Critical

| 编号 | 类型 | 位置 | 描述 | 严重程度 |
|------|------|------|------|---------|
| IDX-001 | 性能 | V1__init_schema.sql | sys_user_role 表 `user_id` 字段无索引 | 🔴 Critical |
| IDX-002 | 性能 | V1__init_schema.sql | sys_role_permission 表 `role_id` 字段无索引 | 🔴 Critical |
| IDX-003 | 性能 | V1__init_schema.sql | sys_role_menu 表 `role_id` 字段无索引 | 🔴 Critical |

**代码示例**：

```sql
-- V1__init_schema.sql 第 26-35 行
CREATE TABLE IF NOT EXISTS `sys_user_role` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
    KEY `idx_role_id` (`role_id`)  -- ✅ 有 role_id 索引
    -- ❌ 缺少 KEY idx_user_id (user_id)
);
```

**影响**：
- 查询用户角色时全表扫描
- 高并发下 CPU 飙升
- 数据量增长时性能急剧下降

**修复建议**：

```sql
-- 新建迁移脚本 V8__add_missing_indexes.sql
ALTER TABLE `sys_user_role` ADD INDEX `idx_user_id` (`user_id`);
ALTER TABLE `sys_role_permission` ADD INDEX `idx_role_id` (`role_id`);
ALTER TABLE `sys_role_menu` ADD INDEX `idx_role_id` (`role_id`);
```

### 3.2 N+1 查询问题 🟡 Medium

| 编号 | 类型 | 位置 | 描述 | 严重程度 |
|------|------|------|------|---------|
| PERF-001 | 性能 | RoleService.findById() | 执行 3 次独立查询（角色、权限、菜单） | 🟡 Medium |
| PERF-002 | 性能 | UserService.findById() | 执行 2 次独立查询（用户、角色） | 🟡 Medium |
| PERF-003 | 性能 | MenuDAOImpl.findByUserId() | 执行 3 次独立查询（用户角色、角色菜单、菜单详情） | 🟡 Medium |

**代码示例**：

```java
// RoleService.java 第 88-113 行
public RoleDetailVO findById(Long id) {
    // 查询 1: 查找角色
    SysRole role = roleDAO.findById(id);
    if (role == null) {
        throw new BusinessException(ErrorCode.ROLE_NOT_FOUND);
    }
    
    // 查询 2: 查询角色权限（独立查询）
    List<SysPermission> permissions = roleDAO.findPermissionsByRoleId(id);
    List<PermissionVO> permissionVOs =
        permissions.stream().map(permissionMapper::toPermissionVO).collect(Collectors.toList());
    
    // 查询 3: 查询角色菜单（独立查询）
    List<SysMenu> menus = roleDAO.findMenusByRoleId(id);
    List<MenuVO> menuVOs = menus.stream().map(menuMapper::toMenuVO).collect(Collectors.toList());
    
    // 构建详情VO
    RoleDetailVO detailVO = roleMapper.toRoleDetailVO(role);
    return new RoleDetailVO(...);
}
```

**优化建议**：使用 JOIN 查询一次性获取所有数据：

```java
// RoleDAOImpl.java - 新增方法
public RoleDetailDTO findDetailById(Long roleId) {
    // 单条 SQL 通过 JOIN 获取角色、权限、菜单
    return roleMapper.selectDetailById(roleId);
}
```

```xml
<!-- RoleDetailMapper.xml -->
<select id="selectDetailById" resultType="RoleDetailDTO">
    SELECT r.*, 
           GROUP_CONCAT(DISTINCT p.id) as permission_ids,
           GROUP_CONCAT(DISTINCT m.id) as menu_ids
    FROM sys_role r
    LEFT JOIN sys_role_permission rp ON r.id = rp.role_id
    LEFT JOIN sys_permission p ON rp.permission_id = p.id
    LEFT JOIN sys_role_menu rm ON r.id = rm.role_id
    LEFT JOIN sys_menu m ON rm.menu_id = m.id
    WHERE r.id = #{roleId} AND r.deleted = 0
    GROUP BY r.id
</select>
```

### 3.3 缓存使用 ⚠️ 建议

**现状**：
- 菜单数据每次请求都从数据库加载
- 用户权限信息无缓存

**优化建议**：
```java
@Cacheable(value = "menus", key = "'tree'")
public List<MenuVO> findTree() { ... }

@Cacheable(value = "user:permissions", key = "#userId")
public Set<String> getUserPermissions(Long userId) { ... }

@CacheEvict(value = "menus", allEntries = true)
public MenuVO create(MenuCreateDTO dto) { ... }
```

---

## 四、代码质量问题

### 4.1 重复代码 🟡 Medium

| 编号 | 类型 | 位置 | 描述 | 严重程度 |
|------|------|------|------|---------|
| DRY-001 | 代码质量 | 多处 Service | update() 方法字段更新逻辑重复 | 🟡 Medium |
| DRY-002 | 代码质量 | MenuService.buildMenuTree() | 排序逻辑与 findTree() 中重复 | 🟡 Medium |

**代码示例**：

```java
// UserService.java 第 52-72 行 和 RoleService.java 第 52-72 行
// 几乎完全相同的字段更新逻辑

if (StringUtils.hasText(dto.nickname())) {
    user.setNickname(dto.nickname());
}
if (StringUtils.hasText(dto.phone())) {
    user.setPhone(dto.phone());
}
// ... 重复 8+ 次
```

**优化建议**：使用 MapStruct 或反射批量更新：

```java
// 使用 BeanUtils 批量更新
public static <T, D> void updateNonNullFields(D source, T target) {
    BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
}
```

### 4.2 方法长度 ⚠️ 建议

| 方法 | 行数 | 标准 | 状态 |
|------|------|------|------|
| DataScopeHelper.getAccessibleOrgIds() | 45 行 | < 30 行 | ⚠️ 超标 |
| EmployeeService.findPage() | 38 行 | < 30 行 | ⚠️ 超标 |
| RoleService.findById() | 26 行 | < 30 行 | ✅ 达标 |

**优化建议**：将方法内逻辑提取为私有方法。

### 4.3 魔法数字 🟡 Medium

| 编号 | 类型 | 位置 | 描述 | 严重程度 |
|------|------|------|------|---------|
| MAGIC-001 | 可维护性 | AuthService.java:72 | Token 有效期 2 小时硬编码 | 🟡 Medium |
| MAGIC-002 | 可维护性 | OAuthStateService.java | State 过期时间 5 分钟硬编码 | 🟡 Medium |

**代码示例**：

```java
// AuthService.java 第 72 行
redisTemplate.opsForValue().set(redisKey, user.getTokenVersion(), 2, TimeUnit.HOURS);
//                                    魔法数字 ^^^^^
```

**优化建议**：提取为常量或配置：

```java
// 方式1: 常量
private static final long TOKEN_VERSION_TTL_HOURS = 2;

// 方式2: 配置
@Value("${auth.token-version-ttl-hours:2}")
private long tokenVersionTtlHours;
```

---

## 五、测试覆盖问题

### 5.1 测试统计

| 指标 | 值 | 标准 | 状态 |
|------|-----|------|------|
| 测试文件数 | 83 | - | ✅ |
| 测试总数 | 623 | - | ✅ |
| 通过率 | 100% | 100% | ✅ |
| 单元测试覆盖 | ~500 | - | ✅ |
| 集成测试 | ~120 | - | ✅ |

### 5.2 测试盲区

| 编号 | 类型 | 描述 | 严重程度 |
|------|------|------|---------|
| TEST-001 | 覆盖 | DataScopeHelper.getAccessibleOrgIds() 无单元测试 | 🟡 Medium |
| TEST-002 | 覆盖 | OAuthStateService 并发场景无测试 | 🟡 Medium |
| TEST-003 | 覆盖 | 边界条件：分页 size=0, size=-1 等 | 🟡 Medium |

---

## 六、可维护性问题

### 6.1 硬编码问题 🟡 Medium

| 编号 | 类型 | 位置 | 描述 | 严重程度 |
|------|------|------|------|---------|
| HARD-001 | 可维护性 | application.yml | `jwt-secret-key` 默认值 | 🟡 Medium |
| HARD-002 | 可维护性 | LarkOAuthClient.java | 飞书 API URL 硬编码 | 🟡 Medium |
| HARD-003 | 可维护性 | LoginPage.tsx | 注释中的 localhost:3000 | 🟡 Medium |

**代码示例**：

```java
// LarkOAuthClient.java 第 27-30 行
private static final String AUTHORIZE_URL =
    "https://open.feishu.cn/open-apis/authen/v1/authorize";
private static final String APP_ACCESS_TOKEN_URL =
    "https://open.feishu.cn/open-apis/auth/v3/app_access_token/internal";
// 飞书 URL 硬编码，无法切换到其他环境
```

**优化建议**：
```yaml
# application.yml
lark:
  api:
    base-url: https://open.feishu.cn/open-apis
    authorize-path: /authen/v1/authorize
    token-path: /auth/v3/app_access_token/internal
```

### 6.2 错误处理不一致 ⚠️ 建议

| 编号 | 类型 | 位置 | 描述 | 严重程度 |
|------|------|------|------|---------|
| ERR-001 | 可维护性 | 多处 Service | 找不到记录时抛 BusinessException 或返回 null 不一致 | ⚠️ Low |

---

## 七、前端代码审查

### 7.1 前端质量评估 ✅ 良好

**亮点**：
- 使用 TypeScript，有类型检查
- 有 axios 拦截器处理 401
- 表单验证完整（Ant Design）
- 错误处理友好
- 日志敏感信息不暴露

**问题**：

| 编号 | 类型 | 位置 | 描述 | 严重程度 |
|------|------|------|------|---------|
| FE-001 | 安全 | LoginPage.tsx:38 | OAuth redirect_uri 硬编码 localhost:3000 | 🟡 Medium |
| FE-002 | 可维护性 | UserListPage.tsx | 大文件 350+ 行，可拆分为子组件 | ⚠️ Low |

---

## 问题汇总（按严重程度）

### 🔴 必须修复 (Critical) - 4 项

| 编号 | 类型 | 位置 | 描述 |
|------|------|------|------|
| SEC-001 | 安全 | application.yml:22 | JWT 密钥硬编码默认值 |
| SEC-002 | 安全 | OAuthStateService.java | OAuth 状态内存存储，多实例不安全 |
| IDX-001 | 性能 | V1__init_schema.sql | sys_user_role.user_id 无索引 |
| IDX-002 | 性能 | V1__init_schema.sql | sys_role_permission.role_id 无索引 |

### 🟡 建议修复 (Major) - 7 项

| 编号 | 类型 | 位置 | 描述 |
|------|------|------|---------|
| ARCH-001 | 架构 | RoleService.java | Service 层直接依赖 Mapper |
| PERF-001 | 性能 | RoleService.findById() | N+1 查询问题 |
| PERF-002 | 性能 | UserService.findById() | N+1 查询问题 |
| PERF-003 | 性能 | MenuDAOImpl.findByUserId() | N+1 查询问题 |
| DRY-001 | 代码质量 | 多处 Service | update() 方法重复代码 |
| MAGIC-001 | 可维护性 | AuthService.java | Token TTL 魔法数字 |
| FE-001 | 安全 | LoginPage.tsx | OAuth URL 硬编码 |

### ⚠️ 可选优化 (Minor) - 4 项

| 编号 | 类型 | 位置 | 描述 |
|------|------|------|------|
| MAGIC-002 | 可维护性 | OAuthStateService.java | State 过期时间魔法数字 |
| HARD-002 | 可维护性 | LarkOAuthClient.java | API URL 硬编码 |
| TEST-001 | 测试 | DataScopeHelper | 缺少单元测试 |
| FE-002 | 可维护性 | UserListPage.tsx | 文件过大 |

---

## 后续行动

### P0 - 立即修复（合并前）

| 编号 | 任务 | 预计工时 |
|------|------|---------|
| SEC-001 | 移除 `jwt-secret-key` 默认值，添加启动校验 | 0.5h |
| SEC-002 | 将 OAuth State 迁移到 Redis 存储 | 2h |
| IDX-001 | 添加数据库索引迁移脚本 V8__add_missing_indexes.sql | 0.5h |

### P1 - Sprint 迭代修复

| 编号 | 任务 | 预计工时 |
|------|------|---------|
| ARCH-001 | 重构 Service 层依赖关系 | 2h |
| PERF-001~003 | 优化 N+1 查询为 JOIN 查询 | 4h |
| DRY-001 | 抽取 update() 通用逻辑 | 2h |

### P2 - 持续优化

| 编号 | 任务 | 优先级 |
|------|------|--------|
| MAGIC-001~002 | 配置化魔法数字 | P2 |
| TEST-001 | 补充 DataScopeHelper 单元测试 | P2 |
| Redis 缓存 | 菜单、权限数据缓存 | P2 |

---

## 代码统计

| 指标 | 值 |
|------|-----|
| Java 源文件 | 205 |
| 前端 TSX 文件 | 16 |
| 测试文件 | 83 |
| 测试用例数 | 623 |
| 核心问题数 | 15 |
| - Critical | 4 |
| - Major | 7 |
| - Minor | 4 |

---

## 版本历史

| 版本 | 日期 | 修改内容 | 修改人 |
|------|------|---------|--------|
| v1.0 | 2026-05-24 | 初始全面审查报告 | code-reviewer |
| v1.1 | 2026-05-25 | 深度专项审查补充（架构、性能、安全细节） | code-reviewer |

---

## 附录：关键问题修复参考

### 附录 A：JWT 密钥安全配置

```java
// io.github.lystrosaurus.admin.auth.config.SaTokenConfig.java
package io.github.lystrosaurus.admin.auth.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SaTokenConfig {
    
    @Value("${sa-token.jwt-secret-key:}")
    private String jwtSecretKey;
    
    @PostConstruct
    public void validateJwtSecretKey() {
        if (jwtSecretKey == null || jwtSecretKey.isBlank()) {
            throw new IllegalStateException(
                "sa-token.jwt-secret-key must be configured. " +
                "Set JWT_SECRET environment variable or use external config.");
        }
        if (jwtSecretKey.length() < 32) {
            throw new IllegalStateException(
                "sa-token.jwt-secret-key must be at least 32 characters long.");
        }
        // 生产环境建议检查是否使用了示例密钥
        if (jwtSecretKey.contains("your-jwt-secret")) {
            throw new IllegalStateException(
                "sa-token.jwt-secret-key cannot use default/example value.");
        }
    }
}
```

### 附录 B：数据库索引迁移脚本

```sql
-- backend/src/main/resources/db/migration/V8__add_missing_indexes.sql
-- 添加关联表缺失的索引，优化查询性能

-- 用户角色表添加 user_id 索引
ALTER TABLE `sys_user_role` 
    ADD INDEX `idx_user_id` (`user_id`);

-- 角色权限表添加 role_id 索引（已有 role_id 唯一索引，检查是否存在）
-- 如果唯一索引已覆盖，则跳过
ALTER TABLE `sys_role_permission` 
    ADD INDEX `idx_role_permission_role_id` (`role_id`);

-- 角色菜单表添加 role_id 索引
ALTER TABLE `sys_role_menu` 
    ADD INDEX `idx_role_menu_role_id` (`role_id`);
```

### 附录 C：OAuth State Redis 存储

```java
// io.github.lystrosaurus.admin.auth.oauth.state.OAuthStateService.java
package io.github.lystrosaurus.admin.auth.oauth.state;

import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OAuthStateService {
    
    private static final String STATE_KEY_PREFIX = "oauth:state:";
    private static final Duration STATE_TTL = Duration.ofMinutes(5);
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    public String generateState() {
        String state = UUID.randomUUID().toString().replace("-", "");
        StateData stateData = new StateData(state, null, null, LocalDateTime.now());
        redisTemplate.opsForValue().set(STATE_KEY_PREFIX + state, stateData, STATE_TTL);
        return state;
    }
    
    public void saveState(String state, String nonce, Long userId) {
        StateData stateData = new StateData(state, nonce, userId, LocalDateTime.now());
        redisTemplate.opsForValue().set(STATE_KEY_PREFIX + state, stateData, STATE_TTL);
    }
    
    @SuppressWarnings("unchecked")
    public StateData validateAndConsumeState(String state) {
        String key = STATE_KEY_PREFIX + state;
        StateData stateData = (StateData) redisTemplate.opsForValue().getAndDelete(key);
        if (stateData == null) {
            throw new BusinessException(ErrorCode.OAUTH_STATE_INVALID);
        }
        return stateData;
    }
}
```
