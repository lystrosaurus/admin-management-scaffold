package io.github.lystrosaurus.admin.integration.principal.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.lystrosaurus.admin.integration.principal.domain.acl.ExtPrincipalRepository;
import io.github.lystrosaurus.admin.integration.principal.domain.model.ExtPrincipal;
import io.github.lystrosaurus.admin.integration.principal.domain.model.ExtPrincipalId;
import io.github.lystrosaurus.admin.integration.principal.domain.model.ExtPrincipalIdentifier;
import io.github.lystrosaurus.admin.integration.principal.infrastructure.converter.ExtPrincipalPoConverter;
import io.github.lystrosaurus.admin.integration.principal.infrastructure.persistence.mapper.ExtPrincipalIdentifierMpMapper;
import io.github.lystrosaurus.admin.integration.principal.infrastructure.persistence.mapper.ExtPrincipalMpMapper;
import io.github.lystrosaurus.admin.integration.principal.infrastructure.persistence.po.ExtPrincipalIdentifierPO;
import io.github.lystrosaurus.admin.integration.principal.infrastructure.persistence.po.ExtPrincipalPO;
import io.github.lystrosaurus.admin.shared.domain.acl.DomainEventPublisher;
import io.github.lystrosaurus.admin.shared.infrastructure.persistence.GenericRepositoryImpl;
import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

/** 外部主体仓储的 MyBatis-Plus 实现（含标识符联动）。 */
@Repository
public class ExtPrincipalRepositoryImpl
    extends GenericRepositoryImpl<ExtPrincipal, ExtPrincipalId, ExtPrincipalPO, ExtPrincipalMpMapper>
    implements ExtPrincipalRepository {

  private final ExtPrincipalIdentifierMpMapper identifierMapper;
  private final ExtPrincipalPoConverter converter;

  public ExtPrincipalRepositoryImpl(
      ExtPrincipalMpMapper mapper,
      DomainEventPublisher eventPublisher,
      ExtPrincipalIdentifierMpMapper identifierMapper,
      ExtPrincipalPoConverter converter) {
    super(mapper, eventPublisher);
    this.identifierMapper = identifierMapper;
    this.converter = converter;
  }

  @Override
  protected ExtPrincipalPO toPo(ExtPrincipal principal) {
    return converter.toPo(principal);
  }

  @Override
  protected ExtPrincipal toDomain(ExtPrincipalPO po) {
    return converter.toDomain(po, findIdentifiers(po.getId()));
  }

  @Override
  public ExtPrincipal save(ExtPrincipal principal) {
    super.save(principal);

    // 新建标识符：已存在的标识符不做覆盖，保持与旧实现一致（更新主体不改标识符）
    for (ExtPrincipalIdentifier identifier : principal.identifiers()) {
      if (identifierMapper.selectById(identifier.id()) == null) {
        identifierMapper.insert(converter.toIdentifierPo(principal.id().value(), identifier));
      }
    }
    return principal;
  }

  @Override
  public void deleteById(ExtPrincipalId id) {
    identifierMapper.delete(
        new LambdaQueryWrapper<ExtPrincipalIdentifierPO>()
            .eq(ExtPrincipalIdentifierPO::getPrincipalId, id.value()));
    super.deleteById(id);
  }

  @Override
  public boolean existsBySourceAndExternalKey(
      Long sourceId, String principalType, String externalKey) {
    return mapper.selectCount(
            new LambdaQueryWrapper<ExtPrincipalPO>()
                .eq(ExtPrincipalPO::getSourceId, sourceId)
                .eq(ExtPrincipalPO::getPrincipalType, principalType)
                .eq(ExtPrincipalPO::getExternalKey, externalKey))
        > 0;
  }

  @Override
  public List<ExtPrincipal> findByCondition(Long sourceId, String linkStatus) {
    LambdaQueryWrapper<ExtPrincipalPO> wrapper = new LambdaQueryWrapper<>();
    if (sourceId != null) {
      wrapper.eq(ExtPrincipalPO::getSourceId, sourceId);
    }
    if (StringUtils.hasText(linkStatus)) {
      wrapper.eq(ExtPrincipalPO::getLinkStatus, linkStatus);
    }
    wrapper.orderByDesc(ExtPrincipalPO::getCreatedAt);
    return mapper.selectList(wrapper).stream().map(this::toDomain).toList();
  }

  private List<ExtPrincipalIdentifier> findIdentifiers(Long principalId) {
    return identifierMapper
        .selectList(
            new LambdaQueryWrapper<ExtPrincipalIdentifierPO>()
                .eq(ExtPrincipalIdentifierPO::getPrincipalId, principalId))
        .stream()
        .map(converter::toIdentifierDomain)
        .toList();
  }
}
