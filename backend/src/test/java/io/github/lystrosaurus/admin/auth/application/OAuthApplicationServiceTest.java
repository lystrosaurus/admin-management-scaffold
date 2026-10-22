package io.github.lystrosaurus.admin.auth.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.lystrosaurus.admin.auth.domain.acl.AuthProviderCatalog;
import io.github.lystrosaurus.admin.auth.domain.acl.ExternalAccountRepository;
import io.github.lystrosaurus.admin.auth.domain.acl.OAuthClientCatalog;
import io.github.lystrosaurus.admin.auth.domain.acl.OAuthStatePort;
import io.github.lystrosaurus.admin.auth.domain.acl.TokenSessionPort;
import io.github.lystrosaurus.admin.auth.domain.model.AuthProvider;
import io.github.lystrosaurus.admin.auth.log.service.LoginLogService;
import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** OAuthApplicationService 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("OAuth服务测试")
class OAuthApplicationServiceTest {

  @Mock private OAuthClientCatalog clientCatalog;

  @Mock private OAuthStatePort statePort;

  @Mock private AuthProviderCatalog providerCatalog;

  @Mock private ExternalAccountRepository accountRepository;

  @Mock private ExternalAccountApplicationService externalAccountService;

  @Mock private TokenSessionPort tokenSessionPort;

  @Mock private LoginLogService loginLogService;

  @InjectMocks private OAuthApplicationService oAuthApplicationService;

  @Test
  @DisplayName("获取不存在的认证源应该抛出异常")
  void should_throw_exception_when_provider_not_found() {
    when(providerCatalog.findByCode("LARK")).thenReturn(Optional.empty());

    BusinessException exception =
        assertThrows(BusinessException.class, () -> oAuthApplicationService.authorize("LARK", null));
    assertEquals(9505, exception.getCode());
  }

  @Test
  @DisplayName("认证源未启用应该抛出异常")
  void should_throw_exception_when_provider_disabled() {
    when(providerCatalog.findByCode("LARK"))
        .thenReturn(
            Optional.of(
                new AuthProvider(1L, "LARK", "飞书", null, null, null, null, 0, null, null, null)));

    BusinessException exception =
        assertThrows(BusinessException.class, () -> oAuthApplicationService.authorize("LARK", null));
    assertEquals(9506, exception.getCode());
  }
}
