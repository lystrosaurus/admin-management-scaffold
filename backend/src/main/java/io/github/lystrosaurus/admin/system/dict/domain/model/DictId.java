package io.github.lystrosaurus.admin.system.dict.domain.model;

import io.github.lystrosaurus.admin.shared.domain.primitive.Identifier;

/**
 * 字典标识。
 *
 * @param value 标识值
 */
public record DictId(Long value) implements Identifier {

  /** 按值创建字典标识 */
  public static DictId of(Long value) {
    return new DictId(value);
  }
}
