package io.github.lystrosaurus.admin.system.permission.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.lystrosaurus.admin.shared.domain.acl.DomainEventPublisher;
import io.github.lystrosaurus.admin.shared.infrastructure.persistence.GenericRepositoryImpl;
import io.github.lystrosaurus.admin.system.permission.domain.acl.PermissionRepository;
import io.github.lystrosaurus.admin.system.permission.domain.model.Permission;
import io.github.lystrosaurus.admin.system.permission.domain.model.PermissionId;
import io.github.lystrosaurus.admin.system.permission.infrastructure.converter.PermissionPoConverter;
import io.github.lystrosaurus.admin.system.permission.infrastructure.persistence.mapper.PermissionMpMapper;
import io.github.lystrosaurus.admin.system.permission.infrastructure.persistence.po.PermissionPO;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/** 权限仓储的 MyBatis-Plus 实现。 */
@Repository
public class PermissionRepositoryImpl
    extends GenericRepositoryImpl<Permission, PermissionId, PermissionPO, PermissionMpMapper>
    implements PermissionRepository {

  private final PermissionPoConverter converter;

  public PermissionRepositoryImpl(
      PermissionMpMapper mapper,
      DomainEventPublisher eventPublisher,
      PermissionPoConverter converter) {
    super(mapper, eventPublisher);
    this.converter = converter;
  }

  @Override
  protected PermissionPO toPo(Permission domain) {
    return converter.toPo(domain);
  }

  @Override
  protected Permission toDomain(PermissionPO po) {
    return converter.toDomain(po);
  }

  @Override
  public Optional<Permission> findByCode(String code) {
    return Optional.ofNullable(
            mapper.selectOne(new LambdaQueryWrapper<PermissionPO>().eq(PermissionPO::getCode, code)))
        .map(this::toDomain);
  }

  @Override
  public boolean existsByCode(String code) {
    return mapper.selectCount(new LambdaQueryWrapper<PermissionPO>().eq(PermissionPO::getCode, code))
        > 0;
  }

  @Override
  public boolean existsByCodeAndIdNot(String code, PermissionId id) {
    return mapper.selectCount(
            new LambdaQueryWrapper<PermissionPO>()
                .eq(PermissionPO::getCode, code)
                .ne(PermissionPO::getId, id.value()))
        > 0;
  }
}
