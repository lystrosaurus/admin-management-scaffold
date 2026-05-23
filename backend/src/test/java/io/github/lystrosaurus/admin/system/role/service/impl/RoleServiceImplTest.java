package io.github.lystrosaurus.admin.system.role.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.system.menu.entity.SysMenu;
import io.github.lystrosaurus.admin.system.menu.mapstruct.MenuMapper;
import io.github.lystrosaurus.admin.system.menu.vo.MenuVO;
import io.github.lystrosaurus.admin.system.permission.entity.SysPermission;
import io.github.lystrosaurus.admin.system.permission.mapstruct.PermissionMapper;
import io.github.lystrosaurus.admin.system.permission.vo.PermissionVO;
import io.github.lystrosaurus.admin.system.role.dao.RoleDAO;
import io.github.lystrosaurus.admin.system.role.dto.RoleCreateDTO;
import io.github.lystrosaurus.admin.system.role.dto.RoleUpdateDTO;
import io.github.lystrosaurus.admin.system.role.entity.SysRole;
import io.github.lystrosaurus.admin.system.role.mapstruct.RoleMapper;
import io.github.lystrosaurus.admin.system.role.vo.RoleDetailVO;
import io.github.lystrosaurus.admin.system.role.vo.RoleVO;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** RoleServiceImpl 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("角色服务测试")
class RoleServiceImplTest {

  @Mock private RoleDAO roleDAO;

  @Mock private RoleMapper roleMapper;

  @Mock private PermissionMapper permissionMapper;

  @Mock private MenuMapper menuMapper;

  @InjectMocks private RoleServiceImpl roleService;

  private SysRole testRole;
  private RoleCreateDTO createDTO;
  private RoleUpdateDTO updateDTO;
  private RoleVO roleVO;
  private RoleDetailVO roleDetailVO;

  @BeforeEach
  void setUp() {
    // 准备测试数据
    testRole = new SysRole();
    testRole.setId(1L);
    testRole.setCode("ADMIN");
    testRole.setName("管理员");
    testRole.setDescription("系统管理员角色");
    testRole.setSortOrder(1);
    testRole.setStatus("ENABLED");
    testRole.setDataScopeType("ALL");
    testRole.setCreatedAt(LocalDateTime.now());

    createDTO = new RoleCreateDTO("ADMIN", "管理员", "系统管理员角色", 1, "ALL");
    updateDTO = new RoleUpdateDTO("超级管理员", "超级管理员角色", 1, "ENABLED", "ALL");
    roleVO = new RoleVO(1L, "ADMIN", "管理员", "系统管理员角色", "ENABLED", "ALL");
    roleDetailVO =
        new RoleDetailVO(
            1L,
            "ADMIN",
            "管理员",
            "系统管理员角色",
            1,
            "ENABLED",
            "ALL",
            Collections.emptyList(),
            Collections.emptyList(),
            LocalDateTime.now());
  }

  @Test
  @DisplayName("应该成功创建角色")
  void should_create_role_when_code_not_exists() {
    // Given
    when(roleDAO.existsByCode("ADMIN")).thenReturn(false);
    doAnswer(
            invocation -> {
              SysRole savedRole = invocation.getArgument(0);
              savedRole.setId(1L);
              return null;
            })
        .when(roleDAO)
        .save(any(SysRole.class));
    when(roleMapper.toRoleVO(any(SysRole.class))).thenReturn(roleVO);

    // When
    RoleVO result = roleService.create(createDTO);

    // Then
    assertNotNull(result);
    assertEquals("ADMIN", result.code());
    assertEquals("管理员", result.name());
    verify(roleDAO).existsByCode("ADMIN");
    verify(roleDAO).save(any(SysRole.class));
  }

  @Test
  @DisplayName("创建角色时编码已存在应该抛出异常")
  void should_throw_exception_when_role_code_already_exists() {
    // Given
    when(roleDAO.existsByCode("ADMIN")).thenReturn(true);

    // When & Then
    BusinessException exception =
        assertThrows(BusinessException.class, () -> roleService.create(createDTO));
    assertEquals(ErrorCode.ROLE_ALREADY_EXISTS.getCode(), exception.getCode());
    verify(roleDAO).existsByCode("ADMIN");
    verify(roleDAO, never()).save(any());
  }

  @Test
  @DisplayName("应该成功更新角色")
  void should_update_role_when_role_exists() {
    // Given
    when(roleDAO.findById(1L)).thenReturn(testRole);
    when(roleMapper.toRoleVO(any(SysRole.class))).thenReturn(roleVO);

    // When
    RoleVO result = roleService.update(1L, updateDTO);

    // Then
    assertNotNull(result);
    verify(roleDAO).findById(1L);
    verify(roleDAO).update(any(SysRole.class));
  }

  @Test
  @DisplayName("更新角色时角色不存在应该抛出异常")
  void should_throw_exception_when_update_nonexistent_role() {
    // Given
    when(roleDAO.findById(999L)).thenReturn(null);

    // When & Then
    BusinessException exception =
        assertThrows(BusinessException.class, () -> roleService.update(999L, updateDTO));
    assertEquals(ErrorCode.ROLE_NOT_FOUND.getCode(), exception.getCode());
    verify(roleDAO).findById(999L);
    verify(roleDAO, never()).update(any());
  }

  @Test
  @DisplayName("应该成功删除角色")
  void should_delete_role_by_id() {
    // When
    roleService.deleteById(1L);

    // Then
    verify(roleDAO).deleteById(1L);
  }

