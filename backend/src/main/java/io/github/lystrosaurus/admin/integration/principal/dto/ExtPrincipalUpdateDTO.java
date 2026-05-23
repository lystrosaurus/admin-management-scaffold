package io.github.lystrosaurus.admin.integration.principal.dto;

/** 外部主体更新DTO */
public record ExtPrincipalUpdateDTO(
    /** 显示名称 */
    String displayName,
    /** 状态(ACTIVE/INACTIVE) */
    String status,
    /** 映射目标类型(EMPLOYEE/ORG_UNIT) */
    String canonicalType,
    /** 映射目标ID */
    Long canonicalId,
    /** 关联状态(UNLINKED/AUTO_LINKED/MANUAL_LINKED/CONFLICT) */
    String linkStatus) {}
