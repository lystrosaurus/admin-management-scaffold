package io.github.lystrosaurus.admin.integration.source.domain.model;

import io.github.lystrosaurus.admin.shared.domain.primitive.Identifier;

/**
 * 外部身份源标识。
 *
 * @param value 标识值
 */
public record ExtSourceId(Long value) implements Identifier {

  /** 按值创建外部身份源标识 */
  public static ExtSourceId of(Long value) {
    return new ExtSourceId(value);
  }
}
