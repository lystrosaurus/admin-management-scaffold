package io.github.lystrosaurus.admin.system.role.infrastructure.acl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.lystrosaurus.admin.system.role.domain.acl.RoleAssociationPort;
import io.github.lystrosaurus.admin.system.role.infrastructure.persistence.mapper.RoleMenuMpMapper;
import io.github.lystrosaurus.admin.system.role.infrastructure.persistence.mapper.RoleOrgMpMapper;
import io.github.lystrosaurus.admin.system.role.infrastructure.persistence.mapper.RolePermissionMpMapper;
import io.github.lystrosaurus.admin.system.role.infrastructure.persistence.mapper.UserRoleMpMapper;
import io.github.lystrosaurus.admin.system.role.infrastructure.persistence.po.RoleMenuPO;
import io.github.lystrosaurus.admin.system.role.infrastructure.persistence.po.RoleOrgPO;
import io.github.lystrosaurus.admin.system.role.infrastructure.persistence.po.RolePermissionPO;
import io.github.lystrosaurus.admin.system.role.infrastructure.persistence.po.UserRolePO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Repository;

/** 角色关联端口的 MyBatis-Plus 实现。 */
@Repository
@RequiredArgsConstructor
public class RoleAssociationPortImpl implements RoleAssociationPort {

  private final UserRoleMpMapper userRoleMapper;
  private final RolePermissionMpMapper rolePermissionMapper;
  private final RoleMenuMpMapper roleMenuMapper;
  private final RoleOrgMpMapper roleOrgMapper;
  private final SqlSessionFactory sqlSessionFactory;

  @Override
  public List<Long> findRoleIdsByUserId(Long userId) {
    return userRoleMapper
        .selectList(new LambdaQueryWrapper<UserRolePO>().eq(UserRolePO::getUserId, userId))
        .stream()
        .map(UserRolePO::getRoleId)
        .toList();
  }

  @Override
  public List<Long> findPermissionIdsByRoleId(Long roleId) {
    return rolePermissionMapper
        .selectList(new LambdaQueryWrapper<RolePermissionPO>().eq(RolePermissionPO::getRoleId, roleId))
        .stream()
        .map(RolePermissionPO::getPermissionId)
        .toList();
  }

  @Override
  public List<Long> findPermissionIdsByUserId(Long userId) {
    List<Long> roleIds = findRoleIdsByUserId(userId);
    if (roleIds.isEmpty()) {
      return List.of();
    }
    return rolePermissionMapper
        .selectList(
            new LambdaQueryWrapper<RolePermissionPO>().in(RolePermissionPO::getRoleId, roleIds))
        .stream()
        .map(RolePermissionPO::getPermissionId)
        .distinct()
        .toList();
  }

  @Override
  public List<Long> findMenuIdsByRoleId(Long roleId) {
    return roleMenuMapper
        .selectList(new LambdaQueryWrapper<RoleMenuPO>().eq(RoleMenuPO::getRoleId, roleId))
        .stream()
        .map(RoleMenuPO::getMenuId)
        .toList();
  }

  @Override
  public List<Long> findMenuIdsByUserId(Long userId) {
    List<Long> roleIds = findRoleIdsByUserId(userId);
    if (roleIds.isEmpty()) {
      return List.of();
    }
    return roleMenuMapper
        .selectList(new LambdaQueryWrapper<RoleMenuPO>().in(RoleMenuPO::getRoleId, roleIds))
        .stream()
        .map(RoleMenuPO::getMenuId)
        .distinct()
        .toList();
  }

  @Override
  public List<Long> findOrgIdsByRoleId(Long roleId) {
    return roleOrgMapper.findByRoleId(roleId).stream().map(RoleOrgPO::getOrgId).toList();
  }

  @Override
  public void assignRoles(Long userId, List<Long> roleIds) {
    if (roleIds == null || roleIds.isEmpty()) {
      return;
    }
    removeRoles(userId);
    try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
      UserRoleMpMapper batchMapper = sqlSession.getMapper(UserRoleMpMapper.class);
      for (Long roleId : roleIds) {
        UserRolePO po = new UserRolePO();
        po.setUserId(userId);
        po.setRoleId(roleId);
        batchMapper.insert(po);
      }
      sqlSession.commit();
    }
  }

  @Override
  public void removeRoles(Long userId) {
    userRoleMapper.delete(new LambdaQueryWrapper<UserRolePO>().eq(UserRolePO::getUserId, userId));
  }
}
