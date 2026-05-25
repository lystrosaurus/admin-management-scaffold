package io.github.lystrosaurus.admin.integration.principal.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.integration.principal.dao.ExtPrincipalDAO;
import io.github.lystrosaurus.admin.integration.principal.dao.ExtPrincipalIdentifierDAO;
import io.github.lystrosaurus.admin.integration.principal.entity.ExtPrincipal;
import io.github.lystrosaurus.admin.integration.principal.mapstruct.ExtPrincipalMapStruct;
import io.github.lystrosaurus.admin.integration.principal.vo.ExtPrincipalVO;
import io.github.lystrosaurus.admin.integration.source.dao.ExtSourceDAO;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** ExtPrincipalService 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("外部主体服务测试")
class ExtPrincipalServiceTest {

  @Mock private ExtPrincipalDAO extPrincipalDAO;

  @Mock private ExtPrincipalIdentifierDAO extPrincipalIdentifierDAO;

  @Mock private ExtSourceDAO extSourceDAO;

  @Mock private ExtPrincipalMapStruct extPrincipalMapStruct;

  @InjectMocks private ExtPrincipalService extPrincipalService;

  private ExtPrincipal testPrincipal;
  private ExtPrincipalVO testPrincipalVO;

  @BeforeEach
  void setUp() {
    testPrincipal = new ExtPrincipal();
    testPrincipal.setId(1L);
    testPrincipal.setSourceId(1L);
    testPrincipal.setPrincipalType("USER");
    testPrincipal.setExternalKey("openid123");
    testPrincipal.setStatus("ACTIVE");
    testPrincipal.setLinkStatus("UNLINKED");

    testPrincipalVO =
        new ExtPrincipalVO(
            1L,
            1L,
            "飞书",
            "USER",
            "openid123",
            null,
            "ACTIVE",
            null,
            null,
            null,
            "UNLINKED",
            null,
            null,
            Collections.emptyList());
  }

  @Test
  @DisplayName("应该成功获取外部主体")
  void should_get_ext_principal_by_id() {
    when(extPrincipalDAO.findById(1L)).thenReturn(testPrincipal);
    when(extPrincipalIdentifierDAO.findByPrincipalId(1L)).thenReturn(Collections.emptyList());
    when(extSourceDAO.findById(1L)).thenReturn(null);
    when(extPrincipalMapStruct.toVO(testPrincipal)).thenReturn(testPrincipalVO);

    ExtPrincipalVO result = extPrincipalService.getById(1L);

    assertNotNull(result);
    assertEquals(1L, result.id());
    verify(extPrincipalDAO).findById(1L);
  }

  @Test
  @DisplayName("获取不存在的外部主体应该抛出异常")
  void should_throw_exception_when_principal_not_found() {
    when(extPrincipalDAO.findById(999L)).thenReturn(null);

    BusinessException exception =
        assertThrows(BusinessException.class, () -> extPrincipalService.getById(999L));
    assertEquals(ErrorCode.PRINCIPAL_NOT_FOUND.getCode(), exception.getCode());
  }
}
