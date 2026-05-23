package io.github.lystrosaurus.admin.common;

import io.github.lystrosaurus.admin.exception.ErrorCode;
import lombok.Data;

/**
 * 统一 API 响应包装类
 *
 * @param <T> 响应数据类型
 */
@Data
public class ApiResponse<T> {

  /** 状态码 */
  private int code;

  /** 消息 */
  private String message;

  /** 响应数据 */
  private T data;

  /** 时间戳 */
  private long timestamp;

  public ApiResponse() {
    this.timestamp = System.currentTimeMillis();
  }

  public ApiResponse(int code, String message, T data) {
    this.code = code;
    this.message = message;
    this.data = data;
    this.timestamp = System.currentTimeMillis();
  }

  /** 成功响应（无数据） */
  public static <T> ApiResponse<T> success() {
    return new ApiResponse<>(200, "操作成功", null);
  }

  /** 成功响应（带数据） */
  public static <T> ApiResponse<T> success(T data) {
    return new ApiResponse<>(200, "操作成功", data);
  }

  /** 成功响应（带消息和数据） */
  public static <T> ApiResponse<T> success(String message, T data) {
    return new ApiResponse<>(200, message, data);
  }

  /** 失败响应 */
  public static <T> ApiResponse<T> error(int code, String message) {
    return new ApiResponse<>(code, message, null);
  }

  /** 失败响应（使用 ErrorCode） */
  public static <T> ApiResponse<T> error(ErrorCode errorCode) {
    return new ApiResponse<>(errorCode.getCode(), errorCode.getMessage(), null);
  }
}
