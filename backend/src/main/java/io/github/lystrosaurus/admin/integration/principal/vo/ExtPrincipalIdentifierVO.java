package io.github.lystrosaurus.admin.integration.principal.vo;

/** 外部主体标识符VO */
public record ExtPrincipalIdentifierVO(
    /** 主键ID */
    Long id,
    /** ID类型 */
    String idType,
    /** ID值 */
    String idValue,
    /** 是否主ID */
    Boolean isPrimary) {}
