-- V13: 创建系统配置项表
-- 任务: sys_config_item 存储系统级配置（key-value，按 config_type 区分类型）

CREATE TABLE IF NOT EXISTS `sys_config_item` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `config_key` VARCHAR(128) NOT NULL COMMENT '配置键(唯一)',
    `config_value` VARCHAR(512) COMMENT '配置值',
    `config_type` VARCHAR(32) NOT NULL DEFAULT 'STRING' COMMENT '配置类型(STRING/NUMBER/BOOLEAN/JSON)',
    `description` VARCHAR(255) COMMENT '描述',
    `status` VARCHAR(32) NOT NULL DEFAULT 'ENABLED' COMMENT '状态(ENABLED/DISABLED)',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `created_by` VARCHAR(50) COMMENT '创建人',
    `updated_by` VARCHAR(50) COMMENT '更新人',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',
    `version` INT DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统配置项表';
