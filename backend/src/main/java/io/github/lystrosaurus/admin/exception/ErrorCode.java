package io.github.lystrosaurus.admin.exception;

/** 错误码枚举 - 使用 CATEGORY_NUMBER 格式 */
public enum ErrorCode {
  // 系统通用错误码 (SYSTEM_xxx)
  SYSTEM_SUCCESS(200, "操作成功"),
  SYSTEM_400(400, "请求参数错误"),
  SYSTEM_401(401, "未授权"),
  SYSTEM_403(403, "禁止访问"),
  SYSTEM_404(404, "资源不存在"),
  SYSTEM_409(409, "资源冲突"),
  SYSTEM_429(429, "请求过于频繁"),
  SYSTEM_500(500, "服务器内部错误"),

  // 认证相关错误码 (AUTH_xxx)
  AUTH_401(1001, "认证失败"),
  AUTH_403(1003, "权限不足"),
  AUTH_TOKEN_EXPIRED(1004, "Token 已过期"),
  AUTH_TOKEN_INVALID(1005, "Token 无效"),

  // 用户相关错误码 (USER_xxx)
  USER_NOT_FOUND(2001, "用户不存在"),
  USER_ALREADY_EXISTS(2002, "用户已存在"),
  USER_INVALID_PASSWORD(2003, "密码错误"),
  USER_ACCOUNT_DISABLED(2004, "账户已禁用"),
  USER_DISABLED(2005, "用户已禁用"),
  USER_LOCKED(2006, "用户已锁定"),
  PASSWORD_MISMATCH(2007, "密码不匹配"),

  // 角色相关错误码 (ROLE_xxx)
  ROLE_NOT_FOUND(3001, "角色不存在"),
  ROLE_ALREADY_EXISTS(3002, "角色已存在"),

  // 权限相关错误码 (PERMISSION_xxx)
  PERMISSION_DENIED(4001, "权限不足"),
  PERMISSION_NOT_FOUND(4002, "权限不存在"),

  // 菜单相关错误码 (MENU_xxx)
  MENU_NOT_FOUND(5001, "菜单不存在"),
  MENU_ALREADY_EXISTS(5002, "菜单已存在"),

  // 用户-员工绑定相关错误码
  USER_EMPLOYEE_ALREADY_BOUND(2008, "该员工已绑定其他用户"),

  // 数据相关错误码 (DATA_xxx)
  DATA_INTEGRITY_VIOLATION(6001, "数据完整性违反"),
  DATA_DUPLICATE_KEY(6002, "数据重复"),

  // 员工相关错误码 (EMPLOYEE_xxx)
  EMPLOYEE_NOT_FOUND(7001, "员工不存在"),
  EMPLOYEE_ALREADY_EXISTS(7002, "员工已存在"),

  // 组织单元相关错误码 (ORG_UNIT_xxx)
  ORG_UNIT_NOT_FOUND(8001, "组织单元不存在"),
  ORG_UNIT_ALREADY_EXISTS(8002, "组织单元已存在"),
  ORG_UNIT_HAS_CHILDREN(8003, "组织单元存在子节点，无法删除"),

  // 外部身份源相关错误码 (SOURCE_xxx)
  SOURCE_NOT_FOUND(9001, "外部身份源不存在"),
  SOURCE_ALREADY_EXISTS(9002, "外部身份源已存在"),

  // 外部主体相关错误码 (PRINCIPAL_xxx)
  PRINCIPAL_NOT_FOUND(9101, "外部主体不存在"),
  PRINCIPAL_ALREADY_EXISTS(9102, "外部主体已存在");

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
