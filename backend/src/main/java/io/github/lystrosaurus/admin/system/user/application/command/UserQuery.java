package io.github.lystrosaurus.admin.system.user.application.command;

/**
 * 用户分页查询条件。
 *
 * @param username 用户名
 * @param status 状态
 * @param keyword 关键词
 */
public record UserQuery(String username, String status, String keyword) {}
