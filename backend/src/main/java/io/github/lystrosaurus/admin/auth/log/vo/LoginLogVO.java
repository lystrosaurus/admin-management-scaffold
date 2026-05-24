package io.github.lystrosaurus.admin.auth.log.vo;

import java.time.LocalDateTime;

/**
 * 登录日志 VO
 *
 * @param id 主键ID
 * @param loginType 登录类型
 * @param providerCode OAuth提供方编码
 * @param ipAddress IP地址
 * @param status 登录状态
 * @param loginAt 登录时间
 */
public record LoginLogVO(
    Long id,
    String loginType,
    String providerCode,
    String ipAddress,
    String status,
    LocalDateTime loginAt) {}
