package io.github.lystrosaurus.admin.system.menu.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.system.menu.dao.MenuDAO;
import io.github.lystrosaurus.admin.system.menu.dto.MenuCreateDTO;
import io.github.lystrosaurus.admin.system.menu.dto.MenuUpdateDTO;
import io.github.lystrosaurus.admin.system.menu.entity.SysMenu;
import io.github.lystrosaurus.admin.system.menu.mapstruct.MenuMapper;
import io.github.lystrosaurus.admin.system.menu.vo.MenuVO;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** MenuServiceImpl 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("菜单服务测试")
class MenuServiceImplTest {

  @Mock private MenuDAO menuDAO;

  @Mock private MenuMapper menuMapper;

  @InjectMocks private MenuServiceImpl menuService;

  private SysMenu testMenu;
  private SysMenu childMenu;
  private MenuCreateDTO createDTO;
  private MenuUpdateDTO updateDTO;
  private MenuVO menuVO;
  private MenuVO childMenuVO;

  @BeforeEach
  void setUp() {
    // 准备测试数据
    testMenu = new SysMenu();
    testMenu.setId(1L);
    testMenu.setParentId(0L);
    testMenu.setName("系统管理");
    testMenu.setPath("/system");
    testMenu.setComponent("SystemManagement");
    testMenu.setIcon("setting");
    testMenu.setSortOrder(1);
    testMenu.setType((byte) 1);
    testMenu.setPermissionCode("system:manage");
    testMenu.setVisible((byte) 1);
    testMenu.setStatus((byte) 1);
    testMenu.setCreatedAt(LocalDateTime.now());

    childMenu = new SysMenu();
    childMenu.setId(2L);
    childMenu.setParentId(1L);
    childMenu.setName("用户管理");
    childMenu.setPath("/system/user");
    childMenu.setComponent("UserManagement");
    childMenu.setIcon("user");
    childMenu.setSortOrder(1);
    childMenu.setType((byte) 2);
    childMenu.setPermissionCode("user:manage");
    childMenu.setVisible((byte) 1);
    childMenu.setStatus((byte) 1);
    childMenu.setCreatedAt(LocalDateTime.now());

    createDTO =
        new MenuCreateDTO(
            0L,
            "系统管理",
            "/system",
            "SystemManagement",
            "setting",
            1,
            (byte) 1,
            "system:manage",
            (byte) 1);
    updateDTO =
        new MenuUpdateDTO(
            "系统设置",
            "/system/settings",
            "SystemSettings",
            "settings",
            1,
            (byte) 1,
            "system:settings",
            (byte) 1,
            (byte) 1);
    menuVO =
        new MenuVO(
            1L,
            0L,
            "系统管理",
            "/system",
            "SystemManagement",
            "setting",
            1,
            (byte) 1,
            "system:manage",
            (byte) 1,
            (byte) 1,
            null);
    childMenuVO =
        new MenuVO(
            2L,
            1L,
            "用户管理",
            "/system/user",
            "UserManagement",
            "user",
            1,
            (byte) 2,
            "user:manage",
            (byte) 1,
            (byte) 1,
            null);
  }

  @Test
  @DisplayName("应该成功创建菜单")
  void should_create_menu_when_parent_exists() {
    // Given
    // 顶级菜单，父ID为0，不需要检查父菜单
    doAnswer(
            invocation -> {
              SysMenu savedMenu = invocation.getArgument(0);
              savedMenu.setId(1L);
              return null;
            })
        .when(menuDAO)
        .save(any(SysMenu.class));
    when(menuMapper.toMenuVO(any(SysMenu.class))).thenReturn(menuVO);

    // When
    MenuVO result = menuService.create(createDTO);

    // Then
    assertNotNull(result);
    assertEquals("系统管理", result.name());
    assertEquals("/system", result.path());
    verify(menuDAO).save(any(SysMenu.class));
  }

  @Test
  @DisplayName("创建子菜单时父菜单不存在应该抛出异常")
  void should_throw_exception_when_parent_menu_not_exists() {
    // Given
    MenuCreateDTO childCreateDTO =
        new MenuCreateDTO(
            999L,
            "用户管理",
            "/system/user",
            "UserManagement",
            "user",
            1,
            (byte) 2,
            "user:manage",
            (byte) 1);
    when(menuDAO.findById(999L)).thenReturn(null);

    // When & Then
    BusinessException exception =
        assertThrows(BusinessException.class, () -> menuService.create(childCreateDTO));
    assertEquals(ErrorCode.MENU_NOT_FOUND.getCode(), exception.getCode());
    verify(menuDAO, never()).save(any());
  }

