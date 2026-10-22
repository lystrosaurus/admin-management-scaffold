package io.github.lystrosaurus.admin.auth.application.command;

/**
 * 登录命令。
 *
 * @param username 用户名
 * @param password 明文密码
 */
public record LoginCommand(String username, String password) {}
