package io.github.lystrosaurus.admin.shared.domain.primitive;

import java.time.LocalDateTime;

/** 领域事件：聚合内发生的、对业务有意义的事实 */
public interface DomainEvent {

  /** 事件唯一标识 */
  String eventId();

  /** 事件发生时间 */
  LocalDateTime occurredAt();

  /** 来源聚合标识（字符串形式，避免跨上下文类型耦合） */
  String aggregateId();
}
