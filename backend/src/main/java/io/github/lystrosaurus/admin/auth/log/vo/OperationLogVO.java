package io.github.lystrosaurus.admin.auth.log.vo;

import java.time.LocalDateTime;

/**
 * 操作日志 VO
 *
 * @param id 主键ID
 * @param userId 用户ID
 * @param operationType 操作类型
 * @param targetType 目标类型
 * @param targetId 目标ID
 * @param ipAddress IP地址
 * @param createdAt 创建时间
 */
public record OperationLogVO(
    Long id,
    Long userId,
    String operationType,
    String targetType,
    Long targetId,
    String ipAddress,
    LocalDateTime createdAt) {}
