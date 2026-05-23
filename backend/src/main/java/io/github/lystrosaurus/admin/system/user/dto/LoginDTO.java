package io.github.lystrosaurus.admin.system.user.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 登录DTO
 *
 * @param username 用户名
 * @param password 密码
 */
public record LoginDTO(
    @NotBlank(message = "用户名不能为空") String username,
    @NotBlank(message = "密码不能为空") String password) {}
