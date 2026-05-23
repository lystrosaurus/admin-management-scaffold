package io.github.lystrosaurus.admin.system.user.dto;

/**
 * 用户更新DTO
 *
 * @param nickname 昵称
 * @param phone 手机号
 * @param email 邮箱
 * @param status 状态
 */
public record UserUpdateDTO(String nickname, String phone, String email, String status) {}
