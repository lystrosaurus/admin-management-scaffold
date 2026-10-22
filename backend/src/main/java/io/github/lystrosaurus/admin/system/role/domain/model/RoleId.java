package io.github.lystrosaurus.admin.system.role.domain.model;

import io.github.lystrosaurus.admin.shared.domain.primitive.Identifier;

/**
 * 角色标识。
 *
 * @param value 标识值
 */
public record RoleId(Long value) implements Identifier {

  /** 按值创建角色标识 */
  public static RoleId of(Long value) {
    return new RoleId(value);
  }
}
