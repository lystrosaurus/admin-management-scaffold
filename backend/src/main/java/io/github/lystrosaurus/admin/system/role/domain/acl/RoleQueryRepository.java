package io.github.lystrosaurus.admin.system.role.domain.acl;

import io.github.lystrosaurus.admin.shared.domain.acl.Page;
import io.github.lystrosaurus.admin.shared.domain.acl.PageQuery;
import io.github.lystrosaurus.admin.system.role.domain.model.Role;
import java.util.List;

/** 角色查询端口：读模型侧查询（分页、列表、统计）。 */
public interface RoleQueryRepository {

  /** 按条件分页查询角色（关键词匹配编码/名称，按创建时间倒序） */
  Page<Role> findPage(String keyword, String status, PageQuery pageQuery);

  /** 按条件统计角色数量 */
  long countByCondition(String keyword, String status);

  /** 查询全部角色（按排序号升序） */
  List<Role> findAll();

  /** 查询用户拥有的角色 */
  List<Role> findByUserId(Long userId);
}
