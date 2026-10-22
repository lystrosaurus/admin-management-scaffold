package io.github.lystrosaurus.admin.organization.orgunit.domain.model;

import io.github.lystrosaurus.admin.shared.domain.exception.DomainErrorCode;

/** 组织单元上下文错误码：数值与旧 ErrorCode 保持一致。 */
public enum OrgUnitErrorCode implements DomainErrorCode {
  ORG_UNIT_NOT_FOUND(8001, "组织单元不存在"),
  ORG_UNIT_ALREADY_EXISTS(8002, "组织单元已存在"),
  ORG_UNIT_HAS_CHILDREN(8003, "组织单元存在子节点，无法删除");

  private final int code;
  private final String message;

  OrgUnitErrorCode(int code, String message) {
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
