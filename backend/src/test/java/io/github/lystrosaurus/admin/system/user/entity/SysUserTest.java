package io.github.lystrosaurus.admin.system.user.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

/** SysUser 实体测试 */
class SysUserTest {

  @Test
  void should_create_sys_user_with_all_fields() {
    // Given
    LocalDateTime now = LocalDateTime.now();

    // When
    SysUser user = new SysUser();
    user.setId(1L);
    user.setUsername("testuser");
    user.setPasswordHash("hashedpassword123");
    user.setNickname("Test User");
    user.setAvatarFileId(100L);
    user.setPhone("13800138000");
    user.setEmail("test@example.com");
    user.setEmployeeId(200L);
    user.setStatus("ENABLED");
    user.setTokenVersion(1);
    user.setLastLoginAt(now);
    user.setLastLoginIp("192.168.1.1");
    user.setCreatedAt(now);
    user.setCreatedBy("system");
    user.setUpdatedAt(now);
    user.setUpdatedBy("system");
    user.setDeleted(0);
    user.setVersion(1);

    // Then
    assertEquals(1L, user.getId());
    assertEquals("testuser", user.getUsername());
    assertEquals("hashedpassword123", user.getPasswordHash());
    assertEquals("Test User", user.getNickname());
    assertEquals(100L, user.getAvatarFileId());
    assertEquals("13800138000", user.getPhone());
    assertEquals("test@example.com", user.getEmail());
    assertEquals(200L, user.getEmployeeId());
    assertEquals("ENABLED", user.getStatus());
    assertEquals(1, user.getTokenVersion());
    assertEquals(now, user.getLastLoginAt());
    assertEquals("192.168.1.1", user.getLastLoginIp());
    assertEquals(now, user.getCreatedAt());
    assertEquals("system", user.getCreatedBy());
    assertEquals(now, user.getUpdatedAt());
    assertEquals("system", user.getUpdatedBy());
    assertEquals(0, user.getDeleted());
    assertEquals(1, user.getVersion());
  }

  @Test
  void should_set_and_get_individual_fields() {
    // Given
    SysUser user = new SysUser();

    // When & Then - 逐个字段测试
    user.setId(2L);
    assertEquals(2L, user.getId());

    user.setUsername("anotheruser");
    assertEquals("anotheruser", user.getUsername());

    user.setPasswordHash("newhashedpassword");
    assertEquals("newhashedpassword", user.getPasswordHash());

    user.setNickname("Another User");
    assertEquals("Another User", user.getNickname());

    user.setAvatarFileId(200L);
    assertEquals(200L, user.getAvatarFileId());

    user.setPhone("13900139000");
    assertEquals("13900139000", user.getPhone());

    user.setEmail("another@example.com");
    assertEquals("another@example.com", user.getEmail());

    user.setEmployeeId(300L);
    assertEquals(300L, user.getEmployeeId());

    user.setStatus("DISABLED");
    assertEquals("DISABLED", user.getStatus());

    user.setTokenVersion(2);
    assertEquals(2, user.getTokenVersion());

    user.setLastLoginAt(LocalDateTime.of(2026, 1, 1, 12, 0, 0));
    assertNotNull(user.getLastLoginAt());

    user.setLastLoginIp("10.0.0.1");
    assertEquals("10.0.0.1", user.getLastLoginIp());
  }

  @Test
  void should_inherit_base_entity_fields() {
    // Given
    SysUser user = new SysUser();
    LocalDateTime now = LocalDateTime.now();

    // When
    user.setCreatedAt(now);
    user.setCreatedBy("admin");
    user.setUpdatedAt(now);
    user.setUpdatedBy("admin");
    user.setDeleted(0);
    user.setVersion(1);

    // Then
    assertEquals(now, user.getCreatedAt());
    assertEquals("admin", user.getCreatedBy());
    assertEquals(now, user.getUpdatedAt());
    assertEquals("admin", user.getUpdatedBy());
    assertEquals(0, user.getDeleted());
    assertEquals(1, user.getVersion());
  }
}
