package io.github.lystrosaurus.admin.system.menu.domain.acl;

import io.github.lystrosaurus.admin.system.menu.domain.model.Menu;
import java.util.List;

/** 菜单查询端口：读模型侧查询。 */
public interface MenuQueryRepository {

  /** 查询全部菜单（按排序号升序） */
  List<Menu> findAll();

  /** 查询角色拥有的菜单 */
  List<Menu> findByRoleId(Long roleId);

  /** 查询用户拥有的菜单 */
  List<Menu> findByUserId(Long userId);
}
