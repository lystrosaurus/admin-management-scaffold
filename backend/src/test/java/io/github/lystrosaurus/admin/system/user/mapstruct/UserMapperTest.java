package io.github.lystrosaurus.admin.system.user.mapstruct;

import static org.junit.jupiter.api.Assertions.*;

import io.github.lystrosaurus.admin.system.user.dto.UserCreateDTO;
import io.github.lystrosaurus.admin.system.user.entity.SysUser;
import io.github.lystrosaurus.admin.system.user.vo.UserDetailVO;
import io.github.lystrosaurus.admin.system.user.vo.UserVO;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

/** UserMapper 测试 */
class UserMapperTest {

  private static final UserMapper mapper = Mappers.getMapper(UserMapper.class);

  @Test
  void should_convert_sys_user_to_user_vo() {
    // Given
    SysUser sysUser = new SysUser();
    sysUser.setId(1L);
    sysUser.setUsername("testuser");
    sysUser.setNickname("Test User");
    sysUser.setPhone("13800138000");
    sysUser.setEmail("test@example.com");
    sysUser.setStatus("ENABLED");
    sysUser.setLastLoginAt(LocalDateTime.now());
    sysUser.setCreatedAt(LocalDateTime.now());

    // When
    UserVO userVO = mapper.toUserVO(sysUser);

    // Then
    assertNotNull(userVO);
    assertEquals(1L, userVO.id());
    assertEquals("testuser", userVO.username());
    assertEquals("Test User", userVO.nickname());
    assertEquals("13800138000", userVO.phone());
    assertEquals("test@example.com", userVO.email());
    assertEquals("ENABLED", userVO.status());
    assertNotNull(userVO.lastLoginAt());
    assertNotNull(userVO.createdAt());
  }

  @Test
  void should_convert_sys_user_to_user_detail_vo() {
    // Given
    SysUser sysUser = new SysUser();
    sysUser.setId(1L);
    sysUser.setUsername("testuser");
    sysUser.setNickname("Test User");
    sysUser.setPhone("13800138000");
    sysUser.setEmail("test@example.com");
    sysUser.setStatus("ENABLED");
    sysUser.setEmployeeId(100L);
    sysUser.setTokenVersion(1);
    sysUser.setLastLoginAt(LocalDateTime.now());
    sysUser.setLastLoginIp("192.168.1.1");
    sysUser.setCreatedAt(LocalDateTime.now());

    // When
    UserDetailVO userDetailVO = mapper.toUserDetailVO(sysUser);

    // Then
    assertNotNull(userDetailVO);
    assertEquals(1L, userDetailVO.id());
    assertEquals("testuser", userDetailVO.username());
    assertEquals("Test User", userDetailVO.nickname());
    assertEquals("13800138000", userDetailVO.phone());
    assertEquals("test@example.com", userDetailVO.email());
    assertEquals("ENABLED", userDetailVO.status());
    assertEquals(100L, userDetailVO.employeeId());
    assertEquals(1, userDetailVO.tokenVersion());
    assertNotNull(userDetailVO.lastLoginAt());
    assertEquals("192.168.1.1", userDetailVO.lastLoginIp());
    assertNotNull(userDetailVO.createdAt());
    assertNull(userDetailVO.roles()); // 角色列表需要单独查询
  }

  @Test
  void should_convert_user_create_dto_to_sys_user() {
    // Given
    UserCreateDTO dto =
        new UserCreateDTO(
            "testuser", "password123", "Test User", "13800138000", "test@example.com");

    // When
    SysUser sysUser = mapper.toEntity(dto);

    // Then
    assertNotNull(sysUser);
    assertEquals("testuser", sysUser.getUsername());
    assertEquals("password123", sysUser.getPasswordHash());
    assertEquals("Test User", sysUser.getNickname());
    assertEquals("13800138000", sysUser.getPhone());
    assertEquals("test@example.com", sysUser.getEmail());
    assertNull(sysUser.getId()); // ID 应该被忽略
    assertNull(sysUser.getCreatedAt()); // 审计字段应该被忽略
    assertNull(sysUser.getCreatedBy());
    assertNull(sysUser.getUpdatedAt());
    assertNull(sysUser.getUpdatedBy());
    assertNull(sysUser.getDeleted());
    assertNull(sysUser.getVersion());
    assertNull(sysUser.getAvatarFileId());
    assertNull(sysUser.getEmployeeId());
    assertNull(sysUser.getTokenVersion());
    assertNull(sysUser.getLastLoginAt());
    assertNull(sysUser.getLastLoginIp());
  }
}
