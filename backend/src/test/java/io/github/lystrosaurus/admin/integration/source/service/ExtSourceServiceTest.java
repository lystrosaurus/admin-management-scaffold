package io.github.lystrosaurus.admin.integration.source.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.integration.source.dao.ExtSourceDAO;
import io.github.lystrosaurus.admin.integration.source.entity.ExtSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** ExtSourceService 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("外部身份源服务测试")
class ExtSourceServiceTest {

  @Mock private ExtSourceDAO sourceDAO;

  @Mock
  private io.github.lystrosaurus.admin.integration.source.mapstruct.ExtSourceMapStruct
      sourceMapStruct;

  @InjectMocks private ExtSourceService extSourceService;

  private ExtSource testSource;

  @BeforeEach
  void setUp() {
    testSource = new ExtSource();
    testSource.setId(1L);
    testSource.setCode("LDAP001");
    testSource.setName("LDAP目录");
    testSource.setSourceType("HR");
    testSource.setStatus("ENABLED");
  }

  @Test
  @DisplayName("应该成功获取身份源")
  void should_get_source_by_id() {
    when(sourceDAO.findById(1L)).thenReturn(testSource);

    extSourceService.getById(1L);

    verify(sourceDAO).findById(1L);
  }

  @Test
  @DisplayName("获取不存在的身份源应该抛出异常")
  void should_throw_exception_when_source_not_found() {
    when(sourceDAO.findById(999L)).thenReturn(null);

    BusinessException exception =
        assertThrows(BusinessException.class, () -> extSourceService.getById(999L));
    assertEquals(ErrorCode.SOURCE_NOT_FOUND.getCode(), exception.getCode());
  }
}
