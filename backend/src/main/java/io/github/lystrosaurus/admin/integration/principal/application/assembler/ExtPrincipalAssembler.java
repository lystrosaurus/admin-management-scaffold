package io.github.lystrosaurus.admin.integration.principal.application.assembler;

import io.github.lystrosaurus.admin.integration.principal.adapter.response.ExtPrincipalIdentifierResponse;
import io.github.lystrosaurus.admin.integration.principal.adapter.response.ExtPrincipalResponse;
import io.github.lystrosaurus.admin.integration.principal.domain.model.ExtPrincipal;
import java.util.List;
import org.springframework.stereotype.Component;

/** 外部主体装配器：聚合 → 响应模型。 */
@Component
public class ExtPrincipalAssembler {

  /** 聚合与身份源名称转响应 */
  public ExtPrincipalResponse toResponse(ExtPrincipal principal, String sourceName) {
    List<ExtPrincipalIdentifierResponse> identifiers =
        principal.identifiers().stream()
            .map(
                identifier ->
                    new ExtPrincipalIdentifierResponse(
                        identifier.id(),
                        identifier.idType(),
                        identifier.idValue(),
                        identifier.primary()))
            .toList();
    return new ExtPrincipalResponse(
        principal.id().value(),
        principal.sourceId(),
        sourceName,
        principal.principalType(),
        principal.externalKey(),
        principal.displayName(),
        principal.status(),
        principal.lastSyncAt(),
        principal.canonicalType(),
        principal.canonicalId(),
        principal.linkStatus(),
        principal.createdAt(),
        principal.updatedAt(),
        identifiers);
  }
}
