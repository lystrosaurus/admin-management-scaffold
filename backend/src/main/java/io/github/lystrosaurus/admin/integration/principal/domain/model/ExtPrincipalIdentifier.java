package io.github.lystrosaurus.admin.integration.principal.domain.model;

/**
 * 外部主体标识符。
 *
 * @param id 标识符ID
 * @param idType ID类型
 * @param idValue ID值
 * @param primary 是否主ID
 */
public record ExtPrincipalIdentifier(Long id, String idType, String idValue, boolean primary) {}
