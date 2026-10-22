package io.github.lystrosaurus.admin.system.user.domain.model;

import io.github.lystrosaurus.admin.shared.domain.primitive.Identifier;

/**
 * 用户标识。
 *
 * @param value 标识值
 */
public record UserId(Long value) implements Identifier {

  /** 按值创建用户标识 */
  public static UserId of(Long value) {
    return new UserId(value);
  }
}
