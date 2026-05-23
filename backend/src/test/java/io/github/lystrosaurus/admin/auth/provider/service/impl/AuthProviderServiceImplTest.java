package io.github.lystrosaurus.admin.auth.provider.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import io.github.lystrosaurus.admin.auth.provider.dao.AuthProviderDAO;
import io.github.lystrosaurus.admin.auth.provider.dto.AuthProviderCreateDTO;
import io.github.lystrosaurus.admin.auth.provider.dto.AuthProviderUpdateDTO;
import io.github.lystrosaurus.admin.auth.provider.entity.AuthProvider;
import io.github.lystrosaurus.admin.auth.provider.mapstruct.AuthProviderMapStruct;
import io.github.lystrosaurus.admin.auth.provider.vo.AuthProviderVO;
import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
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

/** AuthProviderServiceImpl 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthProviderServiceImpl 测试")
class AuthProviderServiceImplTest {

  @Mock private AuthProviderDAO providerDAO;

  @Mock private AuthProviderMapStruct providerMapStruct;

  @InjectMocks private AuthProviderServiceImpl providerService;

  private AuthProvider testProvider;
  private AuthProviderCreateDTO createDTO;
  private AuthProviderUpdateDTO updateDTO;
  private AuthProviderVO providerVO;

  @BeforeEach
  void setUp() {
    testProvider = new AuthProvider();
    testProvider.setId(1L);
    testProvider.setCode("github");
    testProvider.setName("GitHub");
    testProvider.setClientId("client-id-123");
    testProvider.setClientSecretEncrypted("encrypted-secret");
    testProvider.setRedirectUri("https://example.com/callback");
    testProvider.setScopes("user:email");
    testProvider.setEnabled(1);
    testProvider.setConfigJson("{\"timeout\":5000}");
    testProvider.setCreatedAt(LocalDateTime.now());
    testProvider.setUpdatedAt(LocalDateTime.now());

    createDTO =
        new AuthProviderCreateDTO(
            "github",
            "GitHub",
            "client-id-123",
            "encrypted-secret",
            "https://example.com/callback",
            "user:email",
            1,
            "{\"timeout\":5000}");

    updateDTO =
        new AuthProviderUpdateDTO(
            "GitHub Updated", null, null, "https://new-callback.com", null, 0, null);

    providerVO =
        new AuthProviderVO(
            1L,
            "github",
            "GitHub",
            "client-id-123",
            "https://example.com/callback",
            "user:email",
            1,
            "{\"timeout\":5000}",
            LocalDateTime.now(),
            LocalDateTime.now());
  }

  @Test
  @DisplayName("应该成功创建认证源")
  void should_create_auth_provider_when_code_not_exists() {
    // Given
    when(providerDAO.findByCode("github")).thenReturn(null);
    when(providerMapStruct.toEntity(createDTO)).thenReturn(testProvider);
    doAnswer(
            invocation -> {
              AuthProvider saved = invocation.getArgument(0);
              saved.setId(1L);
              return null;
            })
        .when(providerDAO)
        .save(any(AuthProvider.class));
    when(providerMapStruct.toVO(any(AuthProvider.class))).thenReturn(providerVO);

    // When
    AuthProviderVO result = providerService.create(createDTO);

    // Then
    assertNotNull(result);
    assertEquals("github", result.code());
    assertEquals("GitHub", result.name());
    verify(providerDAO).findByCode("github");
    verify(providerDAO).save(any(AuthProvider.class));
  }

  @Test
  @DisplayName("创建认证源时编码已存在应该抛出异常")
  void should_throw_exception_when_code_already_exists() {
    // Given
    when(providerDAO.findByCode("github")).thenReturn(testProvider);

    // When & Then
    BusinessException exception =
        assertThrows(BusinessException.class, () -> providerService.create(createDTO));
    assertEquals(ErrorCode.AUTH_PROVIDER_ALREADY_EXISTS.getCode(), exception.getCode());
    verify(providerDAO, never()).save(any());
  }

  @Test
  @DisplayName("应该成功更新认证源")
  void should_update_auth_provider_when_exists() {
    // Given
    when(providerDAO.findById(1L)).thenReturn(testProvider);
    when(providerMapStruct.toVO(any(AuthProvider.class))).thenReturn(providerVO);

    // When
    AuthProviderVO result = providerService.update(1L, updateDTO);

    // Then
    assertNotNull(result);
    verify(providerDAO).findById(1L);
    verify(providerMapStruct).updateEntity(eq(updateDTO), any(AuthProvider.class));
    verify(providerDAO).updateById(any(AuthProvider.class));
  }

  @Test
  @DisplayName("更新不存在的认证源应该抛出异常")
  void should_throw_exception_when_update_nonexistent_provider() {
    // Given
    when(providerDAO.findById(999L)).thenReturn(null);

    // When & Then
    BusinessException exception =
        assertThrows(BusinessException.class, () -> providerService.update(999L, updateDTO));
    assertEquals(ErrorCode.AUTH_PROVIDER_NOT_FOUND.getCode(), exception.getCode());
    verify(providerDAO, never()).updateById(any());
  }

  @Test
  @DisplayName("应该成功删除认证源")
  void should_delete_auth_provider_when_exists() {
    // Given
    when(providerDAO.findById(1L)).thenReturn(testProvider);

    // When
    providerService.delete(1L);

    // Then
    verify(providerDAO).findById(1L);
    verify(providerDAO).deleteById(1L);
  }

  @Test
  @DisplayName("删除不存在的认证源应该抛出异常")
  void should_throw_exception_when_delete_nonexistent_provider() {
    // Given
    when(providerDAO.findById(999L)).thenReturn(null);

    // When & Then
    BusinessException exception =
        assertThrows(BusinessException.class, () -> providerService.delete(999L));
    assertEquals(ErrorCode.AUTH_PROVIDER_NOT_FOUND.getCode(), exception.getCode());
    verify(providerDAO, never()).deleteById(any());
  }

  @Test
  @DisplayName("应该成功根据ID获取认证源")
  void should_get_auth_provider_by_id() {
    // Given
    when(providerDAO.findById(1L)).thenReturn(testProvider);
    when(providerMapStruct.toVO(testProvider)).thenReturn(providerVO);

    // When
    AuthProviderVO result = providerService.getById(1L);

    // Then
    assertNotNull(result);
    assertEquals("github", result.code());
    // 验证VO不包含clientSecretEncrypted（MapStruct已排除）
    verify(providerMapStruct).toVO(testProvider);
  }

  @Test
  @DisplayName("获取不存在的认证源应该抛出异常")
  void should_throw_exception_when_get_nonexistent_provider() {
    // Given
    when(providerDAO.findById(999L)).thenReturn(null);

    // When & Then
    BusinessException exception =
        assertThrows(BusinessException.class, () -> providerService.getById(999L));
    assertEquals(ErrorCode.AUTH_PROVIDER_NOT_FOUND.getCode(), exception.getCode());
  }

  @Test
  @DisplayName("应该成功查询所有认证源列表")
  void should_list_all_auth_providers() {
    // Given
    List<AuthProvider> providers = Arrays.asList(testProvider);
    when(providerDAO.listAll()).thenReturn(providers);
    when(providerMapStruct.toVO(testProvider)).thenReturn(providerVO);

    // When
    List<AuthProviderVO> result = providerService.list();

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());
    verify(providerDAO).listAll();
  }

  @Test
  @DisplayName("应该成功根据编码获取认证源")
  void should_get_auth_provider_by_code() {
    // Given
    when(providerDAO.findByCode("github")).thenReturn(testProvider);
    when(providerMapStruct.toVO(testProvider)).thenReturn(providerVO);

    // When
    AuthProviderVO result = providerService.getByCode("github");

    // Then
    assertNotNull(result);
    assertEquals("github", result.code());
  }

  @Test
  @DisplayName("获取不存在的编码认证源应该抛出异常")
  void should_throw_exception_when_get_by_nonexistent_code() {
    // Given
    when(providerDAO.findByCode("nonexistent")).thenReturn(null);

    // When & Then
    BusinessException exception =
        assertThrows(BusinessException.class, () -> providerService.getByCode("nonexistent"));
    assertEquals(ErrorCode.AUTH_PROVIDER_NOT_FOUND.getCode(), exception.getCode());
  }
}