  @Test
  @DisplayName("应该成功更新菜单")
  void should_update_menu_when_menu_exists() {
    // Given
    when(menuDAO.findById(1L)).thenReturn(testMenu);
    when(menuMapper.toMenuVO(any(SysMenu.class))).thenReturn(menuVO);

    // When
    MenuVO result = menuService.update(1L, updateDTO);

    // Then
    assertNotNull(result);
    verify(menuDAO).findById(1L);
    verify(menuDAO).update(any(SysMenu.class));
  }

  @Test
  @DisplayName("更新菜单时菜单不存在应该抛出异常")
  void should_throw_exception_when_update_nonexistent_menu() {
    // Given
    when(menuDAO.findById(999L)).thenReturn(null);

    // When & Then
    BusinessException exception =
        assertThrows(BusinessException.class, () -> menuService.update(999L, updateDTO));
    assertEquals(ErrorCode.MENU_NOT_FOUND.getCode(), exception.getCode());
    verify(menuDAO).findById(999L);
    verify(menuDAO, never()).update(any());
  }

  @Test
  @DisplayName("应该成功删除菜单")
  void should_delete_menu_by_id() {
    // When
    menuService.deleteById(1L);

    // Then
    verify(menuDAO).deleteById(1L);
  }

  @Test
  @DisplayName("应该成功获取菜单树")
  void should_find_menu_tree() {
    // Given
    List<SysMenu> allMenus = Arrays.asList(testMenu, childMenu);
    when(menuDAO.findAll()).thenReturn(allMenus);
    when(menuMapper.toMenuVO(testMenu)).thenReturn(menuVO);
    when(menuMapper.toMenuVO(childMenu)).thenReturn(childMenuVO);

    // When
    List<MenuVO> result = menuService.findTree();

    // Then
    assertNotNull(result);
    assertEquals(1, result.size()); // 只有顶级菜单
    assertEquals("系统管理", result.get(0).name());
    assertNotNull(result.get(0).children());
    assertEquals(1, result.get(0).children().size());
    assertEquals("用户管理", result.get(0).children().get(0).name());
    verify(menuDAO).findAll();
  }

  @Test
  @DisplayName("应该成功根据角色ID查询菜单")
  void should_find_menus_by_role_id() {
    // Given
    List<SysMenu> menus = Arrays.asList(testMenu, childMenu);
    when(menuDAO.findByRoleId(1L)).thenReturn(menus);
    when(menuMapper.toMenuVO(testMenu)).thenReturn(menuVO);
    when(menuMapper.toMenuVO(childMenu)).thenReturn(childMenuVO);

    // When
    List<MenuVO> result = menuService.findByRoleId(1L);

    // Then
    assertNotNull(result);
    assertEquals(2, result.size());
    verify(menuDAO).findByRoleId(1L);
  }

  @Test
  @DisplayName("应该成功根据用户ID查询菜单")
  void should_find_menus_by_user_id() {
    // Given
    List<SysMenu> menus = Arrays.asList(testMenu, childMenu);
    when(menuDAO.findByUserId(1L)).thenReturn(menus);
    when(menuMapper.toMenuVO(testMenu)).thenReturn(menuVO);
    when(menuMapper.toMenuVO(childMenu)).thenReturn(childMenuVO);

    // When
    List<MenuVO> result = menuService.findByUserId(1L);

    // Then
    assertNotNull(result);
    assertEquals(2, result.size());
    verify(menuDAO).findByUserId(1L);
  }

  @Test
  @DisplayName("获取菜单树时返回空列表")
  void should_return_empty_list_when_no_menus() {
    // Given
    when(menuDAO.findAll()).thenReturn(Arrays.asList());

    // When
    List<MenuVO> result = menuService.findTree();

    // Then
    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(menuDAO).findAll();
  }

  @Test
  @DisplayName("根据角色ID查询菜单时返回空列表")
  void should_return_empty_list_when_no_menus_for_role() {
    // Given
    when(menuDAO.findByRoleId(999L)).thenReturn(Arrays.asList());

    // When
    List<MenuVO> result = menuService.findByRoleId(999L);

    // Then
    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(menuDAO).findByRoleId(999L);
  }

  @Test
  @DisplayName("根据用户ID查询菜单时返回空列表")
  void should_return_empty_list_when_no_menus_for_user() {
    // Given
    when(menuDAO.findByUserId(999L)).thenReturn(Arrays.asList());

    // When
    List<MenuVO> result = menuService.findByUserId(999L);

    // Then
    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(menuDAO).findByUserId(999L);
  }
}
