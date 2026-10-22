package io.github.lystrosaurus.admin.auth.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.lystrosaurus.admin.auth.domain.acl.AuthProviderRepository;
import io.github.lystrosaurus.admin.auth.domain.model.AuthProvider;
import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** AuthProviderApplicationService 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("认证源服务测试")
class AuthProviderApplicationServiceTest {

  @Mock private AuthProviderRepository providerRepository;

  @InjectMocks private AuthProviderApplicationService authProviderApplicationService;

  private AuthProvider testProvider;

  @BeforeEach
  void setUp() {
    testProvider =
        new AuthProvider(1L, "LARK", "飞书", null, null, null, null, 1, null, null, null);
  }

  @Test
  @DisplayName("应该成功根据编码获取认证源")
  void should_get_provider_by_code() {
    when(providerRepository.findByCode("LARK")).thenReturn(Optional.of(testProvider));

    authProviderApplicationService.getByCode("LARK");

    verify(providerRepository).findByCode("LARK");
  }

  @Test
  @DisplayName("根据编码获取不存在的认证源应该抛出异常")
  void should_throw_exception_when_provider_not_found() {
    when(providerRepository.findByCode("UNKNOWN")).thenReturn(Optional.empty());

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> authProviderApplicationService.getByCode("UNKNOWN"));
    assertEquals(ErrorCode.AUTH_PROVIDER_NOT_FOUND.getCode(), exception.getCode());
  }
}
