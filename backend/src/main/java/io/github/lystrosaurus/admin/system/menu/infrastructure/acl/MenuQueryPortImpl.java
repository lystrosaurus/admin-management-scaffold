package io.github.lystrosaurus.admin.system.menu.infrastructure.acl;

import io.github.lystrosaurus.admin.system.menu.domain.acl.MenuQueryPort;
import io.github.lystrosaurus.admin.system.menu.domain.acl.MenuQueryRepository;
import io.github.lystrosaurus.admin.system.menu.domain.acl.MenuSummary;
import io.github.lystrosaurus.admin.shared.infrastructure.cache.TwoLevelCache;
import io.github.lystrosaurus.admin.system.menu.domain.model.Menu;
import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import tools.jackson.core.type.TypeReference;

/** 菜单查询端口的跨上下文实现。 */
@Repository
@RequiredArgsConstructor
public class MenuQueryPortImpl implements MenuQueryPort {

  private static final String CACHE_NAME = "menu:user";
  private static final TypeReference<List<MenuSummary>> MENU_LIST = new TypeReference<>() {};
  private static final Duration L1_TTL = Duration.ofSeconds(60);
  private static final Duration L2_TTL = Duration.ofSeconds(300);

  private final MenuQueryRepository queryRepository;
  private final TwoLevelCache twoLevelCache;

  @Override
  public List<MenuSummary> findByUserId(Long userId) {
    return twoLevelCache.get(
        CACHE_NAME,
        userId,
        MENU_LIST,
        L1_TTL,
        L2_TTL,
        () -> queryRepository.findByUserId(userId).stream().map(this::toSummary).toList());
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
