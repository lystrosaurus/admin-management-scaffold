package io.github.lystrosaurus.admin.integration.principal.adapter.web;

import io.github.lystrosaurus.admin.auth.annotation.RequiresPermission;
import io.github.lystrosaurus.admin.shared.adapter.web.ApiResponse;
import io.github.lystrosaurus.admin.integration.principal.adapter.request.ExtPrincipalCreateRequest;
import io.github.lystrosaurus.admin.integration.principal.adapter.request.ExtPrincipalUpdateRequest;
import io.github.lystrosaurus.admin.integration.principal.adapter.response.ExtPrincipalResponse;
import io.github.lystrosaurus.admin.integration.principal.application.ExtPrincipalApplicationService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 外部主体控制器
 *
 * <p>提供外部主体管理相关接口
 */
@RestController
@RequestMapping("/app/integration/principals")
@RequiredArgsConstructor
public class IntegrationPrincipalController {

  private final ExtPrincipalApplicationService extPrincipalApplicationService;

  /**
   * 创建外部主体
   *
   * @param request 创建请求
   * @return 外部主体响应
   */
  @RequiresPermission("integration:principal:manage")
  @PostMapping
  public ApiResponse<ExtPrincipalResponse> create(
      @RequestBody @Valid ExtPrincipalCreateRequest request) {
    ExtPrincipalResponse response = extPrincipalApplicationService.create(request.toCommand());
    return ApiResponse.success(response);
  }

  /**
   * 获取外部主体详情
   *
   * @param id 外部主体ID
   * @return 外部主体响应
   */
  @RequiresPermission("integration:principal:view")
  @GetMapping("/{id}")
  public ApiResponse<ExtPrincipalResponse> findById(@PathVariable Long id) {
    ExtPrincipalResponse response = extPrincipalApplicationService.findById(id);
    return ApiResponse.success(response);
  }

  /**
   * 更新外部主体
   *
   * @param id 外部主体ID
   * @param request 更新请求
   * @return 外部主体响应
   */
  @RequiresPermission("integration:principal:manage")
  @PutMapping("/{id}")
  public ApiResponse<ExtPrincipalResponse> update(
      @PathVariable Long id, @RequestBody ExtPrincipalUpdateRequest request) {
    ExtPrincipalResponse response = extPrincipalApplicationService.update(id, request.toCommand());
    return ApiResponse.success(response);
  }

  /**
   * 删除外部主体
   *
   * @param id 外部主体ID
   * @return 空响应
   */
  @RequiresPermission("integration:principal:manage")
  @DeleteMapping("/{id}")
  public ApiResponse<Void> deleteById(@PathVariable Long id) {
    extPrincipalApplicationService.delete(id);
    return ApiResponse.success();
  }

  /**
   * 查询外部主体列表
   *
   * @param sourceId 外部系统ID（可选）
   * @param linkStatus 关联状态（可选）
   * @return 外部主体列表
   */
  @RequiresPermission("integration:principal:view")
  @GetMapping
  public ApiResponse<List<ExtPrincipalResponse>> list(
      @RequestParam(required = false) Long sourceId,
      @RequestParam(required = false) String linkStatus) {
    List<ExtPrincipalResponse> principals =
        extPrincipalApplicationService.list(sourceId, linkStatus);
    return ApiResponse.success(principals);
  }
}
