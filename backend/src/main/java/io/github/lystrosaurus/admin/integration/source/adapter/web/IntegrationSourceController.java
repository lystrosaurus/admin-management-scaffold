package io.github.lystrosaurus.admin.integration.source.adapter.web;

import io.github.lystrosaurus.admin.auth.annotation.RequiresPermission;
import io.github.lystrosaurus.admin.shared.adapter.web.ApiResponse;
import io.github.lystrosaurus.admin.integration.source.adapter.request.ExtSourceCreateRequest;
import io.github.lystrosaurus.admin.integration.source.adapter.request.ExtSourceUpdateRequest;
import io.github.lystrosaurus.admin.integration.source.adapter.response.ExtSourceResponse;
import io.github.lystrosaurus.admin.integration.source.application.ExtSourceApplicationService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 外部身份源控制器
 *
 * <p>提供外部身份源管理相关接口
 */
@RestController
@RequestMapping("/app/integration/sources")
@RequiredArgsConstructor
public class IntegrationSourceController {

  private final ExtSourceApplicationService extSourceApplicationService;

  /**
   * 创建外部身份源
   *
   * @param request 创建请求
   * @return 外部身份源响应
   */
  @RequiresPermission("integration:source:manage")
  @PostMapping
  public ApiResponse<ExtSourceResponse> create(@RequestBody @Valid ExtSourceCreateRequest request) {
    ExtSourceResponse response = extSourceApplicationService.create(request.toCommand());
    return ApiResponse.success(response);
  }

  /**
   * 获取外部身份源详情
   *
   * @param id 外部身份源ID
   * @return 外部身份源响应
   */
  @RequiresPermission("integration:source:view")
  @GetMapping("/{id}")
  public ApiResponse<ExtSourceResponse> findById(@PathVariable Long id) {
    ExtSourceResponse response = extSourceApplicationService.findById(id);
    return ApiResponse.success(response);
  }

  /**
   * 更新外部身份源
   *
   * @param id 外部身份源ID
   * @param request 更新请求
   * @return 外部身份源响应
   */
  @RequiresPermission("integration:source:manage")
  @PutMapping("/{id}")
  public ApiResponse<ExtSourceResponse> update(
      @PathVariable Long id, @RequestBody ExtSourceUpdateRequest request) {
    ExtSourceResponse response = extSourceApplicationService.update(id, request.toCommand());
    return ApiResponse.success(response);
  }

  /**
   * 删除外部身份源
   *
   * @param id 外部身份源ID
   * @return 空响应
   */
  @RequiresPermission("integration:source:manage")
  @DeleteMapping("/{id}")
  public ApiResponse<Void> deleteById(@PathVariable Long id) {
    extSourceApplicationService.delete(id);
    return ApiResponse.success();
  }

  /**
   * 查询所有外部身份源
   *
   * @return 外部身份源列表
   */
  @RequiresPermission("integration:source:view")
  @GetMapping
  public ApiResponse<List<ExtSourceResponse>> list() {
    List<ExtSourceResponse> sources = extSourceApplicationService.list();
    return ApiResponse.success(sources);
  }
}
