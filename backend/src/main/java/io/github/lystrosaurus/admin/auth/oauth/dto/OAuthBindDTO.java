package io.github.lystrosaurus.admin.auth.oauth.dto;

/**
 * OAuth 绑定 DTO
 *
 * @param code 授权码
 * @param state 状态值
 */
public record OAuthBindDTO(String code, String state) {}
