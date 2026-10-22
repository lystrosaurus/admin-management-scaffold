package io.github.lystrosaurus.admin.shared.domain.acl;

import io.github.lystrosaurus.admin.shared.domain.primitive.AggregateRoot;

/** 领域事件发布端口：仓储保存聚合后调用，监听方在事务提交后执行 */
public interface DomainEventPublisher {

  /** 发布聚合登记的全部事件并清空 */
  void publishAll(AggregateRoot<?> aggregateRoot);
}
