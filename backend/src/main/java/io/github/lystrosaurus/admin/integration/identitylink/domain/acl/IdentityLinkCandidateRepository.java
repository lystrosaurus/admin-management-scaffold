package io.github.lystrosaurus.admin.integration.identitylink.domain.acl;

import io.github.lystrosaurus.admin.integration.identitylink.domain.model.IdentityLinkCandidate;
import io.github.lystrosaurus.admin.integration.identitylink.domain.model.IdentityLinkCandidateId;
import io.github.lystrosaurus.admin.shared.domain.acl.GenericRepository;
import java.util.List;

/** 身份匹配候选仓储端口：候选聚合的存取契约。 */
public interface IdentityLinkCandidateRepository
    extends GenericRepository<IdentityLinkCandidate, IdentityLinkCandidateId> {

  /** 查询待处理候选列表 */
  List<IdentityLinkCandidate> findPending();

  /** 按外部主体ID查询候选列表 */
  List<IdentityLinkCandidate> findByPrincipalId(Long principalId);
}
