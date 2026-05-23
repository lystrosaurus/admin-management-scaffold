package io.github.lystrosaurus.admin.integration.principal.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.integration.principal.dao.ExtPrincipalDAO;
import io.github.lystrosaurus.admin.integration.principal.dao.ExtPrincipalIdentifierDAO;
import io.github.lystrosaurus.admin.integration.principal.dto.ExtPrincipalCreateDTO;
import io.github.lystrosaurus.admin.integration.principal.dto.ExtPrincipalUpdateDTO;
import io.github.lystrosaurus.admin.integration.principal.entity.ExtPrincipal;
import io.github.lystrosaurus.admin.integration.principal.entity.ExtPrincipalIdentifier;
import io.github.lystrosaurus.admin.integration.principal.mapstruct.ExtPrincipalMapStruct;
import io.github.lystrosaurus.admin.integration.principal.vo.ExtPrincipalIdentifierVO;
import io.github.lystrosaurus.admin.integration.principal.vo.ExtPrincipalVO;
import io.github.lystrosaurus.admin.integration.source.dao.ExtSourceDAO;
import io.github.lystrosaurus.admin.integration.source.entity.ExtSource;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** ExtPrincipalServiceImpl 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ExtPrincipalServiceImpl 测试")
class ExtPrincipalServiceImplTest {

  @Mock private ExtPrincipalDAO extPrincipalDAO;

  @Mock private ExtPrincipalIdentifierDAO extPrincipalIdentifierDAO;

  @Mock private ExtSourceDAO extSourceDAO;

  @Mock private ExtPrincipalMapStruct extPrincipalMapStruct;

  @InjectMocks private ExtPrincipalServiceImpl extPrincipalService;

  private ExtPrincipal testPrincipal;
  private ExtPrincipalCreateDTO createDTO;
  private ExtPrincipalUpdateDTO updateDTO;
  private ExtPrincipalVO principalVO;
  private ExtSource testSource;

  @BeforeEach
  void setUp() {
    testPrincipal = new ExtPrincipal();
    testPrincipal.setId(1L);
    testPrincipal.setSourceId(1L);
    testPrincipal.setPrincipalType("USER");
    testPrincipal.setExternalKey("lark_user_001");
    testPrincipal.setDisplayName("张三");
    testPrincipal.setStatus("ACTIVE");
    testPrincipal.setRawPayloadJson("{\"name\":\"张三\"}");
    testPrincipal.setLastSyncAt(LocalDateTime.now());
    testPrincipal.setCanonicalType(null);
    testPrincipal.setCanonicalId(null);
    testPrincipal.setLinkStatus("UNLINKED");
    testPrincipal.setCreatedAt(LocalDateTime.now());
    testPrincipal.setCreatedBy("system");
    testPrincipal.setUpdatedAt(LocalDateTime.now());
    testPrincipal.setUpdatedBy("system");

    testSource = new ExtSource();
    testSource.setId(1L);
    testSource.setCode("LARK");
    testSource.setName("飞书");
    testSource.setSourceType("IM");
    testSource.setStatus("ENABLED");

    createDTO =
        new ExtPrincipalCreateDTO(
            1L,
            "USER",
            "lark_user_001",
            "张三",
            "{\"name\":\"张三\"}",
            Arrays.asList(
                new ExtPrincipalCreateDTO.IdentifierItem("lark_open_id", "open_001", true),
                new ExtPrincipalCreateDTO.IdentifierItem("lark_user_id", "user_001", false)));

    updateDTO = new ExtPrincipalUpdateDTO("张三丰", "ACTIVE", "EMPLOYEE", 100L, "MANUAL_LINKED");

    principalVO =
        new ExtPrincipalVO(
            1L,
            1L,
            "飞书",
            "USER",
            "lark_user_001",
            "张三",
            "ACTIVE",
            LocalDateTime.now(),
            null,
            null,
            "UNLINKED",
            LocalDateTime.now(),
            LocalDateTime.now(),
            Collections.emptyList());
  }

