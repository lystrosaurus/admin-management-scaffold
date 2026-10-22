package io.github.lystrosaurus.admin.organization.orgunit.domain.model;

import io.github.lystrosaurus.admin.shared.domain.primitive.Identifier;

/**
 * 组织单元标识。
 *
 * @param value 标识值
 */
public record OrgUnitId(Long value) implements Identifier {

  /** 按值创建组织单元标识 */
  public static OrgUnitId of(Long value) {
    return new OrgUnitId(value);
  }
}
