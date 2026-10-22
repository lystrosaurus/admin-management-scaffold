package io.github.lystrosaurus.admin.integration.principal.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.lystrosaurus.admin.integration.principal.adapter.response.ExtPrincipalResponse;
import io.github.lystrosaurus.admin.integration.principal.application.assembler.ExtPrincipalAssembler;
import io.github.lystrosaurus.admin.integration.principal.domain.acl.ExtPrincipalRepository;
import io.github.lystrosaurus.admin.integration.principal.domain.model.ExtPrincipal;
import io.github.lystrosaurus.admin.integration.principal.domain.model.ExtPrincipalErrorCode;
import io.github.lystrosaurus.admin.integration.principal.domain.model.ExtPrincipalId;
import io.github.lystrosaurus.admin.integration.source.domain.acl.ExtSourceQueryPort;
import io.github.lystrosaurus.admin.shared.domain.acl.IdGenerator;
import io.github.lystrosaurus.admin.shared.domain.exception.DomainException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** ExtPrincipalApplicationService 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("外部主体应用服务测试")
class ExtPrincipalApplicationServiceTest {

  @Mock private ExtPrincipalRepository extPrincipalRepository;

  @Mock private ExtSourceQueryPort extSourceQueryPort;

  @Mock private IdGenerator idGenerator;

  private ExtPrincipalApplicationService extPrincipalApplicationService;

  private ExtPrincipal testPrincipal;

  @BeforeEach
  void setUp() {
    extPrincipalApplicationService =
        new ExtPrincipalApplicationService(
            extPrincipalRepository, extSourceQueryPort, idGenerator, new ExtPrincipalAssembler());
    testPrincipal =
        ExtPrincipal.restore(
            ExtPrincipalId.of(1L),
            1L,
            "USER",
            "openid123",
            null,
            "ACTIVE",
            null,
            null,
            null,
            null,
            "UNLINKED",
            null,
            null,
            List.of());
  }

  @Test
  @DisplayName("应该成功获取外部主体")
  void should_get_ext_principal_by_id() {
    when(extPrincipalRepository.findById(ExtPrincipalId.of(1L)))
        .thenReturn(Optional.of(testPrincipal));
    when(extSourceQueryPort.findNameById(1L)).thenReturn("飞书");

    ExtPrincipalResponse result = extPrincipalApplicationService.findById(1L);

    assertNotNull(result);
    assertEquals(1L, result.id());
    assertEquals("飞书", result.sourceName());
    verify(extPrincipalRepository).findById(ExtPrincipalId.of(1L));
  }

  @Test
  @DisplayName("获取不存在的外部主体应该抛出异常")
  void should_throw_exception_when_principal_not_found() {
    when(extPrincipalRepository.findById(ExtPrincipalId.of(999L))).thenReturn(Optional.empty());

    DomainException exception =
        assertThrows(DomainException.class, () -> extPrincipalApplicationService.findById(999L));
    assertEquals(ExtPrincipalErrorCode.PRINCIPAL_NOT_FOUND.code(), exception.code());
  }
}
