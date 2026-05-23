-- V2: 添加菜单表和权限字段扩展
-- 任务: 创建 sys_menu 表，补充 sys_permission 字段，创建 sys_role_menu 关联表

-- ============================================================
-- 1. 创建菜单表 sys_menu
-- ============================================================
CREATE TABLE IF NOT EXISTS `sys_menu` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父菜单ID，0表示顶级菜单',
    `name` VARCHAR(64) NOT NULL COMMENT '菜单名称',
    `path` VARCHAR(255) COMMENT '路由路径',
    `component` VARCHAR(255) COMMENT '前端组件路径',
    `icon` VARCHAR(64) COMMENT '图标',
    `sort_order` INT DEFAULT 0 COMMENT '排序号',
    `type` TINYINT NOT NULL COMMENT '类型：1-目录，2-菜单，3-按钮',
    `permission_code` VARCHAR(64) COMMENT '权限码',
    `visible` TINYINT DEFAULT 1 COMMENT '是否可见：0-隐藏，1-显示',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `created_by` VARCHAR(50) COMMENT '创建人',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `updated_by` VARCHAR(50) COMMENT '更新人',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',
    `version` INT DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_permission_code` (`permission_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统菜单表';

-- ============================================================
-- 2. 扩展 sys_permission 表字段
-- ============================================================
ALTER TABLE `sys_permission`
    ADD COLUMN `type` VARCHAR(32) COMMENT '权限类型：MENU-菜单权限，BUTTON-按钮权限，API-接口权限' AFTER `description`,
    ADD COLUMN `module` VARCHAR(64) COMMENT '所属模块，如 system、auth' AFTER `type`,
    ADD COLUMN `resource` VARCHAR(64) COMMENT '资源标识，如 user、role' AFTER `module`,
    ADD COLUMN `action` VARCHAR(32) COMMENT '操作类型，如 view、create、update、delete' AFTER `resource`;

-- 为权限类型字段添加索引
ALTER TABLE `sys_permission`
    ADD KEY `idx_type` (`type`);

-- ============================================================
-- 3. 创建角色菜单关联表 sys_role_menu
-- ============================================================
CREATE TABLE IF NOT EXISTS `sys_role_menu` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `menu_id` BIGINT NOT NULL COMMENT '菜单ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `created_by` VARCHAR(50) COMMENT '创建人',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `updated_by` VARCHAR(50) COMMENT '更新人',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_menu` (`role_id`, `menu_id`),
    KEY `idx_menu_id` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色菜单关联表';
