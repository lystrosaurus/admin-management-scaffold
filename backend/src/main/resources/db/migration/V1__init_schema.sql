-- 初始化数据库架构
-- 创建用户表
CREATE TABLE IF NOT EXISTS `sys_user` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `nickname` VARCHAR(50) COMMENT '昵称',
    `email` VARCHAR(100) COMMENT '邮箱',
    `phone` VARCHAR(20) COMMENT '手机号',
    `avatar` VARCHAR(255) COMMENT '头像',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `created_by` VARCHAR(50) COMMENT '创建人',
    `updated_by` VARCHAR(50) COMMENT '更新人',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',
    `version` INT DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_email` (`email`),
    KEY `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户表';

-- 创建角色表
CREATE TABLE IF NOT EXISTS `sys_role` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
    `role_code` VARCHAR(50) NOT NULL COMMENT '角色编码',
    `description` VARCHAR(255) COMMENT '描述',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `created_by` VARCHAR(50) COMMENT '创建人',
    `updated_by` VARCHAR(50) COMMENT '更新人',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',
    `version` INT DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统角色表';

-- 创建用户角色关联表
CREATE TABLE IF NOT EXISTS `sys_user_role` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `created_by` VARCHAR(50) COMMENT '创建人',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `updated_by` VARCHAR(50) COMMENT '更新人',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
    KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- 创建权限表
CREATE TABLE IF NOT EXISTS `sys_permission` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `permission_name` VARCHAR(50) NOT NULL COMMENT '权限名称',
    `permission_code` VARCHAR(100) NOT NULL COMMENT '权限编码',
    `description` VARCHAR(255) COMMENT '描述',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `created_by` VARCHAR(50) COMMENT '创建人',
    `updated_by` VARCHAR(50) COMMENT '更新人',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',
    `version` INT DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_permission_code` (`permission_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统权限表';

-- 创建角色权限关联表
CREATE TABLE IF NOT EXISTS `sys_role_permission` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `permission_id` BIGINT NOT NULL COMMENT '权限ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `created_by` VARCHAR(50) COMMENT '创建人',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `updated_by` VARCHAR(50) COMMENT '更新人',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`),
    KEY `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';