package io.github.lystrosaurus.admin.audit.domain.model;

import java.time.LocalDateTime;

/** 操作日志领域对象：记录一次管理端操作事实，无业务行为。 */
public class OperationLog {

  /** 主键ID */
  private final Long id;

  /** 用户ID */
  private final Long userId;

  /** 操作类型 */
  private final String operationType;

  /** 目标类型 */
  private final String targetType;

  /** 目标ID */
  private final Long targetId;

  /** 操作详情JSON */
  private final String detailJson;

  /** IP地址 */
  private final String ipAddress;

  /** 创建时间 */
  private final LocalDateTime createdAt;

  private OperationLog(
      Long id,
      Long userId,
      String operationType,
      String targetType,
      Long targetId,
      String detailJson,
      String ipAddress,
      LocalDateTime createdAt) {
    this.id = id;
    this.userId = userId;
    this.operationType = operationType;
    this.targetType = targetType;
    this.targetId = targetId;
    this.detailJson = detailJson;
    this.ipAddress = ipAddress;
    this.createdAt = createdAt;
  }

  /** 创建待落库的操作日志（主键与创建时间由持久化层生成） */
  public static OperationLog record(
      Long userId,
      String operationType,
      String targetType,
      Long targetId,
      String detailJson,
      String ipAddress) {
    return new OperationLog(
        null, userId, operationType, targetType, targetId, detailJson, ipAddress, null);
  }

  /** 从持久化数据重建操作日志 */
  public static OperationLog restore(
      Long id,
      Long userId,
      String operationType,
      String targetType,
      Long targetId,
      String detailJson,
      String ipAddress,
      LocalDateTime createdAt) {
    return new OperationLog(
        id, userId, operationType, targetType, targetId, detailJson, ipAddress, createdAt);
  }

  public Long id() {
    return id;
  }

  public Long userId() {
    return userId;
  }

  public String operationType() {
    return operationType;
  }

  public String targetType() {
    return targetType;
  }

  public Long targetId() {
    return targetId;
  }

  public String detailJson() {
    return detailJson;
  }

  public String ipAddress() {
    return ipAddress;
  }

  public LocalDateTime createdAt() {
    return createdAt;
  }
}
