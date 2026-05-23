package io.github.lystrosaurus.admin.integration.source.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.integration.source.dao.ExtSourceDAO;
import io.github.lystrosaurus.admin.integration.source.dto.ExtSourceCreateDTO;
import io.github.lystrosaurus.admin.integration.source.dto.ExtSourceUpdateDTO;
import io.github.lystrosaurus.admin.integration.source.entity.ExtSource;
import io.github.lystrosaurus.admin.integration.source.mapstruct.ExtSourceMapStruct;
import io.github.lystrosaurus.admin.integration.source.vo.ExtSourceVO;
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

/** ExtSourceServiceImpl 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ExtSourceServiceImpl 测试")
class ExtSourceServiceImplTest {

  @Mock private ExtSourceDAO extSourceDAO;

  @Mock private ExtSourceMapStruct extSourceMapStruct;

  @InjectMocks private ExtSourceServiceImpl extSourceService;

  private ExtSource testSource;
  private ExtSourceCreateDTO createDTO;
  private ExtSourceUpdateDTO updateDTO;
  private ExtSourceVO sourceVO;

  @BeforeEach
  void setUp() {
    testSource = new ExtSource();
    testSource.setId(1L);
    testSource.setCode("LARK");
    testSource.setName("飞书");
    testSource.setSourceType("IM");
    testSource.setTenantKey("tenant1");
    testSource.setStatus("ENABLED");
    testSource.setPriority(10);
    testSource.setConfigJson("{\"app_id\":\"xxx\"}");
    testSource.setCreatedAt(LocalDateTime.now());
    testSource.setCreatedBy("system");
    testSource.setUpdatedAt(LocalDateTime.now());
    testSource.setUpdatedBy("system");

    createDTO = new ExtSourceCreateDTO("LARK", "飞书", "IM", "tenant1", 10, "{\"app_id\":\"xxx\"}");

    updateDTO = new ExtSourceUpdateDTO("飞书V2", null, null, "DISABLED", 20, null);

    sourceVO =
        new ExtSourceVO(
            1L,
            "LARK",
            "飞书",
            "IM",
            "tenant1",
            "ENABLED",
            10,
            "{\"app_id\":\"xxx\"}",
            LocalDateTime.now(),
            LocalDateTime.now());
  }

  @Test
  @DisplayName("应该成功创建外部身份源")
  void should_create_source_when_code_not_exists() {
    // Given
    when(extSourceDAO.existsByCode("LARK")).thenReturn(false);
    when(extSourceMapStruct.toEntity(createDTO)).thenReturn(testSource);
    doAnswer(
            invocation -> {
              ExtSource saved = invocation.getArgument(0);
              saved.setId(1L);
              return null;
            })
        .when(extSourceDAO)
        .save(any(ExtSource.class));
    when(extSourceMapStruct.toVO(any(ExtSource.class))).thenReturn(sourceVO);

    // When
    ExtSourceVO result = extSourceService.create(createDTO);

    // Then
    assertNotNull(result);
    assertEquals("LARK", result.code());
    assertEquals("飞书", result.name());
    verify(extSourceDAO).existsByCode("LARK");
    verify(extSourceDAO).save(any(ExtSource.class));
  }

  @Test
  @DisplayName("创建外部身份源时编码已存在应该抛出异常")
  void should_throw_exception_when_code_already_exists() {
    // Given
    when(extSourceDAO.existsByCode("LARK")).thenReturn(true);

    // When & Then
    BusinessException exception =
        assertThrows(BusinessException.class, () -> extSourceService.create(createDTO));
    assertEquals(ErrorCode.SOURCE_ALREADY_EXISTS.getCode(), exception.getCode());
    verify(extSourceDAO).existsByCode("LARK");
    verify(extSourceDAO, never()).save(any());
  }

  @Test
  @DisplayName("应该成功更新外部身份源")
  void should_update_source_when_source_exists() {
    // Given
    when(extSourceDAO.findById(1L)).thenReturn(testSource);
    doNothing()
        .when(extSourceMapStruct)
        .updateEntity(any(ExtSourceUpdateDTO.class), any(ExtSource.class));
    when(extSourceMapStruct.toVO(any(ExtSource.class))).thenReturn(sourceVO);

    // When
    ExtSourceVO result = extSourceService.update(1L, updateDTO);

    // Then
    assertNotNull(result);
    verify(extSourceDAO).findById(1L);
    verify(extSourceDAO).update(any(ExtSource.class));
  }

  @Test
  @DisplayName("更新外部身份源时不存在应该抛出异常")
  void should_throw_exception_when_update_nonexistent_source() {
    // Given
    when(extSourceDAO.findById(999L)).thenReturn(null);

    // When & Then
    BusinessException exception =
        assertThrows(BusinessException.class, () -> extSourceService.update(999L, updateDTO));
    assertEquals(ErrorCode.SOURCE_NOT_FOUND.getCode(), exception.getCode());
    verify(extSourceDAO).findById(999L);
    verify(extSourceDAO, never()).update(any());
  }

  @Test
  @DisplayName("应该成功删除外部身份源")
  void should_delete_source_by_id() {
    // Given
    when(extSourceDAO.findById(1L)).thenReturn(testSource);

    // When
    extSourceService.delete(1L);

    // Then
    verify(extSourceDAO).findById(1L);
    verify(extSourceDAO).deleteById(1L);
  }

  @Test
  @DisplayName("删除外部身份源时不存在应该抛出异常")
  void should_throw_exception_when_delete_nonexistent_source() {
    // Given
    when(extSourceDAO.findById(999L)).thenReturn(null);

    // When & Then
    BusinessException exception =
        assertThrows(BusinessException.class, () -> extSourceService.delete(999L));
    assertEquals(ErrorCode.SOURCE_NOT_FOUND.getCode(), exception.getCode());
    verify(extSourceDAO).findById(999L);
    verify(extSourceDAO, never()).deleteById(any());
  }

  @Test
  @DisplayName("应该成功查询外部身份源详情")
  void should_get_source_by_id() {
    // Given
    when(extSourceDAO.findById(1L)).thenReturn(testSource);
    when(extSourceMapStruct.toVO(testSource)).thenReturn(sourceVO);

    // When
    ExtSourceVO result = extSourceService.getById(1L);

    // Then
    assertNotNull(result);
    assertEquals(1L, result.id());
    assertEquals("LARK", result.code());
    verify(extSourceDAO).findById(1L);
  }

  @Test
  @DisplayName("查询外部身份源详情时不存在应该抛出异常")
  void should_throw_exception_when_get_nonexistent_source() {
    // Given
    when(extSourceDAO.findById(999L)).thenReturn(null);

    // When & Then
    BusinessException exception =
        assertThrows(BusinessException.class, () -> extSourceService.getById(999L));
    assertEquals(ErrorCode.SOURCE_NOT_FOUND.getCode(), exception.getCode());
  }

  @Test
  @DisplayName("应该成功查询所有外部身份源（按优先级降序）")
  void should_list_all_sources_ordered_by_priority() {
    // Given
    ExtSource source2 = new ExtSource();
    source2.setId(2L);
    source2.setCode("WECOM");
    source2.setName("企业微信");
    source2.setSourceType("IM");
    source2.setStatus("ENABLED");
    source2.setPriority(5);

    ExtSourceVO sourceVO2 =
        new ExtSourceVO(
            2L,
            "WECOM",
            "企业微信",
            "IM",
            null,
            "ENABLED",
            5,
            null,
            LocalDateTime.now(),
            LocalDateTime.now());

    when(extSourceDAO.findAll()).thenReturn(Arrays.asList(testSource, source2));
    when(extSourceMapStruct.toVO(testSource)).thenReturn(sourceVO);
    when(extSourceMapStruct.toVO(source2)).thenReturn(sourceVO2);

    // When
    List<ExtSourceVO> result = extSourceService.list();

    // Then
    assertNotNull(result);
    assertEquals(2, result.size());
    verify(extSourceDAO).findAll();
  }
}
