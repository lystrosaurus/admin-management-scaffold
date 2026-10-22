package io.github.lystrosaurus.admin.system.configitem.domain.model;

import io.github.lystrosaurus.admin.shared.domain.primitive.Identifier;

/**
 * 配置项标识。
 *
 * @param value 标识值
 */
public record ConfigItemId(Long value) implements Identifier {

  /** 按值创建配置项标识 */
  public static ConfigItemId of(Long value) {
    return new ConfigItemId(value);
  }
}
