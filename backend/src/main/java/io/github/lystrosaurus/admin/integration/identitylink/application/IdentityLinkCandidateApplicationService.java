package io.github.lystrosaurus.admin.integration.identitylink.application;

import io.github.lystrosaurus.admin.integration.identitylink.adapter.response.IdentityLinkCandidateResponse;
import io.github.lystrosaurus.admin.integration.identitylink.application.assembler.IdentityLinkCandidateAssembler;
import io.github.lystrosaurus.admin.integration.identitylink.application.command.IdentityLinkCandidateCreateCommand;
import io.github.lystrosaurus.admin.integration.identitylink.domain.acl.IdentityLinkCandidateRepository;
import io.github.lystrosaurus.admin.integration.identitylink.domain.model.IdentityLinkCandidate;
import io.github.lystrosaurus.admin.integration.identitylink.domain.model.IdentityLinkCandidateErrorCode;
import io.github.lystrosaurus.admin.integration.identitylink.domain.model.IdentityLinkCandidateId;
import io.github.lystrosaurus.admin.integration.principal.domain.acl.PrincipalLinkPort;
import io.github.lystrosaurus.admin.shared.domain.acl.IdGenerator;
import io.github.lystrosaurus.admin.shared.domain.exception.DomainException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 身份匹配候选应用服务：编排候选的创建、确认、拒绝与查询。 */
@Service
@RequiredArgsConstructor
public class IdentityLinkCandidateApplicationService {

  private final IdentityLinkCandidateRepository candidateRepository;
  private final PrincipalLinkPort principalLinkPort;
  private final IdGenerator idGenerator;
  private final IdentityLinkCandidateAssembler assembler;

  /** 创建身份匹配候选 */
  @Transactional(rollbackFor = Exception.class)
  public IdentityLinkCandidateResponse create(IdentityLinkCandidateCreateCommand command) {
    IdentityLinkCandidate candidate =
        IdentityLinkCandidate.create(
            IdentityLinkCandidateId.of(idGenerator.nextId()),
            command.sourcePrincipalId(),
            command.candidateType(),
            command.candidateId(),
            command.score(),
            command.reason());
    candidateRepository.save(candidate);
    return assembler.toResponse(
        candidateRepository.findById(candidate.id()).orElse(candidate));
  }

  /** 确认候选：更新候选状态并把外部主体标记为人工关联 */
  @Transactional(rollbackFor = Exception.class)
  public void confirm(Long id, String handledBy) {
    IdentityLinkCandidate candidate = findOrThrow(id);
    candidate.confirm(handledBy);
    candidateRepository.save(candidate);

    principalLinkPort.markManualLinked(
        candidate.sourcePrincipalId(), candidate.candidateType(), candidate.candidateId());
  }

  /** 拒绝候选 */
  @Transactional(rollbackFor = Exception.class)
  public void reject(Long id, String handledBy) {
    IdentityLinkCandidate candidate = findOrThrow(id);
    candidate.reject(handledBy);
    candidateRepository.save(candidate);
  }

  /** 查询待处理候选列表 */
  public List<IdentityLinkCandidateResponse> listPending() {
    return candidateRepository.findPending().stream().map(assembler::toResponse).toList();
  }

  /** 按外部主体ID查询候选列表 */
  public List<IdentityLinkCandidateResponse> listByPrincipalId(Long principalId) {
    return candidateRepository.findByPrincipalId(principalId).stream()
        .map(assembler::toResponse)
        .toList();
  }

  private IdentityLinkCandidate findOrThrow(Long id) {
    return candidateRepository
        .findById(IdentityLinkCandidateId.of(id))
        .orElseThrow(
            () -> new DomainException(IdentityLinkCandidateErrorCode.CANDIDATE_NOT_FOUND));
  }
}
