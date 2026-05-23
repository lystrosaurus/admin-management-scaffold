-- M4: 外部身份源 + 映射表 + 身份匹配
-- ext_source: 外部系统实例
-- ext_principal: 外部主体主数据
-- ext_principal_identifier: 外部主体多ID映射
-- identity_link_candidate: 身份匹配候选
-- auth_provider: OAuth 认证源配置
-- auth_external_account: 三方账号绑定记录

-- 外部系统实例表
CREATE TABLE IF NOT EXISTS `ext_source` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `code` VARCHAR(64) NOT NULL COMMENT '系统编码(BEISEN/LARK/WECOM)',
    `name` VARCHAR(100) NOT NULL COMMENT '系统名称',
    `source_type` VARCHAR(32) NOT NULL COMMENT '来源类型(HR/IM/OA)',
    `tenant_key` VARCHAR(200) COMMENT '租户标识',
    `status` VARCHAR(32) NOT NULL DEFAULT 'ENABLED' COMMENT '状态(ENABLED/DISABLED)',
    `priority` INT DEFAULT 0 COMMENT '优先级(越高越优先)',
    `config_json` TEXT COMMENT '扩展配置JSON',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `created_by` VARCHAR(50) COMMENT '创建人',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `updated_by` VARCHAR(50) COMMENT '更新人',
    `deleted` INT DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='外部系统实例表';

-- 外部主体主数据表
CREATE TABLE IF NOT EXISTS `ext_principal` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `source_id` BIGINT NOT NULL COMMENT '外部系统ID',
    `principal_type` VARCHAR(32) NOT NULL COMMENT '主体类型(USER/ORG)',
    `external_key` VARCHAR(200) NOT NULL COMMENT '外部系统主键',
    `display_name` VARCHAR(200) COMMENT '显示名称',
    `status` VARCHAR(32) DEFAULT 'ACTIVE' COMMENT '状态(ACTIVE/INACTIVE)',
    `raw_payload_json` TEXT COMMENT '原始数据JSON',
    `last_sync_at` DATETIME COMMENT '最后同步时间',
    `canonical_type` VARCHAR(32) COMMENT '映射目标类型(EMPLOYEE/ORG_UNIT)',
    `canonical_id` BIGINT COMMENT '映射目标ID(hr_employee.id或org_unit.id)',
    `link_status` VARCHAR(32) NOT NULL DEFAULT 'UNLINKED' COMMENT '关联状态(UNLINKED/AUTO_LINKED/MANUAL_LINKED/CONFLICT)',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `created_by` VARCHAR(50) COMMENT '创建人',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `updated_by` VARCHAR(50) COMMENT '更新人',
    `deleted` INT DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_source_principal` (`source_id`, `principal_type`, `external_key`),
    KEY `idx_canonical` (`canonical_type`, `canonical_id`),
    KEY `idx_link_status` (`link_status`),
    CONSTRAINT `fk_principal_source` FOREIGN KEY (`source_id`) REFERENCES `ext_source` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='外部主体主数据表';

-- 外部主体多ID映射表
CREATE TABLE IF NOT EXISTS `ext_principal_identifier` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `principal_id` BIGINT NOT NULL COMMENT '外部主体ID',
    `id_type` VARCHAR(64) NOT NULL COMMENT 'ID类型(lark_open_id/lark_user_id/wecom_userid/beisen_employee_id)',
    `id_value` VARCHAR(200) NOT NULL COMMENT 'ID值',
    `is_primary` TINYINT DEFAULT 0 COMMENT '是否主ID(0-否，1-是)',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `created_by` VARCHAR(50) COMMENT '创建人',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `updated_by` VARCHAR(50) COMMENT '更新人',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_principal_id_type_value` (`principal_id`, `id_type`, `id_value`),
    KEY `idx_id_type_value` (`id_type`, `id_value`),
    CONSTRAINT `fk_identifier_principal` FOREIGN KEY (`principal_id`) REFERENCES `ext_principal` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='外部主体多ID映射表';

-- 身份匹配候选表
CREATE TABLE IF NOT EXISTS `identity_link_candidate` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `source_principal_id` BIGINT NOT NULL COMMENT '外部主体ID',
    `candidate_type` VARCHAR(32) NOT NULL COMMENT '候选类型(EMPLOYEE/ORG_UNIT)',
    `candidate_id` BIGINT NOT NULL COMMENT '候选目标ID',
    `score` INT DEFAULT 0 COMMENT '匹配分数(0-100)',
    `reason` VARCHAR(500) COMMENT '匹配原因',
    `status` VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '状态(PENDING/CONFIRMED/REJECTED)',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `handled_by` VARCHAR(50) COMMENT '处理人',
    `handled_at` DATETIME COMMENT '处理时间',
    PRIMARY KEY (`id`),
    KEY `idx_principal` (`source_principal_id`),
    KEY `idx_status` (`status`),
    CONSTRAINT `fk_candidate_principal` FOREIGN KEY (`source_principal_id`) REFERENCES `ext_principal` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='身份匹配候选表';

-- OAuth 认证源配置表
CREATE TABLE IF NOT EXISTS `auth_provider` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `code` VARCHAR(64) NOT NULL COMMENT '认证源编码(LARK/WECOM/WECHAT)',
    `name` VARCHAR(100) NOT NULL COMMENT '认证源名称',
    `client_id` VARCHAR(200) COMMENT '客户端ID',
    `client_secret_encrypted` VARCHAR(500) COMMENT '客户端密钥(加密存储)',
    `redirect_uri` VARCHAR(500) COMMENT '回调地址',
    `scopes` VARCHAR(500) COMMENT '授权范围',
    `enabled` TINYINT DEFAULT 1 COMMENT '是否启用(0-否，1-是)',
    `config_json` TEXT COMMENT '扩展配置JSON',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `created_by` VARCHAR(50) COMMENT '创建人',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `updated_by` VARCHAR(50) COMMENT '更新人',
    `deleted` INT DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='OAuth认证源配置表';

-- 三方账号绑定记录表
CREATE TABLE IF NOT EXISTS `auth_external_account` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `provider_id` BIGINT NOT NULL COMMENT '认证源ID',
    `provider_user_key` VARCHAR(200) NOT NULL COMMENT '三方用户标识',
    `user_id` BIGINT COMMENT '关联用户ID(sys_user.id)',
    `employee_id` BIGINT COMMENT '关联员工ID(hr_employee.id)',
    `identifier_json` TEXT COMMENT '三方标识JSON(open_id/union_id等)',
    `nickname` VARCHAR(100) COMMENT '三方昵称',
    `avatar_url` VARCHAR(500) COMMENT '三方头像URL',
    `bind_status` VARCHAR(32) NOT NULL DEFAULT 'BOUND' COMMENT '绑定状态(BOUND/UNBOUND/CONFLICT)',
    `last_login_at` DATETIME COMMENT '最后登录时间',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `created_by` VARCHAR(50) COMMENT '创建人',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `updated_by` VARCHAR(50) COMMENT '更新人',
    `deleted` INT DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_provider_user_key` (`provider_id`, `provider_user_key`),
    UNIQUE KEY `uk_provider_user` (`provider_id`, `user_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_employee_id` (`employee_id`),
    KEY `idx_bind_status` (`bind_status`),
    CONSTRAINT `fk_account_provider` FOREIGN KEY (`provider_id`) REFERENCES `auth_provider` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='三方账号绑定记录表';
