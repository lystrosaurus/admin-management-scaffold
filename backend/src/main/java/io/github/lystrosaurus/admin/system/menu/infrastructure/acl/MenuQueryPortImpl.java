package io.github.lystrosaurus.admin.system.menu.infrastructure.acl;

import io.github.lystrosaurus.admin.system.menu.domain.acl.MenuQueryPort;
import io.github.lystrosaurus.admin.system.menu.domain.acl.MenuQueryRepository;
import io.github.lystrosaurus.admin.system.menu.domain.acl.MenuSummary;
import io.github.lystrosaurus.admin.system.menu.domain.model.Menu;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/** 菜单查询端口的跨上下文实现。 */
@Repository
@RequiredArgsConstructor
public class MenuQueryPortImpl implements MenuQueryPort {

  private final MenuQueryRepository queryRepository;

  @Override
  public List<MenuSummary> findByUserId(Long userId) {
    return queryRepository.findByUserId(userId).stream().map(this::toSummary).toList();
  }

  @Override
  public List<MenuSummary> findByRoleId(Long roleId) {
    return queryRepository.findByRoleId(roleId).stream().map(this::toSummary).toList();
  }

  private MenuSummary toSummary(Menu menu) {
    return new MenuSummary(
        menu.id().value(),
        menu.parentId(),
        menu.name(),
        menu.path(),
        menu.component(),
        menu.icon(),
        menu.sortOrder(),
        menu.type(),
        menu.permissionCode(),
        menu.visible(),
        menu.status());
  }
}
