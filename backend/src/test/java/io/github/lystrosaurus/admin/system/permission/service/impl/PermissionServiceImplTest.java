package io.github.lystrosaurus.admin.system.permission.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.system.permission.dao.PermissionDAO;
import io.github.lystrosaurus.admin.system.permission.dto.PermissionCreateDTO;
import io.github.lystrosaurus.admin.system.permission.dto.PermissionUpdateDTO;
import io.github.lystrosaurus.admin.system.permission.entity.SysPermission;
import io.github.lystrosaurus.admin.system.permission.mapstruct.PermissionMapper;
import io.github.lystrosaurus.admin.system.permission.vo.PermissionVO;
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

/** PermissionServiceImpl 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("权限服务测试")
class PermissionServiceImplTest {

  @Mock private PermissionDAO permissionDAO;

  @Mock private PermissionMapper permissionMapper;

  @InjectMocks private PermissionServiceImpl permissionService;

  private SysPermission testPermission;
  private PermissionCreateDTO createDTO;
  private PermissionUpdateDTO updateDTO;
  private PermissionVO permissionVO;

  @BeforeEach
  void setUp() {
    // 准备测试数据
    testPermission = new SysPermission();
    testPermission.setId(1L);
    testPermission.setCode("user:read");
    testPermission.setName("查看用户");
    testPermission.setType("API");
    testPermission.setModule("user");
    testPermission.setResource("user");
    testPermission.setAction("read");
    testPermission.setStatus("ENABLED");
    testPermission.setSortOrder(1);
    testPermission.setCreatedAt(LocalDateTime.now());

    createDTO = new PermissionCreateDTO("user:read", "查看用户", "API", "user", "user", "read");
    updateDTO = new PermissionUpdateDTO("编辑用户", "API", "user", "user", "update", "ENABLED");
    permissionVO = new PermissionVO(1L, "user:read", "查看用户", "API", "user", "user", "read");
  }

  @Test
  @DisplayName("应该成功创建权限")
  void should_create_permission_when_code_not_exists() {
    // Given
    when(permissionDAO.existsByCode("user:read")).thenReturn(false);
    doAnswer(
            invocation -> {
              SysPermission savedPermission = invocation.getArgument(0);
              savedPermission.setId(1L);
              return null;
            })
        .when(permissionDAO)
        .save(any(SysPermission.class));
    when(permissionMapper.toPermissionVO(any(SysPermission.class))).thenReturn(permissionVO);

    // When
    PermissionVO result = permissionService.create(createDTO);

    // Then
    assertNotNull(result);
    assertEquals("user:read", result.code());
    assertEquals("查看用户", result.name());
    verify(permissionDAO).existsByCode("user:read");
    verify(permissionDAO).save(any(SysPermission.class));
  }

  @Test
  @DisplayName("创建权限时编码已存在应该抛出异常")
  void should_throw_exception_when_permission_code_already_exists() {
    // Given
    when(permissionDAO.existsByCode("user:read")).thenReturn(true);

    // When & Then
    BusinessException exception =
        assertThrows(BusinessException.class, () -> permissionService.create(createDTO));
    assertEquals(ErrorCode.DATA_DUPLICATE_KEY.getCode(), exception.getCode());
    verify(permissionDAO).existsByCode("user:read");
    verify(permissionDAO, never()).save(any());
  }

  @Test
  @DisplayName("应该成功更新权限")
  void should_update_permission_when_permission_exists() {
    // Given
    when(permissionDAO.findById(1L)).thenReturn(testPermission);
    when(permissionMapper.toPermissionVO(any(SysPermission.class))).thenReturn(permissionVO);

    // When
    PermissionVO result = permissionService.update(1L, updateDTO);

    // Then
    assertNotNull(result);
    verify(permissionDAO).findById(1L);
    verify(permissionDAO).update(any(SysPermission.class));
  }

  @Test
  @DisplayName("更新权限时权限不存在应该抛出异常")
  void should_throw_exception_when_update_nonexistent_permission() {
    // Given
    when(permissionDAO.findById(999L)).thenReturn(null);

    // When & Then
    BusinessException exception =
        assertThrows(BusinessException.class, () -> permissionService.update(999L, updateDTO));
    assertEquals(ErrorCode.PERMISSION_NOT_FOUND.getCode(), exception.getCode());
    verify(permissionDAO).findById(999L);
    verify(permissionDAO, never()).update(any());
  }

  @Test
  @DisplayName("应该成功删除权限")
  void should_delete_permission_by_id() {
    // When
    permissionService.deleteById(1L);

    // Then
    verify(permissionDAO).deleteById(1L);
  }

  @Test
  @DisplayName("应该成功根据角色ID查询权限")
  void should_find_permissions_by_role_id() {
    // Given
    List<SysPermission> permissions = Arrays.asList(testPermission);
    when(permissionDAO.findByRoleId(1L)).thenReturn(permissions);
    when(permissionMapper.toPermissionVO(testPermission)).thenReturn(permissionVO);

    // When
    List<PermissionVO> result = permissionService.findByRoleId(1L);

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("user:read", result.get(0).code());
    verify(permissionDAO).findByRoleId(1L);
  }

  @Test
  @DisplayName("应该成功根据用户ID查询权限")
  void should_find_permissions_by_user_id() {
    // Given
    List<SysPermission> permissions = Arrays.asList(testPermission);
    when(permissionDAO.findByUserId(1L)).thenReturn(permissions);
    when(permissionMapper.toPermissionVO(testPermission)).thenReturn(permissionVO);

    // When
    List<PermissionVO> result = permissionService.findByUserId(1L);

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("user:read", result.get(0).code());
    verify(permissionDAO).findByUserId(1L);
  }

  @Test
  @DisplayName("根据角色ID查询权限时返回空列表")
  void should_return_empty_list_when_no_permissions_for_role() {
    // Given
    when(permissionDAO.findByRoleId(999L)).thenReturn(Arrays.asList());

    // When
    List<PermissionVO> result = permissionService.findByRoleId(999L);

    // Then
    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(permissionDAO).findByRoleId(999L);
  }

  @Test
  @DisplayName("根据用户ID查询权限时返回空列表")
  void should_return_empty_list_when_no_permissions_for_user() {
    // Given
    when(permissionDAO.findByUserId(999L)).thenReturn(Arrays.asList());

    // When
    List<PermissionVO> result = permissionService.findByUserId(999L);

    // Then
    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(permissionDAO).findByUserId(999L);
  }
}
