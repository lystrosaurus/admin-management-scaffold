package io.github.lystrosaurus.admin.integration.source.adapter.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.lystrosaurus.admin.integration.source.adapter.request.ExtSourceCreateRequest;
import io.github.lystrosaurus.admin.integration.source.adapter.request.ExtSourceUpdateRequest;
import io.github.lystrosaurus.admin.integration.source.adapter.response.ExtSourceResponse;
import io.github.lystrosaurus.admin.integration.source.application.ExtSourceApplicationService;
import io.github.lystrosaurus.admin.integration.source.application.command.ExtSourceCreateCommand;
import io.github.lystrosaurus.admin.integration.source.application.command.ExtSourceUpdateCommand;
import io.github.lystrosaurus.admin.integration.source.domain.model.ExtSourceErrorCode;
import io.github.lystrosaurus.admin.shared.domain.exception.DomainException;
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

/**
 * IntegrationSourceController 测试
 *
 * <p>测试外部身份源控制器的各个接口
 */
@DisplayName("IntegrationSourceController 测试")
class IntegrationSourceControllerTest extends SaTokenTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private ExtSourceApplicationService extSourceApplicationService;

  private ExtSourceResponse extSourceResponse;

  @BeforeEach
  void setUp() {
    extSourceResponse =
        new ExtSourceResponse(
            1L,
            "FEISHU",
            "飞书",
            "IM",
            "tenant_001",
            "ENABLED",
            100,
            "{\"appId\":\"cli_xxx\"}",
            LocalDateTime.of(2026, 1, 1, 10, 0, 0),
            LocalDateTime.of(2026, 1, 2, 10, 0, 0));
  }

  @Test
  @DisplayName("应该成功创建外部身份源")
  void should_create_ext_source_successfully() throws Exception {
    ExtSourceCreateRequest createRequest =
        new ExtSourceCreateRequest(
            "FEISHU", "飞书", "IM", "tenant_001", 100, "{\"appId\":\"cli_xxx\"}");
    when(extSourceApplicationService.create(any(ExtSourceCreateCommand.class)))
        .thenReturn(extSourceResponse);

    mockMvc
        .perform(
            post("/app/integration/sources")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.message").value("操作成功"))
        .andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.code").value("FEISHU"))
        .andExpect(jsonPath("$.data.name").value("飞书"))
        .andExpect(jsonPath("$.data.sourceType").value("IM"));
  }

  @Test
  @DisplayName("应该成功获取外部身份源详情")
  void should_get_ext_source_by_id_successfully() throws Exception {
    when(extSourceApplicationService.findById(1L)).thenReturn(extSourceResponse);

    mockMvc
        .perform(get("/app/integration/sources/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.code").value("FEISHU"))
        .andExpect(jsonPath("$.data.name").value("飞书"));
  }

  @Test
  @DisplayName("应该返回错误码当外部身份源不存在")
  void should_return_error_when_ext_source_not_found() throws Exception {
    when(extSourceApplicationService.findById(999L))
        .thenThrow(new DomainException(ExtSourceErrorCode.SOURCE_NOT_FOUND));

    mockMvc
        .perform(get("/app/integration/sources/999"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ExtSourceErrorCode.SOURCE_NOT_FOUND.code()));
  }

  @Test
  @DisplayName("应该成功更新外部身份源")
  void should_update_ext_source_successfully() throws Exception {
    ExtSourceUpdateRequest updateRequest =
        new ExtSourceUpdateRequest("飞书办公", "IM", "tenant_001", "ENABLED", 200, null);
    ExtSourceResponse updatedResponse =
        new ExtSourceResponse(
            1L,
            "FEISHU",
            "飞书办公",
            "IM",
            "tenant_001",
            "ENABLED",
            200,
            null,
            LocalDateTime.of(2026, 1, 1, 10, 0, 0),
            LocalDateTime.of(2026, 1, 3, 10, 0, 0));
    when(extSourceApplicationService.update(eq(1L), any(ExtSourceUpdateCommand.class)))
        .thenReturn(updatedResponse);

    mockMvc
        .perform(
            put("/app/integration/sources/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data.name").value("飞书办公"))
        .andExpect(jsonPath("$.data.priority").value(200));
  }

  @Test
  @DisplayName("应该成功删除外部身份源")
  void should_delete_ext_source_successfully() throws Exception {
    doNothing().when(extSourceApplicationService).delete(1L);

    mockMvc
        .perform(delete("/app/integration/sources/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("应该成功查询所有外部身份源")
  void should_list_ext_sources_successfully() throws Exception {
    when(extSourceApplicationService.list()).thenReturn(List.of(extSourceResponse));

    mockMvc
        .perform(get("/app/integration/sources"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data.length()").value(1))
        .andExpect(jsonPath("$.data[0].code").value("FEISHU"));
  }
}
