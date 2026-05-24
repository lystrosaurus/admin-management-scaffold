package io.github.lystrosaurus.admin.auth.external.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import io.github.lystrosaurus.admin.auth.external.dao.AuthExternalAccountDAO;
import io.github.lystrosaurus.admin.auth.external.dto.ExternalAccountBindDTO;
import io.github.lystrosaurus.admin.auth.external.entity.AuthExternalAccount;
import io.github.lystrosaurus.admin.auth.external.mapstruct.ExternalAccountMapStruct;
import io.github.lystrosaurus.admin.auth.external.vo.ExternalAccountVO;
import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.system.user.dao.UserDAO;
import io.github.lystrosaurus.admin.system.user.entity.SysUser;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** ExternalAccountServiceImpl 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ExternalAccountServiceImpl 测试")
class ExternalAccountServiceImplTest {

  @Mock private AuthExternalAccountDAO accountDAO;

  @Mock private ExternalAccountMapStruct accountMapStruct;

  @Mock private UserDAO userDAO;

  @InjectMocks private ExternalAccountServiceImpl accountService;

  private AuthExternalAccount testAccount;
  private ExternalAccountBindDTO bindDTO;
  private ExternalAccountVO accountVO;

  @BeforeEach
  void setUp() {
    testAccount = new AuthExternalAccount();
    testAccount.setId(1L);
    testAccount.setProviderId(10L);
    testAccount.setProviderUserId("github-user-123");
    testAccount.setUserId(100L);
    testAccount.setEmployeeId(200L);
    testAccount.setNickname("张三");
    testAccount.setAvatarUrl("https://avatar.url");
    testAccount.setIdentifierJson("{\"login\":\"zhangsan\"}");
    testAccount.setBindStatus("BOUND");
    testAccount.setCreatedAt(LocalDateTime.now());
    testAccount.setUpdatedAt(LocalDateTime.now());

    bindDTO =
        new ExternalAccountBindDTO(
            10L,
            "github-user-123",
            100L,
            200L,
            "张三",
            "https://avatar.url",
            "{\"login\":\"zhangsan\"}");

    accountVO =
        new ExternalAccountVO(
            1L,
            10L,
            null,
            "github-user-123",
            100L,
            200L,
            "张三",
            "https://avatar.url",
            "BOUND",
            null,
            LocalDateTime.now(),
            LocalDateTime.now());
  }

  @Test
  @DisplayName("应该成功绑定三方账号")
  void should_bind_external_account_when_not_exists() {
    // Given
    when(accountDAO.findByProviderIdAndProviderUserId(10L, "github-user-123")).thenReturn(null);
    when(accountDAO.findByProviderIdAndUserId(10L, 100L)).thenReturn(null);
    when(accountMapStruct.toEntity(bindDTO)).thenReturn(testAccount);
    doAnswer(
            invocation -> {
              AuthExternalAccount saved = invocation.getArgument(0);
              saved.setId(1L);
              return null;
            })
        .when(accountDAO)
        .save(any(AuthExternalAccount.class));
    when(accountMapStruct.toVO(any(AuthExternalAccount.class))).thenReturn(accountVO);

    // When
    ExternalAccountVO result = accountService.bind(bindDTO);

    // Then
    assertNotNull(result);
    assertEquals(1L, result.id());
    assertEquals("BOUND", result.bindStatus());
    verify(accountDAO).save(any(AuthExternalAccount.class));
  }

  @Test
  @DisplayName("绑定已存在的三方账号（providerId+providerUserId）应该抛出异常")
  void should_throw_exception_when_provider_user_already_bound() {
    // Given
    when(accountDAO.findByProviderIdAndProviderUserId(10L, "github-user-123"))
        .thenReturn(testAccount);

    // When & Then
    BusinessException exception =
        assertThrows(BusinessException.class, () -> accountService.bind(bindDTO));
    assertEquals(ErrorCode.EXTERNAL_ACCOUNT_ALREADY_BOUND.getCode(), exception.getCode());
    verify(accountDAO, never()).save(any());
  }

