-- V3: 修复列名不匹配 + 种子数据
-- LOCAL-ONLY: 仅用于本地开发环境
-- 修复 V1 Schema 与 Entity 字段名之间的不一致（MyBatis-Plus 驼峰映射）

-- ============================================================
-- 1. 修复 sys_user 列名
-- ============================================================
-- status: TINYINT → VARCHAR(32)，映射 0→DISABLED, 1→ENABLED
ALTER TABLE `sys_user` MODIFY COLUMN `status` VARCHAR(32) NOT NULL DEFAULT 'ENABLED' COMMENT '状态(ENABLED/DISABLED/LOCKED)';
UPDATE `sys_user` SET `status` = CASE WHEN `status` = '0' THEN 'DISABLED' WHEN `status` = '1' THEN 'ENABLED' ELSE `status` END WHERE `status` IN ('0', '1');
-- password → password_hash
ALTER TABLE `sys_user` CHANGE COLUMN `password` `password_hash` VARCHAR(100) NOT NULL COMMENT '密码哈希';
-- avatar → avatar_file_id（类型不变仍为 VARCHAR，后续可通过新迁移改为 BIGINT）
ALTER TABLE `sys_user` CHANGE COLUMN `avatar` `avatar_file_id` VARCHAR(255) COMMENT '头像文件ID';
-- 添加缺失列
ALTER TABLE `sys_user` ADD COLUMN `token_version` INT DEFAULT 1 COMMENT 'token版本号' AFTER `status`;
ALTER TABLE `sys_user` ADD COLUMN `last_login_at` DATETIME COMMENT '最后登录时间' AFTER `token_version`;
ALTER TABLE `sys_user` ADD COLUMN `last_login_ip` VARCHAR(50) COMMENT '最后登录IP' AFTER `last_login_at`;
ALTER TABLE `sys_user` ADD COLUMN `employee_id` BIGINT COMMENT '绑定员工ID' AFTER `avatar_file_id`;

-- ============================================================
-- 1b. 修复 sys_user_role 缺失列
-- ============================================================
ALTER TABLE `sys_user_role` ADD COLUMN `version` INT DEFAULT 1 COMMENT '版本号' AFTER `deleted`;

-- ============================================================
-- 1c. 修复 sys_role_permission 缺失列
-- ============================================================
ALTER TABLE `sys_role_permission` ADD COLUMN `version` INT DEFAULT 1 COMMENT '版本号' AFTER `deleted`;

-- ============================================================
-- 1d. 修复 sys_role_menu 缺失列
-- ============================================================
ALTER TABLE `sys_role_menu` ADD COLUMN `version` INT DEFAULT 1 COMMENT '版本号' AFTER `deleted`;

-- ============================================================
-- 2. 修复 sys_role 列名
-- ============================================================
-- status: TINYINT → VARCHAR(32)
ALTER TABLE `sys_role` MODIFY COLUMN `status` VARCHAR(32) NOT NULL DEFAULT 'ENABLED' COMMENT '状态(ENABLED/DISABLED)';
UPDATE `sys_role` SET `status` = CASE WHEN `status` = '0' THEN 'DISABLED' WHEN `status` = '1' THEN 'ENABLED' ELSE `status` END WHERE `status` IN ('0', '1');
-- role_name → name, role_code → code
ALTER TABLE `sys_role` CHANGE COLUMN `role_name` `name` VARCHAR(50) NOT NULL COMMENT '角色名称';
ALTER TABLE `sys_role` CHANGE COLUMN `role_code` `code` VARCHAR(50) NOT NULL COMMENT '角色编码';
-- 添加缺失列
ALTER TABLE `sys_role` ADD COLUMN `sort_order` INT DEFAULT 0 COMMENT '排序号' AFTER `description`;
ALTER TABLE `sys_role` ADD COLUMN `data_scope_type` VARCHAR(32) DEFAULT 'SELF' COMMENT '数据权限范围' AFTER `sort_order`;

