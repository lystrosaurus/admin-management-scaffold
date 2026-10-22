package io.github.lystrosaurus.admin.system.user.application;

import io.github.lystrosaurus.admin.shared.adapter.web.PageResult;
import io.github.lystrosaurus.admin.shared.annotation.AggregateLock;
import io.github.lystrosaurus.admin.shared.domain.acl.IdGenerator;
import io.github.lystrosaurus.admin.shared.domain.acl.Page;
import io.github.lystrosaurus.admin.shared.domain.acl.PageQuery;
import io.github.lystrosaurus.admin.shared.domain.exception.DomainException;
import io.github.lystrosaurus.admin.system.user.adapter.response.UserDetailResponse;
import io.github.lystrosaurus.admin.system.user.adapter.response.UserResponse;
import io.github.lystrosaurus.admin.system.user.application.assembler.UserAssembler;
import io.github.lystrosaurus.admin.system.user.application.command.BindEmployeeCommand;
import io.github.lystrosaurus.admin.system.user.application.command.ChangePasswordCommand;
import io.github.lystrosaurus.admin.system.user.application.command.UserCreateCommand;
import io.github.lystrosaurus.admin.system.user.application.command.UserQuery;
import io.github.lystrosaurus.admin.system.user.application.command.UserUpdateCommand;
import io.github.lystrosaurus.admin.system.user.domain.acl.EmployeeExistenceChecker;
import io.github.lystrosaurus.admin.system.user.domain.acl.UserQueryRepository;
import io.github.lystrosaurus.admin.system.user.domain.acl.UserRepository;
import io.github.lystrosaurus.admin.system.user.domain.acl.UserRoleInfo;
import io.github.lystrosaurus.admin.system.user.domain.model.User;
import io.github.lystrosaurus.admin.system.user.domain.model.UserErrorCode;
import io.github.lystrosaurus.admin.system.user.domain.model.UserId;
import io.github.lystrosaurus.admin.system.user.domain.model.UserStatus;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/** 用户应用服务：编排用户用例，业务规则落在聚合与领域端口上。 */
@Service
@RequiredArgsConstructor
public class UserApplicationService {

  private final UserRepository userRepository;
  private final UserQueryRepository userQueryRepository;
  private final EmployeeExistenceChecker employeeExistenceChecker;
  private final IdGenerator idGenerator;
  private final PasswordEncoder passwordEncoder;
  private final UserAssembler userAssembler;

  /** 创建用户 */
  @Transactional(rollbackFor = Exception.class)
  public UserResponse create(UserCreateCommand command) {
    if (userRepository.existsByUsername(command.username())) {
      throw new DomainException(UserErrorCode.USER_ALREADY_EXISTS);
    }

    User user =
        User.register(
            UserId.of(idGenerator.nextId()),
            command.username(),
            passwordEncoder.encode(command.password()),
            command.nickname(),
            command.phone(),
            command.email());
    userRepository.save(user);

    // 重新加载以回读数据库填充的审计字段（createdAt 等），保持响应契约与旧实现一致
    return userAssembler.toResponse(userRepository.findById(user.id()).orElseThrow());
  }

  /** 更新用户（部分更新：仅非空文本字段生效） */
  @AggregateLock(prefix = "user:update", key = "#id")
  @Transactional(rollbackFor = Exception.class)
  public UserResponse update(Long id, UserUpdateCommand command) {
    User user = findOrThrow(UserId.of(id));

    user.updateProfile(command.nickname(), command.phone(), command.email());
    if (StringUtils.hasText(command.status())) {
      user.changeStatus(UserStatus.fromString(command.status()));
    }
    userRepository.save(user);
    return userAssembler.toResponse(user);
  }

  /** 删除用户 */
  @AggregateLock(prefix = "user:delete", key = "#id")
  @Transactional(rollbackFor = Exception.class)
  public void deleteById(Long id) {
    userRepository.deleteById(UserId.of(id));
  }

  /** 查询用户详情（含角色） */
  public UserDetailResponse findById(Long id) {
    User user = findOrThrow(UserId.of(id));
    List<UserRoleInfo> roles = userQueryRepository.findRolesByUserId(user.id());
    return userAssembler.toDetailResponse(user, roles);
  }

  /** 分页查询用户 */
  public PageResult<UserResponse> findPage(UserQuery query, int page, int size) {
    Page<User> result =
        userQueryRepository.findPage(query.username(), query.status(), PageQuery.of(page, size));
    List<UserResponse> responses = result.records().stream().map(userAssembler::toResponse).toList();
    return new PageResult<>(responses, result.total(), result.pageNum(), result.pageSize());
  }

  /** 修改密码 */
  @AggregateLock(prefix = "user:change-password", key = "#id")
  @Transactional(rollbackFor = Exception.class)
  public void changePassword(Long id, ChangePasswordCommand command) {
    User user = findOrThrow(UserId.of(id));

    if (!passwordEncoder.matches(command.oldPassword(), user.passwordHash())) {
      throw new DomainException(UserErrorCode.USER_INVALID_PASSWORD);
    }

    user.changePassword(passwordEncoder.encode(command.newPassword()));
    userRepository.save(user);
  }

  /** 绑定员工 */
  @AggregateLock(prefix = "user:bind-employee", key = "#userId")
  @Transactional(rollbackFor = Exception.class)
  public void bindEmployee(Long userId, BindEmployeeCommand command) {
    User user = findOrThrow(UserId.of(userId));

    if (!employeeExistenceChecker.exists(command.employeeId())) {
      throw new DomainException(UserErrorCode.EMPLOYEE_NOT_FOUND);
    }

    userRepository
        .findByEmployeeId(command.employeeId())
        .filter(existing -> !existing.id().equals(user.id()))
        .ifPresent(
            existing -> {
              throw new DomainException(UserErrorCode.USER_EMPLOYEE_ALREADY_BOUND);
            });

    user.bindEmployee(command.employeeId());
    userRepository.save(user);
  }

  /** 解绑员工 */
  @AggregateLock(prefix = "user:unbind-employee", key = "#userId")
  @Transactional(rollbackFor = Exception.class)
  public void unbindEmployee(Long userId) {
    User user = findOrThrow(UserId.of(userId));
    user.unbindEmployee();
    userRepository.save(user);
  }

  private User findOrThrow(UserId id) {
    return userRepository
        .findById(id)
        .orElseThrow(() -> new DomainException(UserErrorCode.USER_NOT_FOUND));
  }
}
