package io.github.lystrosaurus.admin.system.user.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import io.github.lystrosaurus.admin.shared.domain.exception.DomainException;
import io.github.lystrosaurus.admin.system.user.domain.event.UserEmployeeBound;
import io.github.lystrosaurus.admin.system.user.domain.event.UserEmployeeUnbound;
import io.github.lystrosaurus.admin.system.user.domain.event.UserPasswordChanged;
import io.github.lystrosaurus.admin.system.user.domain.event.UserRegistered;
import io.github.lystrosaurus.admin.system.user.domain.event.UserUpdated;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** User 聚合根单元测试 */
@DisplayName("User 聚合测试")
class UserTest {

  private User buildUser() {
    return User.restore(
        UserId.of(1L),
        "testuser",
        "$2a$10$hash",
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
  }

  @Test
  @DisplayName("注册新用户应初始化状态并登记 UserRegistered 事件")
  void should_register_user_with_defaults_and_event() {
    // When
    User user = User.register(UserId.of(1L), "newuser", "hash", "新用户", null, null);

    // Then
    assertEquals(UserId.of(1L), user.id());
    assertEquals("newuser", user.username());
    assertEquals(UserStatus.ENABLED, user.status());
    assertEquals(1, user.tokenVersion());
    assertEquals(1, user.pullEvents().size());
  }

  @Test
  @DisplayName("注册时非法手机号应抛出 DomainException(2010)")
  void should_throw_when_register_with_invalid_phone() {
    DomainException exception =
        assertThrows(
            DomainException.class,
            () -> User.register(UserId.of(1L), "newuser", "hash", "新用户", "abc", null));
    assertEquals(UserErrorCode.USER_PHONE_INVALID.code(), exception.code());
  }

  @Test
  @DisplayName("注册时非法邮箱应抛出 DomainException(2009)")
  void should_throw_when_register_with_invalid_email() {
    DomainException exception =
        assertThrows(
            DomainException.class,
            () -> User.register(UserId.of(1L), "newuser", "hash", "新用户", null, "not-an-email"));
    assertEquals(UserErrorCode.USER_EMAIL_INVALID.code(), exception.code());
  }

  @Test
  @DisplayName("重建聚合不应登记事件")
  void should_not_register_event_when_restore() {
    // When
    User user = buildUser();

    // Then
    assertTrue(user.pullEvents().isEmpty());
  }

  @Test
  @DisplayName("更新资料仅覆盖非空文本字段")
  void should_update_only_non_blank_fields() {
    // Given
    User user = buildUser();

    // When
    user.updateProfile("新昵称", "", null);

    // Then
    assertEquals("新昵称", user.nickname());
    assertEquals("13800138000", user.phone().value());
    assertEquals("test@example.com", user.email().value());
    assertEquals(1, user.pullEvents().size());
  }

  @Test
  @DisplayName("修改密码应登记事件且不递增 tokenVersion")
  void should_change_password_without_bumping_token_version() {
    // Given
    User user = buildUser();

    // When
    user.changePassword("$2a$10$newhash");

    // Then
    assertEquals("$2a$10$newhash", user.passwordHash());
    assertEquals(1, user.tokenVersion());
    assertTrue(user.pullEvents().stream().anyMatch(e -> e instanceof UserPasswordChanged));
  }

  @Test
  @DisplayName("禁用已禁用的用户应幂等不登记事件")
  void should_be_idempotent_when_disable_already_disabled() {
    // Given
    User user = buildUser();
    user.disable();
    user.pullEvents();

    // When
    user.disable();

    // Then
    assertEquals(UserStatus.DISABLED, user.status());
    assertTrue(user.pullEvents().isEmpty());
  }

  @Test
  @DisplayName("启用已启用的用户应幂等不登记事件")
  void should_be_idempotent_when_enable_already_enabled() {
    // Given
    User user = buildUser();

    // When
    user.enable();

    // Then
    assertEquals(UserStatus.ENABLED, user.status());
    assertTrue(user.pullEvents().isEmpty());
  }

  @Test
  @DisplayName("绑定相同员工应幂等不登记事件")
  void should_be_idempotent_when_bind_same_employee() {
    // Given
    User user = buildUser();
    user.bindEmployee(100L);
    user.pullEvents();

    // When
    user.bindEmployee(100L);

    // Then
    assertEquals(100L, user.employeeId());
    assertTrue(user.pullEvents().isEmpty());
  }

  @Test
  @DisplayName("绑定新员工应登记 UserEmployeeBound 事件")
  void should_register_event_when_bind_new_employee() {
    // Given
    User user = buildUser();

    // When
    user.bindEmployee(100L);

    // Then
    assertEquals(100L, user.employeeId());
    assertTrue(user.pullEvents().stream().anyMatch(e -> e instanceof UserEmployeeBound));
  }

  @Test
  @DisplayName("解绑员工应登记 UserEmployeeUnbound 事件")
  void should_register_event_when_unbind_employee() {
    // Given
    User user = buildUser();
    user.bindEmployee(100L);
    user.pullEvents();

    // When
    user.unbindEmployee();

    // Then
    assertNull(user.employeeId());
    assertTrue(user.pullEvents().stream().anyMatch(e -> e instanceof UserEmployeeUnbound));
  }

  @Test
  @DisplayName("未绑定员工时解绑不应登记事件")
  void should_not_register_event_when_unbind_without_employee() {
    // Given
    User user = buildUser();

    // When
    user.unbindEmployee();

    // Then
    assertTrue(user.pullEvents().isEmpty());
  }

  @Test
  @DisplayName("记录登录不应登记事件")
  void should_not_register_event_when_record_login() {
    // Given
    User user = buildUser();
    LocalDateTime now = LocalDateTime.now();

    // When
    user.recordLogin(now, "127.0.0.1");

    // Then
    assertEquals(now, user.lastLoginAt());
    assertEquals("127.0.0.1", user.lastLoginIp());
    assertTrue(user.pullEvents().isEmpty());
  }

  @Test
  @DisplayName("迁移状态应登记 UserUpdated 事件")
  void should_register_event_when_change_status() {
    // Given
    User user = buildUser();

    // When
    user.changeStatus(UserStatus.LOCKED);

    // Then
    assertEquals(UserStatus.LOCKED, user.status());
    assertTrue(user.pullEvents().stream().anyMatch(e -> e instanceof UserUpdated));
  }

  @Test
  @DisplayName("UserStatus.fromString 非法值应抛出 DomainException(2011)")
  void should_throw_when_status_invalid() {
    DomainException exception =
        assertThrows(DomainException.class, () -> UserStatus.fromString("INVALID"));
    assertEquals(UserErrorCode.USER_STATUS_INVALID.code(), exception.code());
  }

  @Test
  @DisplayName("UserStatus.fromString 应忽略大小写与空白")
  void should_parse_status_case_insensitively() {
    assertEquals(UserStatus.ENABLED, UserStatus.fromString(" enabled "));
    assertEquals(UserStatus.DISABLED, UserStatus.fromString("DISABLED"));
  }

  @Test
  @DisplayName("值对象允许 null 值")
  void should_allow_null_phone_and_email() {
    // Given
    User user = User.register(UserId.of(2L), "u2", "hash", "用户2", null, null);

    // Then
    assertNull(user.phone());
    assertNull(user.email());
  }
}
