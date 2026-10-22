package io.github.lystrosaurus.admin.system.user.adapter.response;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户详情响应（字段集合与原 UserDetailVO 完全一致，保证 API 契约不变）。
 *
 * @param id 主键ID
 * @param username 用户名
 * @param nickname 昵称
 * @param phone 手机号
 * @param email 邮箱
 * @param status 状态
 * @param employeeId 绑定员工ID
 * @param tokenVersion token版本号
 * @param lastLoginAt 最后登录时间
 * @param lastLoginIp 最后登录IP
 * @param roles 用户角色列表
 * @param createdAt 创建时间
 */
public record UserDetailResponse(
    Long id,
    String username,
    String nickname,
    String phone,
    String email,
    String status,
    Long employeeId,
    Integer tokenVersion,
    LocalDateTime lastLoginAt,
    String lastLoginIp,
    List<UserRoleResponse> roles,
    LocalDateTime createdAt) {}
