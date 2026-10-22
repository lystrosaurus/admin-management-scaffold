package io.github.lystrosaurus.admin.system.permission.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import io.github.lystrosaurus.admin.shared.domain.acl.IdGenerator;
import io.github.lystrosaurus.admin.shared.domain.exception.DomainException;
import io.github.lystrosaurus.admin.system.permission.adapter.response.PermissionResponse;
import io.github.lystrosaurus.admin.system.permission.application.assembler.PermissionAssembler;
import io.github.lystrosaurus.admin.system.permission.application.command.PermissionCreateCommand;
import io.github.lystrosaurus.admin.system.permission.application.command.PermissionUpdateCommand;
import io.github.lystrosaurus.admin.system.permission.domain.acl.PermissionQueryRepository;
import io.github.lystrosaurus.admin.system.permission.domain.acl.PermissionRepository;
import io.github.lystrosaurus.admin.system.permission.domain.model.Permission;
import io.github.lystrosaurus.admin.system.permission.domain.model.PermissionErrorCode;
import io.github.lystrosaurus.admin.system.permission.domain.model.PermissionId;
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

/** PermissionApplicationService 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("权限应用服务测试")
class PermissionApplicationServiceTest {

  @Mock private PermissionRepository permissionRepository;
  @Mock private PermissionQueryRepository permissionQueryRepository;
  @Mock private IdGenerator idGenerator;
  @Mock private PermissionAssembler assembler;

  @InjectMocks private PermissionApplicationService permissionApplicationService;

  private Permission testPermission;
  private PermissionResponse permissionResponse;

  @BeforeEach
  void setUp() {
    testPermission =
        Permission.restore(
            PermissionId.of(1L),
            "user:read",
            "查看用户",
            "API",
            "user",
            "user",
            "read",
            "查看用户权限",
            1,
            "ENABLED",
            LocalDateTime.now(),
            1);
    permissionResponse =
        new PermissionResponse(1L, "user:read", "查看用户", "API", "user", "user", "read");
  }

  @Test
  @DisplayName("应该成功创建权限")
  void should_create_permission_when_code_not_exists() {
    when(permissionRepository.existsByCode("user:read")).thenReturn(false);
    when(idGenerator.nextId()).thenReturn(1L);
    when(assembler.toResponse(any(Permission.class))).thenReturn(permissionResponse);

    PermissionResponse result =
        permissionApplicationService.create(
            new PermissionCreateCommand("user:read", "查看用户", "API", "user", "user", "read"));

    assertNotNull(result);
    assertEquals("user:read", result.code());
    verify(permissionRepository).save(any(Permission.class));
  }

  @Test
  @DisplayName("创建权限时编码重复应该抛出异常")
  void should_throw_exception_when_permission_code_duplicate() {
    when(permissionRepository.existsByCode("user:read")).thenReturn(true);

    DomainException exception =
        assertThrows(
            DomainException.class,
            () ->
                permissionApplicationService.create(
                    new PermissionCreateCommand(
                        "user:read", "查看用户", "API", "user", "user", "read")));

    assertEquals(PermissionErrorCode.PERMISSION_DUPLICATE_KEY.code(), exception.code());
    verify(permissionRepository, never()).save(any());
  }

  @Test
  @DisplayName("应该成功更新权限")
  void should_update_permission_when_exists() {
    when(permissionRepository.findById(PermissionId.of(1L)))
        .thenReturn(Optional.of(testPermission));
    when(assembler.toResponse(any(Permission.class))).thenReturn(permissionResponse);

    PermissionResponse result =
        permissionApplicationService.update(
            1L,
            new PermissionUpdateCommand("查看用户", "API", "user", "user", "read", "ENABLED"));

    assertNotNull(result);
    verify(permissionRepository).save(any(Permission.class));
  }

  @Test
  @DisplayName("更新权限时权限不存在应该抛出异常")
  void should_throw_exception_when_update_nonexistent_permission() {
    when(permissionRepository.findById(PermissionId.of(999L))).thenReturn(Optional.empty());

    DomainException exception =
        assertThrows(
            DomainException.class,
            () ->
                permissionApplicationService.update(
                    999L,
                    new PermissionUpdateCommand(
                        "查看用户", "API", "user", "user", "read", "ENABLED")));

    assertEquals(PermissionErrorCode.PERMISSION_NOT_FOUND.code(), exception.code());
  }

  @Test
  @DisplayName("应该成功删除权限")
  void should_delete_permission_by_id() {
    permissionApplicationService.deleteById(1L);

    verify(permissionRepository).deleteById(PermissionId.of(1L));
  }

  @Test
  @DisplayName("应该按角色查询权限")
  void should_find_permissions_by_role_id() {
    when(permissionQueryRepository.findByRoleId(2L)).thenReturn(List.of(testPermission));
    when(assembler.toResponse(testPermission)).thenReturn(permissionResponse);

    List<PermissionResponse> result = permissionApplicationService.findByRoleId(2L);

    assertEquals(1, result.size());
    verify(permissionQueryRepository).findByRoleId(2L);
  }

  @Test
  @DisplayName("应该按用户查询权限")
  void should_find_permissions_by_user_id() {
    when(permissionQueryRepository.findByUserId(3L)).thenReturn(List.of(testPermission));
    when(assembler.toResponse(testPermission)).thenReturn(permissionResponse);

    List<PermissionResponse> result = permissionApplicationService.findByUserId(3L);

    assertEquals(1, result.size());
    verify(permissionQueryRepository).findByUserId(3L);
  }

  @Test
  @DisplayName("应该查询全部权限")
  void should_find_all_permissions() {
    when(permissionQueryRepository.findAll()).thenReturn(List.of(testPermission));
    when(assembler.toResponse(testPermission)).thenReturn(permissionResponse);

    List<PermissionResponse> result = permissionApplicationService.findAll();

    assertEquals(1, result.size());
    verify(permissionQueryRepository).findAll();
  }
}
