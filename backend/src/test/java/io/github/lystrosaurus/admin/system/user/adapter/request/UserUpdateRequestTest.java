package io.github.lystrosaurus.admin.system.user.adapter.request;

import static org.junit.jupiter.api.Assertions.*;

import io.github.lystrosaurus.admin.system.user.application.command.UserUpdateCommand;
import org.junit.jupiter.api.Test;

/** UserUpdateRequest 测试 */
class UserUpdateRequestTest {

  @Test
  void should_create_user_update_request_with_all_fields() {
    // Given & When
    UserUpdateRequest request =
        new UserUpdateRequest("Test User", "13800138000", "test@example.com", "ENABLED");

    // Then
    assertEquals("Test User", request.nickname());
    assertEquals("13800138000", request.phone());
    assertEquals("test@example.com", request.email());
    assertEquals("ENABLED", request.status());
  }

  @Test
  void should_create_user_update_request_with_null_fields() {
    // Given & When
    UserUpdateRequest request = new UserUpdateRequest(null, null, null, null);

    // Then
    assertNull(request.nickname());
    assertNull(request.phone());
    assertNull(request.email());
    assertNull(request.status());
  }

  @Test
  void should_convert_to_command() {
    // Given
    UserUpdateRequest request =
        new UserUpdateRequest("Test User", "13800138000", "test@example.com", "ENABLED");

    // When
    UserUpdateCommand command = request.toCommand();

    // Then
    assertEquals("Test User", command.nickname());
    assertEquals("13800138000", command.phone());
    assertEquals("test@example.com", command.email());
    assertEquals("ENABLED", command.status());
  }
}
