package io.github.lystrosaurus.admin.system.role.dao.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.lystrosaurus.admin.system.menu.entity.SysMenu;
import io.github.lystrosaurus.admin.system.menu.mapper.SysMenuMapper;
import io.github.lystrosaurus.admin.system.permission.entity.SysPermission;
import io.github.lystrosaurus.admin.system.permission.mapper.SysPermissionMapper;
import io.github.lystrosaurus.admin.system.role.entity.SysRole;
import io.github.lystrosaurus.admin.system.role.entity.SysRoleMenu;
import io.github.lystrosaurus.admin.system.role.entity.SysRolePermission;
import io.github.lystrosaurus.admin.system.role.mapper.SysRoleMapper;
import io.github.lystrosaurus.admin.system.role.mapper.SysRoleMenuMapper;
import io.github.lystrosaurus.admin.system.role.mapper.SysRolePermissionMapper;
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
 * RoleDAOImpl 单元测试
 *
 * <p>使用 Mockito mock Mapper，测试 DAO 层的业务逻辑。
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RoleDAOImpl 测试")
class RoleDAOImplTest {

  @Mock private SysRoleMapper roleMapper;
  @Mock private SysRolePermissionMapper rolePermissionMapper;
  @Mock private SysRoleMenuMapper roleMenuMapper;
  @Mock private SysPermissionMapper permissionMapper;
  @Mock private SysMenuMapper menuMapper;

  @InjectMocks private RoleDAOImpl roleDAO;

  private SysRole testRole;
  private SysPermission testPermission;
  private SysMenu testMenu;

  @BeforeAll
  static void initTableInfo() {
    // 初始化 MyBatis-Plus Lambda 缓存
    MybatisConfiguration configuration = new MybatisConfiguration();
    MapperBuilderAssistant assistant = new MapperBuilderAssistant(configuration, "");
    TableInfoHelper.initTableInfo(assistant, SysRole.class);
    TableInfoHelper.initTableInfo(assistant, SysRolePermission.class);
    TableInfoHelper.initTableInfo(assistant, SysRoleMenu.class);
    TableInfoHelper.initTableInfo(assistant, SysPermission.class);
    TableInfoHelper.initTableInfo(assistant, SysMenu.class);
  }

  @BeforeEach
  void setUp() {
    testRole = new SysRole();
    testRole.setId(1L);
    testRole.setCode("ADMIN");
    testRole.setName("管理员");
    testRole.setStatus("ENABLED");
    testRole.setCreatedAt(LocalDateTime.now());
    testRole.setUpdatedAt(LocalDateTime.now());

    testPermission = new SysPermission();
    testPermission.setId(1L);
    testPermission.setCode("system:user:view");
    testPermission.setName("查看用户");
    testPermission.setType("API");
    testPermission.setModule("system");
    testPermission.setStatus("ENABLED");

    testMenu = new SysMenu();
    testMenu.setId(1L);
    testMenu.setTitle("用户管理");
    testMenu.setRoutePath("/system/user");
    testMenu.setRouteName("SystemUser");
    testMenu.setStatus("ENABLED");
  }

  @Test
  @DisplayName("应该根据ID查找角色")
  void should_findRoleById_when_idExists() {
    // Given
    when(roleMapper.selectById(1L)).thenReturn(testRole);

    // When
    SysRole result = roleDAO.findById(1L);

    // Then
    assertNotNull(result);
    assertEquals("ADMIN", result.getCode());
    verify(roleMapper).selectById(1L);
  }

  @Test
  @DisplayName("应该返回null当ID不存在时")
  void should_returnNull_when_idNotExists() {
    // Given
    when(roleMapper.selectById(999L)).thenReturn(null);

    // When
    SysRole result = roleDAO.findById(999L);

    // Then
    assertNull(result);
    verify(roleMapper).selectById(999L);
  }

