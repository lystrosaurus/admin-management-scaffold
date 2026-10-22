package io.github.lystrosaurus.admin.shared.domain.primitive;

import java.util.Objects;

/**
 * 实体基类：以标识判等。
 *
 * @param <ID> 标识类型
 */
public abstract class Entity<ID extends Identifier> {

  protected final ID id;

  protected Entity(ID id) {
    this.id = Objects.requireNonNull(id, "id 不能为 null");
  }

  /** 实体标识 */
  public ID id() {
    return id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Entity<?> other = (Entity<?>) o;
    return Objects.equals(id, other.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
