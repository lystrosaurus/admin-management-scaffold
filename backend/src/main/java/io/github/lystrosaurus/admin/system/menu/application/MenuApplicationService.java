package io.github.lystrosaurus.admin.system.menu.application;

import io.github.lystrosaurus.admin.shared.domain.acl.IdGenerator;
import io.github.lystrosaurus.admin.shared.domain.exception.DomainException;
import io.github.lystrosaurus.admin.system.menu.adapter.response.MenuResponse;
import io.github.lystrosaurus.admin.system.menu.application.assembler.MenuAssembler;
import io.github.lystrosaurus.admin.system.menu.application.command.MenuCreateCommand;
import io.github.lystrosaurus.admin.system.menu.application.command.MenuUpdateCommand;
import io.github.lystrosaurus.admin.system.menu.domain.acl.MenuQueryRepository;
import io.github.lystrosaurus.admin.system.menu.domain.acl.MenuRepository;
import io.github.lystrosaurus.admin.system.menu.domain.model.Menu;
import io.github.lystrosaurus.admin.system.menu.domain.model.MenuErrorCode;
import io.github.lystrosaurus.admin.system.menu.domain.model.MenuId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 菜单应用服务：编排菜单聚合的创建、更新、删除与查询。 */
@Service
@RequiredArgsConstructor
public class MenuApplicationService {

  private final MenuRepository menuRepository;
  private final MenuQueryRepository menuQueryRepository;
  private final IdGenerator idGenerator;
  private final MenuAssembler assembler;

  /** 创建菜单 */
  @Transactional(rollbackFor = Exception.class)
  public MenuResponse create(MenuCreateCommand command) {
    if (command.parentId() != null && command.parentId() > 0) {
      MenuId parentId = MenuId.of(command.parentId());
      if (!menuRepository.existsById(parentId)) {
        throw new DomainException(MenuErrorCode.MENU_NOT_FOUND);
      }
    }
    Menu menu =
        Menu.create(
            MenuId.of(idGenerator.nextId()),
            command.parentId(),
            command.name(),
            command.path(),
            command.component(),
            command.icon(),
            command.sortOrder(),
            command.type(),
            command.permissionCode(),
            command.visible());
    menuRepository.save(menu);
    return assembler.toResponse(menu);
  }

  /** 更新菜单 */
  @Transactional(rollbackFor = Exception.class)
  public MenuResponse update(Long id, MenuUpdateCommand command) {
    Menu menu =
        menuRepository
            .findById(MenuId.of(id))
            .orElseThrow(() -> new DomainException(MenuErrorCode.MENU_NOT_FOUND));
    menu.update(
        command.name(),
        command.path(),
        command.component(),
        command.icon(),
        command.sortOrder(),
        command.type(),
        command.permissionCode(),
        command.visible(),
        command.status());
    menuRepository.save(menu);
    return assembler.toResponse(menu);
  }

  /** 删除菜单 */
  @Transactional(rollbackFor = Exception.class)
  public void deleteById(Long id) {
    menuRepository.deleteById(MenuId.of(id));
  }

  /** 查询菜单树 */
  public List<MenuResponse> findTree() {
    return assembler.toTree(menuQueryRepository.findAll());
  }

  /** 按角色查询菜单 */
  public List<MenuResponse> findByRoleId(Long roleId) {
    return menuQueryRepository.findByRoleId(roleId).stream().map(assembler::toResponse).toList();
  }

  /** 按用户查询菜单 */
  public List<MenuResponse> findByUserId(Long userId) {
    return menuQueryRepository.findByUserId(userId).stream().map(assembler::toResponse).toList();
  }
}
