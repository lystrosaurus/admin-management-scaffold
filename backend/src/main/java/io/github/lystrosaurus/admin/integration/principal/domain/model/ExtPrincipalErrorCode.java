package io.github.lystrosaurus.admin.integration.principal.domain.model;

import io.github.lystrosaurus.admin.shared.domain.exception.DomainErrorCode;

/** 外部主体上下文错误码：数值与旧 ErrorCode 保持一致。 */
public enum ExtPrincipalErrorCode implements DomainErrorCode {
  PRINCIPAL_NOT_FOUND(9101, "外部主体不存在"),
  PRINCIPAL_ALREADY_EXISTS(9102, "外部主体已存在");

  private final int code;
  private final String message;

  ExtPrincipalErrorCode(int code, String message) {
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
