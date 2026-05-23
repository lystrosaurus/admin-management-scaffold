package io.github.lystrosaurus.admin.integration.source.controller;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.integration.source.dto.ExtSourceCreateDTO;
import io.github.lystrosaurus.admin.integration.source.dto.ExtSourceUpdateDTO;
import io.github.lystrosaurus.admin.integration.source.service.ExtSourceService;
import io.github.lystrosaurus.admin.integration.source.vo.ExtSourceVO;
import io.github.lystrosaurus.admin.test.SaTokenTest;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * IntegrationSourceController 测试
 *
 * <p>测试外部身份源控制器的各个接口
 */
@DisplayName("IntegrationSourceController 测试")
class IntegrationSourceControllerTest extends SaTokenTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private ExtSourceService extSourceService;

  private ExtSourceVO extSourceVO;

  @BeforeEach
  void setUp() {
    extSourceVO =
        new ExtSourceVO(
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
    ExtSourceCreateDTO createDTO =
        new ExtSourceCreateDTO("FEISHU", "飞书", "IM", "tenant_001", 100, "{\"appId\":\"cli_xxx\"}");
    when(extSourceService.create(any(ExtSourceCreateDTO.class))).thenReturn(extSourceVO);

    mockMvc
        .perform(
            post("/app/integration/sources")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO)))
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
    when(extSourceService.getById(1L)).thenReturn(extSourceVO);

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
    when(extSourceService.getById(999L))
        .thenThrow(new BusinessException(ErrorCode.SOURCE_NOT_FOUND));

    mockMvc
        .perform(get("/app/integration/sources/999"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.SOURCE_NOT_FOUND.getCode()));
  }

  @Test
  @DisplayName("应该成功更新外部身份源")
  void should_update_ext_source_successfully() throws Exception {
    ExtSourceUpdateDTO updateDTO =
        new ExtSourceUpdateDTO("飞书办公", "IM", "tenant_001", "ENABLED", 200, null);
    ExtSourceVO updatedVO =
        new ExtSourceVO(
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
    when(extSourceService.update(eq(1L), any(ExtSourceUpdateDTO.class))).thenReturn(updatedVO);

    mockMvc
        .perform(
            put("/app/integration/sources/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data.name").value("飞书办公"))
        .andExpect(jsonPath("$.data.priority").value(200));
  }

  @Test
  @DisplayName("应该成功删除外部身份源")
  void should_delete_ext_source_successfully() throws Exception {
    doNothing().when(extSourceService).delete(1L);

    mockMvc
        .perform(delete("/app/integration/sources/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("应该成功查询所有外部身份源")
  void should_list_ext_sources_successfully() throws Exception {
    when(extSourceService.list()).thenReturn(List.of(extSourceVO));

    mockMvc
        .perform(get("/app/integration/sources"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data.length()").value(1))
        .andExpect(jsonPath("$.data[0].code").value("FEISHU"));
  }
}
