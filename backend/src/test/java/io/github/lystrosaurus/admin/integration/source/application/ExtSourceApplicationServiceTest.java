package io.github.lystrosaurus.admin.integration.source.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.lystrosaurus.admin.integration.source.application.assembler.ExtSourceAssembler;
import io.github.lystrosaurus.admin.integration.source.adapter.response.ExtSourceResponse;
import io.github.lystrosaurus.admin.integration.source.domain.acl.ExtSourceRepository;
import io.github.lystrosaurus.admin.integration.source.domain.model.ExtSource;
import io.github.lystrosaurus.admin.integration.source.domain.model.ExtSourceErrorCode;
import io.github.lystrosaurus.admin.integration.source.domain.model.ExtSourceId;
import io.github.lystrosaurus.admin.shared.domain.acl.IdGenerator;
import io.github.lystrosaurus.admin.shared.domain.exception.DomainException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** ExtSourceApplicationService 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("外部身份源应用服务测试")
class ExtSourceApplicationServiceTest {

  @Mock private ExtSourceRepository extSourceRepository;

  @Mock private IdGenerator idGenerator;

  private ExtSourceApplicationService extSourceApplicationService;

  private ExtSource testSource;

  @BeforeEach
  void setUp() {
    extSourceApplicationService =
        new ExtSourceApplicationService(extSourceRepository, idGenerator, new ExtSourceAssembler());
    testSource =
        ExtSource.restore(
            ExtSourceId.of(1L),
            "LDAP001",
            "LDAP目录",
            "HR",
            null,
            "ENABLED",
            null,
            null,
            null,
            null);
  }

  @Test
  @DisplayName("应该成功获取身份源")
  void should_get_source_by_id() {
    when(extSourceRepository.findById(ExtSourceId.of(1L))).thenReturn(Optional.of(testSource));

    ExtSourceResponse result = extSourceApplicationService.findById(1L);

    assertNotNull(result);
    assertEquals(1L, result.id());
    verify(extSourceRepository).findById(ExtSourceId.of(1L));
  }

  @Test
  @DisplayName("获取不存在的身份源应该抛出异常")
  void should_throw_exception_when_source_not_found() {
    when(extSourceRepository.findById(ExtSourceId.of(999L))).thenReturn(Optional.empty());

    DomainException exception =
        assertThrows(DomainException.class, () -> extSourceApplicationService.findById(999L));
    assertEquals(ExtSourceErrorCode.SOURCE_NOT_FOUND.code(), exception.code());
  }
}
