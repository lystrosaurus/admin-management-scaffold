package io.github.lystrosaurus.admin.system.user.vo;

import io.github.lystrosaurus.admin.system.role.vo.RoleVO;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户详情VO
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
public record UserDetailVO(
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
    List<RoleVO> roles,
    LocalDateTime createdAt) {}
