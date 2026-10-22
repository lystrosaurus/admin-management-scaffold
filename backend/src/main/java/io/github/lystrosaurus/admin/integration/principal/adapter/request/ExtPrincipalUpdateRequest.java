package io.github.lystrosaurus.admin.integration.principal.adapter.request;

import io.github.lystrosaurus.admin.integration.principal.application.command.ExtPrincipalUpdateCommand;

/**
 * 外部主体更新请求（字段与原 ExtPrincipalUpdateDTO 一致）。
 *
 * @param displayName 显示名称
 * @param status 状态(ACTIVE/INACTIVE)
 * @param canonicalType 映射目标类型(EMPLOYEE/ORG_UNIT)
 * @param canonicalId 映射目标ID
 * @param linkStatus 关联状态
 */
public record ExtPrincipalUpdateRequest(
    String displayName, String status, String canonicalType, Long canonicalId, String linkStatus) {

  /** 转为应用层命令 */
  public ExtPrincipalUpdateCommand toCommand() {
    return new ExtPrincipalUpdateCommand(displayName, status, canonicalType, canonicalId, linkStatus);
  }
}
