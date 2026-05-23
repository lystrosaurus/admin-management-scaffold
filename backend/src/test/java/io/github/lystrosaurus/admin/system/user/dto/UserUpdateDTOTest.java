package io.github.lystrosaurus.admin.system.user.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/** UserUpdateDTO 测试 */
class UserUpdateDTOTest {

  @Test
  void should_create_user_update_dto_with_all_fields() {
    // Given & When
    UserUpdateDTO dto =
        new UserUpdateDTO("Test User", "13800138000", "test@example.com", "ENABLED");

    // Then
    assertEquals("Test User", dto.nickname());
    assertEquals("13800138000", dto.phone());
    assertEquals("test@example.com", dto.email());
    assertEquals("ENABLED", dto.status());
  }

  @Test
  void should_create_user_update_dto_with_null_fields() {
    // Given & When
    UserUpdateDTO dto = new UserUpdateDTO(null, null, null, null);

    // Then
    assertNull(dto.nickname());
    assertNull(dto.phone());
    assertNull(dto.email());
    assertNull(dto.status());
  }
}
