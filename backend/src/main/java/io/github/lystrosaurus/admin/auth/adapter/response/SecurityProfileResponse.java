package io.github.lystrosaurus.admin.auth.adapter.response;

import io.github.lystrosaurus.admin.audit.adapter.response.LoginLogResponse;
import java.util.List;

/**
 * 账号安全信息响应（字段与旧 SecurityProfileVO 完全一致，保证 API 契约不变）
 *
 * @param hasPassword 是否设置了密码
 * @param boundAccounts 已绑定的三方账号列表
 * @param recentLogins 最近登录记录
 */
public record SecurityProfileResponse(
    boolean hasPassword,
    List<ExternalAccountResponse> boundAccounts,
    List<LoginLogResponse> recentLogins) {}
