package io.github.lystrosaurus.admin.auth.log.vo;

import java.time.LocalDateTime;

/**
 * 操作日志 VO
 *
 * <p>字段命名与前端的 {@code OperationLog} 类型保持一致（username / module / action / bizId / opAt / ip）。
 * userId→username、targetType→module、operationType→action、targetId→bizId、createdAt→opAt、ipAddress→ip
 * 的映射在服务端完成，前端无需再额外请求用户信息服务。
 *
 * @param id 主键ID
 * @param username 用户名（由 userId 解析，未知/解析失败时为 null）
 * @param module 目标类型（如 User/Role）
 * @param action 操作类型（如 CREATE/UPDATE/DELETE）
 * @param bizId 目标ID
 * @param opAt 操作时间
 * @param ip IP 地址
 */
public record OperationLogVO(
    Long id,
    String username,
    String module,
    String action,
    Long bizId,
    LocalDateTime opAt,
    String ip) {}
