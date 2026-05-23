package io.github.lystrosaurus.admin.system.user.dto;

/**
 * 用户查询DTO
 *
 * @param username 用户名
 * @param status 状态
 * @param keyword 关键词
 */
public record UserQueryDTO(String username, String status, String keyword) {}
