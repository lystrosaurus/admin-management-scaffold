package io.github.lystrosaurus.admin.system.user.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/** UserCreateDTO 测试 */
class UserCreateDTOTest {

  @Test
  void should_create_user_create_dto_with_all_fields() {
    // Given & When
    UserCreateDTO dto =
        new UserCreateDTO(
            "testuser", "password123", "Test User", "13800138000", "test@example.com");

    // Then
    assertEquals("testuser", dto.username());
    assertEquals("password123", dto.password());
    assertEquals("Test User", dto.nickname());
    assertEquals("13800138000", dto.phone());
    assertEquals("test@example.com", dto.email());
  }

  @Test
  void should_create_user_create_dto_with_null_optional_fields() {
    // Given & When
    UserCreateDTO dto = new UserCreateDTO("testuser", "password123", null, null, null);

    // Then
    assertEquals("testuser", dto.username());
    assertEquals("password123", dto.password());
    assertNull(dto.nickname());
    assertNull(dto.phone());
    assertNull(dto.email());
  }

  @Test
  void should_implement_equals_and_hashcode() {
    // Given
    UserCreateDTO dto1 =
        new UserCreateDTO(
            "testuser", "password123", "Test User", "13800138000", "test@example.com");
    UserCreateDTO dto2 =
        new UserCreateDTO(
            "testuser", "password123", "Test User", "13800138000", "test@example.com");
    UserCreateDTO dto3 =
        new UserCreateDTO(
            "otheruser", "password123", "Other User", "13900139000", "other@example.com");

    // Then
    assertEquals(dto1, dto2);
    assertEquals(dto1.hashCode(), dto2.hashCode());
    assertNotEquals(dto1, dto3);
  }

  @Test
  void should_implement_tostring() {
    // Given
    UserCreateDTO dto =
        new UserCreateDTO(
            "testuser", "password123", "Test User", "13800138000", "test@example.com");

    // When
    String toString = dto.toString();

    // Then
    assertNotNull(toString);
    assertTrue(toString.contains("testuser"));
    assertTrue(toString.contains("Test User"));
  }
}
