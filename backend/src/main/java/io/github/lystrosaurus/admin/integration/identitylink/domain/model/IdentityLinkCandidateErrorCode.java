package io.github.lystrosaurus.admin.integration.identitylink.domain.model;

import io.github.lystrosaurus.admin.shared.domain.exception.DomainErrorCode;

/** 身份匹配候选上下文错误码：数值与旧 ErrorCode 保持一致。 */
public enum IdentityLinkCandidateErrorCode implements DomainErrorCode {
  CANDIDATE_NOT_FOUND(9201, "身份匹配候选不存在"),
  CANDIDATE_ALREADY_HANDLED(9202, "身份匹配候选已处理");

  private final int code;
  private final String message;

  IdentityLinkCandidateErrorCode(int code, String message) {
    this.code = code;
    this.message = message;
  }

  @Override
  public int code() {
    return code;
  }

  @Override
  public String message() {
    return message;
  }
}
