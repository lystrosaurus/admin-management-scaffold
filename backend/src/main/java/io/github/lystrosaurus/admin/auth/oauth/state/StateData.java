package io.github.lystrosaurus.admin.auth.oauth.state;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * OAuth 状态数据
 *
 * @param state 状态值
 * @param nonce 随机数
 * @param userId 用户ID（可选）
 * @param createdAt 创建时间
 */
public record StateData(String state, String nonce, Long userId, LocalDateTime createdAt)
    implements Serializable {}
