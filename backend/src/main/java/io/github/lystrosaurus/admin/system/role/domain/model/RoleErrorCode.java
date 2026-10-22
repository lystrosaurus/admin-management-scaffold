package io.github.lystrosaurus.admin.system.role.domain.model;

import io.github.lystrosaurus.admin.shared.domain.exception.DomainErrorCode;

/** 角色上下文错误码（数值与旧 {@code ErrorCode} 保持一致，保证 API 契约不变）。 */
public enum RoleErrorCode implements DomainErrorCode {
  /** 角色不存在 */
  ROLE_NOT_FOUND(3001, "角色不存在"),
  /** 角色已存在 */
  ROLE_ALREADY_EXISTS(3002, "角色已存在");

  private final int code;
  private final String message;

  RoleErrorCode(int code, String message) {
    this.code = code;
    this.message = message;
  }

  @Override
  public int code() {
    return code;
  }

  @Override
  public String message() {
    return message;
  }
}
