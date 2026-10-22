package io.github.lystrosaurus.admin.system.user.adapter.request;

import io.github.lystrosaurus.admin.system.user.application.command.ChangePasswordCommand;
import jakarta.validation.constraints.NotBlank;

/**
 * 修改密码请求。
 *
 * @param oldPassword 旧密码
 * @param newPassword 新密码
 */
public record ChangePasswordRequest(
    @NotBlank(message = "旧密码不能为空") String oldPassword,
    @NotBlank(message = "新密码不能为空") String newPassword) {

  /** 转修改密码命令 */
  public ChangePasswordCommand toCommand() {
    return new ChangePasswordCommand(oldPassword, newPassword);
  }
}
