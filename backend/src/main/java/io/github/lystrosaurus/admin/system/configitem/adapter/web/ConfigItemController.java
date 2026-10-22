package io.github.lystrosaurus.admin.system.configitem.adapter.web;

import io.github.lystrosaurus.admin.auth.annotation.RequiresPermission;
import io.github.lystrosaurus.admin.shared.adapter.web.ApiResponse;
import io.github.lystrosaurus.admin.shared.adapter.web.PageResult;
import io.github.lystrosaurus.admin.system.configitem.adapter.request.ConfigItemCreateRequest;
import io.github.lystrosaurus.admin.system.configitem.adapter.request.ConfigItemUpdateRequest;
import io.github.lystrosaurus.admin.system.configitem.adapter.response.ConfigItemResponse;
import io.github.lystrosaurus.admin.system.configitem.application.ConfigItemApplicationService;
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
 * 系统配置项控制器
 *
 * <p>提供系统配置项管理相关接口
 */
@RestController
@RequestMapping("/app/system/config-item")
@RequiredArgsConstructor
public class ConfigItemController {

  private final ConfigItemApplicationService configItemApplicationService;

  /**
   * 查询配置项列表
   *
   * @return 配置项列表
   */
  @RequiresPermission("config:view")
  @GetMapping
  public ApiResponse<PageResult<ConfigItemResponse>> list(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) String configKey) {
    PageResult<ConfigItemResponse> result =
        configItemApplicationService.list(configKey, page, size);
    return ApiResponse.success(result);
  }

  /**
   * 根据ID获取配置项
   *
   * @param id 配置项ID
   * @return 配置项响应
   */
  @RequiresPermission("config:view")
  @GetMapping("/{id}")
  public ApiResponse<ConfigItemResponse> getById(@PathVariable Long id) {
    ConfigItemResponse response = configItemApplicationService.getById(id);
    return ApiResponse.success(response);
  }

  /**
   * 创建配置项
   *
   * @param request 配置项创建请求
   * @return 配置项响应
   */
  @RequiresPermission("config:manage")
  @PostMapping
  public ApiResponse<ConfigItemResponse> create(
      @RequestBody @Valid ConfigItemCreateRequest request) {
    ConfigItemResponse response = configItemApplicationService.create(request.toCommand());
    return ApiResponse.success(response);
  }

  /**
   * 更新配置项
   *
   * @param id 配置项ID
   * @param request 配置项更新请求
   * @return 配置项响应
   */
  @RequiresPermission("config:manage")
  @PutMapping("/{id}")
  public ApiResponse<ConfigItemResponse> update(
      @PathVariable Long id, @RequestBody ConfigItemUpdateRequest request) {
    ConfigItemResponse response = configItemApplicationService.update(id, request.toCommand());
    return ApiResponse.success(response);
  }

  /**
   * 删除配置项
   *
   * @param id 配置项ID
   * @return 空响应
   */
  @RequiresPermission("config:manage")
  @DeleteMapping("/{id}")
  public ApiResponse<Void> deleteById(@PathVariable Long id) {
    configItemApplicationService.deleteById(id);
    return ApiResponse.success();
  }
}
