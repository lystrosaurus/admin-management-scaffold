-- 登录日志表
CREATE TABLE sys_login_log (
    id            BIGINT       NOT NULL PRIMARY KEY,
    user_id       BIGINT       NOT NULL COMMENT '用户ID',
    login_type    VARCHAR(32)  NOT NULL COMMENT '登录类型: PASSWORD/OAUTH_LARK/OAUTH_WECOM/OAUTH_WECHAT',
    provider_code VARCHAR(32)  NULL COMMENT 'OAuth提供方编码: LARK/WECOM/WECHAT',
    ip_address    VARCHAR(128) NULL COMMENT 'IP地址',
    user_agent    VARCHAR(512) NULL COMMENT 'User-Agent',
    status        VARCHAR(32)  NOT NULL DEFAULT 'SUCCESS' COMMENT '登录状态: SUCCESS/FAILED',
    failure_reason VARCHAR(256) NULL COMMENT '失败原因',
    login_at      DATETIME     NOT NULL COMMENT '登录时间',
    INDEX idx_user_id (user_id),
    INDEX idx_login_at (login_at),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='登录日志';

-- 操作日志表
CREATE TABLE sys_operation_log (
    id              BIGINT       NOT NULL PRIMARY KEY,
    user_id         BIGINT       NOT NULL COMMENT '用户ID',
    operation_type  VARCHAR(64)  NOT NULL COMMENT '操作类型',
    target_type     VARCHAR(64)  NULL COMMENT '目标类型',
    target_id       BIGINT       NULL COMMENT '目标ID',
    detail_json     TEXT         NULL COMMENT '操作详情JSON',
    ip_address      VARCHAR(128) NULL COMMENT 'IP地址',
    created_at      DATETIME     NOT NULL COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_operation_type (operation_type),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='操作日志';