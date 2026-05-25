package io.github.lystrosaurus.admin.integration.identitylink.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.integration.identitylink.dao.IdentityLinkCandidateDAO;
import io.github.lystrosaurus.admin.integration.identitylink.entity.IdentityLinkCandidate;
import io.github.lystrosaurus.admin.integration.principal.dao.ExtPrincipalDAO;
import io.github.lystrosaurus.admin.integration.principal.entity.ExtPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** IdentityLinkCandidateService 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("身份关联候选人服务测试")
class IdentityLinkCandidateServiceTest {

  @Mock private IdentityLinkCandidateDAO candidateDAO;

  @Mock private ExtPrincipalDAO extPrincipalDAO;

  @Mock
  private io.github.lystrosaurus.admin.integration.identitylink.mapstruct
          .IdentityLinkCandidateMapStruct
      candidateMapStruct;

  @InjectMocks private IdentityLinkCandidateService identityLinkCandidateService;

  private IdentityLinkCandidate testCandidate;

  @BeforeEach
  void setUp() {
    testCandidate = new IdentityLinkCandidate();
    testCandidate.setId(1L);
    testCandidate.setSourcePrincipalId(1L);
    testCandidate.setCandidateType("USER");
    testCandidate.setScore(85);
    testCandidate.setStatus("PENDING");
  }

  @Test
  @DisplayName("确认候选记录应该成功")
  void should_confirm_candidate_successfully() {
    when(candidateDAO.findById(1L)).thenReturn(testCandidate);

    identityLinkCandidateService.confirm(1L, "admin");

    verify(candidateDAO).updateById(testCandidate);
    verify(extPrincipalDAO).update(any(ExtPrincipal.class));
  }

  @Test
  @DisplayName("确认不存在的候选记录应该抛出异常")
  void should_throw_exception_when_candidate_not_found() {
    when(candidateDAO.findById(999L)).thenReturn(null);

    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> identityLinkCandidateService.confirm(999L, "admin"));
    assertEquals(ErrorCode.CANDIDATE_NOT_FOUND.getCode(), exception.getCode());
  }

  @Test
  @DisplayName("确认已处理的候选记录应该抛出异常")
  void should_throw_exception_when_candidate_already_handled() {
    testCandidate.setStatus("CONFIRMED");
    when(candidateDAO.findById(1L)).thenReturn(testCandidate);

    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> identityLinkCandidateService.confirm(1L, "admin"));
    assertEquals(ErrorCode.CANDIDATE_ALREADY_HANDLED.getCode(), exception.getCode());
  }
}
