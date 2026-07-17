package io.github.lystrosaurus.admin.auth.external.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import cn.dev33.satoken.stp.StpUtil;
import io.github.lystrosaurus.admin.auth.external.dao.AuthExternalAccountDAO;
import io.github.lystrosaurus.admin.auth.external.entity.AuthExternalAccount;
import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.system.user.dao.UserDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

/** ExternalAccountService 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("三方账号服务测试")
class ExternalAccountServiceTest {

  /** 当前登录用户ID（与 SaTokenTest.TEST_USER_ID 一致） */
  private static final Long CURRENT_USER_ID = 1L;

  @Mock private AuthExternalAccountDAO accountDAO;

  @Mock
  private io.github.lystrosaurus.admin.auth.external.mapstruct.ExternalAccountMapStruct
      accountMapStruct;

  @Mock private UserDAO userDAO;

  @InjectMocks private ExternalAccountService externalAccountService;

  private MockedStatic<StpUtil> stpUtilMock;

  @BeforeEach
  void setUp() {
    // 模拟当前登录用户（与 SaTokenTest 相同的 StpUtil 静态打桩模式）
    stpUtilMock = mockStatic(StpUtil.class);
    stpUtilMock.when(StpUtil::isLogin).thenReturn(true);
    stpUtilMock.when(StpUtil::getLoginIdAsLong).thenReturn(CURRENT_USER_ID);
  }

  @AfterEach
  void tearDown() {
    if (stpUtilMock != null) {
      stpUtilMock.close();
    }
  }

  @Test
  @DisplayName("应该成功获取当前用户自己的三方账号")
  void should_get_external_account_by_id() {
    AuthExternalAccount testAccount = new AuthExternalAccount();
    testAccount.setId(1L);
    testAccount.setProviderId(1L);
    testAccount.setUserId(CURRENT_USER_ID);
    testAccount.setBindStatus("BOUND");
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

  @Test
  @DisplayName("获取他人三方账号应因归属校验失败而抛出与不存在相同的异常（IDOR 防护）")
  void should_throw_when_getting_other_users_account() {
    AuthExternalAccount otherAccount = new AuthExternalAccount();
    otherAccount.setId(2L);
    otherAccount.setProviderId(1L);
    otherAccount.setUserId(999L); // 归属于其他用户
    otherAccount.setBindStatus("BOUND");
    when(accountDAO.findById(2L)).thenReturn(otherAccount);

    BusinessException exception =
        assertThrows(BusinessException.class, () -> externalAccountService.getById(2L));
    assertEquals(ErrorCode.EXTERNAL_ACCOUNT_NOT_FOUND.getCode(), exception.getCode());
  }
}