  @Test
  @DisplayName("应该成功查询角色详情")
  void should_find_role_detail_by_id() {
    // Given
    SysPermission permission = new SysPermission();
    permission.setId(1L);
    permission.setCode("user:read");
    permission.setName("查看用户");

    SysMenu menu = new SysMenu();
    menu.setId(1L);
    menu.setName("用户管理");
    menu.setParentId(0L);

    PermissionVO permissionVO =
        new PermissionVO(1L, "user:read", "查看用户", "API", "user", "user", "read");
    MenuVO menuVO =
        new MenuVO(
            1L,
            0L,
            "用户管理",
            "/user",
            "UserManagement",
            "user",
            1,
            (byte) 1,
            "user:read",
            (byte) 1,
            (byte) 1,
            null);

    when(roleDAO.findById(1L)).thenReturn(testRole);
    when(roleDAO.findPermissionsByRoleId(1L)).thenReturn(Arrays.asList(permission));
    when(roleDAO.findMenusByRoleId(1L)).thenReturn(Arrays.asList(menu));
    when(roleMapper.toRoleDetailVO(testRole)).thenReturn(roleDetailVO);
    when(permissionMapper.toPermissionVO(permission)).thenReturn(permissionVO);
    when(menuMapper.toMenuVO(menu)).thenReturn(menuVO);

    // When
    RoleDetailVO result = roleService.findById(1L);

    // Then
    assertNotNull(result);
    assertEquals(1L, result.id());
    assertEquals(1, result.permissions().size());
    assertEquals(1, result.menus().size());
    verify(roleDAO).findById(1L);
    verify(roleDAO).findPermissionsByRoleId(1L);
    verify(roleDAO).findMenusByRoleId(1L);
  }

  @Test
  @DisplayName("查询角色详情时角色不存在应该抛出异常")
  void should_throw_exception_when_find_nonexistent_role() {
    // Given
    when(roleDAO.findById(999L)).thenReturn(null);

    // When & Then
    BusinessException exception =
        assertThrows(BusinessException.class, () -> roleService.findById(999L));
    assertEquals(ErrorCode.ROLE_NOT_FOUND.getCode(), exception.getCode());
  }

  @Test
  @DisplayName("应该成功查询所有角色")
  void should_find_all_roles() {
    // Given
    List<SysRole> roles = Arrays.asList(testRole);
    when(roleDAO.findAll()).thenReturn(roles);
    when(roleMapper.toRoleVO(testRole)).thenReturn(roleVO);

    // When
    List<RoleVO> result = roleService.findAll();

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());
    verify(roleDAO).findAll();
  }

  @Test
  @DisplayName("应该成功根据用户ID查询角色")
  void should_find_roles_by_user_id() {
    // Given
    List<SysRole> roles = Arrays.asList(testRole);
    when(roleDAO.findByUserId(1L)).thenReturn(roles);
    when(roleMapper.toRoleVO(testRole)).thenReturn(roleVO);

    // When
    List<RoleVO> result = roleService.findByUserId(1L);

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());
    verify(roleDAO).findByUserId(1L);
  }

  @Test
  @DisplayName("应该成功分配权限给角色")
  void should_assign_permissions_to_role() {
    // Given
    List<Long> permissionIds = Arrays.asList(1L, 2L, 3L);
    when(roleDAO.findById(1L)).thenReturn(testRole);

    // When
    roleService.assignPermissions(1L, permissionIds);

    // Then
    verify(roleDAO).findById(1L);
    verify(roleDAO).removePermissions(1L);
    verify(roleDAO).assignPermissions(1L, permissionIds);
  }

  @Test
  @DisplayName("分配权限时角色不存在应该抛出异常")
  void should_throw_exception_when_assign_permissions_to_nonexistent_role() {
    // Given
    List<Long> permissionIds = Arrays.asList(1L, 2L, 3L);
    when(roleDAO.findById(999L)).thenReturn(null);

    // When & Then
    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> roleService.assignPermissions(999L, permissionIds));
    assertEquals(ErrorCode.ROLE_NOT_FOUND.getCode(), exception.getCode());
    verify(roleDAO, never()).assignPermissions(anyLong(), anyList());
  }

  @Test
  @DisplayName("应该成功分配菜单给角色")
  void should_assign_menus_to_role() {
    // Given
    List<Long> menuIds = Arrays.asList(1L, 2L, 3L);
    when(roleDAO.findById(1L)).thenReturn(testRole);

    // When
    roleService.assignMenus(1L, menuIds);

    // Then
    verify(roleDAO).findById(1L);
    verify(roleDAO).removeMenus(1L);
    verify(roleDAO).assignMenus(1L, menuIds);
  }

  @Test
  @DisplayName("分配菜单时角色不存在应该抛出异常")
  void should_throw_exception_when_assign_menus_to_nonexistent_role() {
    // Given
    List<Long> menuIds = Arrays.asList(1L, 2L, 3L);
    when(roleDAO.findById(999L)).thenReturn(null);

    // When & Then
    BusinessException exception =
        assertThrows(BusinessException.class, () -> roleService.assignMenus(999L, menuIds));
    assertEquals(ErrorCode.ROLE_NOT_FOUND.getCode(), exception.getCode());
    verify(roleDAO, never()).assignMenus(anyLong(), anyList());
  }
}
