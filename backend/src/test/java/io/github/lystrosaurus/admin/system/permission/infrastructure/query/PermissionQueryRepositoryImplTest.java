package io.github.lystrosaurus.admin.system.permission.infrastructure.query;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import io.github.lystrosaurus.admin.system.permission.domain.model.Permission;
import io.github.lystrosaurus.admin.system.permission.infrastructure.converter.PermissionPoConverter;
import io.github.lystrosaurus.admin.system.permission.infrastructure.persistence.mapper.PermissionMpMapper;
import io.github.lystrosaurus.admin.system.permission.infrastructure.persistence.po.PermissionPO;
import io.github.lystrosaurus.admin.system.role.domain.acl.RoleAssociationPort;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** PermissionQueryRepositoryImpl 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("权限查询仓储实现测试")
class PermissionQueryRepositoryImplTest {

  @Mock private PermissionMpMapper permissionMpMapper;
  @Mock private RoleAssociationPort roleAssociationPort;

  private PermissionQueryRepositoryImpl queryRepository;

  @BeforeEach
  void setUp() {
    queryRepository =
        new PermissionQueryRepositoryImpl(
            permissionMpMapper, roleAssociationPort, new PermissionPoConverter());
  }

  private PermissionPO samplePo() {
    PermissionPO po = new PermissionPO();
    po.setId(10L);
    po.setCode("user:read");
    po.setName("查看用户");
    po.setSortOrder(1);
    po.setStatus("ENABLED");
    po.setCreatedAt(LocalDateTime.now());
    po.setVersion(1);
    return po;
  }

  @Test
  @DisplayName("查询全部权限")
  void should_find_all() {
    when(permissionMpMapper.selectList(any())).thenReturn(List.of(samplePo()));

    List<Permission> result = queryRepository.findAll();

    assertEquals(1, result.size());
    assertEquals("user:read", result.get(0).code());
  }

  @Test
  @DisplayName("按角色查询权限")
  void should_find_by_role_id() {
    when(roleAssociationPort.findPermissionIdsByRoleId(2L)).thenReturn(List.of(10L));
    when(permissionMpMapper.selectBatchIds(List.of(10L))).thenReturn(List.of(samplePo()));

    List<Permission> result = queryRepository.findByRoleId(2L);

    assertEquals(1, result.size());
    assertEquals(10L, result.get(0).id().value());
  }

  @Test
  @DisplayName("按用户查询权限")
  void should_find_by_user_id() {
    when(roleAssociationPort.findPermissionIdsByUserId(3L)).thenReturn(List.of(10L));
    when(permissionMpMapper.selectBatchIds(List.of(10L))).thenReturn(List.of(samplePo()));

    List<Permission> result = queryRepository.findByUserId(3L);

    assertEquals(1, result.size());
  }

  @Test
  @DisplayName("角色没有权限时应该返回空且不查询权限表")
  void should_return_empty_when_role_has_no_permissions() {
    when(roleAssociationPort.findPermissionIdsByRoleId(2L)).thenReturn(List.of());

    assertTrue(queryRepository.findByRoleId(2L).isEmpty());
    verify(permissionMpMapper, never()).selectBatchIds(any());
  }

  @Test
  @DisplayName("用户没有权限时应该返回空且不查询权限表")
  void should_return_empty_when_user_has_no_permissions() {
    when(roleAssociationPort.findPermissionIdsByUserId(3L)).thenReturn(List.of());

    assertTrue(queryRepository.findByUserId(3L).isEmpty());
    verify(permissionMpMapper, never()).selectBatchIds(any());
  }
}
