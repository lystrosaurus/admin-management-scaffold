package io.github.lystrosaurus.admin.system.user.vo;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

/** UserVO 测试 */
class UserVOTest {

  @Test
  void should_create_user_vo_with_all_fields() {
    // Given
    LocalDateTime now = LocalDateTime.now();

    // When
    UserVO vo =
        new UserVO(
            1L, "testuser", "Test User", "13800138000", "test@example.com", "ENABLED", now, now);

    // Then
    assertEquals(1L, vo.id());
    assertEquals("testuser", vo.username());
    assertEquals("Test User", vo.nickname());
    assertEquals("13800138000", vo.phone());
    assertEquals("test@example.com", vo.email());
    assertEquals("ENABLED", vo.status());
    assertEquals(now, vo.lastLoginAt());
    assertEquals(now, vo.createdAt());
  }

  @Test
  void should_create_user_vo_with_null_fields() {
    // Given & When
    UserVO vo = new UserVO(null, null, null, null, null, null, null, null);

    // Then
    assertNull(vo.id());
    assertNull(vo.username());
    assertNull(vo.nickname());
    assertNull(vo.phone());
    assertNull(vo.email());
    assertNull(vo.status());
    assertNull(vo.lastLoginAt());
    assertNull(vo.createdAt());
  }
}
