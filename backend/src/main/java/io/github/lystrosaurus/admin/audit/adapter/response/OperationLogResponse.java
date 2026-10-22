package io.github.lystrosaurus.admin.audit.adapter.response;

import java.time.LocalDateTime;

/**
 * 操作日志响应（字段与旧 OperationLogVO 完全一致，保证 API 契约不变）
 *
 * @param id 主键ID
 * @param username 用户名（由 userId 解析，未知/解析失败时为 null）
 * @param module 目标类型（如 User/Role）
 * @param action 操作类型（如 CREATE/UPDATE/DELETE）
 * @param bizId 目标ID
 * @param opAt 操作时间
 * @param ip IP 地址
 */
public record OperationLogResponse(
    Long id,
    String username,
    String module,
    String action,
    Long bizId,
    LocalDateTime opAt,
    String ip) {}
