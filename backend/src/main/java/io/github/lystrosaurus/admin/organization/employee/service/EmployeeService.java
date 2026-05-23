package io.github.lystrosaurus.admin.organization.employee.service;

import io.github.lystrosaurus.admin.common.PageResult;
import io.github.lystrosaurus.admin.organization.employee.dto.EmployeeCreateDTO;
import io.github.lystrosaurus.admin.organization.employee.dto.EmployeeQueryDTO;
import io.github.lystrosaurus.admin.organization.employee.dto.EmployeeUpdateDTO;
import io.github.lystrosaurus.admin.organization.employee.vo.EmployeeDetailVO;
import io.github.lystrosaurus.admin.organization.employee.vo.EmployeeVO;

/** 员工服务接口 */
public interface EmployeeService {

  /**
   * 创建员工
   *
   * @param dto 员工创建DTO
   * @return 员工VO
   */
  EmployeeVO create(EmployeeCreateDTO dto);

  /**
   * 更新员工
   *
   * @param id 员工ID
   * @param dto 员工更新DTO
   * @return 员工VO
   */
  EmployeeVO update(Long id, EmployeeUpdateDTO dto);

  /**
   * 删除员工
   *
   * @param id 员工ID
   */
  void deleteById(Long id);

  /**
   * 查询员工详情（含组织信息）
   *
   * @param id 员工ID
   * @return 员工详情VO
   */
  EmployeeDetailVO findById(Long id);

  /**
   * 分页查询员工
   *
   * @param dto 查询条件
   * @param page 页码
   * @param size 每页大小
   * @return 分页结果
   */
  PageResult<EmployeeVO> findPage(EmployeeQueryDTO dto, int page, int size);
}
