package io.github.lystrosaurus.admin.system.role.infrastructure.persistence;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import io.github.lystrosaurus.admin.shared.domain.acl.DomainEventPublisher;
import io.github.lystrosaurus.admin.system.role.domain.model.Role;
import io.github.lystrosaurus.admin.system.role.domain.model.RoleId;
import io.github.lystrosaurus.admin.system.role.infrastructure.converter.RolePoConverter;
import io.github.lystrosaurus.admin.system.role.infrastructure.persistence.mapper.RoleMenuMpMapper;
import io.github.lystrosaurus.admin.system.role.infrastructure.persistence.mapper.RoleMpMapper;
import io.github.lystrosaurus.admin.system.role.infrastructure.persistence.mapper.RolePermissionMpMapper;
import io.github.lystrosaurus.admin.system.role.infrastructure.persistence.po.RoleMenuPO;
import io.github.lystrosaurus.admin.system.role.infrastructure.persistence.po.RolePO;
import io.github.lystrosaurus.admin.system.role.infrastructure.persistence.po.RolePermissionPO;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** RoleRepositoryImpl 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("角色仓储实现测试")
class RoleRepositoryImplTest {

  @Mock private RoleMpMapper roleMpMapper;
  @Mock private DomainEventPublisher eventPublisher;
  @Mock private RolePermissionMpMapper rolePermissionMpMapper;
  @Mock private RoleMenuMpMapper roleMenuMpMapper;
  @Mock private SqlSessionFactory sqlSessionFactory;
  @Mock private SqlSession sqlSession;

  private RoleRepositoryImpl repository;

  @BeforeEach
  void setUp() {
    repository =
        new RoleRepositoryImpl(
            roleMpMapper,
            eventPublisher,
            new RolePoConverter(),
            rolePermissionMpMapper,
            roleMenuMpMapper,
            sqlSessionFactory);
  }

  private RolePO samplePo() {
    RolePO po = new RolePO();
    po.setId(1L);
    po.setCode("ADMIN");
    po.setName("管理员");
    po.setDescription("系统管理员角色");
    po.setSortOrder(1);
    po.setStatus("ENABLED");
    po.setDataScopeType("ALL");
    po.setCreatedAt(LocalDateTime.now());
    po.setVersion(1);
    return po;
  }

  private Role sampleRole() {
    return Role.restore(
        RoleId.of(1L),
        "ADMIN",
        "管理员",
        "系统管理员角色",
        1,
        "ENABLED",
        "ALL",
        LocalDateTime.now(),
        1);
  }

  @Test
  @DisplayName("根据 ID 查询角色应该返回领域对象")
  void should_find_role_by_id() {
    when(roleMpMapper.selectById(1L)).thenReturn(samplePo());

    Optional<Role> result = repository.findById(RoleId.of(1L));

    assertTrue(result.isPresent());
    assertEquals("ADMIN", result.get().code());
  }

