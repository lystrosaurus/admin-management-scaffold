-- 为 integration 上下文补齐四层持久化所需的审计列（不改动既有业务列）
-- ext_source / ext_principal 缺少 version；ext_principal_identifier 缺少 deleted、version
-- identity_link_candidate 缺少 updated_at、created_by、updated_by、deleted、version

ALTER TABLE `ext_source`
    ADD COLUMN `version` INT DEFAULT 1 COMMENT '版本号（乐观锁）';

ALTER TABLE `ext_principal`
    ADD COLUMN `version` INT DEFAULT 1 COMMENT '版本号（乐观锁）';

ALTER TABLE `ext_principal_identifier`
    ADD COLUMN `deleted` INT DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',
    ADD COLUMN `version` INT DEFAULT 1 COMMENT '版本号（乐观锁）';

ALTER TABLE `identity_link_candidate`
    ADD COLUMN `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    ADD COLUMN `created_by` VARCHAR(50) COMMENT '创建人',
    ADD COLUMN `updated_by` VARCHAR(50) COMMENT '更新人',
    ADD COLUMN `deleted` INT DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',
    ADD COLUMN `version` INT DEFAULT 1 COMMENT '版本号（乐观锁）';
