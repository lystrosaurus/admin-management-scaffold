package io.github.lystrosaurus.admin.system.user.application.command;

/**
 * 更新用户命令。
 *
 * @param nickname 昵称
 * @param phone 手机号
 * @param email 邮箱
 * @param status 状态
 */
public record UserUpdateCommand(String nickname, String phone, String email, String status) {}
