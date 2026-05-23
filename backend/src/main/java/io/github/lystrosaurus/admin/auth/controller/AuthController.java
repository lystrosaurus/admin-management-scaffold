package io.github.lystrosaurus.admin.auth.controller;

import io.github.lystrosaurus.admin.auth.service.AuthService;
import io.github.lystrosaurus.admin.auth.vo.LoginVO;
import io.github.lystrosaurus.admin.common.ApiResponse;
import io.github.lystrosaurus.admin.system.user.dto.LoginDTO;
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

  private final AuthService authService;

  /**
   * 用户登录
   *
   * @param dto 登录参数
   * @return 登录结果
   */
  @PostMapping("/login")
  public ApiResponse<LoginVO> login(@RequestBody @Valid LoginDTO dto) {
    LoginVO loginVO = authService.login(dto);
    return ApiResponse.success(loginVO);
  }

  /**
   * 用户登出
   *
   * @return 登出结果
   */
  @PostMapping("/logout")
  public ApiResponse<Void> logout() {
    authService.logout();
    return ApiResponse.success();
  }

  /**
   * 刷新 Token
   *
   * @return 新的 Token
   */
  @GetMapping("/refresh")
  public ApiResponse<LoginVO> refreshToken() {
    LoginVO loginVO = authService.refreshToken();
    return ApiResponse.success(loginVO);
  }
}
