package io.github.lystrosaurus.admin.integration.identitylink.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.integration.identitylink.dao.IdentityLinkCandidateDAO;
import io.github.lystrosaurus.admin.integration.identitylink.dto.IdentityLinkCandidateCreateDTO;
import io.github.lystrosaurus.admin.integration.identitylink.entity.IdentityLinkCandidate;
import io.github.lystrosaurus.admin.integration.identitylink.mapstruct.IdentityLinkCandidateMapStruct;
import io.github.lystrosaurus.admin.integration.identitylink.vo.IdentityLinkCandidateVO;
import io.github.lystrosaurus.admin.integration.principal.dao.ExtPrincipalDAO;
import io.github.lystrosaurus.admin.integration.principal.entity.ExtPrincipal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** IdentityLinkCandidateServiceImpl 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("IdentityLinkCandidateServiceImpl 测试")
class IdentityLinkCandidateServiceImplTest {

  @Mock private IdentityLinkCandidateDAO candidateDAO;

  @Mock private ExtPrincipalDAO extPrincipalDAO;

  @Mock private IdentityLinkCandidateMapStruct candidateMapStruct;

  @InjectMocks private IdentityLinkCandidateServiceImpl candidateService;

  private IdentityLinkCandidate testCandidate;
  private IdentityLinkCandidateCreateDTO createDTO;
  private IdentityLinkCandidateVO candidateVO;

  @BeforeEach
  void setUp() {
    testCandidate = new IdentityLinkCandidate();
    testCandidate.setId(1L);
    testCandidate.setSourcePrincipalId(100L);
    testCandidate.setCandidateType("USER");
    testCandidate.setCandidateId(200L);
    testCandidate.setScore(85);
    testCandidate.setReason("姓名匹配");
    testCandidate.setStatus("PENDING");
    testCandidate.setCreatedAt(LocalDateTime.now());

    createDTO = new IdentityLinkCandidateCreateDTO(100L, "USER", 200L, 85, "姓名匹配");

    candidateVO =
        new IdentityLinkCandidateVO(
            1L, 100L, "USER", 200L, 85, "姓名匹配", "PENDING", LocalDateTime.now(), null, null);
  }

  @Test
  @DisplayName("应该成功创建候选记录")
  void should_create_candidate_when_valid_dto() {
    // Given
    when(candidateMapStruct.toEntity(createDTO)).thenReturn(testCandidate);
    doAnswer(
            invocation -> {
              IdentityLinkCandidate saved = invocation.getArgument(0);
              saved.setId(1L);
              return null;
            })
        .when(candidateDAO)
        .save(any(IdentityLinkCandidate.class));
    when(candidateMapStruct.toVO(any(IdentityLinkCandidate.class))).thenReturn(candidateVO);

    // When
    IdentityLinkCandidateVO result = candidateService.create(createDTO);

    // Then
    assertNotNull(result);
    assertEquals(1L, result.id());
    assertEquals("PENDING", result.status());
    verify(candidateDAO).save(any(IdentityLinkCandidate.class));
  }

  @Test
  @DisplayName("应该成功确认候选记录并更新外部主体")
  void should_confirm_candidate_and_update_principal() {
    // Given
    when(candidateDAO.findById(1L)).thenReturn(testCandidate);

    // When
    candidateService.confirm(1L, "admin");

    // Then
    // 验证候选记录状态更新
    ArgumentCaptor<IdentityLinkCandidate> candidateCaptor =
        ArgumentCaptor.forClass(IdentityLinkCandidate.class);
    verify(candidateDAO).updateById(candidateCaptor.capture());
    IdentityLinkCandidate updated = candidateCaptor.getValue();
    assertEquals("CONFIRMED", updated.getStatus());
    assertEquals("admin", updated.getHandledBy());
    assertNotNull(updated.getHandledAt());

    // 验证外部主体更新
    ArgumentCaptor<ExtPrincipal> principalCaptor = ArgumentCaptor.forClass(ExtPrincipal.class);
    verify(extPrincipalDAO).updateById(principalCaptor.capture());
    ExtPrincipal updatedPrincipal = principalCaptor.getValue();
    assertEquals(100L, updatedPrincipal.getId());
    assertEquals("MANUAL_LINKED", updatedPrincipal.getLinkStatus());
    assertEquals("USER", updatedPrincipal.getCanonicalType());
    assertEquals("200", updatedPrincipal.getCanonicalId());
  }

