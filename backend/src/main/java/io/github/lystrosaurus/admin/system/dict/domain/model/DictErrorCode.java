package io.github.lystrosaurus.admin.system.dict.domain.model;

import io.github.lystrosaurus.admin.shared.domain.exception.DomainErrorCode;

/** 字典上下文错误码（数值与消息与旧 {@code ErrorCode} 保持一致，保证 API 契约不变）。 */
public enum DictErrorCode implements DomainErrorCode {
  /** 字典不存在（旧 ErrorCode.SYSTEM_404） */
  DICT_NOT_FOUND(404, "资源不存在");

  private final int code;
  private final String message;

  DictErrorCode(int code, String message) {
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
