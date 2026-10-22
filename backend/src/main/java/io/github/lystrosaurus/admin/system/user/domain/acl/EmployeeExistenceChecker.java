package io.github.lystrosaurus.admin.system.user.domain.acl;

/** 员工存在性检查端口：user 上下文不直接依赖 organization 上下文的存储细节。 */
public interface EmployeeExistenceChecker {

  /** 判断员工是否存在 */
  boolean exists(Long employeeId);
}
