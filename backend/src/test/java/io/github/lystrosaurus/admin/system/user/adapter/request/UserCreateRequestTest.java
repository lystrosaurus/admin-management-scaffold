package io.github.lystrosaurus.admin.system.user.adapter.request;

import static org.junit.jupiter.api.Assertions.*;

import io.github.lystrosaurus.admin.system.user.application.command.UserCreateCommand;
import org.junit.jupiter.api.Test;

/** UserCreateRequest 测试 */
class UserCreateRequestTest {

  @Test
  void should_create_user_create_request_with_all_fields() {
    // Given & When
    UserCreateRequest request =
        new UserCreateRequest("testuser", "password123", "Test User", "13800138000", "test@example.com");

    // Then
    assertEquals("testuser", request.username());
    assertEquals("password123", request.password());
    assertEquals("Test User", request.nickname());
    assertEquals("13800138000", request.phone());
    assertEquals("test@example.com", request.email());
  }

  @Test
  void should_create_user_create_request_with_null_fields() {
    // Given & When
    UserCreateRequest request = new UserCreateRequest(null, null, null, null, null);

    // Then
    assertNull(request.username());
    assertNull(request.password());
    assertNull(request.nickname());
    assertNull(request.phone());
    assertNull(request.email());
  }

  @Test
  void should_convert_to_command() {
    // Given
    UserCreateRequest request =
        new UserCreateRequest("testuser", "password123", "Test User", "13800138000", "test@example.com");

    // When
    UserCreateCommand command = request.toCommand();

    // Then
    assertEquals("testuser", command.username());
    assertEquals("password123", command.password());
    assertEquals("Test User", command.nickname());
    assertEquals("13800138000", command.phone());
    assertEquals("test@example.com", command.email());
  }
}
