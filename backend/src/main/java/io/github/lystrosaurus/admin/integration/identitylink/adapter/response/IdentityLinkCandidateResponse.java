package io.github.lystrosaurus.admin.integration.identitylink.adapter.response;

import java.time.LocalDateTime;

/**
 * 身份匹配候选响应（字段与原 IdentityLinkCandidateVO 完全一致，保证 API 契约不变）。
 *
 * @param id 主键ID
 * @param sourcePrincipalId 关联的外部主体ID
 * @param candidateType 候选类型
 * @param candidateId 候选目标ID
 * @param score 匹配分数
 * @param reason 匹配原因
 * @param status 状态
 * @param createdAt 创建时间
 * @param handledBy 处理人
 * @param handledAt 处理时间
 */
public record IdentityLinkCandidateResponse(
    Long id,
    Long sourcePrincipalId,
    String candidateType,
    Long candidateId,
    Integer score,
    String reason,
    String status,
    LocalDateTime createdAt,
    String handledBy,
    LocalDateTime handledAt) {}
