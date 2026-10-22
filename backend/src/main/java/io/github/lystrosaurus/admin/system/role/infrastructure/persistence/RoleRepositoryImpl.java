package io.github.lystrosaurus.admin.system.role.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.lystrosaurus.admin.shared.domain.acl.DomainEventPublisher;
import io.github.lystrosaurus.admin.shared.infrastructure.persistence.GenericRepositoryImpl;
import io.github.lystrosaurus.admin.system.role.domain.acl.RoleRepository;
import io.github.lystrosaurus.admin.system.role.domain.model.Role;
import io.github.lystrosaurus.admin.system.role.domain.model.RoleId;
import io.github.lystrosaurus.admin.system.role.infrastructure.converter.RolePoConverter;
import io.github.lystrosaurus.admin.system.role.infrastructure.persistence.mapper.RoleMenuMpMapper;
import io.github.lystrosaurus.admin.system.role.infrastructure.persistence.mapper.RoleMpMapper;
import io.github.lystrosaurus.admin.system.role.infrastructure.persistence.mapper.RolePermissionMpMapper;
import io.github.lystrosaurus.admin.system.role.infrastructure.persistence.po.RoleMenuPO;
import io.github.lystrosaurus.admin.system.role.infrastructure.persistence.po.RolePO;
import io.github.lystrosaurus.admin.system.role.infrastructure.persistence.po.RolePermissionPO;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Repository;

/** 角色仓储的 MyBatis-Plus 实现。 */
@Repository
public class RoleRepositoryImpl extends GenericRepositoryImpl<Role, RoleId, RolePO, RoleMpMapper>
    implements RoleRepository {

  private final RolePoConverter converter;
  private final RolePermissionMpMapper rolePermissionMapper;
  private final RoleMenuMpMapper roleMenuMapper;
  private final SqlSessionFactory sqlSessionFactory;

  public RoleRepositoryImpl(
      RoleMpMapper mapper,
      DomainEventPublisher eventPublisher,
      RolePoConverter converter,
      RolePermissionMpMapper rolePermissionMapper,
      RoleMenuMpMapper roleMenuMapper,
      SqlSessionFactory sqlSessionFactory) {
    super(mapper, eventPublisher);
    this.converter = converter;
    this.rolePermissionMapper = rolePermissionMapper;
    this.roleMenuMapper = roleMenuMapper;
    this.sqlSessionFactory = sqlSessionFactory;
  }

  @Override
  protected RolePO toPo(Role domain) {
    return converter.toPo(domain);
  }

  @Override
  protected Role toDomain(RolePO po) {
    return converter.toDomain(po);
  }

  @Override
  public Optional<Role> findByCode(String code) {
    return Optional.ofNullable(
            mapper.selectOne(new LambdaQueryWrapper<RolePO>().eq(RolePO::getCode, code)))
        .map(this::toDomain);
  }

  @Override
  public boolean existsByCode(String code) {
    return mapper.selectCount(new LambdaQueryWrapper<RolePO>().eq(RolePO::getCode, code)) > 0;
  }

  @Override
  public boolean existsByCodeAndIdNot(String code, RoleId id) {
    return mapper.selectCount(
            new LambdaQueryWrapper<RolePO>()
                .eq(RolePO::getCode, code)
                .ne(RolePO::getId, id.value()))
        > 0;
  }

  @Override
  public void replacePermissions(RoleId roleId, List<Long> permissionIds) {
    rolePermissionMapper.delete(
        new LambdaQueryWrapper<RolePermissionPO>().eq(RolePermissionPO::getRoleId, roleId.value()));
    if (permissionIds == null || permissionIds.isEmpty()) {
      return;
    }
    try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
      RolePermissionMpMapper batchMapper = sqlSession.getMapper(RolePermissionMpMapper.class);
      for (Long permissionId : permissionIds) {
        RolePermissionPO po = new RolePermissionPO();
        po.setRoleId(roleId.value());
        po.setPermissionId(permissionId);
        batchMapper.insert(po);
      }
      sqlSession.commit();
    }
  }

  @Override
  public void replaceMenus(RoleId roleId, List<Long> menuIds) {
    roleMenuMapper.delete(
        new LambdaQueryWrapper<RoleMenuPO>().eq(RoleMenuPO::getRoleId, roleId.value()));
    if (menuIds == null || menuIds.isEmpty()) {
      return;
    }
    try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
      RoleMenuMpMapper batchMapper = sqlSession.getMapper(RoleMenuMpMapper.class);
      for (Long menuId : menuIds) {
        RoleMenuPO po = new RoleMenuPO();
        po.setRoleId(roleId.value());
        po.setMenuId(menuId);
        batchMapper.insert(po);
      }
      sqlSession.commit();
    }
  }
}
