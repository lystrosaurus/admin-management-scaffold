package io.github.lystrosaurus.admin.integration.identitylink.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.lystrosaurus.admin.integration.identitylink.domain.acl.IdentityLinkCandidateRepository;
import io.github.lystrosaurus.admin.integration.identitylink.domain.model.IdentityLinkCandidate;
import io.github.lystrosaurus.admin.integration.identitylink.domain.model.IdentityLinkCandidateId;
import io.github.lystrosaurus.admin.integration.identitylink.infrastructure.converter.IdentityLinkCandidatePoConverter;
import io.github.lystrosaurus.admin.integration.identitylink.infrastructure.persistence.mapper.IdentityLinkCandidateMpMapper;
import io.github.lystrosaurus.admin.integration.identitylink.infrastructure.persistence.po.IdentityLinkCandidatePO;
import io.github.lystrosaurus.admin.shared.domain.acl.DomainEventPublisher;
import io.github.lystrosaurus.admin.shared.infrastructure.persistence.GenericRepositoryImpl;
import java.util.List;
import org.springframework.stereotype.Repository;

/** 身份匹配候选仓储的 MyBatis-Plus 实现。 */
@Repository
public class IdentityLinkCandidateRepositoryImpl
    extends GenericRepositoryImpl<
        IdentityLinkCandidate, IdentityLinkCandidateId, IdentityLinkCandidatePO,
        IdentityLinkCandidateMpMapper>
    implements IdentityLinkCandidateRepository {

  private final IdentityLinkCandidatePoConverter converter;

  public IdentityLinkCandidateRepositoryImpl(
      IdentityLinkCandidateMpMapper mapper,
      DomainEventPublisher eventPublisher,
      IdentityLinkCandidatePoConverter converter) {
    super(mapper, eventPublisher);
    this.converter = converter;
  }

  @Override
  protected IdentityLinkCandidatePO toPo(IdentityLinkCandidate candidate) {
    return converter.toPo(candidate);
  }

  @Override
  protected IdentityLinkCandidate toDomain(IdentityLinkCandidatePO po) {
    return converter.toDomain(po);
  }

  @Override
  public List<IdentityLinkCandidate> findPending() {
    return mapper
        .selectList(
            new LambdaQueryWrapper<IdentityLinkCandidatePO>()
                .eq(IdentityLinkCandidatePO::getStatus, "PENDING")
                .orderByDesc(IdentityLinkCandidatePO::getCreatedAt))
        .stream()
        .map(converter::toDomain)
        .toList();
  }

  @Override
  public List<IdentityLinkCandidate> findByPrincipalId(Long principalId) {
    return mapper
        .selectList(
            new LambdaQueryWrapper<IdentityLinkCandidatePO>()
                .eq(IdentityLinkCandidatePO::getSourcePrincipalId, principalId)
                .orderByDesc(IdentityLinkCandidatePO::getCreatedAt))
        .stream()
        .map(converter::toDomain)
        .toList();
  }
}
