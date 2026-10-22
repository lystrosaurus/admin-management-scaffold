package io.github.lystrosaurus.admin.integration.principal.application.command;

import java.util.List;

/**
 * 外部主体创建命令。
 *
 * @param sourceId 外部系统ID
 * @param principalType 主体类型(USER/ORG)
 * @param externalKey 外部系统主键
 * @param displayName 显示名称
 * @param rawPayloadJson 原始数据JSON
 * @param identifiers 标识符列表
 */
public record ExtPrincipalCreateCommand(
    Long sourceId,
    String principalType,
    String externalKey,
    String displayName,
    String rawPayloadJson,
    List<IdentifierItem> identifiers) {

  /**
   * 标识符项。
   *
   * @param idType ID类型
   * @param idValue ID值
   * @param isPrimary 是否主ID
   */
  public record IdentifierItem(String idType, String idValue, Boolean isPrimary) {}
}
