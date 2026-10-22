package io.github.lystrosaurus.admin.system.user.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import io.github.lystrosaurus.admin.common.PageResult;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

/** UserApplicationService 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("用户应用服务测试")
class UserApplicationServiceTest {

  @Mock private UserRepository userRepository;

  @Mock private UserQueryRepository userQueryRepository;

  @Mock private EmployeeExistenceChecker employeeExistenceChecker;

  @Mock private IdGenerator idGenerator;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private UserAssembler userAssembler;

  @InjectMocks private UserApplicationService userApplicationService;

  private User testUser;
  private UserCreateCommand createCommand;
  private UserUpdateCommand updateCommand;
  private UserResponse userResponse;

  @BeforeEach
  void setUp() {
    testUser =
        User.restore(
            UserId.of(1L),
            "testuser",
            "$2a$10$hashedpassword",
            "测试用户",
            null,
            "13800138000",
            "test@example.com",
            null,
            UserStatus.ENABLED,
            1,
            null,
            null,
            LocalDateTime.now(),
            1);

    createCommand =
        new UserCreateCommand("testuser", "password123", "测试用户", "13800138000", "test@example.com");
    updateCommand = new UserUpdateCommand("新昵称", "13900139000", "new@example.com", "ENABLED");
    userResponse =
        new UserResponse(
            1L,
            "testuser",
            "测试用户",
            "13800138000",
            "test@example.com",
            "ENABLED",
            null,
            LocalDateTime.now());
  }

  @Test
  @DisplayName("应该成功创建用户")
  void should_create_user_when_username_not_exists() {
    // Given
    when(userRepository.existsByUsername("testuser")).thenReturn(false);
    when(idGenerator.nextId()).thenReturn(1L);
    when(passwordEncoder.encode("password123")).thenReturn("$2a$10$encodedpassword");
    when(userRepository.findById(UserId.of(1L))).thenReturn(Optional.of(testUser));
    when(userAssembler.toResponse(any(User.class))).thenReturn(userResponse);

    // When
    UserResponse result = userApplicationService.create(createCommand);

    // Then
    assertNotNull(result);
    assertEquals("testuser", result.username());
    assertEquals("测试用户", result.nickname());
    verify(userRepository).existsByUsername("testuser");
    verify(passwordEncoder).encode("password123");
    verify(userRepository).save(any(User.class));
  }

  @Test
  @DisplayName("创建用户时用户名已存在应该抛出异常")
  void should_throw_exception_when_username_already_exists() {
    // Given
    when(userRepository.existsByUsername("testuser")).thenReturn(true);

    // When & Then
    DomainException exception =
        assertThrows(DomainException.class, () -> userApplicationService.create(createCommand));
    assertEquals(UserErrorCode.USER_ALREADY_EXISTS.code(), exception.code());
    verify(userRepository).existsByUsername("testuser");
    verify(userRepository, never()).save(any());
  }

  @Test
  @DisplayName("应该成功更新用户")
  void should_update_user_when_user_exists() {
    // Given
    when(userRepository.findById(UserId.of(1L))).thenReturn(Optional.of(testUser));
    when(userAssembler.toResponse(any(User.class))).thenReturn(userResponse);

    // When
    UserResponse result = userApplicationService.update(1L, updateCommand);

    // Then
    assertNotNull(result);
    verify(userRepository).findById(UserId.of(1L));
    verify(userRepository).save(any(User.class));
  }

  @Test
  @DisplayName("更新用户时用户不存在应该抛出异常")
  void should_throw_exception_when_update_nonexistent_user() {
    // Given
    when(userRepository.findById(UserId.of(999L))).thenReturn(Optional.empty());

    // When & Then
    DomainException exception =
        assertThrows(DomainException.class, () -> userApplicationService.update(999L, updateCommand));
    assertEquals(UserErrorCode.USER_NOT_FOUND.code(), exception.code());
    verify(userRepository).findById(UserId.of(999L));
    verify(userRepository, never()).save(any());
  }

  @Test
  @DisplayName("应该成功删除用户")
  void should_delete_user_by_id() {
    // When
    userApplicationService.deleteById(1L);

    // Then
    verify(userRepository).deleteById(UserId.of(1L));
  }

  @Test
  @DisplayName("应该成功查询用户详情")
  void should_find_user_detail_by_id() {
    // Given
    UserRoleInfo roleInfo = new UserRoleInfo(1L, "ADMIN", "管理员", null, "ENABLED", null);
    UserDetailResponse detailResponse =
        new UserDetailResponse(
            1L,
            "testuser",
            "测试用户",
            "13800138000",
            "test@example.com",
            "ENABLED",
            null,
            1,
            null,
            null,
            List.of(),
            LocalDateTime.now());

    when(userRepository.findById(UserId.of(1L))).thenReturn(Optional.of(testUser));
    when(userQueryRepository.findRolesByUserId(UserId.of(1L))).thenReturn(List.of(roleInfo));
    when(userAssembler.toDetailResponse(any(User.class), anyList())).thenReturn(detailResponse);

    // When
    UserDetailResponse result = userApplicationService.findById(1L);

    // Then
    assertNotNull(result);
    assertEquals(1L, result.id());
    verify(userRepository).findById(UserId.of(1L));
    verify(userQueryRepository).findRolesByUserId(UserId.of(1L));
  }

  @Test
  @DisplayName("查询用户详情时用户不存在应该抛出异常")
  void should_throw_exception_when_find_nonexistent_user() {
    // Given
    when(userRepository.findById(UserId.of(999L))).thenReturn(Optional.empty());

    // When & Then
    DomainException exception =
        assertThrows(DomainException.class, () -> userApplicationService.findById(999L));
    assertEquals(UserErrorCode.USER_NOT_FOUND.code(), exception.code());
  }

  @Test
  @DisplayName("应该成功分页查询用户")
  void should_find_users_by_page() {
    // Given
    UserQuery query = new UserQuery("test", "ENABLED", null);
    when(userQueryRepository.findPage(eq("test"), eq("ENABLED"), any(PageQuery.class)))
        .thenReturn(new Page<>(List.of(testUser), 1L, 1, 10));
    when(userAssembler.toResponse(testUser)).thenReturn(userResponse);

    // When
    PageResult<UserResponse> result = userApplicationService.findPage(query, 1, 10);

    // Then
    assertNotNull(result);
    assertEquals(1, result.items().size());
    assertEquals(1L, result.total());
    assertEquals(1, result.page());
    assertEquals(10, result.size());
  }

  @Test
  @DisplayName("应该成功修改密码")
  void should_change_password_when_old_password_matches() {
    // Given
    ChangePasswordCommand changePasswordCommand = new ChangePasswordCommand("oldpassword", "newpassword123");
    User userWithOldPassword =
        User.restore(
            UserId.of(1L),
            "testuser",
            "$2a$10$oldencodedpassword",
            "测试用户",
            null,
            null,
            null,
            null,
            UserStatus.ENABLED,
            1,
            null,
            null,
            LocalDateTime.now(),
            1);
    when(userRepository.findById(UserId.of(1L))).thenReturn(Optional.of(userWithOldPassword));
    when(passwordEncoder.matches("oldpassword", "$2a$10$oldencodedpassword")).thenReturn(true);
    when(passwordEncoder.encode("newpassword123")).thenReturn("$2a$10$newencodedpassword");

    // When
    userApplicationService.changePassword(1L, changePasswordCommand);

    // Then
    verify(userRepository).findById(UserId.of(1L));
    verify(passwordEncoder).matches("oldpassword", "$2a$10$oldencodedpassword");
    verify(passwordEncoder).encode("newpassword123");
    verify(userRepository).save(any(User.class));
  }

  @Test
  @DisplayName("修改密码时旧密码错误应该抛出异常")
  void should_throw_exception_when_old_password_mismatch() {
    // Given
    ChangePasswordCommand changePasswordCommand = new ChangePasswordCommand("wrongpassword", "newpassword123");
    User userWithOldPassword =
        User.restore(
            UserId.of(1L),
            "testuser",
            "$2a$10$oldencodedpassword",
            "测试用户",
            null,
            null,
            null,
            null,
            UserStatus.ENABLED,
            1,
            null,
            null,
            LocalDateTime.now(),
            1);
    when(userRepository.findById(UserId.of(1L))).thenReturn(Optional.of(userWithOldPassword));
    when(passwordEncoder.matches("wrongpassword", "$2a$10$oldencodedpassword")).thenReturn(false);

    // When & Then
    DomainException exception =
        assertThrows(
            DomainException.class,
            () -> userApplicationService.changePassword(1L, changePasswordCommand));
    assertEquals(UserErrorCode.USER_INVALID_PASSWORD.code(), exception.code());
    verify(userRepository, never()).save(any());
  }

  @Test
  @DisplayName("修改密码时用户不存在应该抛出异常")
  void should_throw_exception_when_change_password_for_nonexistent_user() {
    // Given
    ChangePasswordCommand changePasswordCommand = new ChangePasswordCommand("oldpassword", "newpassword123");
    when(userRepository.findById(UserId.of(999L))).thenReturn(Optional.empty());

    // When & Then
    DomainException exception =
        assertThrows(
            DomainException.class,
            () -> userApplicationService.changePassword(999L, changePasswordCommand));
    assertEquals(UserErrorCode.USER_NOT_FOUND.code(), exception.code());
  }

  // ==================== 员工绑定测试 ====================

  @Test
  @DisplayName("应该成功绑定员工")
  void should_bind_employee_successfully() {
    // Given
    when(userRepository.findById(UserId.of(1L))).thenReturn(Optional.of(testUser));
    when(employeeExistenceChecker.exists(100L)).thenReturn(true);
    when(userRepository.findByEmployeeId(100L)).thenReturn(Optional.empty());

    // When
    userApplicationService.bindEmployee(1L, new BindEmployeeCommand(100L));

    // Then
    verify(userRepository).findById(UserId.of(1L));
    verify(employeeExistenceChecker).exists(100L);
    verify(userRepository).save(any(User.class));
  }

  @Test
  @DisplayName("绑定员工时用户不存在应该抛出异常")
  void should_throw_exception_when_bind_employee_user_not_found() {
    // Given
    when(userRepository.findById(UserId.of(999L))).thenReturn(Optional.empty());

    // When & Then
    DomainException exception =
        assertThrows(
            DomainException.class,
            () -> userApplicationService.bindEmployee(999L, new BindEmployeeCommand(100L)));
    assertEquals(UserErrorCode.USER_NOT_FOUND.code(), exception.code());
  }

  @Test
  @DisplayName("绑定员工时员工不存在应该抛出异常")
  void should_throw_exception_when_employee_not_found() {
    // Given
    when(userRepository.findById(UserId.of(1L))).thenReturn(Optional.of(testUser));
    when(employeeExistenceChecker.exists(100L)).thenReturn(false);

    // When & Then
    DomainException exception =
        assertThrows(
            DomainException.class,
            () -> userApplicationService.bindEmployee(1L, new BindEmployeeCommand(100L)));
    assertEquals(UserErrorCode.EMPLOYEE_NOT_FOUND.code(), exception.code());
    verify(userRepository, never()).save(any());
  }

  @Test
  @DisplayName("绑定员工时员工已被其他用户绑定应该抛出异常")
  void should_throw_exception_when_employee_already_bound() {
    // Given
    User otherUser =
        User.restore(
            UserId.of(2L),
            "otheruser",
            "$2a$10$hash",
            "其他用户",
            null,
            null,
            null,
            100L,
            UserStatus.ENABLED,
            1,
            null,
            null,
            LocalDateTime.now(),
            1);
    when(userRepository.findById(UserId.of(1L))).thenReturn(Optional.of(testUser));
    when(employeeExistenceChecker.exists(100L)).thenReturn(true);
    when(userRepository.findByEmployeeId(100L)).thenReturn(Optional.of(otherUser));

    // When & Then
    DomainException exception =
        assertThrows(
            DomainException.class,
            () -> userApplicationService.bindEmployee(1L, new BindEmployeeCommand(100L)));
    assertEquals(UserErrorCode.USER_EMPLOYEE_ALREADY_BOUND.code(), exception.code());
    verify(userRepository, never()).save(any());
  }

  @Test
  @DisplayName("应该成功解绑员工")
  void should_unbind_employee_successfully() {
    // Given
    User boundUser =
        User.restore(
            UserId.of(1L),
            "testuser",
            "$2a$10$hashedpassword",
            "测试用户",
            null,
            null,
            null,
            100L,
            UserStatus.ENABLED,
            1,
            null,
            null,
            LocalDateTime.now(),
            1);
    when(userRepository.findById(UserId.of(1L))).thenReturn(Optional.of(boundUser));

    // When
    userApplicationService.unbindEmployee(1L);

    // Then
    verify(userRepository).findById(UserId.of(1L));
    verify(userRepository).save(any(User.class));
  }

  @Test
  @DisplayName("解绑员工时用户不存在应该抛出异常")
  void should_throw_exception_when_unbind_user_not_found() {
    // Given
    when(userRepository.findById(UserId.of(999L))).thenReturn(Optional.empty());

    // When & Then
    DomainException exception =
        assertThrows(DomainException.class, () -> userApplicationService.unbindEmployee(999L));
    assertEquals(UserErrorCode.USER_NOT_FOUND.code(), exception.code());
  }
}
