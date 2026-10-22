package io.github.lystrosaurus.admin.system.menu.domain.model;

import io.github.lystrosaurus.admin.shared.domain.primitive.Identifier;

/**
 * 菜单标识。
 *
 * @param value 标识值
 */
public record MenuId(Long value) implements Identifier {

  /** 按值创建菜单标识 */
  public static MenuId of(Long value) {
    return new MenuId(value);
  }
}
