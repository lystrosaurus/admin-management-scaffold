package io.github.lystrosaurus.admin.system.menu.domain.acl;

import io.github.lystrosaurus.admin.shared.domain.acl.GenericRepository;
import io.github.lystrosaurus.admin.system.menu.domain.model.Menu;
import io.github.lystrosaurus.admin.system.menu.domain.model.MenuId;

/** 菜单仓储端口：菜单聚合的存取契约。 */
public interface MenuRepository extends GenericRepository<Menu, MenuId> {}
