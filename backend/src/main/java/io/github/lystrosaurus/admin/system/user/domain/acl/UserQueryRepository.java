package io.github.lystrosaurus.admin.system.user.domain.acl;

import io.github.lystrosaurus.admin.shared.domain.acl.Page;
import io.github.lystrosaurus.admin.shared.domain.acl.PageQuery;
import io.github.lystrosaurus.admin.system.user.domain.model.User;
import io.github.lystrosaurus.admin.system.user.domain.model.UserId;
import java.util.List;

/** 用户查询端口：面向读模型的查询契约（CQRS 查询侧）。 */
public interface UserQueryRepository {

  /** 按条件分页查询用户 */
  Page<User> findPage(String username, String status, PageQuery pageQuery);

  /** 按条件统计用户数量 */
  long countByCondition(String username, String status);

  /** 查询用户关联的角色 */
  List<UserRoleInfo> findRolesByUserId(UserId userId);
}