  @Test
  @DisplayName("应该根据角色编码查找角色")
  void should_findRoleByCode_when_codeExists() {
    // Given
    when(roleMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testRole);

    // When
    SysRole result = roleDAO.findByCode("ADMIN");

    // Then
    assertNotNull(result);
    assertEquals("ADMIN", result.getCode());
    verify(roleMapper).selectOne(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该返回null当角色编码不存在时")
  void should_returnNull_when_codeNotExists() {
    // Given
    when(roleMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

    // When
    SysRole result = roleDAO.findByCode("NONEXISTENT");

    // Then
    assertNull(result);
    verify(roleMapper).selectOne(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该保存角色")
  void should_saveRole_when_validRole() {
    // Given
    when(roleMapper.insert(any(SysRole.class))).thenReturn(1);

    // When
    roleDAO.save(testRole);

    // Then
    verify(roleMapper).insert(testRole);
  }

  @Test
  @DisplayName("应该更新角色")
  void should_updateRole_when_validRole() {
    // Given
    when(roleMapper.updateById(any(SysRole.class))).thenReturn(1);

    // When
    roleDAO.update(testRole);

    // Then
    verify(roleMapper).updateById(testRole);
  }

  @Test
  @DisplayName("应该删除角色")
  void should_deleteRole_when_idExists() {
    // Given
    when(roleMapper.deleteById(1L)).thenReturn(1);

    // When
    roleDAO.deleteById(1L);

    // Then
    verify(roleMapper).deleteById(1L);
  }

  @Test
  @DisplayName("应该根据条件查找角色列表")
  void should_findRolesByCondition_when_conditionsProvided() {
    // Given
    List<SysRole> expectedRoles = Arrays.asList(testRole);
    Page<SysRole> mockPage = new Page<>();
    mockPage.setRecords(expectedRoles);
    when(roleMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
        .thenReturn(mockPage);

    // When
    List<SysRole> result = roleDAO.findByCondition("admin", "ENABLED", 1, 10);

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());
    verify(roleMapper).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该根据条件统计角色数量")
  void should_countRolesByCondition_when_conditionsProvided() {
    // Given
    when(roleMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(3L);

    // When
    long result = roleDAO.countByCondition("admin", "ENABLED");

    // Then
    assertEquals(3L, result);
    verify(roleMapper).selectCount(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该检查角色编码是否存在")
  void should_returnTrue_when_codeExists() {
    // Given
    when(roleMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

    // When
    boolean result = roleDAO.existsByCode("ADMIN");

    // Then
    assertTrue(result);
    verify(roleMapper).selectCount(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该检查角色编码不存在")
  void should_returnFalse_when_codeNotExists() {
    // Given
    when(roleMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

    // When
    boolean result = roleDAO.existsByCode("NONEXISTENT");

    // Then
    assertFalse(result);
    verify(roleMapper).selectCount(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该检查角色编码是否存在且排除指定ID")
  void should_returnTrue_when_codeExistsAndIdNotExcluded() {
    // Given
    when(roleMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

    // When
    boolean result = roleDAO.existsByCodeAndIdNot("ADMIN", 2L);

    // Then
    assertTrue(result);
    verify(roleMapper).selectCount(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该检查角色编码不存在且排除指定ID")
  void should_returnFalse_when_codeNotExistsAndIdExcluded() {
    // Given
    when(roleMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

    // When
    boolean result = roleDAO.existsByCodeAndIdNot("ADMIN", 1L);

    // Then
    assertFalse(result);
    verify(roleMapper).selectCount(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该查找角色关联的权限")
  void should_findPermissionsByRoleId_when_roleHasPermissions() {
    // Given
    // 先 mock 角色权限关联查询
    SysRolePermission rolePermission = new SysRolePermission();
    rolePermission.setId(1L);
    rolePermission.setRoleId(1L);
    rolePermission.setPermissionId(1L);
    List<SysRolePermission> rolePermissions = Arrays.asList(rolePermission);
    when(rolePermissionMapper.selectList(any(LambdaQueryWrapper.class)))
        .thenReturn(rolePermissions);

    // 再 mock 权限查询
    List<SysPermission> expectedPermissions = Arrays.asList(testPermission);
    when(permissionMapper.selectList(any(LambdaQueryWrapper.class)))
        .thenReturn(expectedPermissions);

    // When
    List<SysPermission> result = roleDAO.findPermissionsByRoleId(1L);

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("system:user:view", result.get(0).getCode());
    verify(rolePermissionMapper).selectList(any(LambdaQueryWrapper.class));
    verify(permissionMapper).selectList(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该分配权限给角色")
  void should_assignPermissions_when_validRoleIdAndPermissionIds() {
    // Given
    List<Long> permissionIds = Arrays.asList(1L, 2L);
    // 先 mock 删除现有权限
    when(rolePermissionMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(0);
    // 再 mock 插入新权限
    when(rolePermissionMapper.insert(any(SysRolePermission.class))).thenReturn(1);

    // When
    roleDAO.assignPermissions(1L, permissionIds);

    // Then
    verify(rolePermissionMapper).delete(any(LambdaQueryWrapper.class));
    verify(rolePermissionMapper, times(2)).insert(any(SysRolePermission.class));
  }

  @Test
  @DisplayName("应该移除角色的所有权限")
  void should_removePermissions_when_roleIdProvided() {
    // Given
    when(rolePermissionMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(2);

    // When
    roleDAO.removePermissions(1L);

    // Then
    verify(rolePermissionMapper).delete(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该查找角色关联的菜单")
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
    List<SysMenu> result = roleDAO.findMenusByRoleId(1L);

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("用户管理", result.get(0).getTitle());
    verify(roleMenuMapper).selectList(any(LambdaQueryWrapper.class));
    verify(menuMapper).selectList(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该分配菜单给角色")
  void should_assignMenus_when_validRoleIdAndMenuIds() {
    // Given
    List<Long> menuIds = Arrays.asList(1L, 2L);
    // 先 mock 删除现有菜单
    when(roleMenuMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(0);
    // 再 mock 插入新菜单
    when(roleMenuMapper.insert(any(SysRoleMenu.class))).thenReturn(1);

    // When
    roleDAO.assignMenus(1L, menuIds);

    // Then
    verify(roleMenuMapper).delete(any(LambdaQueryWrapper.class));
    verify(roleMenuMapper, times(2)).insert(any(SysRoleMenu.class));
  }

  @Test
  @DisplayName("应该移除角色的所有菜单")
  void should_removeMenus_when_roleIdProvided() {
    // Given
    when(roleMenuMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(2);

    // When
    roleDAO.removeMenus(1L);

    // Then
    verify(roleMenuMapper).delete(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该返回空列表当角色没有权限时")
  void should_returnEmptyList_when_roleHasNoPermissions() {
    // Given
    when(rolePermissionMapper.selectList(any(LambdaQueryWrapper.class)))
        .thenReturn(Collections.emptyList());

    // When
    List<SysPermission> result = roleDAO.findPermissionsByRoleId(1L);

    // Then
    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(rolePermissionMapper).selectList(any(LambdaQueryWrapper.class));
    verify(permissionMapper, never()).selectList(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该返回空列表当角色没有菜单时")
  void should_returnEmptyList_when_roleHasNoMenus() {
    // Given
    when(roleMenuMapper.selectList(any(LambdaQueryWrapper.class)))
        .thenReturn(Collections.emptyList());

    // When
    List<SysMenu> result = roleDAO.findMenusByRoleId(1L);

    // Then
    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(roleMenuMapper).selectList(any(LambdaQueryWrapper.class));
    verify(menuMapper, never()).selectList(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该处理空权限ID列表")
  void should_handleEmptyPermissionIds_when_assigningPermissions() {
    // Given
    List<Long> emptyPermissionIds = Collections.emptyList();

    // When
    roleDAO.assignPermissions(1L, emptyPermissionIds);

    // Then
    verify(rolePermissionMapper, never()).insert(any(SysRolePermission.class));
  }

  @Test
  @DisplayName("应该处理空菜单ID列表")
  void should_handleEmptyMenuIds_when_assigningMenus() {
    // Given
    List<Long> emptyMenuIds = Collections.emptyList();

    // When
    roleDAO.assignMenus(1L, emptyMenuIds);

    // Then
    verify(roleMenuMapper, never()).insert(any(SysRoleMenu.class));
  }
}
