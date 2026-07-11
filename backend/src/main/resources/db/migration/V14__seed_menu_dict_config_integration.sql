-- V14: 种子数据 - 数据字典 / 配置项 / 集成管理 权限与菜单
-- LOCAL-ONLY: 仅用于本地开发环境
-- 依赖 V3 的系统管理父菜单(id=1)、管理员角色(id=1)
-- 既有最大 id：sys_permission=11，sys_menu=8，sys_role_permission=11，sys_role_menu=8

-- ============================================================
-- 1. 种子数据：七个新权限（12-18）
-- ============================================================
INSERT INTO sys_permission (id, code, name, description, status, type, module, resource, action, sort_order, created_at, updated_at, created_by, updated_by, deleted, version)
VALUES
(12, 'dict:view',          '字典查看', '查看数据字典',           'ENABLED', 'BUTTON', 'dict',       'dict',       'view',   12, NOW(), NOW(), 'system', 'system', 0, 1),
(13, 'dict:manage',        '字典管理', '管理数据字典',           'ENABLED', 'BUTTON', 'dict',       'dict',       'manage', 13, NOW(), NOW(), 'system', 'system', 0, 1),
(14, 'config:view',        '配置查看', '查看系统配置项',         'ENABLED', 'BUTTON', 'config',     'config',     'view',   14, NOW(), NOW(), 'system', 'system', 0, 1),
(15, 'config:manage',      '配置管理', '管理系统配置项',         'ENABLED', 'BUTTON', 'config',     'config',     'manage', 15, NOW(), NOW(), 'system', 'system', 0, 1),
(16, 'integration:source:view',    '外部身份源查看', '查看外部身份源',   'ENABLED', 'BUTTON', 'integration', 'source',     'view',   16, NOW(), NOW(), 'system', 'system', 0, 1),
(17, 'integration:principal:view', '外部主体查看',   '查看外部主体',     'ENABLED', 'BUTTON', 'integration', 'principal',  'view',   17, NOW(), NOW(), 'system', 'system', 0, 1),
(18, 'integration:link:view',     '身份匹配候选查看','查看身份匹配候选', 'ENABLED', 'BUTTON', 'integration', 'link',       'view',   18, NOW(), NOW(), 'system', 'system', 0, 1);

-- ============================================================
-- 2. 种子数据：六个新菜单(挂在"系统管理"父菜单 id=1 下)
--    type: 1-目录 2-菜单 3-按钮
-- ============================================================
INSERT INTO sys_menu (id, parent_id, name, path, component, icon, sort_order, type, permission_code, visible, status, created_at, updated_at, created_by, updated_by, deleted, version)
VALUES
(9,  1, '数据字典',   '/dict',                  'dict/index',                    'book',    8, 2, 'dict:view',           1, 1, NOW(), NOW(), 'system', 'system', 0, 1),
(10, 1, '配置项',     '/config-items',          'config/index',                  'sliders', 9, 2, 'config:view',         1, 1, NOW(), NOW(), 'system', 'system', 0, 1),
(11, 1, '集成管理',   '/integration',           '',                              'folder',  7, 1, 'integration:source:view', 1, 1, NOW(), NOW(), 'system', 'system', 0, 1),
(12, 11, '外部身份源', '/integration/sources',      'integration/sources/index',      'api',     1, 2, 'integration:source:view',    1, 1, NOW(), NOW(), 'system', 'system', 0, 1),
(13, 11, '外部主体',   '/integration/principals',   'integration/principals/index',   'user',    2, 2, 'integration:principal:view', 1, 1, NOW(), NOW(), 'system', 'system', 0, 1),
(14, 11, '身份匹配',   '/integration/link-candidates','integration/link-candidates/index','link',    3, 2, 'integration:link:view',     1, 1, NOW(), NOW(), 'system', 'system', 0, 1);

-- ============================================================
-- 3. 关联管理员角色(id=1)和七个新权限(12-18)
-- ============================================================
INSERT INTO sys_role_permission (id, role_id, permission_id, created_at, created_by, updated_at, updated_by, deleted)
VALUES
(12, 1, 12, NOW(), 'system', NOW(), 'system', 0),
(13, 1, 13, NOW(), 'system', NOW(), 'system', 0),
(14, 1, 14, NOW(), 'system', NOW(), 'system', 0),
(15, 1, 15, NOW(), 'system', NOW(), 'system', 0),
(16, 1, 16, NOW(), 'system', NOW(), 'system', 0),
(17, 1, 17, NOW(), 'system', NOW(), 'system', 0),
(18, 1, 18, NOW(), 'system', NOW(), 'system', 0);

-- ============================================================
-- 4. 关联管理员角色(id=1)和六个新菜单(9-14)
-- ============================================================
INSERT INTO sys_role_menu (id, role_id, menu_id, created_at, created_by, updated_at, updated_by, deleted)
VALUES
(9,  1, 9,  NOW(), 'system', NOW(), 'system', 0),
(10, 1, 10, NOW(), 'system', NOW(), 'system', 0),
(11, 1, 11, NOW(), 'system', NOW(), 'system', 0),
(12, 1, 12, NOW(), 'system', NOW(), 'system', 0),
(13, 1, 13, NOW(), 'system', NOW(), 'system', 0),
(14, 1, 14, NOW(), 'system', NOW(), 'system', 0);
