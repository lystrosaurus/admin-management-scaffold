package io.github.lystrosaurus.admin.system.dict.domain.event;

import io.github.lystrosaurus.admin.shared.domain.primitive.DomainEvent;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 字典已创建事件。
 *
 * @param dictId 字典ID
 * @param dictType 字典类型
 * @param eventId 事件唯一标识
 * @param occurredAt 事件发生时间
 * @param aggregateId 来源聚合标识
 */
public record DictCreated(
    Long dictId, String dictType, String eventId, LocalDateTime occurredAt, String aggregateId)
    implements DomainEvent {

  /** 创建事件实例 */
  public static DictCreated of(Long dictId, String dictType) {
    return new DictCreated(
        dictId, dictType, UUID.randomUUID().toString(), LocalDateTime.now(), String.valueOf(dictId));
  }
}
