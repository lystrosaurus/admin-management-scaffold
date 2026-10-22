package io.github.lystrosaurus.admin.auth.adapter.web;

import io.github.lystrosaurus.admin.auth.adapter.request.LoginRequest;
import io.github.lystrosaurus.admin.auth.adapter.response.LoginResponse;
import io.github.lystrosaurus.admin.auth.application.AuthApplicationService;
import io.github.lystrosaurus.admin.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证控制器
 *
 * <p>提供登录、登出、刷新 Token 等认证相关接口
 */
@RestController
@RequestMapping("/public/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthApplicationService authApplicationService;

  /**
   * 用户登录
   *
   * @param request 登录请求
   * @return 登录结果
   */
  @PostMapping("/login")
  public ApiResponse<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
    LoginResponse loginResponse = authApplicationService.login(request);
    return ApiResponse.success(loginResponse);
  }

  /**
   * 用户登出
   *
   * @return 登出结果
   */
  @PostMapping("/logout")
  public ApiResponse<Void> logout() {
    authApplicationService.logout();
    return ApiResponse.success();
  }

  /**
   * 刷新 Token
   *
   * @return 新的 Token
   */
  @GetMapping("/refresh")
  public ApiResponse<LoginResponse> refreshToken() {
    LoginResponse loginResponse = authApplicationService.refreshToken();
    return ApiResponse.success(loginResponse);
  }
}
