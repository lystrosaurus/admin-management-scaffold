package io.github.lystrosaurus.admin.shared.domain.exception;

/** 领域错误码契约：各上下文的错误码枚举实现此接口即可被领域异常携带 */
public interface DomainErrorCode {

  /** 数值错误码 */
  int code();

  /** 默认错误消息 */
  String message();
}
