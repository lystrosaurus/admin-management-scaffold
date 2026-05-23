package io.github.lystrosaurus.admin.system.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 用户创建DTO
 *
 * @param username 用户名
 * @param password 密码
 * @param nickname 昵称
 * @param phone 手机号
 * @param email 邮箱
 */
public record UserCreateDTO(
    @NotBlank(message = "用户名不能为空") String username,
    @NotBlank(message = "密码不能为空") @Size(min = 6, message = "密码长度不能小于6位") String password,
    String nickname,
    String phone,
    String email) {}
