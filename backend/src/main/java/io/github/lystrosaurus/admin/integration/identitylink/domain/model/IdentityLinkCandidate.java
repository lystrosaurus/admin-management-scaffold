package io.github.lystrosaurus.admin.integration.identitylink.domain.model;

import io.github.lystrosaurus.admin.integration.identitylink.domain.event.IdentityLinkCandidateCreated;
import io.github.lystrosaurus.admin.integration.identitylink.domain.event.IdentityLinkCandidateUpdated;
import io.github.lystrosaurus.admin.shared.domain.exception.DomainException;
import io.github.lystrosaurus.admin.shared.domain.primitive.AggregateRoot;
import java.time.LocalDateTime;

/** 身份匹配候选聚合根：守卫候选只能被处理一次的状态机不变量。 */
public class IdentityLinkCandidate extends AggregateRoot<IdentityLinkCandidateId> {

  /** 关联的外部主体ID */
  private Long sourcePrincipalId;

  /** 候选类型(EMPLOYEE/ORG_UNIT) */
  private String candidateType;

  /** 候选目标ID */
  private Long candidateId;

  /** 匹配分数(0-100) */
  private Integer score;

  /** 匹配原因 */
  private String reason;

  /** 状态(PENDING/CONFIRMED/REJECTED) */
  private String status;

  /** 创建时间 */
  private LocalDateTime createdAt;

  /** 处理人 */
  private String handledBy;

  /** 处理时间 */
  private LocalDateTime handledAt;

  private IdentityLinkCandidate(IdentityLinkCandidateId id) {
    super(id);
  }

  /**
   * 创建身份匹配候选：状态默认 PENDING，并登记 {@link IdentityLinkCandidateCreated} 事件。
   *
   * @param id 由应用层通过 IdGenerator 预生成的标识
   * @param sourcePrincipalId 外部主体ID
   * @param candidateType 候选类型
   * @param candidateId 候选目标ID
   * @param score 匹配分数
   * @param reason 匹配原因
   * @return 新候选聚合
   */
  public static IdentityLinkCandidate create(
      IdentityLinkCandidateId id,
      Long sourcePrincipalId,
      String candidateType,
      Long candidateId,
      Integer score,
      String reason) {
    IdentityLinkCandidate candidate = new IdentityLinkCandidate(id);
    candidate.sourcePrincipalId = sourcePrincipalId;
    candidate.candidateType = candidateType;
    candidate.candidateId = candidateId;
    candidate.score = score;
    candidate.reason = reason;
    candidate.status = "PENDING";
    candidate.registerEvent(IdentityLinkCandidateCreated.of(id.value(), sourcePrincipalId));
    return candidate;
  }

  /**
   * 从持久化数据重建聚合（不登记事件）。
   *
   * @return 候选聚合
   */
  public static IdentityLinkCandidate restore(
      IdentityLinkCandidateId id,
      Long sourcePrincipalId,
      String candidateType,
      Long candidateId,
      Integer score,
      String reason,
      String status,
      LocalDateTime createdAt,
      String handledBy,
      LocalDateTime handledAt) {
    IdentityLinkCandidate candidate = new IdentityLinkCandidate(id);
    candidate.sourcePrincipalId = sourcePrincipalId;
    candidate.candidateType = candidateType;
    candidate.candidateId = candidateId;
    candidate.score = score;
    candidate.reason = reason;
    candidate.status = status;
    candidate.createdAt = createdAt;
    candidate.handledBy = handledBy;
    candidate.handledAt = handledAt;
    return candidate;
  }

  /** 确认候选：仅 PENDING 状态可处理，登记 {@link IdentityLinkCandidateUpdated} 事件。 */
  public void confirm(String handledBy) {
    markHandled("CONFIRMED", handledBy);
  }

  /** 拒绝候选：仅 PENDING 状态可处理，登记 {@link IdentityLinkCandidateUpdated} 事件。 */
  public void reject(String handledBy) {
    markHandled("REJECTED", handledBy);
  }

  private void markHandled(String targetStatus, String handledBy) {
    if (!"PENDING".equals(status)) {
      throw new DomainException(IdentityLinkCandidateErrorCode.CANDIDATE_ALREADY_HANDLED);
    }
    this.status = targetStatus;
    this.handledBy = handledBy;
    this.handledAt = LocalDateTime.now();
    registerEvent(IdentityLinkCandidateUpdated.of(id().value(), targetStatus, handledBy));
  }

  public Long sourcePrincipalId() {
    return sourcePrincipalId;
  }

  public String candidateType() {
    return candidateType;
  }

  public Long candidateId() {
    return candidateId;
  }

  public Integer score() {
    return score;
  }

  public String reason() {
    return reason;
  }

  public String status() {
    return status;
  }

  public LocalDateTime createdAt() {
    return createdAt;
  }

  public String handledBy() {
    return handledBy;
  }

  public LocalDateTime handledAt() {
    return handledAt;
  }
}
