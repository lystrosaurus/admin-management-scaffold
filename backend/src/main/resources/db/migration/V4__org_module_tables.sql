-- V4: 组织模块基础表（工作树临时版本，合并时由 Agent A 的版本替换）
-- 包含：hr_employee, org_unit, employee_org, sys_role_org

-- ============================================================
-- 1. 部门表 org_unit
-- ============================================================
CREATE TABLE IF NOT EXISTS `org_unit` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父部门ID，0表示顶级',
    `code` VARCHAR(50) NOT NULL COMMENT '部门编码',
    `name` VARCHAR(100) NOT NULL COMMENT '部门名称',
    `leader_id` BIGINT COMMENT '负责人ID',
    `sort_order` INT DEFAULT 0 COMMENT '排序号',
    `status` VARCHAR(32) NOT NULL DEFAULT 'ENABLED' COMMENT '状态(ENABLED/DISABLED)',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `created_by` VARCHAR(50) COMMENT '创建人',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `updated_by` VARCHAR(50) COMMENT '更新人',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',
    `version` INT DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='部门表';

-- ============================================================
-- 2. 员工表 hr_employee
-- ============================================================
CREATE TABLE IF NOT EXISTS `hr_employee` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `org_id` BIGINT NOT NULL COMMENT '所属部门ID',
    `employee_no` VARCHAR(50) NOT NULL COMMENT '员工编号',
    `name` VARCHAR(50) NOT NULL COMMENT '员工姓名',
    `phone` VARCHAR(20) COMMENT '手机号',
    `email` VARCHAR(100) COMMENT '邮箱',
    `position` VARCHAR(100) COMMENT '职位',
    `status` VARCHAR(32) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态(ACTIVE/INACTIVE)',
    `hire_date` DATE COMMENT '入职日期',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `created_by` VARCHAR(50) COMMENT '创建人',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `updated_by` VARCHAR(50) COMMENT '更新人',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',
    `version` INT DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_employee_no` (`employee_no`),
    KEY `idx_org_id` (`org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='员工表';

-- ============================================================
-- 3. 员工-部门关联表 employee_org
-- ============================================================
CREATE TABLE IF NOT EXISTS `employee_org` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `employee_id` BIGINT NOT NULL COMMENT '员工ID',
    `org_id` BIGINT NOT NULL COMMENT '部门ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `created_by` VARCHAR(50) COMMENT '创建人',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `updated_by` VARCHAR(50) COMMENT '更新人',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_employee_org` (`employee_id`, `org_id`),
    KEY `idx_org_id` (`org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='员工部门关联表';

-- ============================================================
-- 4. 角色-部门关联表 sys_role_org（数据权限 CUSTOM 范围）
-- ============================================================
CREATE TABLE IF NOT EXISTS `sys_role_org` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `org_id` BIGINT NOT NULL COMMENT '部门ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `created_by` VARCHAR(50) COMMENT '创建人',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_org` (`role_id`, `org_id`),
    KEY `idx_org_id` (`org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色部门关联表';
