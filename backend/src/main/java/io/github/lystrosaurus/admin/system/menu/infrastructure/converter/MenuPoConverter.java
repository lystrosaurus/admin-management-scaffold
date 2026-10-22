package io.github.lystrosaurus.admin.system.menu.infrastructure.converter;

import io.github.lystrosaurus.admin.system.menu.domain.model.Menu;
import io.github.lystrosaurus.admin.system.menu.domain.model.MenuId;
import io.github.lystrosaurus.admin.system.menu.infrastructure.persistence.po.MenuPO;
import org.springframework.stereotype.Component;

/** 菜单聚合 ↔ 持久化对象转换器。审计字段由 MyBatis-Plus 自动填充，不在此转换。 */
@Component
public class MenuPoConverter {

  /** 领域对象转持久化对象 */
  public MenuPO toPo(Menu menu) {
    MenuPO po = new MenuPO();
    po.setId(menu.id() == null ? null : menu.id().value());
    po.setParentId(menu.parentId());
    po.setName(menu.name());
    po.setPath(menu.path());
    po.setComponent(menu.component());
    po.setIcon(menu.icon());
    po.setSortOrder(menu.sortOrder());
    po.setType(menu.type());
    po.setPermissionCode(menu.permissionCode());
    po.setVisible(menu.visible());
    po.setStatus(menu.status());
    po.setVersion(menu.version());
    return po;
  }

  /** 持久化对象转领域对象（重建聚合，不登记事件） */
  public Menu toDomain(MenuPO po) {
    return Menu.restore(
        MenuId.of(po.getId()),
        po.getParentId(),
        po.getName(),
        po.getPath(),
        po.getComponent(),
        po.getIcon(),
        po.getSortOrder(),
        po.getType(),
        po.getPermissionCode(),
        po.getVisible(),
        po.getStatus(),
        po.getCreatedAt(),
        po.getVersion());
  }
}
