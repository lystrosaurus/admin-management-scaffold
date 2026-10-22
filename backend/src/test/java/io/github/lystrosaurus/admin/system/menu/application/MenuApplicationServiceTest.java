package io.github.lystrosaurus.admin.system.menu.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** MenuApplicationService 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("菜单应用服务测试")
class MenuApplicationServiceTest {

  @Mock private MenuRepository menuRepository;
  @Mock private MenuQueryRepository menuQueryRepository;
  @Mock private IdGenerator idGenerator;
  @Mock private MenuAssembler assembler;

  @InjectMocks private MenuApplicationService menuApplicationService;

  private Menu testMenu;
  private MenuResponse menuResponse;
  private MenuCreateCommand createCommand;

  @BeforeEach
  void setUp() {
    testMenu =
        Menu.restore(
            MenuId.of(1L),
            0L,
            "系统管理",
            "/system",
            "System",
            "setting",
            1,
            (byte) 1,
            null,
            (byte) 1,
            (byte) 1,
            LocalDateTime.now(),
            1);
    menuResponse =
        new MenuResponse(
            1L, 0L, "系统管理", "/system", "System", "setting", 1, (byte) 1, null, (byte) 1, (byte) 1,
            null);
    createCommand =
        new MenuCreateCommand(null, "系统管理", "/system", "System", "setting", 1, (byte) 1, null,
            (byte) 1);
  }

  @Test
  @DisplayName("创建顶级菜单时不应校验父菜单")
  void should_create_root_menu_without_parent_check() {
    when(idGenerator.nextId()).thenReturn(1L);
    when(assembler.toResponse(any(Menu.class))).thenReturn(menuResponse);

    MenuResponse result = menuApplicationService.create(createCommand);

    assertNotNull(result);
    verify(menuRepository, never()).existsById(any());
    verify(menuRepository).save(any(Menu.class));
  }

  @Test
  @DisplayName("创建子菜单时父菜单存在应该成功")
  void should_create_child_menu_when_parent_exists() {
    MenuCreateCommand childCommand =
        new MenuCreateCommand(2L, "用户管理", "/system/user", "SystemUser", "user", 1, (byte) 1,
            "system:user:view", (byte) 1);
    when(menuRepository.existsById(MenuId.of(2L))).thenReturn(true);
    when(idGenerator.nextId()).thenReturn(3L);
    when(assembler.toResponse(any(Menu.class))).thenReturn(menuResponse);

    MenuResponse result = menuApplicationService.create(childCommand);

    assertNotNull(result);
    verify(menuRepository).existsById(MenuId.of(2L));
    verify(menuRepository).save(any(Menu.class));
  }

  @Test
  @DisplayName("创建子菜单时父菜单不存在应该抛出异常")
  void should_throw_exception_when_create_child_with_missing_parent() {
    MenuCreateCommand childCommand =
        new MenuCreateCommand(999L, "用户管理", "/system/user", "SystemUser", "user", 1, (byte) 1,
            "system:user:view", (byte) 1);
    when(menuRepository.existsById(MenuId.of(999L))).thenReturn(false);

    DomainException exception =
        assertThrows(
            DomainException.class, () -> menuApplicationService.create(childCommand));

    assertEquals(MenuErrorCode.MENU_NOT_FOUND.code(), exception.code());
    verify(menuRepository, never()).save(any());
  }

  @Test
  @DisplayName("应该成功更新菜单")
  void should_update_menu_when_exists() {
    when(menuRepository.findById(MenuId.of(1L))).thenReturn(Optional.of(testMenu));
    when(assembler.toResponse(any(Menu.class))).thenReturn(menuResponse);

    MenuResponse result =
        menuApplicationService.update(
            1L,
            new MenuUpdateCommand(
                "新名称", "/new", "New", "new", 2, (byte) 1, null, (byte) 1, (byte) 1));

    assertNotNull(result);
    verify(menuRepository).save(any(Menu.class));
  }

  @Test
  @DisplayName("更新菜单时菜单不存在应该抛出异常")
  void should_throw_exception_when_update_nonexistent_menu() {
    when(menuRepository.findById(MenuId.of(999L))).thenReturn(Optional.empty());

    MenuUpdateCommand command =
        new MenuUpdateCommand("新名称", "/new", "New", "new", 2, (byte) 1, null, (byte) 1, (byte) 1);

    DomainException exception =
        assertThrows(DomainException.class, () -> menuApplicationService.update(999L, command));

    assertEquals(MenuErrorCode.MENU_NOT_FOUND.code(), exception.code());
  }

  @Test
  @DisplayName("应该成功删除菜单")
  void should_delete_menu_by_id() {
    menuApplicationService.deleteById(1L);

    verify(menuRepository).deleteById(MenuId.of(1L));
  }

  @Test
  @DisplayName("菜单树查询应该委托装配器")
  void should_find_tree() {
    when(menuQueryRepository.findAll()).thenReturn(List.of(testMenu));
    when(assembler.toTree(List.of(testMenu))).thenReturn(List.of(menuResponse));

    List<MenuResponse> result = menuApplicationService.findTree();

    assertEquals(1, result.size());
    verify(menuQueryRepository).findAll();
    verify(assembler).toTree(List.of(testMenu));
  }

  @Test
  @DisplayName("应该按角色查询菜单")
  void should_find_menus_by_role_id() {
    when(menuQueryRepository.findByRoleId(2L)).thenReturn(List.of(testMenu));
    when(assembler.toResponse(testMenu)).thenReturn(menuResponse);

    List<MenuResponse> result = menuApplicationService.findByRoleId(2L);

    assertEquals(1, result.size());
    verify(menuQueryRepository).findByRoleId(2L);
  }

  @Test
  @DisplayName("应该按用户查询菜单")
  void should_find_menus_by_user_id() {
    when(menuQueryRepository.findByUserId(3L)).thenReturn(List.of(testMenu));
    when(assembler.toResponse(testMenu)).thenReturn(menuResponse);

    List<MenuResponse> result = menuApplicationService.findByUserId(3L);

    assertEquals(1, result.size());
    verify(menuQueryRepository).findByUserId(3L);
  }
}
