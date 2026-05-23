package io.github.lystrosaurus.admin.system.user.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 修改密码DTO
 *
 * @param oldPassword 旧密码
 * @param newPassword 新密码
 */
public record ChangePasswordDTO(
    @NotBlank(message = "旧密码不能为空") String oldPassword,
    @NotBlank(message = "新密码不能为空") String newPassword) {}
