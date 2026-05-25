package io.github.lystrosaurus.admin.system.permission.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.system.permission.dao.PermissionDAO;
import io.github.lystrosaurus.admin.system.permission.dto.PermissionCreateDTO;
import io.github.lystrosaurus.admin.system.permission.entity.SysPermission;
import io.github.lystrosaurus.admin.system.permission.mapstruct.PermissionMapper;
import io.github.lystrosaurus.admin.system.permission.vo.PermissionVO;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** PermissionService 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("权限服务测试")
class PermissionServiceTest {

  @Mock private PermissionDAO permissionDAO;

  @Mock private PermissionMapper permissionMapper;

  @InjectMocks private PermissionService permissionService;

  private SysPermission testPermission;
  private PermissionCreateDTO createDTO;
  private PermissionVO permissionVO;

  @BeforeEach
  void setUp() {
    testPermission = new SysPermission();
    testPermission.setId(1L);
    testPermission.setCode("user:read");
    testPermission.setName("查看用户");
    testPermission.setStatus("ENABLED");

    createDTO = new PermissionCreateDTO("user:read", "查看用户", "API", "user", "user", "read");
    permissionVO = new PermissionVO(1L, "user:read", "查看用户", "API", "user", "user", "read");
  }

  @Test
  @DisplayName("应该成功创建权限")
  void should_create_permission_when_code_not_exists() {
    when(permissionDAO.existsByCode("user:read")).thenReturn(false);
    doAnswer(
            invocation -> {
              SysPermission saved = invocation.getArgument(0);
              saved.setId(1L);
              return null;
            })
        .when(permissionDAO)
        .save(any(SysPermission.class));
    when(permissionMapper.toPermissionVO(any(SysPermission.class))).thenReturn(permissionVO);

    PermissionVO result = permissionService.create(createDTO);

    assertNotNull(result);
    assertEquals("user:read", result.code());
    verify(permissionDAO).existsByCode("user:read");
    verify(permissionDAO).save(any(SysPermission.class));
  }

  @Test
  @DisplayName("创建权限时编码已存在应该抛出异常")
  void should_throw_exception_when_permission_code_already_exists() {
    when(permissionDAO.existsByCode("user:read")).thenReturn(true);

    BusinessException exception =
        assertThrows(BusinessException.class, () -> permissionService.create(createDTO));
    assertEquals(ErrorCode.DATA_DUPLICATE_KEY.getCode(), exception.getCode());
  }

  @Test
  @DisplayName("应该成功删除权限")
  void should_delete_permission_by_id() {
    permissionService.deleteById(1L);
    verify(permissionDAO).deleteById(1L);
  }

  @Test
  @DisplayName("应该成功查询所有权限")
  void should_find_all_permissions() {
    List<SysPermission> permissions = Arrays.asList(testPermission);
    when(permissionDAO.findAll()).thenReturn(permissions);
    when(permissionMapper.toPermissionVO(testPermission)).thenReturn(permissionVO);

    List<PermissionVO> result = permissionService.findAll();

    assertNotNull(result);
    assertEquals(1, result.size());
    verify(permissionDAO).findAll();
  }
}
