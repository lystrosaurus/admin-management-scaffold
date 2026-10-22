package io.github.lystrosaurus.admin.shared.domain.primitive;

import java.util.ArrayList;
import java.util.List;

/**
 * 聚合根：守卫聚合不变量，登记领域事件。
 *
 * <p>事件在仓储保存聚合后由 {@code DomainEventPublisher} 统一派发。
 *
 * @param <ID> 标识类型
 */
public abstract class AggregateRoot<ID extends Identifier> extends Entity<ID> {

  private final transient List<DomainEvent> domainEvents = new ArrayList<>();

  protected AggregateRoot(ID id) {
    super(id);
  }

  /** 登记领域事件（聚合内部行为调用） */
  protected void registerEvent(DomainEvent event) {
    domainEvents.add(event);
  }

  /** 取出并清空已登记事件，由事件发布器调用 */
  public List<DomainEvent> pullEvents() {
    List<DomainEvent> pulled = List.copyOf(domainEvents);
    domainEvents.clear();
    return pulled;
  }
}
