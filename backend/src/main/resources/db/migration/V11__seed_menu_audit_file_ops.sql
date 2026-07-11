-- V11: 种子数据 - 审计日志 / 文件管理 / 系统状态 菜单与权限
-- LOCAL-ONLY: 仅用于本地开发环境
-- 依赖 V3 的系统管理父菜单(id=1)、管理员角色(id=1)

-- ============================================================
-- 1. 种子数据：三个新权限
-- ============================================================
INSERT INTO sys_permission (id, code, name, description, status, type, module, resource, action, sort_order, created_at, updated_at, created_by, updated_by, deleted, version)
VALUES
(9,  'audit:log:view', '审计日志查看', '查看登录与操作审计日志', 'ENABLED', 'BUTTON', 'audit', 'log',      'view',   9, NOW(), NOW(), 'system', 'system', 0, 1),
(10, 'file:manage',    '文件管理',     '管理上传的文件',         'ENABLED', 'BUTTON', 'file',  'file',     'manage', 10, NOW(), NOW(), 'system', 'system', 0, 1),
(11, 'ops:view',       '系统状态查看', '查看系统健康与状态',     'ENABLED', 'BUTTON', 'ops',   'ops',      'view',   11, NOW(), NOW(), 'system', 'system', 0, 1);

-- ============================================================
-- 2. 种子数据：三个新菜单(挂在"系统管理"父菜单 id=1 下)
-- ============================================================
INSERT INTO sys_menu (id, parent_id, name, path, component, icon, sort_order, type, permission_code, visible, status, created_at, updated_at, created_by, updated_by, deleted, version)
VALUES
(6, 1, '审计日志', '/audit', 'audit/index', 'file-search', 5, 2, 'audit:log:view', 1, 1, NOW(), NOW(), 'system', 'system', 0, 1),
(7, 1, '文件管理', '/files', 'files/index', 'upload',       6, 2, 'file:manage',    1, 1, NOW(), NOW(), 'system', 'system', 0, 1),
(8, 1, '系统状态', '/ops',   'ops/index',   'dashboard',    7, 2, 'ops:view',       1, 1, NOW(), NOW(), 'system', 'system', 0, 1);

-- ============================================================
-- 3. 关联管理员角色(id=1)和三个新权限
-- ============================================================
INSERT INTO sys_role_permission (id, role_id, permission_id, created_at, created_by, updated_at, updated_by, deleted)
VALUES
(9,  1, 9,  NOW(), 'system', NOW(), 'system', 0),
(10, 1, 10, NOW(), 'system', NOW(), 'system', 0),
(11, 1, 11, NOW(), 'system', NOW(), 'system', 0);

-- ============================================================
-- 4. 关联管理员角色(id=1)和三个新菜单
-- ============================================================
INSERT INTO sys_role_menu (id, role_id, menu_id, created_at, created_by, updated_at, updated_by, deleted)
VALUES
(6, 1, 6, NOW(), 'system', NOW(), 'system', 0),
(7, 1, 7, NOW(), 'system', NOW(), 'system', 0),
(8, 1, 8, NOW(), 'system', NOW(), 'system', 0);
