-- V10: 创建文件管理表
-- 任务: sys_file 存储上传文件的元数据，物理文件落本地磁盘(由应用配置决定目录)

CREATE TABLE IF NOT EXISTS `sys_file` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `original_name` VARCHAR(255) NOT NULL COMMENT '原始文件名',
    `stored_name` VARCHAR(255) NOT NULL COMMENT '存储文件名(uuid)',
    `path` VARCHAR(512) NOT NULL COMMENT '存储相对路径',
    `ext` VARCHAR(16) COMMENT '扩展名(不含点)',
    `mime_type` VARCHAR(128) COMMENT 'MIME类型',
    `size_bytes` BIGINT NOT NULL DEFAULT 0 COMMENT '文件大小(字节)',
    `md5` VARCHAR(64) COMMENT '文件MD5',
    `uploader_id` BIGINT COMMENT '上传用户ID',
    `biz_type` VARCHAR(64) COMMENT '业务类型',
    `status` VARCHAR(32) NOT NULL DEFAULT 'NORMAL' COMMENT '状态(NORMAL/DELETED)',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `created_by` VARCHAR(50) COMMENT '创建人',
    `updated_by` VARCHAR(50) COMMENT '更新人',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',
    `version` INT DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (`id`),
    KEY `idx_md5` (`md5`),
    KEY `idx_uploader_id` (`uploader_id`),
    KEY `idx_biz_type` (`biz_type`),
    KEY `idx_original_name` (`original_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统文件表';
