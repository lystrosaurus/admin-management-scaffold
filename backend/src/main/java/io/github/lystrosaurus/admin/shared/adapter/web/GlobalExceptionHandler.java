package io.github.lystrosaurus.admin.shared.adapter.web;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import io.github.lystrosaurus.admin.shared.adapter.web.ApiResponse;
import io.github.lystrosaurus.admin.shared.kernel.exception.BusinessException;
import io.github.lystrosaurus.admin.shared.kernel.exception.ErrorCode;
import io.github.lystrosaurus.admin.shared.domain.exception.DomainException;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/** 全局异常处理器 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  /** 处理业务异常 */
  @ExceptionHandler(BusinessException.class)
  @ResponseStatus(HttpStatus.OK)
  public ApiResponse<Void> handleBusinessException(BusinessException e) {
    log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
    return ApiResponse.error(e.getCode(), e.getMessage());
  }

  /** 处理领域异常（DDD 四层化后由聚合/应用服务抛出，数值错误码与业务异常同契约） */
  @ExceptionHandler(DomainException.class)
  @ResponseStatus(HttpStatus.OK)
  public ApiResponse<Void> handleDomainException(DomainException e) {
    log.warn("领域异常: code={}, message={}", e.code(), e.getMessage());
    return ApiResponse.error(e.code(), e.getMessage());
  }

  /** 处理未登录异常（Sa-Token 拦截器在 /app/** 未携带有效 token 时抛出） */
  @ExceptionHandler(NotLoginException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ApiResponse<Void> handleNotLoginException(NotLoginException e) {
    log.warn("未登录: {}", e.getMessage());
    return ApiResponse.error(ErrorCode.AUTH_401);
  }

  /** 处理无权限 / 无角色异常 */
  @ExceptionHandler({NotPermissionException.class, NotRoleException.class})
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ApiResponse<Void> handleNotPermissionException(Exception e) {
    log.warn("无权限: {}", e.getMessage());
    return ApiResponse.error(ErrorCode.PERMISSION_DENIED);
  }

  /** 处理参数校验异常 */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiResponse<Void> handleValidationException(MethodArgumentNotValidException e) {
    String message =
        e.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining(", "));
    log.warn("参数校验失败: {}", message);
    return ApiResponse.error(ErrorCode.SYSTEM_400.getCode(), message);
  }

  /** 处理绑定异常 */
  @ExceptionHandler(BindException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiResponse<Void> handleBindException(BindException e) {
    String message =
        e.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining(", "));
    log.warn("参数绑定失败: {}", message);
    return ApiResponse.error(ErrorCode.SYSTEM_400.getCode(), message);
  }

  /** 处理不存在的接口 / 静态资源：应返回 404，而不是落到 500 */
  @ExceptionHandler({NoResourceFoundException.class, NoHandlerFoundException.class})
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ApiResponse<Void> handleNotFound(Exception e) {
    log.warn("资源不存在: {}", e.getMessage());
    return ApiResponse.error(ErrorCode.SYSTEM_404);
  }

  /** 处理运行时异常 */
  @ExceptionHandler(RuntimeException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ApiResponse<Void> handleRuntimeException(RuntimeException e) {
    log.error("运行时异常", e);
    return ApiResponse.error(ErrorCode.SYSTEM_500);
  }

  /** 处理所有其他异常 */
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ApiResponse<Void> handleException(Exception e) {
    log.error("系统异常", e);
    return ApiResponse.error(ErrorCode.SYSTEM_500);
  }
}
