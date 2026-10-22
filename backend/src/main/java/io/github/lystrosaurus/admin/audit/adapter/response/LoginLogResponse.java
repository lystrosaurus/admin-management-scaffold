package io.github.lystrosaurus.admin.audit.adapter.response;

import java.time.LocalDateTime;

/**
 * 登录日志响应（字段与旧 LoginLogVO 完全一致，保证 API 契约不变）
 *
 * @param id 主键ID
 * @param username 用户名（由 userId 解析，失败/未知时为 null）
 * @param ip 客户端 IP
 * @param success 是否登录成功
 * @param loginAt 登录时间
 * @param userAgent User-Agent
 */
public record LoginLogResponse(
    Long id,
    String username,
    String ip,
    boolean success,
    LocalDateTime loginAt,
    String userAgent) {}
