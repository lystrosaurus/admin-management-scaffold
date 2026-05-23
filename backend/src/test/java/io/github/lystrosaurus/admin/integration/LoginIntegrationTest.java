package io.github.lystrosaurus.admin.integration;

import static org.junit.jupiter.api.Assertions.*;

import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import io.github.lystrosaurus.admin.IntegrationTest;
import io.github.lystrosaurus.admin.auth.service.AuthService;
import io.github.lystrosaurus.admin.auth.vo.LoginVO;
import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.system.user.dao.UserDAO;
import io.github.lystrosaurus.admin.system.user.dto.LoginDTO;
import io.github.lystrosaurus.admin.system.user.entity.SysUser;
import javax.sql.DataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

/** 登录集成测试 — 测试完整的认证流程 */
@DisplayName("登录集成测试")
@Transactional
class LoginIntegrationTest extends IntegrationTest {

  @Autowired private AuthService authService;

  @Autowired private UserDAO userDAO;

  @Autowired private BCryptPasswordEncoder passwordEncoder;

  @Autowired private DataSource dataSource;

  private SysUser testUser;

  @BeforeEach
  void setUp() {
    // 初始化 Sa-Token mock 上下文（StpUtil 需要上下文才能执行 login）
    SaTokenContextMockUtil.setMockContext();

    // 使用 JDBC 直接清理残留数据（绕过 MyBatis-Plus @TableLogic 逻辑删除问题）
    JdbcTemplate jdbc = new JdbcTemplate(dataSource);
    jdbc.update(
        "DELETE FROM sys_user_role WHERE user_id IN (SELECT id FROM sys_user WHERE username = 'login_test_user')");
    jdbc.update("DELETE FROM sys_user WHERE username = 'login_test_user'");

    // 创建测试用户
    testUser = new SysUser();
    testUser.setUsername("login_test_user");
    testUser.setPasswordHash(passwordEncoder.encode("password123"));
    testUser.setNickname("登录测试用户");
    testUser.setStatus("ENABLED");
    userDAO.save(testUser);
  }

  @AfterEach
  void tearDown() {
    SaTokenContextMockUtil.clearContext();
  }

  @Test
  @DisplayName("应该成功登录当用户名和密码正确")
  void should_login_successfully_when_credentials_correct() {
    LoginDTO dto = new LoginDTO("login_test_user", "password123");

    LoginVO result = authService.login(dto);

    assertNotNull(result);
    assertNotNull(result.accessToken(), "Token 不应为空");
    assertEquals("login_test_user", result.user().username());
    assertEquals("登录测试用户", result.user().nickname());
    assertEquals("ENABLED", result.user().status());
  }

  @Test
  @DisplayName("应该返回用户信息当登录成功")
  void should_return_user_info_when_login_success() {
    LoginDTO dto = new LoginDTO("login_test_user", "password123");

    LoginVO result = authService.login(dto);

    assertNotNull(result.user());
    assertNotNull(result.user().id());
    assertEquals("login_test_user", result.user().username());
    assertNotNull(result.user().createdAt());
  }

  @Test
  @DisplayName("应该抛出 USER_NOT_FOUND 当用户名不存在")
  void should_throw_user_not_found_when_username_not_exist() {
    LoginDTO dto = new LoginDTO("nonexistent_user", "password123");

    BusinessException ex = assertThrows(BusinessException.class, () -> authService.login(dto));
    assertEquals(ErrorCode.USER_NOT_FOUND.getCode(), ex.getCode());
  }

  @Test
  @DisplayName("应该抛出 USER_INVALID_PASSWORD 当密码错误")
  void should_throw_invalid_password_when_password_wrong() {
    LoginDTO dto = new LoginDTO("login_test_user", "wrong_password");

    BusinessException ex = assertThrows(BusinessException.class, () -> authService.login(dto));
    assertEquals(ErrorCode.USER_INVALID_PASSWORD.getCode(), ex.getCode());
  }

  @Test
  @DisplayName("应该抛出 USER_ACCOUNT_DISABLED 当用户被禁用")
  void should_throw_disabled_when_user_disabled() {
    // 禁用用户
    testUser.setStatus("DISABLED");
    userDAO.update(testUser);

    LoginDTO dto = new LoginDTO("login_test_user", "password123");

    BusinessException ex = assertThrows(BusinessException.class, () -> authService.login(dto));
    assertEquals(ErrorCode.USER_ACCOUNT_DISABLED.getCode(), ex.getCode());
  }

  @Test
  @DisplayName("应该抛出 USER_ACCOUNT_DISABLED 当用户被锁定")
  void should_throw_disabled_when_user_locked() {
    // 锁定用户
    testUser.setStatus("LOCKED");
    userDAO.update(testUser);

    LoginDTO dto = new LoginDTO("login_test_user", "password123");

    BusinessException ex = assertThrows(BusinessException.class, () -> authService.login(dto));
    assertEquals(ErrorCode.USER_ACCOUNT_DISABLED.getCode(), ex.getCode());
  }

  @Test
  @DisplayName("应该使用 BCrypt 加密存储密码")
  void should_store_password_with_bcrypt_encryption() {
    SysUser storedUser = userDAO.findByUsername("login_test_user");
    assertNotNull(storedUser);

    // 密码应该是 BCrypt 格式（$2a$ 或 $2b$ 开头）
    String hash = storedUser.getPasswordHash();
    assertTrue(
        hash.startsWith("$2a$") || hash.startsWith("$2b$"),
        "密码应使用 BCrypt 加密，实际: " + hash.substring(0, 4));

    // 验证密码匹配
    assertTrue(passwordEncoder.matches("password123", hash));
    assertFalse(passwordEncoder.matches("wrong_password", hash));
  }

  @Test
  @DisplayName("应该更新最后登录时间当登录成功")
  void should_update_last_login_at_when_login_success() {
    LoginDTO dto = new LoginDTO("login_test_user", "password123");

    authService.login(dto);

    SysUser updated = userDAO.findByUsername("login_test_user");
    assertNotNull(updated.getLastLoginAt(), "最后登录时间不应为空");
  }
}
