package io.github.lystrosaurus.admin.integration.identitylink.adapter.web;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.lystrosaurus.admin.integration.identitylink.adapter.response.IdentityLinkCandidateResponse;
import io.github.lystrosaurus.admin.integration.identitylink.application.IdentityLinkCandidateApplicationService;
import io.github.lystrosaurus.admin.integration.identitylink.domain.model.IdentityLinkCandidateErrorCode;
import io.github.lystrosaurus.admin.shared.domain.exception.DomainException;
import io.github.lystrosaurus.admin.test.SaTokenTest;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/** IntegrationLinkCandidateController MVC 测试 */
@DisplayName("IntegrationLinkCandidateController 测试")
class IntegrationLinkCandidateControllerTest extends SaTokenTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private IdentityLinkCandidateApplicationService candidateApplicationService;

  private IdentityLinkCandidateResponse candidateResponse;

  @BeforeEach
  void setUp() {
    candidateResponse =
        new IdentityLinkCandidateResponse(
            1L,
            100L,
            "USER",
            200L,
            85,
            "姓名匹配",
            "PENDING",
            LocalDateTime.of(2025, 1, 1, 10, 0),
            null,
            null);
  }

  @Test
  @DisplayName("应该成功查询待处理候选列表")
  void should_list_pending_successfully() throws Exception {
    when(candidateApplicationService.listPending()).thenReturn(List.of(candidateResponse));

    mockMvc
        .perform(get("/app/integration/link-candidates/pending"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].sourcePrincipalId").value(100))
        .andExpect(jsonPath("$.data[0].status").value("PENDING"));
  }

  @Test
  @DisplayName("应该成功按外部主体ID查询候选列表")
  void should_list_by_principal_id_successfully() throws Exception {
    when(candidateApplicationService.listByPrincipalId(100L))
        .thenReturn(List.of(candidateResponse));

    mockMvc
        .perform(get("/app/integration/link-candidates/by-principal/100"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data[0].sourcePrincipalId").value(100));
  }

  @Test
  @DisplayName("应该成功确认候选记录")
  void should_confirm_candidate_successfully() throws Exception {
    doNothing().when(candidateApplicationService).confirm(eq(1L), nullable(String.class));

    mockMvc
        .perform(post("/app/integration/link-candidates/1/confirm"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("应该返回错误码当候选记录不存在时确认")
  void should_return_error_when_confirm_nonexistent_candidate() throws Exception {
    doThrow(new DomainException(IdentityLinkCandidateErrorCode.CANDIDATE_NOT_FOUND))
        .when(candidateApplicationService)
        .confirm(eq(999L), nullable(String.class));

    mockMvc
        .perform(post("/app/integration/link-candidates/999/confirm"))
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$.code").value(IdentityLinkCandidateErrorCode.CANDIDATE_NOT_FOUND.code()));
  }

  @Test
  @DisplayName("应该成功拒绝候选记录")
  void should_reject_candidate_successfully() throws Exception {
    doNothing().when(candidateApplicationService).reject(eq(1L), nullable(String.class));

    mockMvc
        .perform(post("/app/integration/link-candidates/1/reject"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("应该返回错误码当候选记录已处理时拒绝")
  void should_return_error_when_reject_already_handled() throws Exception {
    doThrow(new DomainException(IdentityLinkCandidateErrorCode.CANDIDATE_ALREADY_HANDLED))
        .when(candidateApplicationService)
        .reject(eq(1L), nullable(String.class));

    mockMvc
        .perform(post("/app/integration/link-candidates/1/reject"))
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$.code")
                .value(IdentityLinkCandidateErrorCode.CANDIDATE_ALREADY_HANDLED.code()));
  }
}
