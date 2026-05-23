package io.github.lystrosaurus.admin.exception;

/** 错误码枚举 */
public enum ErrorCode {
  // 通用错误码
  SUCCESS(200, "操作成功"),
  BAD_REQUEST(400, "请求参数错误"),
  UNAUTHORIZED(401, "未授权"),
  FORBIDDEN(403, "禁止访问"),
  NOT_FOUND(404, "资源不存在"),
  INTERNAL_ERROR(500, "服务器内部错误"),

  // 业务错误码 (1xxx)
  USER_NOT_FOUND(1001, "用户不存在"),
  USER_ALREADY_EXISTS(1002, "用户已存在"),
  INVALID_PASSWORD(1003, "密码错误"),
  ACCOUNT_DISABLED(1004, "账户已禁用"),

  // 数据错误码 (2xxx)
  DATA_INTEGRITY_VIOLATION(2001, "数据完整性违反"),
  DUPLICATE_KEY(2002, "数据重复"),

  // 认证错误码 (3xxx)
  TOKEN_EXPIRED(3001, "Token 已过期"),
  TOKEN_INVALID(3002, "Token 无效"),
  PERMISSION_DENIED(3003, "权限不足");

  private final int code;
  private final String message;

  ErrorCode(int code, String message) {
    this.code = code;
    this.message = message;
  }

  public int getCode() {
    return code;
  }

  public String getMessage() {
    return message;
  }
}
