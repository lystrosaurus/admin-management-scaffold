package io.github.lystrosaurus.admin.system.role.infrastructure.acl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import io.github.lystrosaurus.admin.system.role.infrastructure.persistence.mapper.RoleMenuMpMapper;
import io.github.lystrosaurus.admin.system.role.infrastructure.persistence.mapper.RoleOrgMpMapper;
import io.github.lystrosaurus.admin.system.role.infrastructure.persistence.mapper.RolePermissionMpMapper;
import io.github.lystrosaurus.admin.system.role.infrastructure.persistence.mapper.UserRoleMpMapper;
import io.github.lystrosaurus.admin.system.role.infrastructure.persistence.po.RoleMenuPO;
import io.github.lystrosaurus.admin.system.role.infrastructure.persistence.po.RoleOrgPO;
import io.github.lystrosaurus.admin.system.role.infrastructure.persistence.po.RolePermissionPO;
import io.github.lystrosaurus.admin.system.role.infrastructure.persistence.po.UserRolePO;
import java.util.List;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** RoleAssociationPortImpl 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("角色关联端口实现测试")
class RoleAssociationPortImplTest {

  @Mock private UserRoleMpMapper userRoleMapper;
  @Mock private RolePermissionMpMapper rolePermissionMapper;
  @Mock private RoleMenuMpMapper roleMenuMapper;
  @Mock private RoleOrgMpMapper roleOrgMapper;
  @Mock private SqlSessionFactory sqlSessionFactory;
  @Mock private SqlSession sqlSession;

  private RoleAssociationPortImpl port;

  @BeforeEach
  void setUp() {
    port =
        new RoleAssociationPortImpl(
            userRoleMapper,
            rolePermissionMapper,
            roleMenuMapper,
            roleOrgMapper,
            sqlSessionFactory);
  }

  @Test
  @DisplayName("根据用户 ID 查询角色 ID 列表")
  void should_find_role_ids_by_user_id() {
    UserRolePO userRole = new UserRolePO();
    userRole.setUserId(1L);
    userRole.setRoleId(2L);
    when(userRoleMapper.selectList(any())).thenReturn(List.of(userRole));

    assertEquals(List.of(2L), port.findRoleIdsByUserId(1L));
  }

  @Test
  @DisplayName("根据角色 ID 查询权限 ID 列表")
  void should_find_permission_ids_by_role_id() {
    RolePermissionPO relation = new RolePermissionPO();
    relation.setRoleId(1L);
    relation.setPermissionId(10L);
    when(rolePermissionMapper.selectList(any())).thenReturn(List.of(relation));

    assertEquals(List.of(10L), port.findPermissionIdsByRoleId(1L));
  }

  @Test
  @DisplayName("根据用户 ID 查询权限 ID 列表（去重）")
  void should_find_permission_ids_by_user_id() {
    UserRolePO userRole = new UserRolePO();
    userRole.setUserId(1L);
    userRole.setRoleId(2L);
    RolePermissionPO relation = new RolePermissionPO();
    relation.setRoleId(2L);
    relation.setPermissionId(10L);
    when(userRoleMapper.selectList(any())).thenReturn(List.of(userRole));
    when(rolePermissionMapper.selectList(any())).thenReturn(List.of(relation, relation));

    assertEquals(List.of(10L), port.findPermissionIdsByUserId(1L));
  }

  @Test
  @DisplayName("用户没有角色时权限 ID 列表为空且不查询权限表")
  void should_return_empty_permissions_when_user_has_no_roles() {
    when(userRoleMapper.selectList(any())).thenReturn(List.of());

    assertTrue(port.findPermissionIdsByUserId(1L).isEmpty());
    verify(rolePermissionMapper, never()).selectList(any());
  }

  @Test
  @DisplayName("根据角色 ID 查询菜单 ID 列表")
  void should_find_menu_ids_by_role_id() {
    RoleMenuPO relation = new RoleMenuPO();
    relation.setRoleId(1L);
    relation.setMenuId(20L);
    when(roleMenuMapper.selectList(any())).thenReturn(List.of(relation));

    assertEquals(List.of(20L), port.findMenuIdsByRoleId(1L));
  }

  @Test
  @DisplayName("根据用户 ID 查询菜单 ID 列表（去重）")
  void should_find_menu_ids_by_user_id() {
    UserRolePO userRole = new UserRolePO();
    userRole.setUserId(1L);
    userRole.setRoleId(2L);
    RoleMenuPO relation = new RoleMenuPO();
    relation.setRoleId(2L);
    relation.setMenuId(20L);
    when(userRoleMapper.selectList(any())).thenReturn(List.of(userRole));
    when(roleMenuMapper.selectList(any())).thenReturn(List.of(relation, relation));

    assertEquals(List.of(20L), port.findMenuIdsByUserId(1L));
  }

  @Test
  @DisplayName("用户没有角色时菜单 ID 列表为空且不查询菜单表")
  void should_return_empty_menus_when_user_has_no_roles() {
    when(userRoleMapper.selectList(any())).thenReturn(List.of());

    assertTrue(port.findMenuIdsByUserId(1L).isEmpty());
    verify(roleMenuMapper, never()).selectList(any());
  }

  @Test
  @DisplayName("根据角色 ID 查询组织 ID 列表")
  void should_find_org_ids_by_role_id() {
    RoleOrgPO relation = new RoleOrgPO();
    relation.setRoleId(1L);
    relation.setOrgId(30L);
    when(roleOrgMapper.findByRoleId(1L)).thenReturn(List.of(relation));

    assertEquals(List.of(30L), port.findOrgIdsByRoleId(1L));
  }

  @Test
  @DisplayName("分配角色应该先删除旧关联再批量插入")
  void should_assign_roles() {
    UserRoleMpMapper batchMapper = mock(UserRoleMpMapper.class);
    when(sqlSessionFactory.openSession(ExecutorType.BATCH)).thenReturn(sqlSession);
    when(sqlSession.getMapper(UserRoleMpMapper.class)).thenReturn(batchMapper);

    port.assignRoles(1L, List.of(2L, 3L));

    verify(userRoleMapper).delete(any());
    verify(batchMapper, times(2)).insert(any(UserRolePO.class));
    verify(sqlSession).commit();
    verify(sqlSession).close();
  }

  @Test
  @DisplayName("分配空角色列表应该直接返回")
  void should_do_nothing_when_assign_empty_roles() {
    port.assignRoles(1L, List.of());

    verify(userRoleMapper, never()).delete(any());
    verify(sqlSessionFactory, never()).openSession(ExecutorType.BATCH);
  }

  @Test
  @DisplayName("移除用户角色")
  void should_remove_roles() {
    port.removeRoles(1L);

    verify(userRoleMapper).delete(any());
  }
}
