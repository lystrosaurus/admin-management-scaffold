package io.github.lystrosaurus.admin.integration.principal.adapter.response;

/**
 * 外部主体标识符响应（字段与原 ExtPrincipalIdentifierVO 完全一致）。
 *
 * @param id 主键ID
 * @param idType ID类型
 * @param idValue ID值
 * @param isPrimary 是否主ID
 */
public record ExtPrincipalIdentifierResponse(
    Long id, String idType, String idValue, Boolean isPrimary) {}
