package io.github.lystrosaurus.admin.system.permission.infrastructure.query;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.lystrosaurus.admin.system.permission.domain.acl.PermissionQueryRepository;
import io.github.lystrosaurus.admin.system.permission.domain.model.Permission;
import io.github.lystrosaurus.admin.system.permission.infrastructure.converter.PermissionPoConverter;
import io.github.lystrosaurus.admin.system.permission.infrastructure.persistence.mapper.PermissionMpMapper;
import io.github.lystrosaurus.admin.system.permission.infrastructure.persistence.po.PermissionPO;
import io.github.lystrosaurus.admin.system.role.domain.acl.RoleAssociationPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/** 权限查询端口的 MyBatis-Plus 实现（读模型侧）。 */
@Repository
@RequiredArgsConstructor
public class PermissionQueryRepositoryImpl implements PermissionQueryRepository {

  private final PermissionMpMapper permissionMpMapper;
  private final RoleAssociationPort roleAssociationPort;
  private final PermissionPoConverter converter;

  @Override
  public List<Permission> findAll() {
    return permissionMpMapper
        .selectList(new LambdaQueryWrapper<PermissionPO>().orderByAsc(PermissionPO::getSortOrder))
        .stream()
        .map(converter::toDomain)
        .toList();
  }

  @Override
  public List<Permission> findByRoleId(Long roleId) {
    List<Long> permissionIds = roleAssociationPort.findPermissionIdsByRoleId(roleId);
    return findByIds(permissionIds);
  }

  @Override
  public List<Permission> findByUserId(Long userId) {
    List<Long> permissionIds = roleAssociationPort.findPermissionIdsByUserId(userId);
    return findByIds(permissionIds);
  }

  private List<Permission> findByIds(List<Long> permissionIds) {
    if (permissionIds.isEmpty()) {
      return List.of();
    }
    return permissionMpMapper.selectBatchIds(permissionIds).stream()
        .map(converter::toDomain)
        .toList();
  }
}
