package io.github.lystrosaurus.admin.organization.employee.dao;

import io.github.lystrosaurus.admin.organization.employee.entity.HrEmployee;
import java.util.List;

/**
 * 员工数据访问对象接口
 *
 * <p>定义 Service 需要的数据访问语义，而不是简单地暴露 Mapper 方法。
 */
public interface EmployeeDAO {

  /**
   * 根据ID查找员工
   *
   * @param id 员工ID
   * @return 员工实体，不存在时返回null
   */
  HrEmployee findById(Long id);

  /**
   * 根据工号查找员工
   *
   * @param employeeNo 员工工号
   * @return 员工实体，不存在时返回null
   */
  HrEmployee findByEmployeeNo(String employeeNo);

  /**
   * 保存员工
   *
   * @param employee 员工实体
   */
  void save(HrEmployee employee);

  /**
   * 更新员工
   *
   * @param employee 员工实体
   */
  void update(HrEmployee employee);

  /**
   * 根据ID删除员工
   *
   * @param id 员工ID
   */
  void deleteById(Long id);

  /**
   * 根据条件查找员工列表
   *
   * @param keyword 关键词(模糊匹配姓名/工号/手机号)
   * @param employmentStatus 在职状态
   * @param orgId 主组织ID
   * @param page 页码
   * @param size 每页大小
   * @return 员工列表
   */
  List<HrEmployee> findByCondition(
      String keyword, String employmentStatus, Long orgId, int page, int size);

  /**
   * 根据条件统计员工数量
   *
   * @param keyword 关键词
   * @param employmentStatus 在职状态
   * @param orgId 主组织ID
   * @return 员工数量
   */
  long countByCondition(String keyword, String employmentStatus, Long orgId);

  /**
   * 检查工号是否存在
   *
   * @param employeeNo 员工工号
   * @return 存在返回true，否则返回false
   */
  boolean existsByEmployeeNo(String employeeNo);

  /**
   * 检查工号是否存在且排除指定ID
   *
   * @param employeeNo 员工工号
   * @param id 排除的员工ID
   * @return 存在返回true，否则返回false
   */
  boolean existsByEmployeeNoAndIdNot(String employeeNo, Long id);
}
