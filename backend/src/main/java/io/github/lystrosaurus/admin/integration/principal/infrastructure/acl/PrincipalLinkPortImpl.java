package io.github.lystrosaurus.admin.integration.principal.infrastructure.acl;

import io.github.lystrosaurus.admin.integration.principal.domain.acl.ExtPrincipalRepository;
import io.github.lystrosaurus.admin.integration.principal.domain.acl.PrincipalLinkPort;
import io.github.lystrosaurus.admin.integration.principal.domain.model.ExtPrincipal;
import io.github.lystrosaurus.admin.integration.principal.domain.model.ExtPrincipalErrorCode;
import io.github.lystrosaurus.admin.integration.principal.domain.model.ExtPrincipalId;
import io.github.lystrosaurus.admin.shared.domain.exception.DomainException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** 外部主体关联端口的防腐层实现：供 identitylink 标记人工关联结果。 */
@Component
@RequiredArgsConstructor
public class PrincipalLinkPortImpl implements PrincipalLinkPort {

  private final ExtPrincipalRepository extPrincipalRepository;

  @Override
  public void markManualLinked(Long principalId, String canonicalType, Long canonicalId) {
    ExtPrincipal principal =
        extPrincipalRepository
            .findById(ExtPrincipalId.of(principalId))
            .orElseThrow(() -> new DomainException(ExtPrincipalErrorCode.PRINCIPAL_NOT_FOUND));
    principal.markManualLinked(canonicalType, canonicalId);
    extPrincipalRepository.save(principal);
  }
}
