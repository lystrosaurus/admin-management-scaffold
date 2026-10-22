package io.github.lystrosaurus.admin.organization.orgunit.adapter.web;

import io.github.lystrosaurus.admin.auth.annotation.RequiresPermission;
import io.github.lystrosaurus.admin.shared.adapter.web.ApiResponse;
import io.github.lystrosaurus.admin.shared.adapter.web.PageResult;
import io.github.lystrosaurus.admin.organization.orgunit.adapter.request.OrgUnitCreateRequest;
import io.github.lystrosaurus.admin.organization.orgunit.adapter.request.OrgUnitUpdateRequest;
import io.github.lystrosaurus.admin.organization.orgunit.adapter.response.OrgUnitResponse;
import io.github.lystrosaurus.admin.organization.orgunit.adapter.response.OrgUnitTreeResponse;
import io.github.lystrosaurus.admin.organization.orgunit.application.OrgUnitApplicationService;
import io.github.lystrosaurus.admin.organization.orgunit.application.command.OrgUnitQuery;
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
 * 组织单元控制器
 *
 * <p>提供组织单元管理相关接口
 */
@RestController
@RequestMapping("/app/org-units")
@RequiredArgsConstructor
public class OrgUnitController {

  private final OrgUnitApplicationService orgUnitApplicationService;

  /**
   * 创建组织单元
   *
   * @param request 组织单元创建请求
   * @return 组织单元响应
   */
  @RequiresPermission("organization:org-unit:create")
  @PostMapping
  public ApiResponse<OrgUnitResponse> create(@RequestBody @Valid OrgUnitCreateRequest request) {
    OrgUnitResponse response = orgUnitApplicationService.create(request.toCommand());
    return ApiResponse.success(response);
  }

  /**
   * 获取组织单元详情
   *
   * @param id 组织单元ID
   * @return 组织单元响应
   */
  @RequiresPermission("organization:org-unit:view")
  @GetMapping("/{id}")
  public ApiResponse<OrgUnitResponse> findById(@PathVariable Long id) {
    OrgUnitResponse response = orgUnitApplicationService.findById(id);
    return ApiResponse.success(response);
  }

  /**
   * 更新组织单元
   *
   * @param id 组织单元ID
   * @param request 组织单元更新请求
   * @return 组织单元响应
   */
  @RequiresPermission("organization:org-unit:update")
  @PutMapping("/{id}")
  public ApiResponse<OrgUnitResponse> update(
      @PathVariable Long id, @RequestBody OrgUnitUpdateRequest request) {
    OrgUnitResponse response = orgUnitApplicationService.update(id, request.toCommand());
    return ApiResponse.success(response);
  }

  /**
   * 删除组织单元
   *
   * @param id 组织单元ID
   * @return 空响应
   */
  @RequiresPermission("organization:org-unit:delete")
  @DeleteMapping("/{id}")
  public ApiResponse<Void> deleteById(@PathVariable Long id) {
    orgUnitApplicationService.deleteById(id);
    return ApiResponse.success();
  }

  /**
   * 分页查询组织单元
   *
   * @param page 页码
   * @param size 每页大小
   * @param keyword 关键词
   * @param status 状态
   * @param parentId 父节点ID
   * @return 分页结果
   */
  @RequiresPermission("organization:org-unit:view")
  @GetMapping
  public ApiResponse<PageResult<OrgUnitResponse>> findPage(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) String status,
      @RequestParam(required = false) Long parentId) {
    OrgUnitQuery query = new OrgUnitQuery(keyword, status, parentId);
    PageResult<OrgUnitResponse> pageResult = orgUnitApplicationService.findPage(query, page, size);
    return ApiResponse.success(pageResult);
  }

  /**
   * 获取组织架构树
   *
   * @return 组织架构树
   */
  @RequiresPermission("organization:org-unit:view")
  @GetMapping("/tree")
  public ApiResponse<List<OrgUnitTreeResponse>> findTree() {
    List<OrgUnitTreeResponse> tree = orgUnitApplicationService.findTree();
    return ApiResponse.success(tree);
  }
}
