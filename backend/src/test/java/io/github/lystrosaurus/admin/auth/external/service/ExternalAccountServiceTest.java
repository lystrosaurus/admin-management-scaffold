package io.github.lystrosaurus.admin.auth.external.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.lystrosaurus.admin.auth.external.dao.AuthExternalAccountDAO;
import io.github.lystrosaurus.admin.auth.external.entity.AuthExternalAccount;
import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.system.user.dao.UserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** ExternalAccountService 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("三方账号服务测试")
class ExternalAccountServiceTest {

  @Mock private AuthExternalAccountDAO accountDAO;

  @Mock
  private io.github.lystrosaurus.admin.auth.external.mapstruct.ExternalAccountMapStruct
      accountMapStruct;

  @Mock private UserDAO userDAO;

  @InjectMocks private ExternalAccountService externalAccountService;

  private AuthExternalAccount testAccount;

  @BeforeEach
  void setUp() {
    testAccount = new AuthExternalAccount();
    testAccount.setId(1L);
    testAccount.setProviderId(1L);
    testAccount.setUserId(1L);
    testAccount.setBindStatus("BOUND");
  }

  @Test
  @DisplayName("应该成功获取三方账号")
  void should_get_external_account_by_id() {
    when(accountDAO.findById(1L)).thenReturn(testAccount);

    externalAccountService.getById(1L);

    verify(accountDAO).findById(1L);
  }

  @Test
  @DisplayName("获取三方账号时不存在应该抛出异常")
  void should_throw_exception_when_external_account_not_found() {
    when(accountDAO.findById(999L)).thenReturn(null);

    BusinessException exception =
        assertThrows(BusinessException.class, () -> externalAccountService.getById(999L));
    assertEquals(ErrorCode.EXTERNAL_ACCOUNT_NOT_FOUND.getCode(), exception.getCode());
  }
}
