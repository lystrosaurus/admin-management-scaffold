package io.github.lystrosaurus.admin.integration.identitylink.application.command;

/**
 * 身份匹配候选创建命令。
 *
 * @param sourcePrincipalId 关联的外部主体ID
 * @param candidateType 候选类型（USER/EMPLOYEE）
 * @param candidateId 候选目标ID
 * @param score 匹配分数
 * @param reason 匹配原因
 */
public record IdentityLinkCandidateCreateCommand(
    Long sourcePrincipalId, String candidateType, Long candidateId, Integer score, String reason) {}
