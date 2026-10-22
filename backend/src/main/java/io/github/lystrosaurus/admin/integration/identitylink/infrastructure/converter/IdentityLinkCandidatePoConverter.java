package io.github.lystrosaurus.admin.integration.identitylink.infrastructure.converter;

import io.github.lystrosaurus.admin.integration.identitylink.domain.model.IdentityLinkCandidate;
import io.github.lystrosaurus.admin.integration.identitylink.domain.model.IdentityLinkCandidateId;
import io.github.lystrosaurus.admin.integration.identitylink.infrastructure.persistence.po.IdentityLinkCandidatePO;
import org.springframework.stereotype.Component;

/** 身份匹配候选聚合 ↔ 持久化对象转换器。审计字段由 MyBatis-Plus 自动填充，不在此转换。 */
@Component
public class IdentityLinkCandidatePoConverter {

  /** 领域对象转持久化对象 */
  public IdentityLinkCandidatePO toPo(IdentityLinkCandidate candidate) {
    IdentityLinkCandidatePO po = new IdentityLinkCandidatePO();
    po.setId(candidate.id() == null ? null : candidate.id().value());
    po.setSourcePrincipalId(candidate.sourcePrincipalId());
    po.setCandidateType(candidate.candidateType());
    po.setCandidateId(candidate.candidateId());
    po.setScore(candidate.score());
    po.setReason(candidate.reason());
    po.setStatus(candidate.status());
    po.setHandledBy(candidate.handledBy());
    po.setHandledAt(candidate.handledAt());
    return po;
  }

  /** 持久化对象转领域对象（重建聚合，不登记事件） */
  public IdentityLinkCandidate toDomain(IdentityLinkCandidatePO po) {
    return IdentityLinkCandidate.restore(
        IdentityLinkCandidateId.of(po.getId()),
        po.getSourcePrincipalId(),
        po.getCandidateType(),
        po.getCandidateId(),
        po.getScore(),
        po.getReason(),
        po.getStatus(),
        po.getCreatedAt(),
        po.getHandledBy(),
        po.getHandledAt());
  }
}
