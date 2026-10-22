package io.github.lystrosaurus.admin.system.user.domain.model;

import io.github.lystrosaurus.admin.shared.domain.exception.DomainErrorCode;

/** 用户上下文错误码（数值与旧 {@code ErrorCode} 保持一致，保证 API 契约不变）。 */
public enum UserErrorCode implements DomainErrorCode {
  /** 用户不存在 */
  USER_NOT_FOUND(2001, "用户不存在"),
  /** 用户已存在 */
  USER_ALREADY_EXISTS(2002, "用户已存在"),
  /** 密码错误 */
  USER_INVALID_PASSWORD(2003, "密码错误"),
  /** 该员工已绑定其他用户 */
  USER_EMPLOYEE_ALREADY_BOUND(2008, "该员工已绑定其他用户"),
  /** 邮箱格式不正确 */
  USER_EMAIL_INVALID(2009, "邮箱格式不正确"),
  /** 手机号格式不正确 */
  USER_PHONE_INVALID(2010, "手机号格式不正确"),
  /** 用户状态不合法 */
  USER_STATUS_INVALID(2011, "用户状态不合法"),
  /** 员工不存在 */
  EMPLOYEE_NOT_FOUND(7001, "员工不存在");

  private final int code;
  private final String message;

  UserErrorCode(int code, String message) {
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
