package io.github.lystrosaurus.admin.auth.log.vo;

import java.time.LocalDateTime;

/**
 * 登录日志 VO
 *
 * <p>字段命名与前端的 {@code LoginLog} 类型保持一致（username / ip / success / userAgent）。 userId →
 * username、status(SUCCESS/FAILED) → success(boolean) 的解析在服务端完成， 前端无需再额外请求用户信息服务。
 *
 * @param id 主键ID
 * @param username 用户名（由 userId 解析，失败/未知时为 null）
 * @param ip 客户端 IP
 * @param success 是否登录成功
 * @param loginAt 登录时间
 * @param userAgent User-Agent
 */
public record LoginLogVO(
    Long id,
    String username,
    String ip,
    boolean success,
    LocalDateTime loginAt,
    String userAgent) {}
