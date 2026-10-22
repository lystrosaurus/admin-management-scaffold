package io.github.lystrosaurus.admin.system.user.adapter.response;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

/** UserResponse 测试 */
class UserResponseTest {

  @Test
  void should_create_user_response_with_all_fields() {
    // Given
    LocalDateTime now = LocalDateTime.now();

    // When
    UserResponse response =
        new UserResponse(
            1L, "testuser", "Test User", "13800138000", "test@example.com", "ENABLED", now, now);

    // Then
    assertEquals(1L, response.id());
    assertEquals("testuser", response.username());
    assertEquals("Test User", response.nickname());
    assertEquals("13800138000", response.phone());
    assertEquals("test@example.com", response.email());
    assertEquals("ENABLED", response.status());
    assertEquals(now, response.lastLoginAt());
    assertEquals(now, response.createdAt());
  }

  @Test
  void should_create_user_response_with_null_fields() {
    // Given & When
    UserResponse response = new UserResponse(null, null, null, null, null, null, null, null);

    // Then
    assertNull(response.id());
    assertNull(response.username());
    assertNull(response.nickname());
    assertNull(response.phone());
    assertNull(response.email());
    assertNull(response.status());
    assertNull(response.lastLoginAt());
    assertNull(response.createdAt());
  }
}
