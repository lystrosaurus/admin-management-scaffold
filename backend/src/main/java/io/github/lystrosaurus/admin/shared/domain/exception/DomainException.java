package io.github.lystrosaurus.admin.shared.domain.exception;

/** 领域异常：业务不变量被违反时由聚合或领域服务抛出 */
public class DomainException extends RuntimeException {

  private final int code;

  public DomainException(DomainErrorCode errorCode) {
    super(errorCode.message());
    this.code = errorCode.code();
  }

  public DomainException(DomainErrorCode errorCode, String message) {
    super(message);
    this.code = errorCode.code();
  }

  /** 数值错误码，由全局异常处理器映射到统一响应 */
  public int code() {
    return code;
  }
}
