package io.github.lystrosaurus.admin.organization.employee.dao;

import io.github.lystrosaurus.admin.organization.employee.entity.EmployeeOrg;
import java.util.List;

/** 员工-组织关联数据访问对象接口 */
public interface EmployeeOrgDAO {

  /**
   * 保存员工-组织关联
   *
   * @param employeeOrg 关联实体
   */
  void save(EmployeeOrg employeeOrg);

  /**
   * 根据员工ID查找关联列表
   *
   * @param employeeId 员工ID
   * @return 关联列表
   */
  List<EmployeeOrg> findByEmployeeId(Long employeeId);

  /**
   * 根据组织ID查找关联列表
   *
   * @param orgId 组织ID
   * @return 关联列表
   */
  List<EmployeeOrg> findByOrgId(Long orgId);

  /**
   * 根据员工ID删除所有关联
   *
   * @param employeeId 员工ID
   */
  void deleteByEmployeeId(Long employeeId);

  /**
   * 根据员工ID和组织ID删除关联
   *
   * @param employeeId 员工ID
   * @param orgId 组织ID
   */
  void deleteByEmployeeIdAndOrgId(Long employeeId, Long orgId);
}
