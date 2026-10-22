package io.github.lystrosaurus.admin.organization.employee.adapter.web;

import io.github.lystrosaurus.admin.auth.annotation.RequiresPermission;
import io.github.lystrosaurus.admin.common.ApiResponse;
import io.github.lystrosaurus.admin.common.PageResult;
import io.github.lystrosaurus.admin.organization.employee.adapter.request.EmployeeCreateRequest;
import io.github.lystrosaurus.admin.organization.employee.adapter.request.EmployeeUpdateRequest;
import io.github.lystrosaurus.admin.organization.employee.adapter.response.EmployeeDetailResponse;
import io.github.lystrosaurus.admin.organization.employee.adapter.response.EmployeeResponse;
import io.github.lystrosaurus.admin.organization.employee.application.EmployeeApplicationService;
import io.github.lystrosaurus.admin.organization.employee.application.command.EmployeeQuery;
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
 * 员工控制器
 *
 * <p>提供员工管理相关接口
 */
@RestController
@RequestMapping("/app/employees")
@RequiredArgsConstructor
public class EmployeeController {

  private final EmployeeApplicationService employeeApplicationService;

  /**
   * 创建员工
   *
   * @param request 员工创建请求
   * @return 员工响应
   */
  @RequiresPermission("organization:employee:create")
  @PostMapping
  public ApiResponse<EmployeeResponse> create(@RequestBody @Valid EmployeeCreateRequest request) {
    EmployeeResponse response = employeeApplicationService.create(request.toCommand());
    return ApiResponse.success(response);
  }

  /**
   * 获取员工详情
   *
   * @param id 员工ID
   * @return 员工详情响应
   */
  @RequiresPermission("organization:employee:view")
  @GetMapping("/{id}")
  public ApiResponse<EmployeeDetailResponse> findById(@PathVariable Long id) {
    EmployeeDetailResponse response = employeeApplicationService.findById(id);
    return ApiResponse.success(response);
  }

  /**
   * 更新员工
   *
   * @param id 员工ID
   * @param request 员工更新请求
   * @return 员工响应
   */
  @RequiresPermission("organization:employee:update")
  @PutMapping("/{id}")
  public ApiResponse<EmployeeResponse> update(
      @PathVariable Long id, @RequestBody EmployeeUpdateRequest request) {
    EmployeeResponse response = employeeApplicationService.update(id, request.toCommand());
    return ApiResponse.success(response);
  }

  /**
   * 删除员工
   *
   * @param id 员工ID
   * @return 空响应
   */
  @RequiresPermission("organization:employee:delete")
  @DeleteMapping("/{id}")
  public ApiResponse<Void> deleteById(@PathVariable Long id) {
    employeeApplicationService.deleteById(id);
    return ApiResponse.success();
  }

  /**
   * 分页查询员工
   *
   * @param page 页码
   * @param size 每页大小
   * @param keyword 关键词
   * @param employmentStatus 在职状态
   * @param orgId 主组织ID
   * @return 分页结果
   */
  @RequiresPermission("organization:employee:view")
  @GetMapping
  public ApiResponse<PageResult<EmployeeResponse>> findPage(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) String employmentStatus,
      @RequestParam(required = false) Long orgId) {
    EmployeeQuery query = new EmployeeQuery(keyword, employmentStatus, orgId);
    PageResult<EmployeeResponse> pageResult = employeeApplicationService.findPage(query, page, size);
    return ApiResponse.success(pageResult);
  }
}
