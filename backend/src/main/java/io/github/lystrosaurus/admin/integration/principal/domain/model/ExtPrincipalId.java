package io.github.lystrosaurus.admin.integration.principal.domain.model;

import io.github.lystrosaurus.admin.shared.domain.primitive.Identifier;

/**
 * 外部主体标识。
 *
 * @param value 标识值
 */
public record ExtPrincipalId(Long value) implements Identifier {

  /** 按值创建外部主体标识 */
  public static ExtPrincipalId of(Long value) {
    return new ExtPrincipalId(value);
  }
}
