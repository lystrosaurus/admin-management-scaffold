package io.github.lystrosaurus.admin.auth.domain.model;

import java.time.LocalDateTime;

/**
 * 三方账号领域对象。
 *
 * <p>对应 auth_external_account 表；零框架注解，仅保留最小状态行为。
 */
public class ExternalAccount {

  private Long id;
  private Long providerId;
  private String providerUserId;
  private Long userId;
  private Long employeeId;
  private String identifierJson;
  private String nickname;
  private String avatarUrl;
  private String bindStatus;
  private LocalDateTime lastLoginAt;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public ExternalAccount(
      Long id,
      Long providerId,
      String providerUserId,
      Long userId,
      Long employeeId,
      String identifierJson,
      String nickname,
      String avatarUrl,
      String bindStatus,
      LocalDateTime lastLoginAt,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    this.id = id;
    this.providerId = providerId;
    this.providerUserId = providerUserId;
    this.userId = userId;
    this.employeeId = employeeId;
    this.identifierJson = identifierJson;
    this.nickname = nickname;
    this.avatarUrl = avatarUrl;
    this.bindStatus = bindStatus;
    this.lastLoginAt = lastLoginAt;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  /** 标记为已解绑 */
  public void markUnbound() {
    this.bindStatus = "UNBOUND";
  }

  /** 记录最后登录时间 */
  public void touchLastLogin(LocalDateTime loginAt) {
    this.lastLoginAt = loginAt;
  }

  public Long getId() {
    return id;
  }

  public Long getProviderId() {
    return providerId;
  }

  public String getProviderUserId() {
    return providerUserId;
  }

  public Long getUserId() {
    return userId;
  }

  public Long getEmployeeId() {
    return employeeId;
  }

  public String getIdentifierJson() {
    return identifierJson;
  }

  public String getNickname() {
    return nickname;
  }

  public String getAvatarUrl() {
    return avatarUrl;
  }

  public String getBindStatus() {
    return bindStatus;
  }

  public LocalDateTime getLastLoginAt() {
    return lastLoginAt;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }
}