  @Test
  @DisplayName("应该成功创建外部主体（含标识符）")
  void should_create_principal_with_identifiers() {
    // Given
    when(extPrincipalDAO.existsBySourcePrincipalExternalKey(1L, "USER", "lark_user_001"))
        .thenReturn(false);
    when(extPrincipalMapStruct.toEntity(createDTO)).thenReturn(testPrincipal);
    doAnswer(
            invocation -> {
              ExtPrincipal saved = invocation.getArgument(0);
              saved.setId(1L);
              return null;
            })
        .when(extPrincipalDAO)
        .save(any(ExtPrincipal.class));
    when(extPrincipalIdentifierDAO.findByPrincipalId(1L)).thenReturn(Collections.emptyList());
    when(extPrincipalMapStruct.toIdentifierVOList(anyList())).thenReturn(Collections.emptyList());
    when(extSourceDAO.findById(1L)).thenReturn(testSource);
    when(extPrincipalMapStruct.toVO(any(ExtPrincipal.class))).thenReturn(principalVO);

    // When
    ExtPrincipalVO result = extPrincipalService.create(createDTO);

    // Then
    assertNotNull(result);
    assertEquals(1L, result.id());
    assertEquals("飞书", result.sourceName());
    verify(extPrincipalDAO).existsBySourcePrincipalExternalKey(1L, "USER", "lark_user_001");
    verify(extPrincipalDAO).save(any(ExtPrincipal.class));
    verify(extPrincipalIdentifierDAO).saveBatch(anyList());
  }

  @Test
  @DisplayName("创建外部主体时唯一性冲突应该抛出异常")
  void should_throw_exception_when_principal_already_exists() {
    // Given
    when(extPrincipalDAO.existsBySourcePrincipalExternalKey(1L, "USER", "lark_user_001"))
        .thenReturn(true);

    // When & Then
    BusinessException exception =
        assertThrows(BusinessException.class, () -> extPrincipalService.create(createDTO));
    assertEquals(ErrorCode.PRINCIPAL_ALREADY_EXISTS.getCode(), exception.getCode());
    verify(extPrincipalDAO).existsBySourcePrincipalExternalKey(1L, "USER", "lark_user_001");
    verify(extPrincipalDAO, never()).save(any());
  }

  @Test
  @DisplayName("应该成功更新外部主体")
  void should_update_principal_when_principal_exists() {
    // Given
    when(extPrincipalDAO.findById(1L)).thenReturn(testPrincipal);
    doNothing()
        .when(extPrincipalMapStruct)
        .updateEntity(any(ExtPrincipalUpdateDTO.class), any(ExtPrincipal.class));
    when(extPrincipalIdentifierDAO.findByPrincipalId(1L)).thenReturn(Collections.emptyList());
    when(extPrincipalMapStruct.toIdentifierVOList(anyList())).thenReturn(Collections.emptyList());
    when(extSourceDAO.findById(1L)).thenReturn(testSource);
    when(extPrincipalMapStruct.toVO(any(ExtPrincipal.class))).thenReturn(principalVO);

    // When
    ExtPrincipalVO result = extPrincipalService.update(1L, updateDTO);

    // Then
    assertNotNull(result);
    verify(extPrincipalDAO).findById(1L);
    verify(extPrincipalDAO).update(any(ExtPrincipal.class));
  }

  @Test
  @DisplayName("更新外部主体时不存在应该抛出异常")
  void should_throw_exception_when_update_nonexistent_principal() {
    // Given
    when(extPrincipalDAO.findById(999L)).thenReturn(null);

    // When & Then
    BusinessException exception =
        assertThrows(BusinessException.class, () -> extPrincipalService.update(999L, updateDTO));
    assertEquals(ErrorCode.PRINCIPAL_NOT_FOUND.getCode(), exception.getCode());
    verify(extPrincipalDAO).findById(999L);
    verify(extPrincipalDAO, never()).update(any());
  }

  @Test
  @DisplayName("应该成功删除外部主体（含标识符）")
  void should_delete_principal_with_identifiers() {
    // Given
    when(extPrincipalDAO.findById(1L)).thenReturn(testPrincipal);

    // When
    extPrincipalService.delete(1L);

    // Then
    verify(extPrincipalDAO).findById(1L);
    verify(extPrincipalIdentifierDAO).deleteByPrincipalId(1L);
    verify(extPrincipalDAO).deleteById(1L);
  }

