-- V18: 用户/权限/菜单 管理接口的授权权限码，并关联管理员角色
-- 依赖 V3 的 ADMIN(role id=1) 与既有权限码(id 1-20)。
-- 仅新增 9 个权限码(21-29)，绑定 ADMIN。严禁修改 V1-V17。
-- 幂等保障：INSERT IGNORE + 显式 id(21-29) + sys_permission(uk_permission_code) / sys_role_permission(uk_role_permission) 唯一键
-- 注：V3 菜单种子曾引用孤立码 system:permission:view / system:menu:view（无 DB 行），本迁移补齐这两个码(22/29)，消除孤儿引用。

INSERT IGNORE INTO sys_permission
  (id, code, name, description, status, type, module, resource, action, sort_order, created_at, updated_at, created_by, updated_by, deleted, version)
VALUES
  (21, 'system:user:reset-password',  '重置用户密码', '重置用户登录密码',                 'ENABLED', 'BUTTON', 'system', 'user',     'reset-password', 21, NOW(), NOW(), 'system', 'system', 0, 1),
  (22, 'system:permission:view',      '查看权限',   '查看权限列表与按角色查询权限',       'ENABLED', 'BUTTON', 'system', 'permission','view',          22, NOW(), NOW(), 'system', 'system', 0, 1),
  (23, 'system:permission:create',    '创建权限',   '创建新权限',                       'ENABLED', 'BUTTON', 'system', 'permission','create',        23, NOW(), NOW(), 'system', 'system', 0, 1),
  (24, 'system:permission:update',    '编辑权限',   '编辑权限信息',                     'ENABLED', 'BUTTON', 'system', 'permission','update',        24, NOW(), NOW(), 'system', 'system', 0, 1),
  (25, 'system:permission:delete',    '删除权限',   '删除权限',                         'ENABLED', 'BUTTON', 'system', 'permission','delete',        25, NOW(), NOW(), 'system', 'system', 0, 1),
  (26, 'system:menu:create',          '创建菜单',   '创建新菜单',                       'ENABLED', 'BUTTON', 'system', 'menu',     'create',        26, NOW(), NOW(), 'system', 'system', 0, 1),
  (27, 'system:menu:update',          '编辑菜单',   '编辑菜单信息',                     'ENABLED', 'BUTTON', 'system', 'menu',     'update',        27, NOW(), NOW(), 'system', 'system', 0, 1),
  (28, 'system:menu:delete',          '删除菜单',   '删除菜单',                         'ENABLED', 'BUTTON', 'system', 'menu',     'delete',        28, NOW(), NOW(), 'system', 'system', 0, 1),
  (29, 'system:menu:view',            '查看菜单',   '查看菜单树与按角色查询菜单',       'ENABLED', 'BUTTON', 'system', 'menu',     'view',          29, NOW(), NOW(), 'system', 'system', 0, 1);

INSERT IGNORE INTO sys_role_permission
  (id, role_id, permission_id, created_at, created_by, updated_at, updated_by, deleted)
VALUES
  (21, 1, 21, NOW(), 'system', NOW(), 'system', 0),
  (22, 1, 22, NOW(), 'system', NOW(), 'system', 0),
  (23, 1, 23, NOW(), 'system', NOW(), 'system', 0),
  (24, 1, 24, NOW(), 'system', NOW(), 'system', 0),
  (25, 1, 25, NOW(), 'system', NOW(), 'system', 0),
  (26, 1, 26, NOW(), 'system', NOW(), 'system', 0),
  (27, 1, 27, NOW(), 'system', NOW(), 'system', 0),
  (28, 1, 28, NOW(), 'system', NOW(), 'system', 0),
  (29, 1, 29, NOW(), 'system', NOW(), 'system', 0);