  @Test
  @DisplayName("绑定已存在的三方账号（providerId+userId）应该抛出异常")
  void should_throw_exception_when_user_already_bound_to_provider() {
    // Given
    when(accountDAO.findByProviderIdAndProviderUserId(10L, "github-user-123")).thenReturn(null);
    when(accountDAO.findByProviderIdAndUserId(10L, 100L)).thenReturn(testAccount);

    // When & Then
    BusinessException exception =
        assertThrows(BusinessException.class, () -> accountService.bind(bindDTO));
    assertEquals(ErrorCode.EXTERNAL_ACCOUNT_ALREADY_BOUND.getCode(), exception.getCode());
    verify(accountDAO, never()).save(any());
  }

  @Test
  @DisplayName("应该成功解绑三方账号")
  void should_unbind_external_account_when_exists() {
    // Given — 用户有密码且有2个绑定，允许解绑
    SysUser userWithPassword = new SysUser();
    userWithPassword.setPasswordHash("$2a$10$hashedPassword");
    when(accountDAO.findById(1L)).thenReturn(testAccount);
    when(userDAO.findById(100L)).thenReturn(userWithPassword);
    when(accountDAO.countActiveBindsByUserId(100L)).thenReturn(2L);

    // When
    accountService.unbind(1L);

    // Then
    assertEquals("UNBOUND", testAccount.getBindStatus());
    verify(accountDAO).updateById(testAccount);
  }

  @Test
  @DisplayName("解绑不存在的三方账号应该抛出异常")
  void should_throw_exception_when_unbind_nonexistent_account() {
    // Given
    when(accountDAO.findById(999L)).thenReturn(null);

    // When & Then
    BusinessException exception =
        assertThrows(BusinessException.class, () -> accountService.unbind(999L));
    assertEquals(ErrorCode.EXTERNAL_ACCOUNT_NOT_FOUND.getCode(), exception.getCode());
    verify(accountDAO, never()).updateById(any());
  }

  @Test
  @DisplayName("应该成功根据ID获取三方账号")
  void should_get_external_account_by_id() {
    // Given
    when(accountDAO.findById(1L)).thenReturn(testAccount);
    when(accountMapStruct.toVO(testAccount)).thenReturn(accountVO);

    // When
    ExternalAccountVO result = accountService.getById(1L);

    // Then
    assertNotNull(result);
    assertEquals(1L, result.id());
    assertEquals("BOUND", result.bindStatus());
  }

  @Test
  @DisplayName("获取不存在的三方账号应该抛出异常")
  void should_throw_exception_when_get_nonexistent_account() {
    // Given
    when(accountDAO.findById(999L)).thenReturn(null);

    // When & Then
    BusinessException exception =
        assertThrows(BusinessException.class, () -> accountService.getById(999L));
    assertEquals(ErrorCode.EXTERNAL_ACCOUNT_NOT_FOUND.getCode(), exception.getCode());
  }

