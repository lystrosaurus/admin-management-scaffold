package io.github.lystrosaurus.admin.system.user.service;

import io.github.lystrosaurus.admin.common.PageResult;
import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.organization.employee.dao.EmployeeDAO;
import io.github.lystrosaurus.admin.system.role.entity.SysRole;
import io.github.lystrosaurus.admin.system.role.vo.RoleVO;
import io.github.lystrosaurus.admin.system.user.dao.UserDAO;
import io.github.lystrosaurus.admin.system.user.dto.ChangePasswordDTO;
import io.github.lystrosaurus.admin.system.user.dto.UserCreateDTO;
import io.github.lystrosaurus.admin.system.user.dto.UserQueryDTO;
import io.github.lystrosaurus.admin.system.user.dto.UserUpdateDTO;
import io.github.lystrosaurus.admin.system.user.entity.SysUser;
import io.github.lystrosaurus.admin.system.user.mapstruct.UserMapper;
import io.github.lystrosaurus.admin.system.user.vo.UserDetailVO;
import io.github.lystrosaurus.admin.system.user.vo.UserVO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/** 用户服务实现 */
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserDAO userDAO;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;
  private final EmployeeDAO employeeDAO;

  @Transactional(rollbackFor = Exception.class)
  public UserVO create(UserCreateDTO dto) {
    // 检查用户名唯一性
    if (userDAO.existsByUsername(dto.username())) {
      throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS);
    }

    // 转换并保存用户
    SysUser user = userMapper.toEntity(dto);
    user.setPasswordHash(passwordEncoder.encode(dto.password()));
    userDAO.save(user);

    return userMapper.toUserVO(user);
  }

  @Transactional(rollbackFor = Exception.class)
  public UserVO update(Long id, UserUpdateDTO dto) {
    // 查找用户
    SysUser user = userDAO.findById(id);
    if (user == null) {
      throw new BusinessException(ErrorCode.USER_NOT_FOUND);
    }

    // 更新字段
    if (StringUtils.hasText(dto.nickname())) {
      user.setNickname(dto.nickname());
    }
    if (StringUtils.hasText(dto.phone())) {
      user.setPhone(dto.phone());
    }
    if (StringUtils.hasText(dto.email())) {
      user.setEmail(dto.email());
    }
    if (StringUtils.hasText(dto.status())) {
      user.setStatus(dto.status());
    }

    userDAO.update(user);
    return userMapper.toUserVO(user);
  }

  @Transactional(rollbackFor = Exception.class)
  public void deleteById(Long id) {
    userDAO.deleteById(id);
  }

  public UserDetailVO findById(Long id) {
    // 查询用户
    SysUser user = userDAO.findById(id);
    if (user == null) {
      throw new BusinessException(ErrorCode.USER_NOT_FOUND);
    }

    // 查询用户角色
    List<SysRole> roles = userDAO.findRolesByUserId(id);
    List<RoleVO> roleVOs =
        roles.stream()
            .map(
                role ->
                    new RoleVO(
                        role.getId(),
                        role.getCode(),
                        role.getName(),
                        role.getDescription(),
                        role.getStatus(),
                        role.getDataScopeType()))
            .toList();

    // 直接从实体构建详情VO，避免先MapStruct转换再手动重建的浪费
    return new UserDetailVO(
        user.getId(),
        user.getUsername(),
        user.getNickname(),
        user.getPhone(),
        user.getEmail(),
        user.getStatus(),
        user.getEmployeeId(),
        user.getTokenVersion(),
        user.getLastLoginAt(),
        user.getLastLoginIp(),
        roleVOs,
        user.getCreatedAt());
  }

  public PageResult<UserVO> findPage(UserQueryDTO dto, int page, int size) {
    // 查询用户列表
    List<SysUser> users = userDAO.findByCondition(dto.username(), dto.status(), page, size);
    long total = userDAO.countByCondition(dto.username(), dto.status());

    // 转换为VO列表
    List<UserVO> userVOs = users.stream().map(userMapper::toUserVO).toList();

    return new PageResult<>(userVOs, total, page, size);
  }

  @Transactional(rollbackFor = Exception.class)
  public void changePassword(Long id, ChangePasswordDTO dto) {
    // 查找用户
    SysUser user = userDAO.findById(id);
    if (user == null) {
      throw new BusinessException(ErrorCode.USER_NOT_FOUND);
    }

    // 验证旧密码
    if (!passwordEncoder.matches(dto.oldPassword(), user.getPasswordHash())) {
      throw new BusinessException(ErrorCode.USER_INVALID_PASSWORD);
    }

    // 更新密码
    user.setPasswordHash(passwordEncoder.encode(dto.newPassword()));
    userDAO.update(user);
  }

  @Transactional(rollbackFor = Exception.class)
  public void bindEmployee(Long userId, Long employeeId) {
    // 检查用户是否存在
    SysUser user = userDAO.findById(userId);
    if (user == null) {
      throw new BusinessException(ErrorCode.USER_NOT_FOUND);
    }

    // 检查员工是否存在（通过 hr_employee 表查询）
    if (!employeeExists(employeeId)) {
      throw new BusinessException(ErrorCode.EMPLOYEE_NOT_FOUND);
    }

    // 检查该员工是否已被其他用户绑定
    SysUser existingUser = userDAO.findByEmployeeId(employeeId);
    if (existingUser != null && !existingUser.getId().equals(userId)) {
      throw new BusinessException(ErrorCode.USER_EMPLOYEE_ALREADY_BOUND);
    }

    // 绑定员工
    user.setEmployeeId(employeeId);
    userDAO.update(user);
  }

  @Transactional(rollbackFor = Exception.class)
  public void unbindEmployee(Long userId) {
    // 检查用户是否存在
    SysUser user = userDAO.findById(userId);
    if (user == null) {
      throw new BusinessException(ErrorCode.USER_NOT_FOUND);
    }

    // 解绑员工
    user.setEmployeeId(null);
    userDAO.update(user);
  }

  /**
   * 检查 hr_employee 表中是否存在指定员工
   *
   * @param employeeId 员工ID
   * @return 存在返回 true
   */
  private boolean employeeExists(Long employeeId) {
    return employeeDAO.existsById(employeeId);
  }
}
