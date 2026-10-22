package io.github.lystrosaurus.admin.auth.domain.event;

import io.github.lystrosaurus.admin.shared.domain.primitive.DomainEvent;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 登录成功事件。
 *
 * @param userId 用户ID
 * @param loginType 登录类型（PASSWORD/OAUTH）
 * @param providerCode OAuth 提供方编码（密码登录为 null）
 * @param ipAddress 客户端 IP（OAuth 回调为 null）
 * @param userAgent User-Agent（OAuth 回调为 null）
 * @param occurredAt 事件发生时间
 */
public record LoginSucceeded(
    Long userId,
    String loginType,
    String providerCode,
    String ipAddress,
    String userAgent,
    LocalDateTime occurredAt)
    implements DomainEvent {

  @Override
  public String eventId() {
    return UUID.randomUUID().toString();
  }

  @Override
  public String aggregateId() {
    return String.valueOf(userId);
  }
}
