package io.github.lystrosaurus.admin.integration.identitylink.vo;

import java.time.LocalDateTime;

/**
 * 身份匹配候选VO
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
public record IdentityLinkCandidateVO(
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
