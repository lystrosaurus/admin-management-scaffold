package io.github.lystrosaurus.admin.integration.source.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.lystrosaurus.admin.integration.source.domain.acl.ExtSourceRepository;
import io.github.lystrosaurus.admin.integration.source.domain.model.ExtSource;
import io.github.lystrosaurus.admin.integration.source.domain.model.ExtSourceId;
import io.github.lystrosaurus.admin.integration.source.infrastructure.converter.ExtSourcePoConverter;
import io.github.lystrosaurus.admin.integration.source.infrastructure.persistence.mapper.ExtSourceMpMapper;
import io.github.lystrosaurus.admin.integration.source.infrastructure.persistence.po.ExtSourcePO;
import io.github.lystrosaurus.admin.shared.domain.acl.DomainEventPublisher;
import io.github.lystrosaurus.admin.shared.infrastructure.persistence.GenericRepositoryImpl;
import java.util.List;
import org.springframework.stereotype.Repository;

/** 外部身份源仓储的 MyBatis-Plus 实现。 */
@Repository
public class ExtSourceRepositoryImpl
    extends GenericRepositoryImpl<ExtSource, ExtSourceId, ExtSourcePO, ExtSourceMpMapper>
    implements ExtSourceRepository {

  private final ExtSourcePoConverter converter;

  public ExtSourceRepositoryImpl(
      ExtSourceMpMapper mapper,
      DomainEventPublisher eventPublisher,
      ExtSourcePoConverter converter) {
    super(mapper, eventPublisher);
    this.converter = converter;
  }

  @Override
  protected ExtSourcePO toPo(ExtSource source) {
    return converter.toPo(source);
  }

  @Override
  protected ExtSource toDomain(ExtSourcePO po) {
    return converter.toDomain(po);
  }

  @Override
  public boolean existsByCode(String code) {
    return mapper.selectCount(new LambdaQueryWrapper<ExtSourcePO>().eq(ExtSourcePO::getCode, code))
        > 0;
  }

  @Override
  public List<ExtSource> findAll() {
    return mapper
        .selectList(new LambdaQueryWrapper<ExtSourcePO>().orderByDesc(ExtSourcePO::getPriority))
        .stream()
        .map(converter::toDomain)
        .toList();
  }
}
