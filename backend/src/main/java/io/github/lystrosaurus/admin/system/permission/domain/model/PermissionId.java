package io.github.lystrosaurus.admin.system.permission.domain.model;

import io.github.lystrosaurus.admin.shared.domain.primitive.Identifier;

/**
 * 权限标识。
 *
 * @param value 标识值
 */
public record PermissionId(Long value) implements Identifier {

  /** 按值创建权限标识 */
  public static PermissionId of(Long value) {
    return new PermissionId(value);
  }
}
