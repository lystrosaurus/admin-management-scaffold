package io.github.lystrosaurus.admin.system.configitem.domain.model;

import io.github.lystrosaurus.admin.shared.domain.exception.DomainErrorCode;

/** 配置项上下文错误码（数值与消息与旧 {@code ErrorCode} 保持一致，保证 API 契约不变）。 */
public enum ConfigItemErrorCode implements DomainErrorCode {
  /** 配置项不存在（旧 ErrorCode.SYSTEM_404） */
  CONFIG_ITEM_NOT_FOUND(404, "资源不存在"),
  /** 配置键重复（旧 ErrorCode.DATA_DUPLICATE_KEY） */
  CONFIG_ITEM_DUPLICATE_KEY(6002, "数据重复");

  private final int code;
  private final String message;

  ConfigItemErrorCode(int code, String message) {
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
