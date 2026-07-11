-- M5: 种子数据 - OAuth 认证源
-- 仅 LARK(飞书) 实现了完整的 OAuth 客户端；WECOM/WECHAT 为占位实现
-- （buildAuthorizationUrl/exchangeCode/getUserInfo 暂未实现）。
-- 真实绑定需在对应厂商开放平台创建应用，并将 client_id / client_secret 填入本表，
-- 同时在厂商后台把回调地址登记为：<前端源>/oauth/callback/{provider}，例如
-- http://localhost:3001/oauth/callback/LARK
-- 注意：auth_provider.code 必须与前端 SecurityProfilePage 的 providers 列表、
-- 以及后端 OAuthClient bean 名称(LARK/WECOM/WECHAT) 完全一致。

INSERT IGNORE INTO `auth_provider`
    (`code`, `name`, `client_id`, `client_secret_encrypted`, `redirect_uri`, `scopes`, `enabled`, `created_at`, `created_by`, `updated_at`, `updated_by`, `deleted`)
VALUES
    ('LARK', '飞书', 'YOUR_FEISHU_APP_ID', 'YOUR_FEISHU_APP_SECRET',
     'http://localhost:3001/oauth/callback/LARK', 'openid', 1,
     CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system', 0),
    ('WECOM', '企业微信', 'YOUR_WECOM_CORP_ID', 'YOUR_WECOM_SECRET',
     'http://localhost:3001/oauth/callback/WECOM', 'openid', 1,
     CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system', 0),
    ('WECHAT', '微信', 'YOUR_WECHAT_APP_ID', 'YOUR_WECHAT_SECRET',
     'http://localhost:3001/oauth/callback/WECHAT', 'openid', 1,
     CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system', 0);
