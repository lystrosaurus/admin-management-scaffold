package io.github.lystrosaurus.admin.system.permission.infrastructure.persistence;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import io.github.lystrosaurus.admin.shared.domain.acl.DomainEventPublisher;
import io.github.lystrosaurus.admin.system.permission.domain.model.Permission;
import io.github.lystrosaurus.admin.system.permission.domain.model.PermissionId;
import io.github.lystrosaurus.admin.system.permission.infrastructure.converter.PermissionPoConverter;
import io.github.lystrosaurus.admin.system.permission.infrastructure.persistence.mapper.PermissionMpMapper;
import io.github.lystrosaurus.admin.system.permission.infrastructure.persistence.po.PermissionPO;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** PermissionRepositoryImpl 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("权限仓储实现测试")
class PermissionRepositoryImplTest {

  @Mock private PermissionMpMapper permissionMpMapper;
  @Mock private DomainEventPublisher eventPublisher;

  private PermissionRepositoryImpl repository;

  @BeforeEach
  void setUp() {
    repository =
        new PermissionRepositoryImpl(
            permissionMpMapper, eventPublisher, new PermissionPoConverter());
  }

  private PermissionPO samplePo() {
    PermissionPO po = new PermissionPO();
    po.setId(1L);
    po.setCode("user:read");
    po.setName("查看用户");
    po.setType("API");
    po.setModule("user");
    po.setResource("user");
    po.setAction("read");
    po.setStatus("ENABLED");
    po.setCreatedAt(LocalDateTime.now());
    po.setVersion(1);
    return po;
  }

  private Permission samplePermission() {
    return Permission.restore(
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
  }

  @Test
  @DisplayName("根据 ID 查询权限应该返回领域对象")
  void should_find_permission_by_id() {
    when(permissionMpMapper.selectById(1L)).thenReturn(samplePo());

    Optional<Permission> result = repository.findById(PermissionId.of(1L));

    assertTrue(result.isPresent());
    assertEquals("user:read", result.get().code());
  }

  @Test
  @DisplayName("根据 ID 查询不存在的权限应该返回空")
  void should_return_empty_when_find_by_id_not_exists() {
    when(permissionMpMapper.selectById(999L)).thenReturn(null);

    assertTrue(repository.findById(PermissionId.of(999L)).isEmpty());
  }

  @Test
  @DisplayName("根据编码查询权限")
  void should_find_permission_by_code() {
    when(permissionMpMapper.selectOne(any())).thenReturn(samplePo());

    Optional<Permission> result = repository.findByCode("user:read");

    assertTrue(result.isPresent());
    assertEquals("user:read", result.get().code());
  }

  @Test
  @DisplayName("根据编码查询不存在的权限应该返回空")
  void should_return_empty_when_find_by_code_not_exists() {
    when(permissionMpMapper.selectOne(any())).thenReturn(null);

    assertTrue(repository.findByCode("not:exists").isEmpty());
  }

  @Test
  @DisplayName("编码存在时 existsByCode 应该返回 true")
  void should_return_true_when_code_exists() {
    when(permissionMpMapper.selectCount(any())).thenReturn(1L);

    assertTrue(repository.existsByCode("user:read"));
  }

  @Test
  @DisplayName("编码不存在时 existsByCode 应该返回 false")
  void should_return_false_when_code_not_exists() {
    when(permissionMpMapper.selectCount(any())).thenReturn(0L);

    assertFalse(repository.existsByCode("not:exists"));
  }

  @Test
  @DisplayName("编码被其他权限占用时 existsByCodeAndIdNot 应该返回 true")
  void should_return_true_when_code_used_by_other_permission() {
    when(permissionMpMapper.selectCount(any())).thenReturn(1L);

    assertTrue(repository.existsByCodeAndIdNot("user:read", PermissionId.of(2L)));
  }

  @Test
  @DisplayName("编码未被其他权限占用时 existsByCodeAndIdNot 应该返回 false")
  void should_return_false_when_code_not_used_by_other_permission() {
    when(permissionMpMapper.selectCount(any())).thenReturn(0L);

    assertFalse(repository.existsByCodeAndIdNot("user:read", PermissionId.of(1L)));
  }

  @Test
  @DisplayName("保存新权限应该执行插入并发布事件")
  void should_insert_when_save_new_permission() {
    Permission permission = samplePermission();
    when(permissionMpMapper.exists(any())).thenReturn(false);

    repository.save(permission);

    verify(permissionMpMapper).insert(any(PermissionPO.class));
    verify(eventPublisher).publishAll(permission);
  }

  @Test
  @DisplayName("保存已存在权限应该执行更新")
  void should_update_when_save_existing_permission() {
    Permission permission = samplePermission();
    when(permissionMpMapper.exists(any())).thenReturn(true);

    repository.save(permission);

    verify(permissionMpMapper).updateById(any(PermissionPO.class));
  }

  @Test
  @DisplayName("根据 ID 删除权限")
  void should_delete_permission_by_id() {
    repository.deleteById(PermissionId.of(1L));

    verify(permissionMpMapper).deleteById(1L);
  }
}
