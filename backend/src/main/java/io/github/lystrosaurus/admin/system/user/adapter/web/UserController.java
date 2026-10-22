package io.github.lystrosaurus.admin.system.user.adapter.web;

import io.github.lystrosaurus.admin.auth.annotation.RequiresPermission;
import io.github.lystrosaurus.admin.shared.adapter.web.ApiResponse;
import io.github.lystrosaurus.admin.shared.adapter.web.PageResult;
import io.github.lystrosaurus.admin.shared.annotation.Idempotent;
import io.github.lystrosaurus.admin.system.user.adapter.request.ChangePasswordRequest;
import io.github.lystrosaurus.admin.system.user.adapter.request.EmployeeBindRequest;
import io.github.lystrosaurus.admin.system.user.adapter.request.UserCreateRequest;
import io.github.lystrosaurus.admin.system.user.adapter.request.UserUpdateRequest;
import io.github.lystrosaurus.admin.system.user.adapter.response.UserDetailResponse;
import io.github.lystrosaurus.admin.system.user.adapter.response.UserResponse;
import io.github.lystrosaurus.admin.system.user.application.UserApplicationService;
import io.github.lystrosaurus.admin.system.user.application.command.UserQuery;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户控制器
 *
 * <p>提供用户管理相关接口
 */
@RestController
@RequestMapping("/app/users")
@RequiredArgsConstructor
public class UserController {

  private final UserApplicationService userApplicationService;

  /**
   * 创建用户
   *
   * @param request 用户创建请求
   * @return 用户响应
   */
  @RequiresPermission("system:user:create")
  @Idempotent(prefix = "user:create", ttlSeconds = 10)
  @PostMapping
  public ApiResponse<UserResponse> create(@RequestBody @Valid UserCreateRequest request) {
    UserResponse response = userApplicationService.create(request.toCommand());
    return ApiResponse.success(response);
  }

  /**
   * 获取用户详情
   *
   * @param id 用户ID
   * @return 用户详情响应
   */
  @RequiresPermission("system:user:view")
  @GetMapping("/{id}")
  public ApiResponse<UserDetailResponse> findById(@PathVariable Long id) {
    UserDetailResponse response = userApplicationService.findById(id);
    return ApiResponse.success(response);
  }

  /**
   * 更新用户
   *
   * @param id 用户ID
   * @param request 用户更新请求
   * @return 用户响应
   */
  @RequiresPermission("system:user:update")
  @PutMapping("/{id}")
  public ApiResponse<UserResponse> update(
      @PathVariable Long id, @RequestBody UserUpdateRequest request) {
    UserResponse response = userApplicationService.update(id, request.toCommand());
    return ApiResponse.success(response);
  }

  /**
   * 删除用户
   *
   * @param id 用户ID
   * @return 空响应
   */
  @RequiresPermission("system:user:delete")
  @DeleteMapping("/{id}")
  public ApiResponse<Void> deleteById(@PathVariable Long id) {
    userApplicationService.deleteById(id);
    return ApiResponse.success();
  }

  /**
   * 分页查询用户
   *
   * @param page 页码
   * @param size 每页大小
   * @param username 用户名
   * @param status 状态
   * @param keyword 关键词
   * @return 分页结果
   */
  @RequiresPermission("system:user:view")
  @GetMapping
  public ApiResponse<PageResult<UserResponse>> findPage(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) String username,
      @RequestParam(required = false) String status,
      @RequestParam(required = false) String keyword) {
    UserQuery query = new UserQuery(username, status, keyword);
    PageResult<UserResponse> pageResult = userApplicationService.findPage(query, page, size);
    return ApiResponse.success(pageResult);
  }

  /**
   * 修改密码
   *
   * @param id 用户ID
   * @param request 修改密码请求
   * @return 空响应
   */
  @RequiresPermission("system:user:reset-password")
  @PutMapping("/{id}/password")
  public ApiResponse<Void> changePassword(
      @PathVariable Long id, @RequestBody @Valid ChangePasswordRequest request) {
    userApplicationService.changePassword(id, request.toCommand());
    return ApiResponse.success();
  }

  /**
   * 绑定员工
   *
   * @param id 用户ID
   * @param request 员工绑定请求
   * @return 空响应
   */
  @RequiresPermission("system:user:update")
  @PostMapping("/{id}/employee")
  public ApiResponse<Void> bindEmployee(
      @PathVariable Long id, @RequestBody @Valid EmployeeBindRequest request) {
    userApplicationService.bindEmployee(id, request.toCommand());
    return ApiResponse.success();
  }

  /**
   * 解绑员工
   *
   * @param id 用户ID
   * @return 空响应
   */
  @RequiresPermission("system:user:update")
  @DeleteMapping("/{id}/employee")
  public ApiResponse<Void> unbindEmployee(@PathVariable Long id) {
    userApplicationService.unbindEmployee(id);
    return ApiResponse.success();
  }
}
