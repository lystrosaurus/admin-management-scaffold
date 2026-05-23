package io.github.lystrosaurus.admin.system.menu.dao.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import io.github.lystrosaurus.admin.system.menu.entity.SysMenu;
import io.github.lystrosaurus.admin.system.menu.mapper.SysMenuMapper;
import io.github.lystrosaurus.admin.system.role.entity.SysRoleMenu;
import io.github.lystrosaurus.admin.system.role.mapper.SysRoleMenuMapper;
import io.github.lystrosaurus.admin.system.user.entity.SysUserRole;
import io.github.lystrosaurus.admin.system.user.mapper.SysUserRoleMapper;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * MenuDAOImpl 单元测试
 *
 * <p>使用 Mockito mock Mapper，测试 DAO 层的业务逻辑。
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MenuDAOImpl 测试")
class MenuDAOImplTest {

  @Mock private SysMenuMapper menuMapper;
  @Mock private SysRoleMenuMapper roleMenuMapper;
  @Mock private SysUserRoleMapper userRoleMapper;

  @InjectMocks private MenuDAOImpl menuDAO;

  private SysMenu testMenu;

  @BeforeAll
  static void initTableInfo() {
    // 初始化 MyBatis-Plus Lambda 缓存
    MybatisConfiguration configuration = new MybatisConfiguration();
    MapperBuilderAssistant assistant = new MapperBuilderAssistant(configuration, "");
    TableInfoHelper.initTableInfo(assistant, SysMenu.class);
    TableInfoHelper.initTableInfo(assistant, SysRoleMenu.class);
    TableInfoHelper.initTableInfo(assistant, SysUserRole.class);
  }

  @BeforeEach
  void setUp() {
    testMenu = new SysMenu();
    testMenu.setId(1L);
    testMenu.setParentId(0L);
    testMenu.setName("系统管理");
    testMenu.setPath("/system");
    testMenu.setComponent("Layout");
    testMenu.setIcon("setting");
    testMenu.setPermissionCode("system:view");
    testMenu.setVisible((byte) 1);
    testMenu.setSortOrder(1);
    testMenu.setStatus((byte) 1);
    testMenu.setCreatedAt(LocalDateTime.now());
    testMenu.setUpdatedAt(LocalDateTime.now());
  }

  @Test
  @DisplayName("应该根据ID查找菜单")
  void should_findMenuById_when_idExists() {
    // Given
    when(menuMapper.selectById(1L)).thenReturn(testMenu);

    // When
    SysMenu result = menuDAO.findById(1L);

    // Then
    assertNotNull(result);
    assertEquals("系统管理", result.getName());
    verify(menuMapper).selectById(1L);
  }

  @Test
  @DisplayName("应该返回null当ID不存在时")
  void should_returnNull_when_idNotExists() {
    // Given
    when(menuMapper.selectById(999L)).thenReturn(null);

    // When
    SysMenu result = menuDAO.findById(999L);

    // Then
    assertNull(result);
    verify(menuMapper).selectById(999L);
  }

  @Test
  @DisplayName("应该保存菜单")
  void should_saveMenu_when_validMenu() {
    // Given
    when(menuMapper.insert(any(SysMenu.class))).thenReturn(1);

    // When
    menuDAO.save(testMenu);

    // Then
    verify(menuMapper).insert(testMenu);
  }

  @Test
  @DisplayName("应该更新菜单")
  void should_updateMenu_when_validMenu() {
    // Given
    when(menuMapper.updateById(any(SysMenu.class))).thenReturn(1);

    // When
    menuDAO.update(testMenu);

    // Then
    verify(menuMapper).updateById(testMenu);
  }

  @Test
  @DisplayName("应该删除菜单")
  void should_deleteMenu_when_idExists() {
    // Given
    when(menuMapper.deleteById(1L)).thenReturn(1);

    // When
    menuDAO.deleteById(1L);

    // Then
    verify(menuMapper).deleteById(1L);
  }

