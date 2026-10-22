package io.github.lystrosaurus.admin.shared.infrastructure.event;

import io.github.lystrosaurus.admin.shared.domain.acl.DomainEventPublisher;
import io.github.lystrosaurus.admin.shared.domain.primitive.AggregateRoot;
import io.github.lystrosaurus.admin.shared.domain.primitive.DomainEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * 基于 Spring 的领域事件发布器。
 *
 * <p>事件在仓储保存聚合时（事务内）发布，监听方应以 {@code @TransactionalEventListener(phase = AFTER_COMMIT)}
 * 接收，确保事务回滚时不触发副作用。
 */
@Component
public class SpringDomainEventPublisher implements DomainEventPublisher {

  private final ApplicationEventPublisher delegate;

  public SpringDomainEventPublisher(ApplicationEventPublisher delegate) {
    this.delegate = delegate;
  }

  @Override
  public void publishAll(AggregateRoot<?> aggregateRoot) {
    for (DomainEvent event : aggregateRoot.pullEvents()) {
      delegate.publishEvent(event);
    }
  }

  @Override
  public void publish(DomainEvent event) {
    delegate.publishEvent(event);
  }
}
