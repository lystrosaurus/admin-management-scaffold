package io.github.lystrosaurus.admin.system.menu.application.assembler;

import io.github.lystrosaurus.admin.system.menu.adapter.response.MenuResponse;
import io.github.lystrosaurus.admin.system.menu.domain.model.Menu;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/** 菜单装配器：聚合 → 响应模型，并负责菜单树构建。 */
@Component
public class MenuAssembler {

  /** 聚合转菜单响应（不构建子树） */
  public MenuResponse toResponse(Menu menu) {
    return new MenuResponse(
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
        menu.status(),
        null);
  }

  /**
   * 构建菜单树。
   *
   * @param menus 全部菜单聚合
   * @return 按排序号排序的菜单树
   */
  public List<MenuResponse> toTree(List<Menu> menus) {
    if (menus.isEmpty()) {
      return new ArrayList<>();
    }

    List<MenuResponse> allResponses = menus.stream().map(this::toResponse).toList();
    Map<Long, List<MenuResponse>> parentMenuMap =
        allResponses.stream().collect(Collectors.groupingBy(this::parentKey));

    List<MenuResponse> rootMenus = new ArrayList<>();
    for (MenuResponse menu : allResponses) {
      if (menu.parentId() == null || menu.parentId() == 0L) {
        rootMenus.add(buildMenuTree(menu, parentMenuMap));
      }
    }
    sortBySortOrder(rootMenus);
    return rootMenus;
  }

  /** 递归构建菜单树节点 */
  private MenuResponse buildMenuTree(
      MenuResponse menu, Map<Long, List<MenuResponse>> parentMenuMap) {
    List<MenuResponse> children =
        parentMenuMap.getOrDefault(menu.id(), new ArrayList<>()).stream()
            .map(child -> buildMenuTree(child, parentMenuMap))
            .collect(Collectors.toList());
    sortBySortOrder(children);
    return new MenuResponse(
        menu.id(),
        menu.parentId(),
        menu.name(),
        menu.path(),
        menu.component(),
        menu.icon(),
        menu.sortOrder(),
        menu.type(),
        menu.permissionCode(),
        menu.visible(),
        menu.status(),
        children);
  }

  private Long parentKey(MenuResponse menu) {
    return menu.parentId() == null ? 0L : menu.parentId();
  }

  private void sortBySortOrder(List<MenuResponse> menus) {
    menus.sort(
        (m1, m2) -> {
          int sort1 = m1.sortOrder() != null ? m1.sortOrder() : Integer.MAX_VALUE;
          int sort2 = m2.sortOrder() != null ? m2.sortOrder() : Integer.MAX_VALUE;
          return Integer.compare(sort1, sort2);
        });
  }
}
