package io.github.lystrosaurus.admin.integration;

import static org.junit.jupiter.api.Assertions.*;

import io.github.lystrosaurus.admin.IntegrationTest;
import io.github.lystrosaurus.admin.common.PageResult;
import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.system.user.dao.UserDAO;
import io.github.lystrosaurus.admin.system.user.dto.UserCreateDTO;
import io.github.lystrosaurus.admin.system.user.dto.UserQueryDTO;
import io.github.lystrosaurus.admin.system.user.dto.UserUpdateDTO;
import io.github.lystrosaurus.admin.system.user.entity.SysUser;
import io.github.lystrosaurus.admin.system.user.service.UserService;
import io.github.lystrosaurus.admin.system.user.vo.UserVO;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

/** 用户 CRUD 集成测试 — 测试完整的用户增删改查流程 */
@DisplayName("用户 CRUD 集成测试")
@Transactional
class UserCrudIntegrationTest extends IntegrationTest {

  @Autowired private UserService userService;

  @Autowired private UserDAO userDAO;

  @Autowired private BCryptPasswordEncoder passwordEncoder;

  @Autowired private DataSource dataSource;

  @BeforeEach
  void cleanUp() {
    // 清理可能残留的测试数据
    JdbcTemplate jdbc = new JdbcTemplate(dataSource);
    jdbc.update(
        "DELETE FROM sys_user WHERE username LIKE 'crud\\\\_%' ESCAPE '\\\\' OR username LIKE 'dup\\\\_%' ESCAPE '\\\\' OR username LIKE 'detail\\\\_%' ESCAPE '\\\\' OR username LIKE 'update\\\\_%' ESCAPE '\\\\' OR username LIKE 'delete\\\\_%' ESCAPE '\\\\' OR username LIKE 'page\\\\_%' ESCAPE '\\\\' OR username LIKE 'bcrypt\\\\_%' ESCAPE '\\\\'");
  }

  // ==================== 创建用户 ====================

  @Test
  @DisplayName("应该成功创建用户")
  void should_create_user_successfully() {
    UserCreateDTO dto = new UserCreateDTO("crud_test_user", "pass123456", "CRUD测试", null, null);

    UserVO result = userService.create(dto);

    assertNotNull(result);
    assertNotNull(result.id());
    assertEquals("crud_test_user", result.username());
    assertEquals("CRUD测试", result.nickname());
  }

  @Test
  @DisplayName("应该抛出 USER_ALREADY_EXISTS 当用户名重复")
  void should_throw_already_exists_when_username_duplicate() {
    UserCreateDTO first = new UserCreateDTO("dup_user", "pass123456", null, null, null);
    userService.create(first);

    UserCreateDTO duplicate = new UserCreateDTO("dup_user", "pass123456", null, null, null);
    BusinessException ex =
        assertThrows(BusinessException.class, () -> userService.create(duplicate));
    assertEquals(ErrorCode.USER_ALREADY_EXISTS.getCode(), ex.getCode());
  }

  // ==================== 查询用户 ====================

  @Test
  @DisplayName("应该查询用户详情")
  void should_find_user_detail_by_id() {
    UserCreateDTO dto = new UserCreateDTO("detail_user", "pass123456", "详情测试", null, null);
    UserVO created = userService.create(dto);

    var detail = userService.findById(created.id());

    assertNotNull(detail);
    assertEquals("detail_user", detail.username());
    assertEquals("详情测试", detail.nickname());
  }

  @Test
  @DisplayName("应该抛出 USER_NOT_FOUND 当查询不存在的用户")
  void should_throw_not_found_when_user_not_exist() {
    BusinessException ex =
        assertThrows(BusinessException.class, () -> userService.findById(999999L));
    assertEquals(ErrorCode.USER_NOT_FOUND.getCode(), ex.getCode());
  }

  // ==================== 更新用户 ====================

  @Test
  @DisplayName("应该成功更新用户信息")
  void should_update_user_successfully() {
    UserCreateDTO createDto = new UserCreateDTO("update_user", "pass123456", null, null, null);
    UserVO created = userService.create(createDto);

    UserUpdateDTO updateDto = new UserUpdateDTO("更新昵称", "13900139000", "update@test.com", null);
    UserVO updated = userService.update(created.id(), updateDto);

    assertEquals("更新昵称", updated.nickname());
    assertEquals("13900139000", updated.phone());
    assertEquals("update@test.com", updated.email());
  }

  // ==================== 删除用户 ====================

  @Test
  @DisplayName("应该逻辑删除用户")
  void should_soft_delete_user() {
    UserCreateDTO dto = new UserCreateDTO("delete_user", "pass123456", null, null, null);
    UserVO created = userService.create(dto);

    userService.deleteById(created.id());

    // 逻辑删除后 DAO findById 应返回 null（@TableLogic）
    SysUser deleted = userDAO.findById(created.id());
    assertNull(deleted, "逻辑删除后不应查到该用户");
  }

  // ==================== 分页查询 ====================

  @Test
  @DisplayName("应该分页查询用户列表")
  void should_find_users_with_pagination() {
    // 创建多个测试用户
    for (int i = 1; i <= 5; i++) {
      userService.create(new UserCreateDTO("page_user_" + i, "pass123456", "分页测试" + i, null, null));
    }

    UserQueryDTO query = new UserQueryDTO("page_user_", null, null);
    PageResult<UserVO> page1 = userService.findPage(query, 1, 3);
    PageResult<UserVO> page2 = userService.findPage(query, 2, 3);

    assertTrue(page1.total() >= 5, "总记录数应 >= 5");
    assertEquals(3, page1.items().size(), "第一页应有 3 条记录");
    assertEquals(2, page2.items().size(), "第二页应有 2 条记录");
  }

  // ==================== 密码加密验证 ====================

  @Test
  @DisplayName("创建用户时密码应使用 BCrypt 加密")
  void should_encrypt_password_with_bcrypt_on_create() {
    UserCreateDTO dto = new UserCreateDTO("bcrypt_user", "plaintext_pwd", null, null, null);
    UserVO created = userService.create(dto);

    // 直接从数据库查询密码哈希
    JdbcTemplate jdbc = new JdbcTemplate(dataSource);
    String hash =
        jdbc.queryForObject(
            "SELECT password_hash FROM sys_user WHERE id = ? AND deleted = 0",
            String.class,
            created.id());

    assertNotNull(hash, "密码哈希不应为空");
    assertNotEquals("plaintext_pwd", hash, "密码不应明文存储");
    assertTrue(hash.startsWith("$2a$") || hash.startsWith("$2b$"), "密码应为 BCrypt 格式");
    assertTrue(passwordEncoder.matches("plaintext_pwd", hash), "BCrypt 应能验证原始密码");
  }
}
