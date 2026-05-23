package io.github.lystrosaurus.admin.integration.principal.dto;

import java.util.List;

/** 外部主体创建DTO */
public record ExtPrincipalCreateDTO(
    /** 外部系统ID */
    Long sourceId,
    /** 主体类型(USER/ORG) */
    String principalType,
    /** 外部系统主键 */
    String externalKey,
    /** 显示名称 */
    String displayName,
    /** 原始数据JSON */
    String rawPayloadJson,
    /** 标识符列表 */
    List<IdentifierItem> identifiers) {

  /** 标识符项 */
  public record IdentifierItem(
      /** ID类型 */
      String idType,
      /** ID值 */
      String idValue,
      /** 是否主ID */
      Boolean isPrimary) {}
}