  @Test
  @DisplayName("删除外部主体时不存在应该抛出异常")
  void should_throw_exception_when_delete_nonexistent_principal() {
    // Given
    when(extPrincipalDAO.findById(999L)).thenReturn(null);

    // When & Then
    BusinessException exception =
        assertThrows(BusinessException.class, () -> extPrincipalService.delete(999L));
    assertEquals(ErrorCode.PRINCIPAL_NOT_FOUND.getCode(), exception.getCode());
    verify(extPrincipalDAO).findById(999L);
    verify(extPrincipalIdentifierDAO, never()).deleteByPrincipalId(any());
    verify(extPrincipalDAO, never()).deleteById(any());
  }

  @Test
  @DisplayName("应该成功查询外部主体详情（含标识符）")
  void should_get_principal_by_id_with_identifiers() {
    // Given
    ExtPrincipalIdentifier identifier = new ExtPrincipalIdentifier();
    identifier.setId(1L);
    identifier.setPrincipalId(1L);
    identifier.setIdType("lark_open_id");
    identifier.setIdValue("open_001");
    identifier.setIsPrimary(1);

    ExtPrincipalIdentifierVO identifierVO =
        new ExtPrincipalIdentifierVO(1L, "lark_open_id", "open_001", true);

    when(extPrincipalDAO.findById(1L)).thenReturn(testPrincipal);
    when(extPrincipalIdentifierDAO.findByPrincipalId(1L)).thenReturn(Arrays.asList(identifier));
    when(extPrincipalMapStruct.toIdentifierVOList(anyList()))
        .thenReturn(Arrays.asList(identifierVO));
    when(extSourceDAO.findById(1L)).thenReturn(testSource);
    when(extPrincipalMapStruct.toVO(any(ExtPrincipal.class))).thenReturn(principalVO);

    // When
    ExtPrincipalVO result = extPrincipalService.getById(1L);

    // Then
    assertNotNull(result);
    assertEquals(1L, result.id());
    assertEquals("飞书", result.sourceName());
    verify(extPrincipalDAO).findById(1L);
    verify(extPrincipalIdentifierDAO).findByPrincipalId(1L);
  }

  @Test
  @DisplayName("查询外部主体详情时不存在应该抛出异常")
  void should_throw_exception_when_get_nonexistent_principal() {
    // Given
    when(extPrincipalDAO.findById(999L)).thenReturn(null);

    // When & Then
    BusinessException exception =
        assertThrows(BusinessException.class, () -> extPrincipalService.getById(999L));
    assertEquals(ErrorCode.PRINCIPAL_NOT_FOUND.getCode(), exception.getCode());
  }

  @Test
  @DisplayName("应该成功更新关联状态")
  void should_update_link_status() {
    // Given
    when(extPrincipalDAO.findById(1L)).thenReturn(testPrincipal);

    // When
    extPrincipalService.updateLinkStatus(1L, "EMPLOYEE", 100L, "MANUAL_LINKED");

    // Then
    verify(extPrincipalDAO).findById(1L);
    verify(extPrincipalDAO).update(any(ExtPrincipal.class));
    assertEquals("EMPLOYEE", testPrincipal.getCanonicalType());
    assertEquals(100L, testPrincipal.getCanonicalId());
    assertEquals("MANUAL_LINKED", testPrincipal.getLinkStatus());
  }

  @Test
  @DisplayName("更新关联状态时外部主体不存在应该抛出异常")
  void should_throw_exception_when_update_link_status_nonexistent_principal() {
    // Given
    when(extPrincipalDAO.findById(999L)).thenReturn(null);

    // When & Then
    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> extPrincipalService.updateLinkStatus(999L, "EMPLOYEE", 100L, "MANUAL_LINKED"));
    assertEquals(ErrorCode.PRINCIPAL_NOT_FOUND.getCode(), exception.getCode());
    verify(extPrincipalDAO).findById(999L);
    verify(extPrincipalDAO, never()).update(any());
  }
}
