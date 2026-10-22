package io.github.lystrosaurus.admin.system.dict.infrastructure.persistence;

import io.github.lystrosaurus.admin.shared.domain.acl.DomainEventPublisher;
import io.github.lystrosaurus.admin.shared.infrastructure.persistence.GenericRepositoryImpl;
import io.github.lystrosaurus.admin.system.dict.domain.acl.DictRepository;
import io.github.lystrosaurus.admin.system.dict.domain.model.Dict;
import io.github.lystrosaurus.admin.system.dict.domain.model.DictId;
import io.github.lystrosaurus.admin.system.dict.infrastructure.converter.DictPoConverter;
import io.github.lystrosaurus.admin.system.dict.infrastructure.persistence.mapper.DictMpMapper;
import io.github.lystrosaurus.admin.system.dict.infrastructure.persistence.po.DictPO;
import org.springframework.stereotype.Repository;

/** 字典仓储的 MyBatis-Plus 实现。 */
@Repository
public class DictRepositoryImpl extends GenericRepositoryImpl<Dict, DictId, DictPO, DictMpMapper>
    implements DictRepository {

  private final DictPoConverter converter;

  public DictRepositoryImpl(
      DictMpMapper mapper, DomainEventPublisher eventPublisher, DictPoConverter converter) {
    super(mapper, eventPublisher);
    this.converter = converter;
  }

  @Override
  protected DictPO toPo(Dict domain) {
    return converter.toPo(domain);
  }

  @Override
  protected Dict toDomain(DictPO po) {
    return converter.toDomain(po);
  }
}
