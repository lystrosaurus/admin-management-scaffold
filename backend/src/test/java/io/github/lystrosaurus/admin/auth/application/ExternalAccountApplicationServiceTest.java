package io.github.lystrosaurus.admin.auth.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.lystrosaurus.admin.auth.domain.acl.ExternalAccountRepository;
import io.github.lystrosaurus.admin.auth.domain.acl.TokenSessionPort;
import io.github.lystrosaurus.admin.auth.domain.model.ExternalAccount;
import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.system.user.domain.acl.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** ExternalAccountApplicationService 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("三方账号服务测试")
class ExternalAccountApplicationServiceTest {

  /** 当前登录用户ID（与 SaTokenTest.TEST_USER_ID 一致） */
  private static final Long CURRENT_USER_ID = 1L;

  @Mock private ExternalAccountRepository accountRepository;

  @Mock private UserRepository userRepository;

  @Mock private TokenSessionPort tokenSessionPort;

  @InjectMocks private ExternalAccountApplicationService externalAccountApplicationService;

  @Test
  @DisplayName("应该成功获取当前用户自己的三方账号")
  void should_get_external_account_by_id() {
    ExternalAccount testAccount = account(1L, CURRENT_USER_ID);
    when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
    when(tokenSessionPort.currentUserId()).thenReturn(CURRENT_USER_ID);

    externalAccountApplicationService.getById(1L);

    verify(accountRepository).findById(1L);
  }

  @Test
  @DisplayName("获取三方账号时不存在应该抛出异常")
  void should_throw_exception_when_external_account_not_found() {
    when(accountRepository.findById(999L)).thenReturn(Optional.empty());

    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> externalAccountApplicationService.getById(999L));
    assertEquals(ErrorCode.EXTERNAL_ACCOUNT_NOT_FOUND.getCode(), exception.getCode());
  }

  @Test
  @DisplayName("获取他人三方账号应因归属校验失败而抛出与不存在相同的异常（IDOR 防护）")
  void should_throw_when_getting_other_users_account() {
    ExternalAccount otherAccount = account(2L, 999L); // 归属于其他用户
    when(accountRepository.findById(2L)).thenReturn(Optional.of(otherAccount));
    when(tokenSessionPort.currentUserId()).thenReturn(CURRENT_USER_ID);

    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> externalAccountApplicationService.getById(2L));
    assertEquals(ErrorCode.EXTERNAL_ACCOUNT_NOT_FOUND.getCode(), exception.getCode());
  }

  private ExternalAccount account(Long id, Long userId) {
    return new ExternalAccount(
        id, 1L, "provider-user", userId, null, null, null, null, "BOUND", null, null, null);
  }
}
