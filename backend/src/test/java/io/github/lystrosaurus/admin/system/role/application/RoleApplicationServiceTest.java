package io.github.lystrosaurus.admin.system.role.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import io.github.lystrosaurus.admin.shared.domain.acl.IdGenerator;
import io.github.lystrosaurus.admin.shared.domain.exception.DomainException;
import io.github.lystrosaurus.admin.system.menu.domain.acl.MenuQueryPort;
import io.github.lystrosaurus.admin.system.menu.domain.acl.MenuSummary;
import io.github.lystrosaurus.admin.system.permission.domain.acl.PermissionQueryPort;
import io.github.lystrosaurus.admin.system.permission.domain.acl.PermissionSummary;
import io.github.lystrosaurus.admin.system.role.adapter.response.RoleDetailResponse;
import io.github.lystrosaurus.admin.system.role.adapter.response.RoleResponse;
import io.github.lystrosaurus.admin.system.role.application.assembler.RoleAssembler;
import io.github.lystrosaurus.admin.system.role.application.command.RoleCreateCommand;
import io.github.lystrosaurus.admin.system.role.application.command.RoleUpdateCommand;
import io.github.lystrosaurus.admin.system.role.domain.acl.RoleQueryRepository;
import io.github.lystrosaurus.admin.system.role.domain.acl.RoleRepository;
import io.github.lystrosaurus.admin.system.role.domain.model.Role;
import io.github.lystrosaurus.admin.system.role.domain.model.RoleErrorCode;
import io.github.lystrosaurus.admin.system.role.domain.model.RoleId;
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

