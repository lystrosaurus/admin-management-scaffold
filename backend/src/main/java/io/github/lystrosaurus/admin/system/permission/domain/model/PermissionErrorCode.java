package io.github.lystrosaurus.admin.system.permission.domain.model;

import io.github.lystrosaurus.admin.shared.domain.exception.DomainErrorCode;

/** 权限上下文错误码（数值与旧 {@code ErrorCode} 保持一致，保证 API 契约不变）。 */
public enum PermissionErrorCode implements DomainErrorCode {
  /** 权限不存在 */
  PERMISSION_NOT_FOUND(4002, "权限不存在"),
  /** 权限编码重复 */
  PERMISSION_DUPLICATE_KEY(6002, "数据重复");

  private final int code;
  private final String message;

  PermissionErrorCode(int code, String message) {
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
