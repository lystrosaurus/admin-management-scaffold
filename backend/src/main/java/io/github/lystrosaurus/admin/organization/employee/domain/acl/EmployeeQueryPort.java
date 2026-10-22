package io.github.lystrosaurus.admin.organization.employee.domain.acl;

import java.util.Collection;
import java.util.Map;

/** 员工查询端口：供 user、orgunit 等其它上下文读取员工信息。 */
public interface EmployeeQueryPort {

  /** 判断员工是否存在 */
  boolean existsById(Long employeeId);

  /** 按ID集合查询员工姓名（员工ID → 姓名） */
  Map<Long, String> findNamesByIds(Collection<Long> employeeIds);

  /** 统计员工总数 */
  long countAll();
}