  @Test
  @DisplayName("应该查找所有菜单")
  void should_findAllMenus_when_menusExist() {
    // Given
    List<SysMenu> expectedMenus = Arrays.asList(testMenu);
    when(menuMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(expectedMenus);

    // When
    List<SysMenu> result = menuDAO.findAll();

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("系统管理", result.get(0).getName());
    verify(menuMapper).selectList(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该根据父ID查找子菜单")
  void should_findMenusByParentId_when_parentHasChildren() {
    // Given
    SysMenu childMenu = new SysMenu();
    childMenu.setId(2L);
    childMenu.setParentId(1L);
    childMenu.setName("用户管理");
    childMenu.setPath("/system/user");
    childMenu.setStatus((byte) 1);

    List<SysMenu> expectedMenus = Arrays.asList(childMenu);
    when(menuMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(expectedMenus);

    // When
    List<SysMenu> result = menuDAO.findByParentId(1L);

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("用户管理", result.get(0).getName());
    assertEquals(1L, result.get(0).getParentId());
    verify(menuMapper).selectList(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该根据角色ID查找菜单")
  void should_findMenusByRoleId_when_roleHasMenus() {
    // Given
    // 先 mock 角色菜单关联查询
    SysRoleMenu roleMenu = new SysRoleMenu();
    roleMenu.setId(1L);
    roleMenu.setRoleId(1L);
    roleMenu.setMenuId(1L);
    List<SysRoleMenu> roleMenus = Arrays.asList(roleMenu);
    when(roleMenuMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(roleMenus);

    // 再 mock 菜单查询
    List<SysMenu> expectedMenus = Arrays.asList(testMenu);
    when(menuMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(expectedMenus);

    // When
    List<SysMenu> result = menuDAO.findByRoleId(1L);

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("系统管理", result.get(0).getName());
    verify(roleMenuMapper).selectList(any(LambdaQueryWrapper.class));
    verify(menuMapper).selectList(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该根据用户ID查找菜单")
  void should_findMenusByUserId_when_userHasMenus() {
    // Given
    // 先 mock 用户角色关联查询
    SysUserRole userRole = new SysUserRole();
    userRole.setId(1L);
    userRole.setUserId(1L);
    userRole.setRoleId(1L);
    List<SysUserRole> userRoles = Arrays.asList(userRole);
    when(userRoleMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(userRoles);

    // 再 mock 角色菜单关联查询
    SysRoleMenu roleMenu = new SysRoleMenu();
    roleMenu.setId(1L);
    roleMenu.setRoleId(1L);
    roleMenu.setMenuId(1L);
    List<SysRoleMenu> roleMenus = Arrays.asList(roleMenu);
    when(roleMenuMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(roleMenus);

    // 最后 mock 菜单查询
    List<SysMenu> expectedMenus = Arrays.asList(testMenu);
    when(menuMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(expectedMenus);

    // When
    List<SysMenu> result = menuDAO.findByUserId(1L);

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("系统管理", result.get(0).getName());
    verify(userRoleMapper).selectList(any(LambdaQueryWrapper.class));
    verify(roleMenuMapper).selectList(any(LambdaQueryWrapper.class));
    verify(menuMapper).selectList(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该检查权限编码是否存在")
  void should_returnTrue_when_permissionCodeExists() {
    // Given
    when(menuMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

    // When
    boolean result = menuDAO.existsByPermissionCode("system:view");

    // Then
    assertTrue(result);
    verify(menuMapper).selectCount(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该检查权限编码不存在")
  void should_returnFalse_when_permissionCodeNotExists() {
    // Given
    when(menuMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

    // When
    boolean result = menuDAO.existsByPermissionCode("nonexistent:code");

    // Then
    assertFalse(result);
    verify(menuMapper).selectCount(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该更新菜单状态")
  void should_updateMenuStatus_when_validIdAndStatus() {
    // Given
    when(menuMapper.update(any(), any(LambdaUpdateWrapper.class))).thenReturn(1);

    // When
    menuDAO.updateStatus(1L, 0);

    // Then
    verify(menuMapper).update(any(), any(LambdaUpdateWrapper.class));
  }

  @Test
  @DisplayName("应该返回空列表当没有菜单时")
  void should_returnEmptyList_when_noMenusExist() {
    // Given
    when(menuMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

    // When
    List<SysMenu> result = menuDAO.findAll();

    // Then
    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(menuMapper).selectList(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该返回空列表当父ID没有子菜单时")
  void should_returnEmptyList_when_parentHasNoChildren() {
    // Given
    when(menuMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

    // When
    List<SysMenu> result = menuDAO.findByParentId(999L);

    // Then
    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(menuMapper).selectList(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该返回空列表当角色没有菜单时")
  void should_returnEmptyList_when_roleHasNoMenus() {
    // Given
    when(roleMenuMapper.selectList(any(LambdaQueryWrapper.class)))
        .thenReturn(Collections.emptyList());

    // When
    List<SysMenu> result = menuDAO.findByRoleId(999L);

    // Then
    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(roleMenuMapper).selectList(any(LambdaQueryWrapper.class));
    verify(menuMapper, never()).selectList(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该返回空列表当用户没有菜单时")
  void should_returnEmptyList_when_userHasNoMenus() {
    // Given
    when(userRoleMapper.selectList(any(LambdaQueryWrapper.class)))
        .thenReturn(Collections.emptyList());

    // When
    List<SysMenu> result = menuDAO.findByUserId(999L);

    // Then
    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(userRoleMapper).selectList(any(LambdaQueryWrapper.class));
    verify(roleMenuMapper, never()).selectList(any(LambdaQueryWrapper.class));
    verify(menuMapper, never()).selectList(any(LambdaQueryWrapper.class));
  }
}
