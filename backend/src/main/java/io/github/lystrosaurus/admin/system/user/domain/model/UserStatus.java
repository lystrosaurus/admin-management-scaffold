package io.github.lystrosaurus.admin.system.user.domain.model;

import io.github.lystrosaurus.admin.shared.domain.exception.DomainException;

/** 用户状态。 */
public enum UserStatus {
  /** 启用 */
  ENABLED,
  /** 禁用 */
  DISABLED,
  /** 锁定 */
  LOCKED;

  /**
   * 按名称解析用户状态，非法值抛出领域异常。
   *
   * @param value 状态名称
   * @return 用户状态
   */
  public static UserStatus fromString(String value) {
    if (value != null) {
      for (UserStatus status : values()) {
        if (status.name().equals(value.trim().toUpperCase())) {
          return status;
        }
      }
    }
    throw new DomainException(UserErrorCode.USER_STATUS_INVALID);
  }
}
