package io.github.lystrosaurus.admin.auth.adapter.web;

import io.github.lystrosaurus.admin.auth.adapter.request.ExternalAccountBindRequest;
import io.github.lystrosaurus.admin.auth.adapter.response.ExternalAccountResponse;
import io.github.lystrosaurus.admin.auth.annotation.RequiresPermission;
import io.github.lystrosaurus.admin.auth.application.ExternalAccountApplicationService;
import io.github.lystrosaurus.admin.shared.adapter.web.ApiResponse;
import io.github.lystrosaurus.admin.shared.annotation.Idempotent;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 三方账号控制器
 *
 * <p>提供三方账号绑定、解绑和查询接口
 */
@RestController
@RequestMapping("/app/auth/external-accounts")
@RequiredArgsConstructor
public class AuthExternalAccountController {

  private final ExternalAccountApplicationService externalAccountApplicationService;

  /**
   * 绑定三方账号
   *
   * @param request 绑定请求
   * @return 三方账号响应
   */
  @RequiresPermission("auth:external-account:bind")
  @Idempotent(prefix = "auth:bind", ttlSeconds = 10)
  @PostMapping("/bind")
  public ApiResponse<ExternalAccountResponse> bind(@RequestBody ExternalAccountBindRequest request) {
    ExternalAccountResponse response = externalAccountApplicationService.bind(request.toCommand());
    return ApiResponse.success(response);
  }

  /**
   * 解绑三方账号
   *
   * @param id 三方账号ID
   * @return 空响应
   */
  @RequiresPermission("auth:external-account:unbind")
  @DeleteMapping("/{id}")
  public ApiResponse<Void> unbind(@PathVariable Long id) {
    externalAccountApplicationService.unbind(id);
    return ApiResponse.success();
  }

  /**
   * 根据ID获取三方账号
   *
   * @param id 三方账号ID
   * @return 三方账号响应
   */
  @RequiresPermission("auth:external-account:view")
  @GetMapping("/{id}")
  public ApiResponse<ExternalAccountResponse> findById(@PathVariable Long id) {
    ExternalAccountResponse response = externalAccountApplicationService.getById(id);
    return ApiResponse.success(response);
  }

  /**
   * 根据用户ID查询三方账号列表
   *
   * @param userId 用户ID
   * @return 三方账号列表
   */
  @RequiresPermission("auth:external-account:view")
  @GetMapping("/by-user/{userId}")
  public ApiResponse<List<ExternalAccountResponse>> listByUserId(@PathVariable Long userId) {
    List<ExternalAccountResponse> accounts =
        externalAccountApplicationService.listByUserId(userId);
    return ApiResponse.success(accounts);
  }

  /**
   * 根据员工ID查询三方账号列表
   *
   * @param employeeId 员工ID
   * @return 三方账号列表
   */
  @RequiresPermission("auth:external-account:view")
  @GetMapping("/by-employee/{employeeId}")
  public ApiResponse<List<ExternalAccountResponse>> listByEmployeeId(
      @PathVariable Long employeeId) {
    List<ExternalAccountResponse> accounts =
        externalAccountApplicationService.listByEmployeeId(employeeId);
    return ApiResponse.success(accounts);
  }
}
