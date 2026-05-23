-- M3: 员工组织模块 - 新增4张表
-- hr_employee: 员工主数据表
-- org_unit: 组织架构表
-- employee_org: 员工-组织多对多关联表
-- sys_role_org: 角色-组织自定义数据权限范围表

-- 员工主数据表
CREATE TABLE IF NOT EXISTS `hr_employee` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `employee_no` VARCHAR(64) NOT NULL COMMENT '员工工号',
    `name` VARCHAR(100) NOT NULL COMMENT '员工姓名',
    `preferred_name` VARCHAR(100) COMMENT '常用名/花名',
    `mobile` VARCHAR(32) COMMENT '手机号',
    `email` VARCHAR(100) COMMENT '邮箱',
    `primary_org_id` BIGINT COMMENT '主组织ID',
    `job_title` VARCHAR(100) COMMENT '职位',
    `employment_status` VARCHAR(32) NOT NULL DEFAULT 'ACTIVE' COMMENT '在职状态(ACTIVE/RESIGNED/SUSPENDED)',
    `entry_date` DATE COMMENT '入职日期',
    `leave_date` DATE COMMENT '离职日期',
    `source_type` VARCHAR(32) NOT NULL DEFAULT 'MANUAL' COMMENT '数据来源(MANUAL/IMPORT/API)',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `created_by` VARCHAR(50) COMMENT '创建人',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `updated_by` VARCHAR(50) COMMENT '更新人',
    `deleted` INT DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',
    `version` INT DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_employee_no` (`employee_no`),
    KEY `idx_name` (`name`),
    KEY `idx_mobile` (`mobile`),
    KEY `idx_primary_org_id` (`primary_org_id`),
    KEY `idx_employment_status` (`employment_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='员工主数据表';

-- 组织架构表
CREATE TABLE IF NOT EXISTS `org_unit` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `parent_id` BIGINT NOT NULL DEFAULT 0 COMMENT '父节点ID(0表示根节点)',
    `code` VARCHAR(64) NOT NULL COMMENT '组织编码',
    `name` VARCHAR(100) NOT NULL COMMENT '组织名称',
    `full_path` VARCHAR(500) COMMENT '完整路径(如/1/5/12/)',
    `level` INT DEFAULT 1 COMMENT '层级(从1开始)',
    `manager_employee_id` BIGINT COMMENT '负责人员工ID',
    `sort_order` INT DEFAULT 0 COMMENT '排序号',
    `status` VARCHAR(32) DEFAULT 'ENABLED' COMMENT '状态(ENABLED/DISABLED)',
    `source_type` VARCHAR(32) NOT NULL DEFAULT 'MANUAL' COMMENT '数据来源(MANUAL/IMPORT/API)',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `created_by` VARCHAR(50) COMMENT '创建人',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `updated_by` VARCHAR(50) COMMENT '更新人',
    `deleted` INT DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',
    `version` INT DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_manager_employee_id` (`manager_employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='组织架构表';

-- 员工-组织多对多关联表
CREATE TABLE IF NOT EXISTS `employee_org` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `employee_id` BIGINT NOT NULL COMMENT '员工ID',
    `org_id` BIGINT NOT NULL COMMENT '组织ID',
    `is_primary` TINYINT DEFAULT 0 COMMENT '是否主组织(0-否，1-是)',
    `position_name` VARCHAR(100) COMMENT '岗位名称',
    `start_date` DATE COMMENT '开始日期',
    `end_date` DATE COMMENT '结束日期',
    `status` VARCHAR(32) DEFAULT 'ACTIVE' COMMENT '状态(ACTIVE/INACTIVE)',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `created_by` VARCHAR(50) COMMENT '创建人',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `updated_by` VARCHAR(50) COMMENT '更新人',
    `deleted` INT DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',
    `version` INT DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_employee_org` (`employee_id`, `org_id`),
    KEY `idx_org_id` (`org_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='员工-组织关联表';

-- 角色-组织自定义数据权限范围表(物理删除，不做逻辑删除)
CREATE TABLE IF NOT EXISTS `sys_role_org` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `org_id` BIGINT NOT NULL COMMENT '组织ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `created_by` VARCHAR(50) COMMENT '创建人',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_org` (`role_id`, `org_id`),
    KEY `idx_org_id` (`org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色-组织数据权限范围表';
