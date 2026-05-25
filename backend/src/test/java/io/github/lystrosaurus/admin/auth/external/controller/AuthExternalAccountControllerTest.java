package io.github.lystrosaurus.admin.auth.external.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.lystrosaurus.admin.auth.external.dto.ExternalAccountBindDTO;
import io.github.lystrosaurus.admin.auth.external.service.ExternalAccountService;
import io.github.lystrosaurus.admin.auth.external.vo.ExternalAccountVO;
import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.test.SaTokenTest;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

/** AuthExternalAccountController MVC 测试 */
@DisplayName("AuthExternalAccountController 测试")
class AuthExternalAccountControllerTest extends SaTokenTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private ExternalAccountService externalAccountService;

  private ExternalAccountVO externalAccountVO;

  @BeforeEach
  void setUp() {
    externalAccountVO =
        new ExternalAccountVO(
            1L,
            10L,
            "WECHAT",
            "wx_user_123",
            1L,
            null,
            "微信用户",
            "https://example.com/avatar.jpg",
            "BOUND",
            LocalDateTime.of(2025, 1, 1, 10, 0),
            LocalDateTime.of(2025, 1, 1, 9, 0),
            LocalDateTime.of(2025, 1, 1, 10, 0));
  }

  @Test
  @DisplayName("应该成功绑定三方账号")
  void should_bind_external_account_successfully() throws Exception {
    ExternalAccountBindDTO bindDTO =
        new ExternalAccountBindDTO(
            10L, "wx_user_123", 1L, null, "微信用户", "https://example.com/avatar.jpg", null);
    when(externalAccountService.bind(any(ExternalAccountBindDTO.class)))
        .thenReturn(externalAccountVO);

    mockMvc
        .perform(
            post("/app/auth/external-accounts/bind")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bindDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.providerCode").value("WECHAT"))
        .andExpect(jsonPath("$.data.providerUserId").value("wx_user_123"));
  }

  @Test
  @DisplayName("应该返回错误码当三方账号已绑定时")
  void should_return_error_when_already_bound() throws Exception {
    ExternalAccountBindDTO bindDTO =
        new ExternalAccountBindDTO(10L, "wx_user_123", 1L, null, "微信用户", null, null);
    when(externalAccountService.bind(any(ExternalAccountBindDTO.class)))
        .thenThrow(new BusinessException(ErrorCode.EXTERNAL_ACCOUNT_ALREADY_BOUND));

    mockMvc
        .perform(
            post("/app/auth/external-accounts/bind")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bindDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.EXTERNAL_ACCOUNT_ALREADY_BOUND.getCode()));
  }

  @Test
  @DisplayName("应该成功获取三方账号详情")
  void should_find_external_account_by_id_successfully() throws Exception {
    when(externalAccountService.getById(1L)).thenReturn(externalAccountVO);

    mockMvc
        .perform(get("/app/auth/external-accounts/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.providerCode").value("WECHAT"));
  }

  @Test
  @DisplayName("应该返回错误码当三方账号不存在时")
  void should_return_error_when_external_account_not_found() throws Exception {
    when(externalAccountService.getById(999L))
        .thenThrow(new BusinessException(ErrorCode.EXTERNAL_ACCOUNT_NOT_FOUND));

    mockMvc
        .perform(get("/app/auth/external-accounts/999"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.EXTERNAL_ACCOUNT_NOT_FOUND.getCode()));
  }

  @Test
  @DisplayName("应该成功解绑三方账号")
  void should_unbind_external_account_successfully() throws Exception {
    doNothing().when(externalAccountService).unbind(1L);

    mockMvc
        .perform(delete("/app/auth/external-accounts/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("应该成功按用户ID查询三方账号列表")
  void should_list_by_user_id_successfully() throws Exception {
    when(externalAccountService.listByUserId(1L)).thenReturn(List.of(externalAccountVO));

    mockMvc
        .perform(get("/app/auth/external-accounts/by-user/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data[0].userId").value(1));
  }

  @Test
  @DisplayName("应该成功按员工ID查询三方账号列表")
  void should_list_by_employee_id_successfully() throws Exception {
    when(externalAccountService.listByEmployeeId(201L)).thenReturn(List.of(externalAccountVO));

    mockMvc
        .perform(get("/app/auth/external-accounts/by-employee/201"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data[0].id").value(1));
  }
}