  @Test
  @DisplayName("根据 ID 查询不存在的角色应该返回空")
  void should_return_empty_when_find_by_id_not_exists() {
    when(roleMpMapper.selectById(999L)).thenReturn(null);

    Optional<Role> result = repository.findById(RoleId.of(999L));

    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("根据编码查询角色应该返回领域对象")
  void should_find_role_by_code() {
    when(roleMpMapper.selectOne(any())).thenReturn(samplePo());

    Optional<Role> result = repository.findByCode("ADMIN");

    assertTrue(result.isPresent());
    assertEquals("ADMIN", result.get().code());
  }

  @Test
  @DisplayName("根据编码查询不存在的角色应该返回空")
  void should_return_empty_when_find_by_code_not_exists() {
    when(roleMpMapper.selectOne(any())).thenReturn(null);

    Optional<Role> result = repository.findByCode("NOT_EXISTS");

    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("编码存在时 existsByCode 应该返回 true")
  void should_return_true_when_code_exists() {
    when(roleMpMapper.selectCount(any())).thenReturn(1L);

    assertTrue(repository.existsByCode("ADMIN"));
  }

  @Test
  @DisplayName("编码不存在时 existsByCode 应该返回 false")
  void should_return_false_when_code_not_exists() {
    when(roleMpMapper.selectCount(any())).thenReturn(0L);

    assertFalse(repository.existsByCode("NOT_EXISTS"));
  }

  @Test
  @DisplayName("编码被其他角色占用时 existsByCodeAndIdNot 应该返回 true")
  void should_return_true_when_code_used_by_other_role() {
    when(roleMpMapper.selectCount(any())).thenReturn(1L);

    assertTrue(repository.existsByCodeAndIdNot("ADMIN", RoleId.of(2L)));
  }

  @Test
  @DisplayName("编码未被其他角色占用时 existsByCodeAndIdNot 应该返回 false")
  void should_return_false_when_code_not_used_by_other_role() {
    when(roleMpMapper.selectCount(any())).thenReturn(0L);

    assertFalse(repository.existsByCodeAndIdNot("ADMIN", RoleId.of(1L)));
  }

  @Test
  @DisplayName("保存新角色应该执行插入并发布事件")
  void should_insert_when_save_new_role() {
    Role role = sampleRole();
    when(roleMpMapper.exists(any())).thenReturn(false);

    repository.save(role);

    verify(roleMpMapper).insert(any(RolePO.class));
    verify(roleMpMapper, never()).updateById(any(RolePO.class));
    verify(eventPublisher).publishAll(role);
  }

  @Test
  @DisplayName("保存已存在角色应该执行更新并发布事件")
  void should_update_when_save_existing_role() {
    Role role = sampleRole();
    when(roleMpMapper.exists(any())).thenReturn(true);

    repository.save(role);

    verify(roleMpMapper).updateById(any(RolePO.class));
    verify(roleMpMapper, never()).insert(any(RolePO.class));
    verify(eventPublisher).publishAll(role);
  }

  @Test
  @DisplayName("根据 ID 删除角色")
  void should_delete_role_by_id() {
    repository.deleteById(RoleId.of(1L));

    verify(roleMpMapper).deleteById(1L);
  }

  @Test
  @DisplayName("existsById 应该反映记录是否存在")
  void should_check_exists_by_id() {
    when(roleMpMapper.selectById(1L)).thenReturn(samplePo());
    when(roleMpMapper.selectById(2L)).thenReturn(null);

    assertTrue(repository.existsById(RoleId.of(1L)));
    assertFalse(repository.existsById(RoleId.of(2L)));
  }

  @Test
  @DisplayName("替换角色权限应该先删除再批量插入")
  void should_replace_role_permissions() {
    RolePermissionMpMapper batchMapper = mock(RolePermissionMpMapper.class);
    when(sqlSessionFactory.openSession(ExecutorType.BATCH)).thenReturn(sqlSession);
    when(sqlSession.getMapper(RolePermissionMpMapper.class)).thenReturn(batchMapper);

    repository.replacePermissions(RoleId.of(1L), List.of(10L, 20L));

    verify(rolePermissionMpMapper).delete(any());
    verify(batchMapper, times(2)).insert(any(RolePermissionPO.class));
    verify(sqlSession).commit();
    verify(sqlSession).close();
  }

  @Test
  @DisplayName("替换角色权限时空列表只删除不插入")
  void should_only_delete_when_replace_permissions_with_empty_list() {
    repository.replacePermissions(RoleId.of(1L), List.of());

    verify(rolePermissionMpMapper).delete(any());
    verify(sqlSessionFactory, never()).openSession(ExecutorType.BATCH);
  }

  @Test
  @DisplayName("替换角色菜单应该先删除再批量插入")
  void should_replace_role_menus() {
    RoleMenuMpMapper batchMapper = mock(RoleMenuMpMapper.class);
    when(sqlSessionFactory.openSession(ExecutorType.BATCH)).thenReturn(sqlSession);
    when(sqlSession.getMapper(RoleMenuMpMapper.class)).thenReturn(batchMapper);

    repository.replaceMenus(RoleId.of(1L), List.of(10L, 20L));

    verify(roleMenuMpMapper).delete(any());
    verify(batchMapper, times(2)).insert(any(RoleMenuPO.class));
    verify(sqlSession).commit();
    verify(sqlSession).close();
  }

  @Test
  @DisplayName("替换角色菜单时空列表只删除不插入")
  void should_only_delete_when_replace_menus_with_empty_list() {
    repository.replaceMenus(RoleId.of(1L), List.of());

    verify(roleMenuMpMapper).delete(any());
    verify(sqlSessionFactory, never()).openSession(ExecutorType.BATCH);
  }
}
