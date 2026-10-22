package io.github.lystrosaurus.admin.integration.source.domain.model;

import io.github.lystrosaurus.admin.shared.domain.exception.DomainErrorCode;

/** 外部身份源上下文错误码：数值与旧 ErrorCode 保持一致。 */
public enum ExtSourceErrorCode implements DomainErrorCode {
  SOURCE_NOT_FOUND(9001, "外部身份源不存在"),
  SOURCE_ALREADY_EXISTS(9002, "外部身份源已存在");

  private final int code;
  private final String message;

  ExtSourceErrorCode(int code, String message) {
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
