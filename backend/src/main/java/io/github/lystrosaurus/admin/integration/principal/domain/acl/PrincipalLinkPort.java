package io.github.lystrosaurus.admin.integration.principal.domain.acl;

/** 外部主体关联端口：供 identitylink 等其它上下文标记人工关联结果。 */
public interface PrincipalLinkPort {

  /** 将外部主体标记为人工关联到指定规范目标 */
  void markManualLinked(Long principalId, String canonicalType, Long canonicalId);
}
