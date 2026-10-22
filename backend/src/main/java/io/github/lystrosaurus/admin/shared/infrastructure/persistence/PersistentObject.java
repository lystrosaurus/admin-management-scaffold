package io.github.lystrosaurus.admin.shared.infrastructure.persistence;

/** 持久化对象最小契约：仅要求可读写主键，使通用仓储不强制依赖审计列与乐观锁列。 */
public interface PersistentObject {

  /** 主键 ID */
  Long getId();

  /** 设置主键 ID */
  void setId(Long id);
}
