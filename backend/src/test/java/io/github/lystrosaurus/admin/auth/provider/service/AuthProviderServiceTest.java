package io.github.lystrosaurus.admin.auth.provider.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.lystrosaurus.admin.auth.provider.dao.AuthProviderDAO;
import io.github.lystrosaurus.admin.auth.provider.entity.AuthProvider;
import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** AuthProviderService 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("认证源服务测试")
class AuthProviderServiceTest {

  @Mock private AuthProviderDAO providerDAO;

  @Mock
  private io.github.lystrosaurus.admin.auth.provider.mapstruct.AuthProviderMapStruct
      providerMapStruct;

  @InjectMocks private AuthProviderService authProviderService;

  private AuthProvider testProvider;

  @BeforeEach
  void setUp() {
    testProvider = new AuthProvider();
    testProvider.setId(1L);
    testProvider.setCode("LARK");
    testProvider.setName("飞书");
    testProvider.setEnabled(1);
  }

  @Test
  @DisplayName("应该成功根据编码获取认证源")
  void should_get_provider_by_code() {
    when(providerDAO.findByCode("LARK")).thenReturn(testProvider);

    authProviderService.getByCode("LARK");

    verify(providerDAO).findByCode("LARK");
  }

  @Test
  @DisplayName("根据编码获取不存在的认证源应该抛出异常")
  void should_throw_exception_when_provider_not_found() {
    when(providerDAO.findByCode("UNKNOWN")).thenReturn(null);

    BusinessException exception =
        assertThrows(BusinessException.class, () -> authProviderService.getByCode("UNKNOWN"));
    assertEquals(ErrorCode.AUTH_PROVIDER_NOT_FOUND.getCode(), exception.getCode());
  }
}
