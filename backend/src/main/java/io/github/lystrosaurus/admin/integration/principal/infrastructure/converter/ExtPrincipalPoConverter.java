package io.github.lystrosaurus.admin.integration.principal.infrastructure.converter;

import io.github.lystrosaurus.admin.integration.principal.domain.model.ExtPrincipal;
import io.github.lystrosaurus.admin.integration.principal.domain.model.ExtPrincipalId;
import io.github.lystrosaurus.admin.integration.principal.domain.model.ExtPrincipalIdentifier;
import io.github.lystrosaurus.admin.integration.principal.infrastructure.persistence.po.ExtPrincipalIdentifierPO;
import io.github.lystrosaurus.admin.integration.principal.infrastructure.persistence.po.ExtPrincipalPO;
import java.util.List;
import org.springframework.stereotype.Component;

/** 外部主体聚合 ↔ 持久化对象转换器。审计字段由 MyBatis-Plus 自动填充，不在此转换。 */
@Component
public class ExtPrincipalPoConverter {

  /** 领域对象转持久化对象（标识符单独持久化） */
  public ExtPrincipalPO toPo(ExtPrincipal principal) {
    ExtPrincipalPO po = new ExtPrincipalPO();
    po.setId(principal.id() == null ? null : principal.id().value());
    po.setSourceId(principal.sourceId());
    po.setPrincipalType(principal.principalType());
    po.setExternalKey(principal.externalKey());
    po.setDisplayName(principal.displayName());
    po.setStatus(principal.status());
    po.setRawPayloadJson(principal.rawPayloadJson());
    po.setLastSyncAt(principal.lastSyncAt());
    po.setCanonicalType(principal.canonicalType());
    po.setCanonicalId(principal.canonicalId());
    po.setLinkStatus(principal.linkStatus());
    return po;
  }

  /** 持久化对象转领域对象（重建聚合，不登记事件） */
  public ExtPrincipal toDomain(ExtPrincipalPO po, List<ExtPrincipalIdentifier> identifiers) {
    return ExtPrincipal.restore(
        ExtPrincipalId.of(po.getId()),
        po.getSourceId(),
        po.getPrincipalType(),
        po.getExternalKey(),
        po.getDisplayName(),
        po.getStatus(),
        po.getRawPayloadJson(),
        po.getLastSyncAt(),
        po.getCanonicalType(),
        po.getCanonicalId(),
        po.getLinkStatus(),
        po.getCreatedAt(),
        po.getUpdatedAt(),
        identifiers);
  }

  /** 标识符领域对象转持久化对象 */
  public ExtPrincipalIdentifierPO toIdentifierPo(Long principalId, ExtPrincipalIdentifier identifier) {
    ExtPrincipalIdentifierPO po = new ExtPrincipalIdentifierPO();
    po.setId(identifier.id());
    po.setPrincipalId(principalId);
    po.setIdType(identifier.idType());
    po.setIdValue(identifier.idValue());
    po.setIsPrimary(identifier.primary() ? 1 : 0);
    return po;
  }

  /** 标识符持久化对象转领域对象 */
  public ExtPrincipalIdentifier toIdentifierDomain(ExtPrincipalIdentifierPO po) {
    return new ExtPrincipalIdentifier(
        po.getId(),
        po.getIdType(),
        po.getIdValue(),
        Integer.valueOf(1).equals(po.getIsPrimary()));
  }
}
