package io.github.lystrosaurus.admin.system.menu.domain.model;

import io.github.lystrosaurus.admin.shared.domain.exception.DomainErrorCode;

/** 菜单上下文错误码（数值与旧 {@code ErrorCode} 保持一致，保证 API 契约不变）。 */
public enum MenuErrorCode implements DomainErrorCode {
  /** 菜单不存在 */
  MENU_NOT_FOUND(5001, "菜单不存在"),
  /** 菜单已存在 */
  MENU_ALREADY_EXISTS(5002, "菜单已存在");

  private final int code;
  private final String message;

  MenuErrorCode(int code, String message) {
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
