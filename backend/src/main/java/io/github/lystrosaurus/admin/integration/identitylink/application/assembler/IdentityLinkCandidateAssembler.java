package io.github.lystrosaurus.admin.integration.identitylink.application.assembler;

import io.github.lystrosaurus.admin.integration.identitylink.adapter.response.IdentityLinkCandidateResponse;
import io.github.lystrosaurus.admin.integration.identitylink.domain.model.IdentityLinkCandidate;
import org.springframework.stereotype.Component;

/** 身份匹配候选装配器：聚合 → 响应模型。 */
@Component
public class IdentityLinkCandidateAssembler {

  /** 聚合转响应 */
  public IdentityLinkCandidateResponse toResponse(IdentityLinkCandidate candidate) {
    return new IdentityLinkCandidateResponse(
        candidate.id().value(),
        candidate.sourcePrincipalId(),
        candidate.candidateType(),
        candidate.candidateId(),
        candidate.score(),
        candidate.reason(),
        candidate.status(),
        candidate.createdAt(),
        candidate.handledBy(),
        candidate.handledAt());
  }
}