/** RoleApplicationService 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("角色应用服务测试")
class RoleApplicationServiceTest {

  @Mock private RoleRepository roleRepository;

  @Mock private RoleQueryRepository roleQueryRepository;

  @Mock private PermissionQueryPort permissionQueryPort;

  @Mock private MenuQueryPort menuQueryPort;

  @Mock private IdGenerator idGenerator;

  @Mock private RoleAssembler assembler;

  @InjectMocks private RoleApplicationService roleApplicationService;

  private Role testRole;
  private RoleCreateCommand createCommand;
  private RoleUpdateCommand updateCommand;
  private RoleResponse roleResponse;

  @BeforeEach
  void setUp() {
    testRole =
        Role.restore(
            RoleId.of(1L),
            "ADMIN",
            "管理员",
            "系统管理员角色",
            1,
            "ENABLED",
            "ALL",
            LocalDateTime.now(),
            1);
    createCommand = new RoleCreateCommand("ADMIN", "管理员", "系统管理员角色", 1, "ALL");
    updateCommand = new RoleUpdateCommand("超级管理员", "超级管理员角色", 1, "ENABLED", "ALL");
    roleResponse = new RoleResponse(1L, "ADMIN", "管理员", "系统管理员角色", "ENABLED", "ALL");
  }

  @Test
  @DisplayName("应该成功创建角色")
  void should_create_role_when_code_not_exists() {
    when(roleRepository.existsByCode("ADMIN")).thenReturn(false);
    when(idGenerator.nextId()).thenReturn(1L);
    when(assembler.toResponse(any(Role.class))).thenReturn(roleResponse);

    RoleResponse result = roleApplicationService.create(createCommand);

    assertNotNull(result);
    assertEquals("ADMIN", result.code());
    assertEquals("管理员", result.name());
    verify(roleRepository).existsByCode("ADMIN");
    verify(roleRepository).save(any(Role.class));
  }

  @Test
  @DisplayName("创建角色时编码已存在应该抛出异常")
  void should_throw_exception_when_role_code_already_exists() {
    when(roleRepository.existsByCode("ADMIN")).thenReturn(true);

    DomainException exception =
        assertThrows(DomainException.class, () -> roleApplicationService.create(createCommand));

    assertEquals(RoleErrorCode.ROLE_ALREADY_EXISTS.code(), exception.code());
    verify(roleRepository).existsByCode("ADMIN");
    verify(roleRepository, never()).save(any());
  }

  @Test
  @DisplayName("应该成功更新角色")
  void should_update_role_when_role_exists() {
    when(roleRepository.findById(RoleId.of(1L))).thenReturn(Optional.of(testRole));
    when(assembler.toResponse(any(Role.class))).thenReturn(roleResponse);

    RoleResponse result = roleApplicationService.update(1L, updateCommand);

    assertNotNull(result);
    verify(roleRepository).findById(RoleId.of(1L));
    verify(roleRepository).save(any(Role.class));
  }

  @Test
  @DisplayName("更新角色时角色不存在应该抛出异常")
  void should_throw_exception_when_update_nonexistent_role() {
    when(roleRepository.findById(RoleId.of(999L))).thenReturn(Optional.empty());

    DomainException exception =
        assertThrows(
            DomainException.class, () -> roleApplicationService.update(999L, updateCommand));

    assertEquals(RoleErrorCode.ROLE_NOT_FOUND.code(), exception.code());
    verify(roleRepository, never()).save(any());
  }

  @Test
  @DisplayName("应该成功删除角色")
  void should_delete_role_by_id() {
    roleApplicationService.deleteById(1L);

    verify(roleRepository).deleteById(RoleId.of(1L));
  }

  @Test
  @DisplayName("应该成功查询角色详情")
  void should_find_role_detail_by_id() {
    PermissionSummary permissionSummary =
        new PermissionSummary(1L, "user:read", "查看用户", "API", "user", "user", "read");
    MenuSummary menuSummary =
        new MenuSummary(
            1L, 0L, "用户管理", "/user", "UserManagement", "user", 1, (byte) 1, "user:read",
            (byte) 1, (byte) 1);
    RoleDetailResponse detailResponse =
        new RoleDetailResponse(
            1L,
            "ADMIN",
            "管理员",
            "系统管理员角色",
            1,
            "ENABLED",
            "ALL",
            List.of(),
            List.of(),
            LocalDateTime.now());

    when(roleRepository.findById(RoleId.of(1L))).thenReturn(Optional.of(testRole));
    when(permissionQueryPort.findByRoleId(1L)).thenReturn(List.of(permissionSummary));
    when(menuQueryPort.findByRoleId(1L)).thenReturn(List.of(menuSummary));
    when(assembler.toDetailResponse(any(Role.class), anyList(), anyList()))
        .thenReturn(detailResponse);

    RoleDetailResponse result = roleApplicationService.findById(1L);

    assertNotNull(result);
    assertEquals(1L, result.id());
    verify(roleRepository).findById(RoleId.of(1L));
    verify(permissionQueryPort).findByRoleId(1L);
    verify(menuQueryPort).findByRoleId(1L);
  }

  @Test
  @DisplayName("查询角色详情时角色不存在应该抛出异常")
  void should_throw_exception_when_find_nonexistent_role() {
    when(roleRepository.findById(RoleId.of(999L))).thenReturn(Optional.empty());

    DomainException exception =
        assertThrows(DomainException.class, () -> roleApplicationService.findById(999L));

    assertEquals(RoleErrorCode.ROLE_NOT_FOUND.code(), exception.code());
  }

  @Test
  @DisplayName("应该成功查询所有角色")
  void should_find_all_roles() {
    when(roleQueryRepository.findAll()).thenReturn(List.of(testRole));
    when(assembler.toResponse(testRole)).thenReturn(roleResponse);

    List<RoleResponse> result = roleApplicationService.findAll();

    assertNotNull(result);
    assertEquals(1, result.size());
    verify(roleQueryRepository).findAll();
  }

  @Test
  @DisplayName("应该成功分配权限给角色")
  void should_assign_permissions_to_role() {
    List<Long> permissionIds = List.of(1L, 2L, 3L);
    when(roleRepository.findById(RoleId.of(1L))).thenReturn(Optional.of(testRole));

    roleApplicationService.assignPermissions(1L, permissionIds);

    verify(roleRepository).findById(RoleId.of(1L));
    verify(roleRepository).replacePermissions(RoleId.of(1L), permissionIds);
    verify(roleRepository).save(any(Role.class));
  }

  @Test
  @DisplayName("分配权限时角色不存在应该抛出异常")
  void should_throw_exception_when_assign_permissions_to_nonexistent_role() {
    List<Long> permissionIds = List.of(1L, 2L, 3L);
    when(roleRepository.findById(RoleId.of(999L))).thenReturn(Optional.empty());

    DomainException exception =
        assertThrows(
            DomainException.class,
            () -> roleApplicationService.assignPermissions(999L, permissionIds));

    assertEquals(RoleErrorCode.ROLE_NOT_FOUND.code(), exception.code());
    verify(roleRepository, never()).replacePermissions(any(), anyList());
  }

  @Test
  @DisplayName("应该成功分配菜单给角色")
  void should_assign_menus_to_role() {
    List<Long> menuIds = List.of(1L, 2L, 3L);
    when(roleRepository.findById(RoleId.of(1L))).thenReturn(Optional.of(testRole));

    roleApplicationService.assignMenus(1L, menuIds);

    verify(roleRepository).findById(RoleId.of(1L));
    verify(roleRepository).replaceMenus(RoleId.of(1L), menuIds);
    verify(roleRepository).save(any(Role.class));
  }

  @Test
  @DisplayName("分配菜单时角色不存在应该抛出异常")
  void should_throw_exception_when_assign_menus_to_nonexistent_role() {
    List<Long> menuIds = List.of(1L, 2L, 3L);
    when(roleRepository.findById(RoleId.of(999L))).thenReturn(Optional.empty());

    DomainException exception =
        assertThrows(
            DomainException.class, () -> roleApplicationService.assignMenus(999L, menuIds));

    assertEquals(RoleErrorCode.ROLE_NOT_FOUND.code(), exception.code());
    verify(roleRepository, never()).replaceMenus(any(), anyList());
  }
}
