package io.github.lystrosaurus.admin.system.user.application.command;

/**
 * 修改密码命令。
 *
 * @param oldPassword 旧密码
 * @param newPassword 新密码
 */
public record ChangePasswordCommand(String oldPassword, String newPassword) {}
