package io.github.lystrosaurus.admin.integration.identitylink.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.lystrosaurus.admin.integration.identitylink.application.assembler.IdentityLinkCandidateAssembler;
import io.github.lystrosaurus.admin.integration.identitylink.domain.acl.IdentityLinkCandidateRepository;
import io.github.lystrosaurus.admin.integration.identitylink.domain.model.IdentityLinkCandidate;
import io.github.lystrosaurus.admin.integration.identitylink.domain.model.IdentityLinkCandidateErrorCode;
import io.github.lystrosaurus.admin.integration.identitylink.domain.model.IdentityLinkCandidateId;
import io.github.lystrosaurus.admin.integration.principal.domain.acl.PrincipalLinkPort;
import io.github.lystrosaurus.admin.shared.domain.acl.IdGenerator;
import io.github.lystrosaurus.admin.shared.domain.exception.DomainException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** IdentityLinkCandidateApplicationService 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("身份关联候选人应用服务测试")
class IdentityLinkCandidateApplicationServiceTest {

  @Mock private IdentityLinkCandidateRepository candidateRepository;

  @Mock private PrincipalLinkPort principalLinkPort;

  @Mock private IdGenerator idGenerator;

  private IdentityLinkCandidateApplicationService identityLinkCandidateApplicationService;

  private IdentityLinkCandidate testCandidate;

  @BeforeEach
  void setUp() {
    identityLinkCandidateApplicationService =
        new IdentityLinkCandidateApplicationService(
            candidateRepository,
            principalLinkPort,
            idGenerator,
            new IdentityLinkCandidateAssembler());
    testCandidate =
        IdentityLinkCandidate.restore(
            IdentityLinkCandidateId.of(1L),
            100L,
            "USER",
            200L,
            85,
            "姓名匹配",
            "PENDING",
            null,
            null,
            null);
  }

  @Test
  @DisplayName("确认候选记录应该成功")
  void should_confirm_candidate_successfully() {
    when(candidateRepository.findById(IdentityLinkCandidateId.of(1L)))
        .thenReturn(Optional.of(testCandidate));

    identityLinkCandidateApplicationService.confirm(1L, "admin");

    verify(candidateRepository).save(testCandidate);
    verify(principalLinkPort).markManualLinked(100L, "USER", 200L);
    assertEquals("CONFIRMED", testCandidate.status());
    assertEquals("admin", testCandidate.handledBy());
  }

  @Test
  @DisplayName("确认不存在的候选记录应该抛出异常")
  void should_throw_exception_when_candidate_not_found() {
    when(candidateRepository.findById(IdentityLinkCandidateId.of(999L)))
        .thenReturn(Optional.empty());

    DomainException exception =
        assertThrows(
            DomainException.class,
            () -> identityLinkCandidateApplicationService.confirm(999L, "admin"));
    assertEquals(IdentityLinkCandidateErrorCode.CANDIDATE_NOT_FOUND.code(), exception.code());
  }

  @Test
  @DisplayName("确认已处理的候选记录应该抛出异常")
  void should_throw_exception_when_candidate_already_handled() {
    IdentityLinkCandidate handled =
        IdentityLinkCandidate.restore(
            IdentityLinkCandidateId.of(1L),
            100L,
            "USER",
            200L,
            85,
            "姓名匹配",
            "CONFIRMED",
            null,
            "admin",
            null);
    when(candidateRepository.findById(IdentityLinkCandidateId.of(1L)))
        .thenReturn(Optional.of(handled));

    DomainException exception =
        assertThrows(
            DomainException.class, () -> identityLinkCandidateApplicationService.confirm(1L, "admin"));
    assertEquals(
        IdentityLinkCandidateErrorCode.CANDIDATE_ALREADY_HANDLED.code(), exception.code());
  }
}