  @Test
  @DisplayName("应该成功根据用户ID查询三方账号列表")
  void should_list_by_user_id() {
    // Given
    List<AuthExternalAccount> accounts = Arrays.asList(testAccount);
    when(accountDAO.listByUserId(100L)).thenReturn(accounts);
    when(accountMapStruct.toVO(testAccount)).thenReturn(accountVO);

    // When
    List<ExternalAccountVO> result = accountService.listByUserId(100L);

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());
    verify(accountDAO).listByUserId(100L);
  }

  @Test
  @DisplayName("应该成功根据员工ID查询三方账号列表")
  void should_list_by_employee_id() {
    // Given
    List<AuthExternalAccount> accounts = Arrays.asList(testAccount);
    when(accountDAO.listByEmployeeId(200L)).thenReturn(accounts);
    when(accountMapStruct.toVO(testAccount)).thenReturn(accountVO);

    // When
    List<ExternalAccountVO> result = accountService.listByEmployeeId(200L);

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());
    verify(accountDAO).listByEmployeeId(200L);
  }

  @Test
  @DisplayName("应该成功更新最后登录时间")
  void should_update_last_login_at() {
    // Given
    when(accountDAO.findById(1L)).thenReturn(testAccount);

    // When
    accountService.updateLastLoginAt(1L);

    // Then
    assertNotNull(testAccount.getLastLoginAt());
    verify(accountDAO).updateById(testAccount);
  }

  @Test
  @DisplayName("更新不存在的三方账号最后登录时间应该抛出异常")
  void should_throw_exception_when_update_last_login_nonexistent_account() {
    // Given
    when(accountDAO.findById(999L)).thenReturn(null);

    // When & Then
    BusinessException exception =
        assertThrows(BusinessException.class, () -> accountService.updateLastLoginAt(999L));
    assertEquals(ErrorCode.EXTERNAL_ACCOUNT_NOT_FOUND.getCode(), exception.getCode());
    verify(accountDAO, never()).updateById(any());
  }

  // ==================== unbind 安全检查测试 ====================

  @Test
  @DisplayName("无密码 + 仅剩 1 个绑定 → 拒绝解绑（抛 UNBIND_LAST_LOGIN_METHOD）")
  void should_reject_unbind_when_no_password_and_only_one_bind() {
    // Given — 用户无密码，仅剩1个绑定
    SysUser userNoPassword = new SysUser();
    userNoPassword.setPasswordHash(null);
    when(accountDAO.findById(1L)).thenReturn(testAccount);
    when(userDAO.findById(100L)).thenReturn(userNoPassword);
    when(accountDAO.countActiveBindsByUserId(100L)).thenReturn(1L);

    // When & Then
    BusinessException exception =
        assertThrows(BusinessException.class, () -> accountService.unbind(1L));
    assertEquals(ErrorCode.UNBIND_LAST_LOGIN_METHOD.getCode(), exception.getCode());
    verify(accountDAO, never()).updateById(any());
  }

  @Test
  @DisplayName("有密码 + 1 个绑定 → 允许解绑")
  void should_allow_unbind_when_has_password_and_one_bind() {
    // Given — 用户有密码，仅剩1个绑定
    SysUser userWithPassword = new SysUser();
    userWithPassword.setPasswordHash("$2a$10$hashedPassword");
    when(accountDAO.findById(1L)).thenReturn(testAccount);
    when(userDAO.findById(100L)).thenReturn(userWithPassword);
    when(accountDAO.countActiveBindsByUserId(100L)).thenReturn(1L);

    // When
    accountService.unbind(1L);

    // Then
    assertEquals("UNBOUND", testAccount.getBindStatus());
    verify(accountDAO).updateById(testAccount);
  }

  @Test
  @DisplayName("无密码 + 2 个绑定 → 允许解绑")
  void should_allow_unbind_when_no_password_and_multiple_binds() {
    // Given — 用户无密码，有2个绑定
    SysUser userNoPassword = new SysUser();
    userNoPassword.setPasswordHash(null);
    when(accountDAO.findById(1L)).thenReturn(testAccount);
    when(userDAO.findById(100L)).thenReturn(userNoPassword);
    when(accountDAO.countActiveBindsByUserId(100L)).thenReturn(2L);

    // When
    accountService.unbind(1L);

    // Then
    assertEquals("UNBOUND", testAccount.getBindStatus());
    verify(accountDAO).updateById(testAccount);
  }

  @Test
  @DisplayName("有密码 + 0 个绑定 → 允许解绑（防御性测试）")
  void should_allow_unbind_when_has_password_and_zero_binds() {
    // Given — 用户有密码，0个绑定（理论上不会出现，但防御性测试）
    SysUser userWithPassword = new SysUser();
    userWithPassword.setPasswordHash("$2a$10$hashedPassword");
    when(accountDAO.findById(1L)).thenReturn(testAccount);
    when(userDAO.findById(100L)).thenReturn(userWithPassword);
    when(accountDAO.countActiveBindsByUserId(100L)).thenReturn(0L);

    // When
    accountService.unbind(1L);

    // Then
    assertEquals("UNBOUND", testAccount.getBindStatus());
    verify(accountDAO).updateById(testAccount);
  }

  @Test
  @DisplayName("无密码 + 空密码字符串 → 拒绝解绑")
  void should_reject_unbind_when_blank_password_and_only_one_bind() {
    // Given — 用户密码为空字符串，仅剩1个绑定
    SysUser userBlankPassword = new SysUser();
    userBlankPassword.setPasswordHash("   ");
    when(accountDAO.findById(1L)).thenReturn(testAccount);
    when(userDAO.findById(100L)).thenReturn(userBlankPassword);
    when(accountDAO.countActiveBindsByUserId(100L)).thenReturn(1L);

    // When & Then
    BusinessException exception =
        assertThrows(BusinessException.class, () -> accountService.unbind(1L));
    assertEquals(ErrorCode.UNBIND_LAST_LOGIN_METHOD.getCode(), exception.getCode());
    verify(accountDAO, never()).updateById(any());
  }
}
