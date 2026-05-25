package io.github.lystrosaurus.admin.auth.oauth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.lystrosaurus.admin.auth.oauth.OAuthClientFactory;
import io.github.lystrosaurus.admin.auth.oauth.state.OAuthStateService;
import io.github.lystrosaurus.admin.auth.provider.service.AuthProviderService;
import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** OAuthService 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("OAuth服务测试")
class OAuthServiceTest {

  @Mock private OAuthClientFactory clientFactory;

  @Mock private OAuthStateService stateService;

  @Mock private AuthProviderService providerService;

  @InjectMocks private OAuthService oAuthService;

  @Test
  @DisplayName("获取不存在的认证源应该抛出异常")
  void should_throw_exception_when_provider_not_found() {
    when(providerService.getEnabledByCode("LARK"))
        .thenThrow(new BusinessException(ErrorCode.OAUTH_PROVIDER_NOT_FOUND));

    BusinessException exception =
        assertThrows(BusinessException.class, () -> oAuthService.authorize("LARK"));
    assertEquals(9505, exception.getCode());
  }

  @Test
  @DisplayName("认证源未启用应该抛出异常")
  void should_throw_exception_when_provider_disabled() {
    when(providerService.getEnabledByCode("LARK"))
        .thenThrow(new BusinessException(ErrorCode.OAUTH_PROVIDER_DISABLED));

    BusinessException exception =
        assertThrows(BusinessException.class, () -> oAuthService.authorize("LARK"));
    assertEquals(9506, exception.getCode());
  }
}
