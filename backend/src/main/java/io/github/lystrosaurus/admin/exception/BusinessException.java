package io.github.lystrosaurus.admin.exception;

/** 业务异常类 */
public class BusinessException extends RuntimeException {

  private final int code;
  private final String message;

  public BusinessException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.code = errorCode.getCode();
    this.message = errorCode.getMessage();
  }

  public BusinessException(int code, String message) {
    super(message);
    this.code = code;
    this.message = message;
  }

  public BusinessException(String message) {
    super(message);
    this.code = ErrorCode.INTERNAL_ERROR.getCode();
    this.message = message;
  }

  public BusinessException(int code, String message, Throwable cause) {
    super(message, cause);
    this.code = code;
    this.message = message;
  }

  public int getCode() {
    return code;
  }

  @Override
  public String getMessage() {
    return message;
  }
}
