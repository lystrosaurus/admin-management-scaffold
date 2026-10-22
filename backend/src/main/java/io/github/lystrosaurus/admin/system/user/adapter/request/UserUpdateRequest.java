package io.github.lystrosaurus.admin.system.user.adapter.request;

import io.github.lystrosaurus.admin.system.user.application.command.UserUpdateCommand;

/**
 * 用户更新请求。
 *
 * @param nickname 昵称
 * @param phone 手机号
 * @param email 邮箱
 * @param status 状态
 */
public record UserUpdateRequest(String nickname, String phone, String email, String status) {

  /** 转更新命令 */
  public UserUpdateCommand toCommand() {
    return new UserUpdateCommand(nickname, phone, email, status);
  }
}
