-- V12: 创建数据字典表
-- 任务: sys_dict 存储数据字典项（同类型下多个 key-value）

CREATE TABLE IF NOT EXISTS `sys_dict` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `dict_type` VARCHAR(64) NOT NULL COMMENT '字典类型编码',
    `dict_label` VARCHAR(128) NOT NULL COMMENT '字典标签(显示名)',
    `dict_value` VARCHAR(128) NOT NULL COMMENT '字典值',
    `sort_order` INT DEFAULT 0 COMMENT '排序号',
    `status` VARCHAR(32) NOT NULL DEFAULT 'ENABLED' COMMENT '状态(ENABLED/DISABLED)',
    `remark` VARCHAR(255) COMMENT '备注',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `created_by` VARCHAR(50) COMMENT '创建人',
    `updated_by` VARCHAR(50) COMMENT '更新人',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',
    `version` INT DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (`id`),
    KEY `idx_dict_type` (`dict_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='数据字典表';
