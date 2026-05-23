package io.github.lystrosaurus.admin.system.menu.service.impl;

import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.system.menu.dao.MenuDAO;
import io.github.lystrosaurus.admin.system.menu.dto.MenuCreateDTO;
import io.github.lystrosaurus.admin.system.menu.dto.MenuUpdateDTO;
import io.github.lystrosaurus.admin.system.menu.entity.SysMenu;
import io.github.lystrosaurus.admin.system.menu.mapstruct.MenuMapper;
import io.github.lystrosaurus.admin.system.menu.service.MenuService;
import io.github.lystrosaurus.admin.system.menu.vo.MenuVO;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/** 菜单服务实现 */
@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

  private final MenuDAO menuDAO;
  private final MenuMapper menuMapper;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public MenuVO create(MenuCreateDTO dto) {
    // 如果有父菜单，检查父菜单是否存在
    if (dto.parentId() != null && dto.parentId() > 0) {
      SysMenu parentMenu = menuDAO.findById(dto.parentId());
      if (parentMenu == null) {
        throw new BusinessException(ErrorCode.MENU_NOT_FOUND);
      }
    }

    // 转换并保存菜单
    SysMenu menu = new SysMenu();
    menu.setParentId(dto.parentId() != null ? dto.parentId() : 0L);
    menu.setName(dto.name());
    menu.setPath(dto.path());
    menu.setComponent(dto.component());
    menu.setIcon(dto.icon());
    menu.setSortOrder(dto.sortOrder());
    menu.setType(dto.type());
    menu.setPermissionCode(dto.permissionCode());
    menu.setVisible(dto.visible() != null ? dto.visible() : (byte) 1);
    menu.setStatus((byte) 1);
    menuDAO.save(menu);

    return menuMapper.toMenuVO(menu);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public MenuVO update(Long id, MenuUpdateDTO dto) {
    // 查找菜单
    SysMenu menu = menuDAO.findById(id);
    if (menu == null) {
      throw new BusinessException(ErrorCode.MENU_NOT_FOUND);
    }

    // 更新字段
    if (StringUtils.hasText(dto.name())) {
      menu.setName(dto.name());
    }
    if (StringUtils.hasText(dto.path())) {
      menu.setPath(dto.path());
    }
    if (StringUtils.hasText(dto.component())) {
      menu.setComponent(dto.component());
    }
    if (StringUtils.hasText(dto.icon())) {
      menu.setIcon(dto.icon());
    }
    if (dto.sortOrder() != null) {
      menu.setSortOrder(dto.sortOrder());
    }
    if (dto.type() != null) {
      menu.setType(dto.type());
    }
    if (StringUtils.hasText(dto.permissionCode())) {
      menu.setPermissionCode(dto.permissionCode());
    }
    if (dto.visible() != null) {
      menu.setVisible(dto.visible());
    }
    if (dto.status() != null) {
      menu.setStatus(dto.status());
    }

    menuDAO.update(menu);
    return menuMapper.toMenuVO(menu);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteById(Long id) {
    menuDAO.deleteById(id);
  }

  @Override
  public List<MenuVO> findTree() {
    // 查询所有菜单
    List<SysMenu> allMenus = menuDAO.findAll();
    if (allMenus.isEmpty()) {
      return new ArrayList<>();
    }

    // 转换为VO
    List<MenuVO> allMenuVOs =
        allMenus.stream().map(menuMapper::toMenuVO).collect(Collectors.toList());

    // 按父ID分组
    Map<Long, List<MenuVO>> parentMenuMap =
        allMenuVOs.stream().collect(Collectors.groupingBy(MenuVO::parentId));

    // 构建树结构
    List<MenuVO> rootMenus = new ArrayList<>();
    for (MenuVO menuVO : allMenuVOs) {
      if (menuVO.parentId() == null || menuVO.parentId() == 0L) {
        // 顶级菜单
        rootMenus.add(buildMenuTree(menuVO, parentMenuMap));
      }
    }

    // 按排序号排序
    rootMenus.sort(
        (m1, m2) -> {
          int sort1 = m1.sortOrder() != null ? m1.sortOrder() : Integer.MAX_VALUE;
          int sort2 = m2.sortOrder() != null ? m2.sortOrder() : Integer.MAX_VALUE;
          return Integer.compare(sort1, sort2);
        });

    return rootMenus;
  }

  @Override
  public List<MenuVO> findByRoleId(Long roleId) {
    List<SysMenu> menus = menuDAO.findByRoleId(roleId);
    return menus.stream().map(menuMapper::toMenuVO).collect(Collectors.toList());
  }

  @Override
  public List<MenuVO> findByUserId(Long userId) {
    List<SysMenu> menus = menuDAO.findByUserId(userId);
    return menus.stream().map(menuMapper::toMenuVO).collect(Collectors.toList());
  }

  /**
   * 递归构建菜单树
   *
   * @param menuVO 当前菜单
   * @param parentMenuMap 父菜单映射
   * @return 构建好的菜单树节点
   */
  private MenuVO buildMenuTree(MenuVO menuVO, Map<Long, List<MenuVO>> parentMenuMap) {
    List<MenuVO> children = parentMenuMap.getOrDefault(menuVO.id(), new ArrayList<>());
    // 递归构建子菜单
    List<MenuVO> builtChildren =
        children.stream()
            .map(child -> buildMenuTree(child, parentMenuMap))
            .collect(Collectors.toList());

    // 按排序号排序
    builtChildren.sort(
        (m1, m2) -> {
          int sort1 = m1.sortOrder() != null ? m1.sortOrder() : Integer.MAX_VALUE;
          int sort2 = m2.sortOrder() != null ? m2.sortOrder() : Integer.MAX_VALUE;
          return Integer.compare(sort1, sort2);
        });

    return new MenuVO(
        menuVO.id(),
        menuVO.parentId(),
        menuVO.name(),
        menuVO.path(),
        menuVO.component(),
        menuVO.icon(),
        menuVO.sortOrder(),
        menuVO.type(),
        menuVO.permissionCode(),
        menuVO.visible(),
        menuVO.status(),
        builtChildren);
  }
}