  @Test
  @DisplayName("确认不存在的候选记录应该抛出异常")
  void should_throw_exception_when_confirm_nonexistent_candidate() {
    // Given
    when(candidateDAO.findById(999L)).thenReturn(null);

    // When & Then
    BusinessException exception =
        assertThrows(BusinessException.class, () -> candidateService.confirm(999L, "admin"));
    assertEquals(ErrorCode.CANDIDATE_NOT_FOUND.getCode(), exception.getCode());
    verify(candidateDAO, never()).updateById(any());
    verify(extPrincipalDAO, never()).updateById(any());
  }

  @Test
  @DisplayName("确认已处理的候选记录应该抛出异常")
  void should_throw_exception_when_confirm_already_handled_candidate() {
    // Given
    testCandidate.setStatus("CONFIRMED");
    when(candidateDAO.findById(1L)).thenReturn(testCandidate);

    // When & Then
    BusinessException exception =
        assertThrows(BusinessException.class, () -> candidateService.confirm(1L, "admin"));
    assertEquals(ErrorCode.CANDIDATE_ALREADY_HANDLED.getCode(), exception.getCode());
    verify(candidateDAO, never()).updateById(any());
    verify(extPrincipalDAO, never()).updateById(any());
  }

  @Test
  @DisplayName("应该成功拒绝候选记录")
  void should_reject_candidate() {
    // Given
    when(candidateDAO.findById(1L)).thenReturn(testCandidate);

    // When
    candidateService.reject(1L, "admin");

    // Then
    ArgumentCaptor<IdentityLinkCandidate> captor =
        ArgumentCaptor.forClass(IdentityLinkCandidate.class);
    verify(candidateDAO).updateById(captor.capture());
    assertEquals("REJECTED", captor.getValue().getStatus());
    assertEquals("admin", captor.getValue().getHandledBy());
    assertNotNull(captor.getValue().getHandledAt());
  }

  @Test
  @DisplayName("拒绝已处理的候选记录应该抛出异常")
  void should_throw_exception_when_reject_already_handled_candidate() {
    // Given
    testCandidate.setStatus("REJECTED");
    when(candidateDAO.findById(1L)).thenReturn(testCandidate);

    // When & Then
    BusinessException exception =
        assertThrows(BusinessException.class, () -> candidateService.reject(1L, "admin"));
    assertEquals(ErrorCode.CANDIDATE_ALREADY_HANDLED.getCode(), exception.getCode());
  }

  @Test
  @DisplayName("应该成功查询待处理的候选记录列表")
  void should_list_pending_candidates() {
    // Given
    List<IdentityLinkCandidate> candidates = Arrays.asList(testCandidate);
    when(candidateDAO.listPending()).thenReturn(candidates);
    when(candidateMapStruct.toVO(testCandidate)).thenReturn(candidateVO);

    // When
    List<IdentityLinkCandidateVO> result = candidateService.listPending();

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("PENDING", result.get(0).status());
    verify(candidateDAO).listPending();
  }

  @Test
  @DisplayName("应该成功根据外部主体ID查询候选记录列表")
  void should_list_candidates_by_principal_id() {
    // Given
    List<IdentityLinkCandidate> candidates = Arrays.asList(testCandidate);
    when(candidateDAO.listByPrincipalId(100L)).thenReturn(candidates);
    when(candidateMapStruct.toVO(testCandidate)).thenReturn(candidateVO);

    // When
    List<IdentityLinkCandidateVO> result = candidateService.listByPrincipalId(100L);

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(100L, result.get(0).sourcePrincipalId());
    verify(candidateDAO).listByPrincipalId(100L);
  }
}
