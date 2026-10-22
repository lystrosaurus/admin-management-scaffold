package io.github.lystrosaurus.admin.system.user.application.command;

/**
 * 创建用户命令。
 *
 * @param username 用户名
 * @param password 明文密码
 * @param nickname 昵称
 * @param phone 手机号
 * @param email 邮箱
 */
public record UserCreateCommand(
    String username, String password, String nickname, String phone, String email) {}