-- ============================================================
-- 3. 修复 sys_permission 列名和类型
-- ============================================================
-- status: TINYINT → VARCHAR(32)
ALTER TABLE `sys_permission` MODIFY COLUMN `status` VARCHAR(32) NOT NULL DEFAULT 'ENABLED' COMMENT '状态';
UPDATE `sys_permission` SET `status` = CASE WHEN `status` = '0' THEN 'DISABLED' WHEN `status` = '1' THEN 'ENABLED' ELSE `status` END WHERE `status` IN ('0', '1');
-- permission_name → name, permission_code → code
ALTER TABLE `sys_permission` CHANGE COLUMN `permission_name` `name` VARCHAR(50) NOT NULL COMMENT '权限名称';
ALTER TABLE `sys_permission` CHANGE COLUMN `permission_code` `code` VARCHAR(100) NOT NULL COMMENT '权限编码';
-- 添加缺失列
ALTER TABLE `sys_permission` ADD COLUMN `sort_order` INT DEFAULT 0 COMMENT '排序号' AFTER `status`;

-- ============================================================
-- 4. 种子数据：管理员角色
-- ============================================================
INSERT INTO sys_role (id, code, name, description, status, data_scope_type, sort_order, created_at, updated_at, created_by, updated_by, deleted, version)
VALUES (1, 'ADMIN', '超级管理员', '系统超级管理员，拥有所有权限', 'ENABLED', 'ALL', 1, NOW(), NOW(), 'system', 'system', 0, 1);

-- ============================================================
-- 5. 种子数据：普通用户角色
-- ============================================================
INSERT INTO sys_role (id, code, name, description, status, data_scope_type, sort_order, created_at, updated_at, created_by, updated_by, deleted, version)
VALUES (2, 'USER', '普通用户', '普通用户，拥有基本查看权限', 'ENABLED', 'SELF', 2, NOW(), NOW(), 'system', 'system', 0, 1);

-- ============================================================
-- 6. 种子数据：管理员用户（密码: admin123，BCrypt 加密）
-- ============================================================
INSERT INTO sys_user (id, username, password_hash, nickname, status, created_at, updated_at, created_by, updated_by, deleted, version)
VALUES (1, 'admin', '$2a$10$P6lqkvsCYZINKMUv0gjW7ON8IJB8UM71Rk4i9oAQxaHjSoXZ5TCLm', '超级管理员', 'ENABLED', NOW(), NOW(), 'system', 'system', 0, 1);

-- ============================================================
-- 7. 种子数据：测试用户（密码: test123，BCrypt 加密）
-- ============================================================
INSERT INTO sys_user (id, username, password_hash, nickname, status, created_at, updated_at, created_by, updated_by, deleted, version)
VALUES (2, 'testuser', '$2a$10$P6lqkvsCYZINKMUv0gjW7ON8IJB8UM71Rk4i9oAQxaHjSoXZ5TCLm', '测试用户', 'ENABLED', NOW(), NOW(), 'system', 'system', 0, 1);

-- ============================================================
-- 8. 关联管理员用户和角色
-- ============================================================
INSERT INTO sys_user_role (id, user_id, role_id, created_at, created_by, updated_at, updated_by, deleted)
VALUES (1, 1, 1, NOW(), 'system', NOW(), 'system', 0);

