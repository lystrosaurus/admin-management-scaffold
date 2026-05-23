package io.github.lystrosaurus.admin.organization.employee.controller;

import io.github.lystrosaurus.admin.common.ApiResponse;
import io.github.lystrosaurus.admin.common.PageResult;
import io.github.lystrosaurus.admin.organization.employee.dto.EmployeeCreateDTO;
import io.github.lystrosaurus.admin.organization.employee.dto.EmployeeQueryDTO;
import io.github.lystrosaurus.admin.organization.employee.dto.EmployeeUpdateDTO;
import io.github.lystrosaurus.admin.organization.employee.service.EmployeeService;
import io.github.lystrosaurus.admin.organization.employee.vo.EmployeeDetailVO;
import io.github.lystrosaurus.admin.organization.employee.vo.EmployeeVO;
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

  private final EmployeeService employeeService;

  /**
   * 创建员工
   *
   * @param dto 员工创建DTO
   * @return 员工VO
   */
  @PostMapping
  public ApiResponse<EmployeeVO> create(@RequestBody @Valid EmployeeCreateDTO dto) {
    EmployeeVO employeeVO = employeeService.create(dto);
    return ApiResponse.success(employeeVO);
  }

  /**
   * 获取员工详情
   *
   * @param id 员工ID
   * @return 员工详情VO
   */
  @GetMapping("/{id}")
  public ApiResponse<EmployeeDetailVO> findById(@PathVariable Long id) {
    EmployeeDetailVO detailVO = employeeService.findById(id);
    return ApiResponse.success(detailVO);
  }

  /**
   * 更新员工
   *
   * @param id 员工ID
   * @param dto 员工更新DTO
   * @return 员工VO
   */
  @PutMapping("/{id}")
  public ApiResponse<EmployeeVO> update(@PathVariable Long id, @RequestBody EmployeeUpdateDTO dto) {
    EmployeeVO employeeVO = employeeService.update(id, dto);
    return ApiResponse.success(employeeVO);
  }

  /**
   * 删除员工
   *
   * @param id 员工ID
   * @return 空响应
   */
  @DeleteMapping("/{id}")
  public ApiResponse<Void> deleteById(@PathVariable Long id) {
    employeeService.deleteById(id);
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
  @GetMapping
  public ApiResponse<PageResult<EmployeeVO>> findPage(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) String employmentStatus,
      @RequestParam(required = false) Long orgId) {
    EmployeeQueryDTO queryDTO = new EmployeeQueryDTO(keyword, employmentStatus, orgId);
    PageResult<EmployeeVO> pageResult = employeeService.findPage(queryDTO, page, size);
    return ApiResponse.success(pageResult);
  }
}
