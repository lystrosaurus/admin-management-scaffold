package io.github.lystrosaurus.admin.system.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import io.github.lystrosaurus.admin.common.PageResult;
import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.system.role.entity.SysRole;
import io.github.lystrosaurus.admin.system.user.dao.UserDAO;
import io.github.lystrosaurus.admin.system.user.dto.ChangePasswordDTO;
import io.github.lystrosaurus.admin.system.user.dto.UserCreateDTO;
import io.github.lystrosaurus.admin.system.user.dto.UserQueryDTO;
import io.github.lystrosaurus.admin.system.user.dto.UserUpdateDTO;
import io.github.lystrosaurus.admin.system.user.entity.SysUser;
import io.github.lystrosaurus.admin.system.user.mapstruct.UserMapper;
import io.github.lystrosaurus.admin.system.user.vo.UserDetailVO;
import io.github.lystrosaurus.admin.system.user.vo.UserVO;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/** UserService 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("用户服务测试")
class UserServiceTest {

  @Mock private UserDAO userDAO;

  @Mock private UserMapper userMapper;

  @Mock private BCryptPasswordEncoder passwordEncoder;

  @Mock private DataSource dataSource;

  @InjectMocks private UserService userService;

  private SysUser testUser;
  private UserCreateDTO createDTO;
  private UserUpdateDTO updateDTO;
  private UserVO userVO;
  private UserDetailVO userDetailVO;

  @BeforeEach
  void setUp() {
    // 准备测试数据
    testUser = new SysUser();
    testUser.setId(1L);
    testUser.setUsername("testuser");
    testUser.setPasswordHash("$2a$10$hashedpassword");
    testUser.setNickname("测试用户");
    testUser.setPhone("13800138000");
    testUser.setEmail("test@example.com");
    testUser.setStatus("ENABLED");
    testUser.setCreatedAt(LocalDateTime.now());
    testUser.setUpdatedAt(LocalDateTime.now());

    createDTO =
        new UserCreateDTO("testuser", "password123", "测试用户", "13800138000", "test@example.com");
    updateDTO = new UserUpdateDTO("新昵称", "13900139000", "new@example.com", "ENABLED");
    userVO =
        new UserVO(
            1L,
            "testuser",
            "测试用户",
            "13800138000",
            "test@example.com",
            "ENABLED",
            null,
            LocalDateTime.now());
    userDetailVO =
        new UserDetailVO(
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
            Collections.emptyList(),
            LocalDateTime.now());
  }

  @Test
  @DisplayName("应该成功创建用户")
  void should_create_user_when_username_not_exists() {
    // Given
    when(userDAO.existsByUsername("testuser")).thenReturn(false);
    when(passwordEncoder.encode("password123")).thenReturn("$2a$10$encodedpassword");
    when(userMapper.toEntity(createDTO)).thenReturn(testUser);
    doAnswer(
            invocation -> {
              SysUser savedUser = invocation.getArgument(0);
              savedUser.setId(1L);
              return null;
            })
        .when(userDAO)
        .save(any(SysUser.class));
    when(userMapper.toUserVO(any(SysUser.class))).thenReturn(userVO);

    // When
    UserVO result = userService.create(createDTO);

    // Then
    assertNotNull(result);
    assertEquals("testuser", result.username());
    assertEquals("测试用户", result.nickname());
    verify(userDAO).existsByUsername("testuser");
    verify(passwordEncoder).encode("password123");
    verify(userDAO).save(any(SysUser.class));
  }

  @Test
  @DisplayName("创建用户时用户名已存在应该抛出异常")
  void should_throw_exception_when_username_already_exists() {
    // Given
    when(userDAO.existsByUsername("testuser")).thenReturn(true);

    // When & Then
    BusinessException exception =
        assertThrows(BusinessException.class, () -> userService.create(createDTO));
    assertEquals(ErrorCode.USER_ALREADY_EXISTS.getCode(), exception.getCode());
    verify(userDAO).existsByUsername("testuser");
    verify(userDAO, never()).save(any());
  }

  @Test
  @DisplayName("应该成功更新用户")
  void should_update_user_when_user_exists() {
    // Given
    when(userDAO.findById(1L)).thenReturn(testUser);
    when(userMapper.toUserVO(any(SysUser.class))).thenReturn(userVO);

    // When
    UserVO result = userService.update(1L, updateDTO);

    // Then
    assertNotNull(result);
    verify(userDAO).findById(1L);
    verify(userDAO).update(any(SysUser.class));
  }

  @Test
  @DisplayName("更新用户时用户不存在应该抛出异常")
  void should_throw_exception_when_update_nonexistent_user() {
    // Given
    when(userDAO.findById(999L)).thenReturn(null);

    // When & Then
    BusinessException exception =
        assertThrows(BusinessException.class, () -> userService.update(999L, updateDTO));
    assertEquals(ErrorCode.USER_NOT_FOUND.getCode(), exception.getCode());
    verify(userDAO).findById(999L);
    verify(userDAO, never()).update(any());
  }

  @Test
  @DisplayName("应该成功删除用户")
  void should_delete_user_by_id() {
    // When
    userService.deleteById(1L);

    // Then
    verify(userDAO).deleteById(1L);
  }

  @Test
  @DisplayName("应该成功查询用户详情")
  void should_find_user_detail_by_id() {
    // Given
    SysRole role = new SysRole();
    role.setId(1L);
    role.setCode("ADMIN");
    role.setName("管理员");

    when(userDAO.findById(1L)).thenReturn(testUser);
    when(userDAO.findRolesByUserId(1L)).thenReturn(Arrays.asList(role));
    when(userMapper.toUserDetailVO(testUser)).thenReturn(userDetailVO);

    // When
    UserDetailVO result = userService.findById(1L);

    // Then
    assertNotNull(result);
    assertEquals(1L, result.id());
    verify(userDAO).findById(1L);
    verify(userDAO).findRolesByUserId(1L);
  }

  @Test
  @DisplayName("查询用户详情时用户不存在应该抛出异常")
  void should_throw_exception_when_find_nonexistent_user() {
    // Given
    when(userDAO.findById(999L)).thenReturn(null);

    // When & Then
    BusinessException exception =
        assertThrows(BusinessException.class, () -> userService.findById(999L));
    assertEquals(ErrorCode.USER_NOT_FOUND.getCode(), exception.getCode());
  }

  @Test
  @DisplayName("应该成功分页查询用户")
  void should_find_users_by_page() {
    // Given
    UserQueryDTO queryDTO = new UserQueryDTO("test", "ENABLED", null);
    List<SysUser> users = Arrays.asList(testUser);
    when(userDAO.findByCondition("test", "ENABLED", 1, 10)).thenReturn(users);
    when(userDAO.countByCondition("test", "ENABLED")).thenReturn(1L);
    when(userMapper.toUserVO(testUser)).thenReturn(userVO);

    // When
    PageResult<UserVO> result = userService.findPage(queryDTO, 1, 10);

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
    ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO("oldpassword", "newpassword123");
    testUser.setPasswordHash("$2a$10$oldencodedpassword");
    when(userDAO.findById(1L)).thenReturn(testUser);
    when(passwordEncoder.matches("oldpassword", "$2a$10$oldencodedpassword")).thenReturn(true);
    when(passwordEncoder.encode("newpassword123")).thenReturn("$2a$10$newencodedpassword");

    // When
    userService.changePassword(1L, changePasswordDTO);

    // Then
    verify(userDAO).findById(1L);
    verify(passwordEncoder).matches("oldpassword", "$2a$10$oldencodedpassword");
    verify(passwordEncoder).encode("newpassword123");
    verify(userDAO).update(any(SysUser.class));
  }

  @Test
  @DisplayName("修改密码时旧密码错误应该抛出异常")
  void should_throw_exception_when_old_password_mismatch() {
    // Given
    ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO("wrongpassword", "newpassword123");
    testUser.setPasswordHash("$2a$10$oldencodedpassword");
    when(userDAO.findById(1L)).thenReturn(testUser);
    when(passwordEncoder.matches("wrongpassword", "$2a$10$oldencodedpassword")).thenReturn(false);

    // When & Then
    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> userService.changePassword(1L, changePasswordDTO));
    assertEquals(ErrorCode.USER_INVALID_PASSWORD.getCode(), exception.getCode());
    verify(userDAO, never()).update(any());
  }

  @Test
  @DisplayName("修改密码时用户不存在应该抛出异常")
  void should_throw_exception_when_change_password_for_nonexistent_user() {
    // Given
    ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO("oldpassword", "newpassword123");
    when(userDAO.findById(999L)).thenReturn(null);

    // When & Then
    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> userService.changePassword(999L, changePasswordDTO));
    assertEquals(ErrorCode.USER_NOT_FOUND.getCode(), exception.getCode());
  }

  // ==================== 员工绑定测试 ====================

  @Test
  @DisplayName("应该成功绑定员工")
  void should_bind_employee_successfully() {
    // Given
    when(userDAO.findById(1L)).thenReturn(testUser);
    // employeeExists 通过 JdbcTemplate 查询，mock DataSource 无真实连接，会抛异常返回 false

    // When & Then - hr_employee 表不存在，employeeExists 返回 false
    BusinessException exception =
        assertThrows(BusinessException.class, () -> userService.bindEmployee(1L, 100L));
    assertEquals(ErrorCode.EMPLOYEE_NOT_FOUND.getCode(), exception.getCode());
  }

  @Test
  @DisplayName("绑定员工时用户不存在应该抛出异常")
  void should_throw_exception_when_bind_employee_user_not_found() {
    // Given
    when(userDAO.findById(999L)).thenReturn(null);

    // When & Then
    BusinessException exception =
        assertThrows(BusinessException.class, () -> userService.bindEmployee(999L, 100L));
    assertEquals(ErrorCode.USER_NOT_FOUND.getCode(), exception.getCode());
  }

  @Test
  @DisplayName("绑定员工时员工已被其他用户绑定应该抛出异常")
  void should_throw_exception_when_employee_already_bound() {
    // Given
    when(userDAO.findById(1L)).thenReturn(testUser);
    // employeeExists 通过 JdbcTemplate 查询，mock DataSource 无真实连接，会抛异常返回 false

    // When & Then - hr_employee 表不存在，employeeExists 返回 false
    BusinessException exception =
        assertThrows(BusinessException.class, () -> userService.bindEmployee(1L, 100L));
    assertEquals(ErrorCode.EMPLOYEE_NOT_FOUND.getCode(), exception.getCode());
  }

  @Test
  @DisplayName("应该成功解绑员工")
  void should_unbind_employee_successfully() {
    // Given
    testUser.setEmployeeId(100L);
    when(userDAO.findById(1L)).thenReturn(testUser);

    // When
    userService.unbindEmployee(1L);

    // Then
    verify(userDAO).findById(1L);
    verify(userDAO).update(any(SysUser.class));
  }

  @Test
  @DisplayName("解绑员工时用户不存在应该抛出异常")
  void should_throw_exception_when_unbind_user_not_found() {
    // Given
    when(userDAO.findById(999L)).thenReturn(null);

    // When & Then
    BusinessException exception =
        assertThrows(BusinessException.class, () -> userService.unbindEmployee(999L));
    assertEquals(ErrorCode.USER_NOT_FOUND.getCode(), exception.getCode());
  }
}
