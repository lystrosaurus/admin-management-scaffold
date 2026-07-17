-- V19: 补齐零注解 /app 控制器的授权权限码，并关联管理员角色
-- 依赖 V3 的 ADMIN(role id=1) 与既有权限码(id 1-29)。
-- 仅新增 15 个权限码(30-44)，绑定 ADMIN。严禁修改 V1-V18。
-- 幂等保障：INSERT IGNORE + 显式 id(30-44) + sys_permission(uk_permission_code) / sys_role_permission(uk_role_permission) 唯一键
--
-- 权限码命名严格对齐控制器注解（LOCKED 决策：保持原始短命名，不附加 auth:/system:/organization: 前缀）。
-- 注解字符串是 PermissionAspect 实际校验的标识，故此处 code 必须与 @RequiresPermission(...) 完全一致：
--   external-account:view/bind/unbind    -> 三方账号（AuthExternalAccountController）
--   dashboard:view                        -> 仪表盘（DashboardController）
--   integration:source:manage / integration:principal:manage / integration:link:manage
--                                         -> 外部身份集成（view 码沿用 V14 的 integration:*:view，不再重复插入）
--   org:orgunit:view/create/update/delete -> 组织单元（OrgUnitController）
--   org:employee:view/create/update/delete -> 员工（EmployeeController）

INSERT IGNORE INTO sys_permission
  (id, code, name, description, status, type, module, resource, action, sort_order, created_at, updated_at, created_by, updated_by, deleted, version)
VALUES
  (30, 'external-account:view',   '查看三方账号', '查看三方账号绑定信息',            'ENABLED', 'BUTTON', 'external-account', 'external-account', 'view',   30, NOW(), NOW(), 'system', 'system', 0, 1),
  (31, 'external-account:bind',   '绑定三方账号', '绑定三方账号',                    'ENABLED', 'BUTTON', 'external-account', 'external-account', 'bind',   31, NOW(), NOW(), 'system', 'system', 0, 1),
  (32, 'external-account:unbind', '解绑三方账号', '解绑三方账号',                    'ENABLED', 'BUTTON', 'external-account', 'external-account', 'unbind', 32, NOW(), NOW(), 'system', 'system', 0, 1),
  (33, 'dashboard:view',          '查看仪表盘',   '查看系统统计数据',                'ENABLED', 'BUTTON', 'dashboard',        'dashboard',        'view',   33, NOW(), NOW(), 'system', 'system', 0, 1),
  (34, 'integration:source:manage',    '管理外部身份源', '创建/更新/删除外部身份源',  'ENABLED', 'BUTTON', 'integration', 'source',    'manage', 34, NOW(), NOW(), 'system', 'system', 0, 1),
  (35, 'integration:principal:manage', '管理外部主体',   '创建/更新/删除外部主体',    'ENABLED', 'BUTTON', 'integration', 'principal', 'manage', 35, NOW(), NOW(), 'system', 'system', 0, 1),
  (36, 'integration:link:manage',      '管理身份匹配',   '确认/拒绝身份匹配候选',      'ENABLED', 'BUTTON', 'integration', 'link',      'manage', 36, NOW(), NOW(), 'system', 'system', 0, 1),
  (37, 'org:orgunit:view',   '查看组织单元', '查看组织单元详情与列表', 'ENABLED', 'BUTTON', 'org', 'orgunit', 'view',   37, NOW(), NOW(), 'system', 'system', 0, 1),
  (38, 'org:orgunit:create', '创建组织单元', '创建组织单元',           'ENABLED', 'BUTTON', 'org', 'orgunit', 'create', 38, NOW(), NOW(), 'system', 'system', 0, 1),
  (39, 'org:orgunit:update', '更新组织单元', '更新组织单元信息',       'ENABLED', 'BUTTON', 'org', 'orgunit', 'update', 39, NOW(), NOW(), 'system', 'system', 0, 1),
  (40, 'org:orgunit:delete', '删除组织单元', '删除组织单元',           'ENABLED', 'BUTTON', 'org', 'orgunit', 'delete', 40, NOW(), NOW(), 'system', 'system', 0, 1),
  (41, 'org:employee:view',   '查看员工',     '查看员工详情与列表',     'ENABLED', 'BUTTON', 'org', 'employee', 'view',   41, NOW(), NOW(), 'system', 'system', 0, 1),
  (42, 'org:employee:create', '创建员工',     '创建员工',               'ENABLED', 'BUTTON', 'org', 'employee', 'create', 42, NOW(), NOW(), 'system', 'system', 0, 1),
  (43, 'org:employee:update', '更新员工',     '更新员工信息',           'ENABLED', 'BUTTON', 'org', 'employee', 'update', 43, NOW(), NOW(), 'system', 'system', 0, 1),
  (44, 'org:employee:delete', '删除员工',     '删除员工',               'ENABLED', 'BUTTON', 'org', 'employee', 'delete', 44, NOW(), NOW(), 'system', 'system', 0, 1);

INSERT IGNORE INTO sys_role_permission
  (id, role_id, permission_id, created_at, created_by, updated_at, updated_by, deleted)
VALUES
  (30, 1, 30, NOW(), 'system', NOW(), 'system', 0),
  (31, 1, 31, NOW(), 'system', NOW(), 'system', 0),
  (32, 1, 32, NOW(), 'system', NOW(), 'system', 0),
  (33, 1, 33, NOW(), 'system', NOW(), 'system', 0),
  (34, 1, 34, NOW(), 'system', NOW(), 'system', 0),
  (35, 1, 35, NOW(), 'system', NOW(), 'system', 0),
  (36, 1, 36, NOW(), 'system', NOW(), 'system', 0),
  (37, 1, 37, NOW(), 'system', NOW(), 'system', 0),
  (38, 1, 38, NOW(), 'system', NOW(), 'system', 0),
  (39, 1, 39, NOW(), 'system', NOW(), 'system', 0),
  (40, 1, 40, NOW(), 'system', NOW(), 'system', 0),
  (41, 1, 41, NOW(), 'system', NOW(), 'system', 0),
  (42, 1, 42, NOW(), 'system', NOW(), 'system', 0),
  (43, 1, 43, NOW(), 'system', NOW(), 'system', 0),
  (44, 1, 44, NOW(), 'system', NOW(), 'system', 0);
