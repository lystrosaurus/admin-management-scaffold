package io.github.lystrosaurus.admin.integration.identitylink.dto;

/**
 * 身份匹配候选创建DTO
 *
 * @param sourcePrincipalId 关联的外部主体ID
 * @param candidateType 候选类型（USER/EMPLOYEE）
 * @param candidateId 候选目标ID
 * @param score 匹配分数
 * @param reason 匹配原因
 */
public record IdentityLinkCandidateCreateDTO(
    Long sourcePrincipalId, String candidateType, Long candidateId, Integer score, String reason) {}
