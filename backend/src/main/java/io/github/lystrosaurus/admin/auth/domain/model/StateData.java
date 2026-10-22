package io.github.lystrosaurus.admin.auth.domain.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * OAuth 状态数据（端口返回值对象）。
 *
 * @param state 状态值
 * @param nonce 随机数
 * @param userId 用户ID（可选）
 * @param createdAt 创建时间（LocalDateTime；序列化由 jackson-datatype-jsr310 支持）
 */
public record StateData(String state, String nonce, Long userId, LocalDateTime createdAt)
    implements Serializable {}