-- ============================================================
-- 9. 种子数据：基础权限
-- ============================================================
INSERT INTO sys_permission (id, code, name, description, status, type, module, resource, action, sort_order, created_at, updated_at, created_by, updated_by, deleted, version)
VALUES
(1, 'system:user:view', '查看用户', '查看用户信息', 'ENABLED', 'BUTTON', 'system', 'user', 'view', 1, NOW(), NOW(), 'system', 'system', 0, 1),
(2, 'system:user:create', '创建用户', '创建新用户', 'ENABLED', 'BUTTON', 'system', 'user', 'create', 2, NOW(), NOW(), 'system', 'system', 0, 1),
(3, 'system:user:update', '编辑用户', '编辑用户信息', 'ENABLED', 'BUTTON', 'system', 'user', 'update', 3, NOW(), NOW(), 'system', 'system', 0, 1),
(4, 'system:user:delete', '删除用户', '删除用户', 'ENABLED', 'BUTTON', 'system', 'user', 'delete', 4, NOW(), NOW(), 'system', 'system', 0, 1),
(5, 'system:role:view', '查看角色', '查看角色信息', 'ENABLED', 'BUTTON', 'system', 'role', 'view', 5, NOW(), NOW(), 'system', 'system', 0, 1),
(6, 'system:role:create', '创建角色', '创建新角色', 'ENABLED', 'BUTTON', 'system', 'role', 'create', 6, NOW(), NOW(), 'system', 'system', 0, 1),
(7, 'system:role:update', '编辑角色', '编辑角色信息', 'ENABLED', 'BUTTON', 'system', 'role', 'update', 7, NOW(), NOW(), 'system', 'system', 0, 1),
(8, 'system:role:delete', '删除角色', '删除角色', 'ENABLED', 'BUTTON', 'system', 'role', 'delete', 8, NOW(), NOW(), 'system', 'system', 0, 1);

-- ============================================================
-- 10. 种子数据：基础菜单
-- ============================================================
INSERT INTO sys_menu (id, parent_id, name, path, component, icon, sort_order, type, permission_code, visible, status, created_at, updated_at, created_by, updated_by, deleted, version)
VALUES
(1, 0, '系统管理', '/system', NULL, 'setting', 1, 1, NULL, 1, 1, NOW(), NOW(), 'system', 'system', 0, 1),
(2, 1, '用户管理', '/system/users', 'system/users/index', 'user', 1, 2, 'system:user:view', 1, 1, NOW(), NOW(), 'system', 'system', 0, 1),
(3, 1, '角色管理', '/system/roles', 'system/roles/index', 'team', 2, 2, 'system:role:view', 1, 1, NOW(), NOW(), 'system', 'system', 0, 1),
(4, 1, '权限管理', '/system/permissions', 'system/permissions/index', 'safety', 3, 2, 'system:permission:view', 1, 1, NOW(), NOW(), 'system', 'system', 0, 1),
(5, 1, '菜单管理', '/system/menus', 'system/menus/index', 'menu', 4, 2, 'system:menu:view', 1, 1, NOW(), NOW(), 'system', 'system', 0, 1);

-- ============================================================
-- 11. 关联管理员角色和所有权限
-- ============================================================
INSERT INTO sys_role_permission (id, role_id, permission_id, created_at, created_by, updated_at, updated_by, deleted)
VALUES
(1, 1, 1, NOW(), 'system', NOW(), 'system', 0),
(2, 1, 2, NOW(), 'system', NOW(), 'system', 0),
(3, 1, 3, NOW(), 'system', NOW(), 'system', 0),
(4, 1, 4, NOW(), 'system', NOW(), 'system', 0),
(5, 1, 5, NOW(), 'system', NOW(), 'system', 0),
(6, 1, 6, NOW(), 'system', NOW(), 'system', 0),
(7, 1, 7, NOW(), 'system', NOW(), 'system', 0),
(8, 1, 8, NOW(), 'system', NOW(), 'system', 0);

-- ============================================================
-- 12. 关联管理员角色和所有菜单
-- ============================================================
INSERT INTO sys_role_menu (id, role_id, menu_id, created_at, created_by, updated_at, updated_by, deleted)
VALUES
(1, 1, 1, NOW(), 'system', NOW(), 'system', 0),
(2, 1, 2, NOW(), 'system', NOW(), 'system', 0),
(3, 1, 3, NOW(), 'system', NOW(), 'system', 0),
(4, 1, 4, NOW(), 'system', NOW(), 'system', 0),
(5, 1, 5, NOW(), 'system', NOW(), 'system', 0);
