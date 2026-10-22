package io.github.lystrosaurus.admin.audit.domain.model;

import java.time.LocalDateTime;

/** 登录日志领域对象：记录一次登录事实，无业务行为。 */
public class LoginLog {

  /** 主键ID */
  private final Long id;

  /** 用户ID */
  private final Long userId;

  /** 登录类型: PASSWORD/OAUTH */
  private final String loginType;

  /** OAuth提供方编码: LARK/WECOM/WECHAT */
  private final String providerCode;

  /** IP地址 */
  private final String ipAddress;

  /** User-Agent */
  private final String userAgent;

  /** 登录状态: SUCCESS/FAILED */
  private final String status;

  /** 失败原因 */
  private final String failureReason;

  /** 登录时间 */
  private final LocalDateTime loginAt;

  private LoginLog(
      Long id,
      Long userId,
      String loginType,
      String providerCode,
      String ipAddress,
      String userAgent,
      String status,
      String failureReason,
      LocalDateTime loginAt) {
    this.id = id;
    this.userId = userId;
    this.loginType = loginType;
    this.providerCode = providerCode;
    this.ipAddress = ipAddress;
    this.userAgent = userAgent;
    this.status = status;
    this.failureReason = failureReason;
    this.loginAt = loginAt;
  }

  /** 创建待落库的登录日志（主键由持久化层生成） */
  public static LoginLog record(
      Long userId,
      String loginType,
      String providerCode,
      String ipAddress,
      String userAgent,
      String status,
      String failureReason,
      LocalDateTime loginAt) {
    return new LoginLog(
        null,
        userId,
        loginType,
        providerCode,
        ipAddress,
        userAgent,
        status,
        failureReason,
        loginAt);
  }

  /** 从持久化数据重建登录日志 */
  public static LoginLog restore(
      Long id,
      Long userId,
      String loginType,
      String providerCode,
      String ipAddress,
      String userAgent,
      String status,
      String failureReason,
      LocalDateTime loginAt) {
    return new LoginLog(
        id,
        userId,
        loginType,
        providerCode,
        ipAddress,
        userAgent,
        status,
        failureReason,
        loginAt);
  }

  public Long id() {
    return id;
  }

  public Long userId() {
    return userId;
  }

  public String loginType() {
    return loginType;
  }

  public String providerCode() {
    return providerCode;
  }

  public String ipAddress() {
    return ipAddress;
  }

  public String userAgent() {
    return userAgent;
  }

  public String status() {
    return status;
  }

  public String failureReason() {
    return failureReason;
  }

  public LocalDateTime loginAt() {
    return loginAt;
  }
}
