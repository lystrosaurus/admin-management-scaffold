package io.github.lystrosaurus.admin.organization.orgunit.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.lystrosaurus.admin.organization.orgunit.domain.acl.OrgUnitRepository;
import io.github.lystrosaurus.admin.organization.orgunit.domain.model.OrgUnit;
import io.github.lystrosaurus.admin.organization.orgunit.domain.model.OrgUnitId;
import io.github.lystrosaurus.admin.organization.orgunit.infrastructure.converter.OrgUnitPoConverter;
import io.github.lystrosaurus.admin.organization.orgunit.infrastructure.persistence.mapper.OrgUnitMpMapper;
import io.github.lystrosaurus.admin.organization.orgunit.infrastructure.persistence.po.OrgUnitPO;
import io.github.lystrosaurus.admin.shared.domain.acl.DomainEventPublisher;
import io.github.lystrosaurus.admin.shared.infrastructure.persistence.GenericRepositoryImpl;
import org.springframework.stereotype.Repository;

/** 组织单元仓储的 MyBatis-Plus 实现。 */
@Repository
public class OrgUnitRepositoryImpl
    extends GenericRepositoryImpl<OrgUnit, OrgUnitId, OrgUnitPO, OrgUnitMpMapper>
    implements OrgUnitRepository {

  private final OrgUnitPoConverter converter;

  public OrgUnitRepositoryImpl(
      OrgUnitMpMapper mapper, DomainEventPublisher eventPublisher, OrgUnitPoConverter converter) {
    super(mapper, eventPublisher);
    this.converter = converter;
  }

  @Override
  protected OrgUnitPO toPo(OrgUnit orgUnit) {
    return converter.toPo(orgUnit);
  }

  @Override
  protected OrgUnit toDomain(OrgUnitPO po) {
    return converter.toDomain(po);
  }

  @Override
  public boolean existsByCode(String code) {
    return mapper.selectCount(new LambdaQueryWrapper<OrgUnitPO>().eq(OrgUnitPO::getCode, code)) > 0;
  }

  @Override
  public boolean existsByCodeAndIdNot(String code, OrgUnitId id) {
    return mapper.selectCount(
            new LambdaQueryWrapper<OrgUnitPO>()
                .eq(OrgUnitPO::getCode, code)
                .ne(OrgUnitPO::getId, id.value()))
        > 0;
  }

  @Override
  public boolean hasChildren(Long id) {
    return mapper.selectCount(
            new LambdaQueryWrapper<OrgUnitPO>().eq(OrgUnitPO::getParentId, id))
        > 0;
  }
}
