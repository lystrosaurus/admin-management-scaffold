package io.github.lystrosaurus.admin.shared.domain.acl;

import io.github.lystrosaurus.admin.shared.domain.primitive.AggregateRoot;
import io.github.lystrosaurus.admin.shared.domain.primitive.Identifier;
import java.util.Optional;

/**
 * 通用仓储端口：聚合的存取契约，定义在领域层，实现在基础设施层。
 *
 * @param <T> 聚合根类型
 * @param <ID> 标识类型
 */
public interface GenericRepository<T extends AggregateRoot<ID>, ID extends Identifier> {

  /** 按标识查找聚合 */
  Optional<T> findById(ID id);

  /** 保存聚合（新建或更新），保存后派发聚合登记的领域事件 */
  T save(T aggregate);

  /** 按标识删除聚合（逻辑删除） */
  void deleteById(ID id);

  /** 按标识判断聚合是否存在 */
  boolean existsById(ID id);
}
