package io.github.lystrosaurus.admin.integration.principal.domain.acl;

import io.github.lystrosaurus.admin.integration.principal.domain.model.ExtPrincipal;
import io.github.lystrosaurus.admin.integration.principal.domain.model.ExtPrincipalId;
import io.github.lystrosaurus.admin.shared.domain.acl.GenericRepository;
import java.util.List;

/** 外部主体仓储端口：外部主体聚合的存取契约。 */
public interface ExtPrincipalRepository extends GenericRepository<ExtPrincipal, ExtPrincipalId> {

  /** 检查(sourceId, principalType, externalKey)唯一性 */
  boolean existsBySourceAndExternalKey(Long sourceId, String principalType, String externalKey);

  /** 按条件查询外部主体列表（含标识符） */
  List<ExtPrincipal> findByCondition(Long sourceId, String linkStatus);
}
