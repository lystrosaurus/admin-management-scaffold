package io.github.lystrosaurus.admin.integration.identitylink.domain.model;

import io.github.lystrosaurus.admin.shared.domain.primitive.Identifier;

/**
 * 身份匹配候选标识。
 *
 * @param value 标识值
 */
public record IdentityLinkCandidateId(Long value) implements Identifier {

  /** 按值创建候选标识 */
  public static IdentityLinkCandidateId of(Long value) {
    return new IdentityLinkCandidateId(value);
  }
}
