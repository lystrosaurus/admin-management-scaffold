package io.github.lystrosaurus.admin.integration.principal.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
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
import io.github.lystrosaurus.admin.integration.principal.dto.ExtPrincipalCreateDTO;
import io.github.lystrosaurus.admin.integration.principal.dto.ExtPrincipalUpdateDTO;
import io.github.lystrosaurus.admin.integration.principal.service.ExtPrincipalService;
import io.github.lystrosaurus.admin.integration.principal.vo.ExtPrincipalIdentifierVO;
import io.github.lystrosaurus.admin.integration.principal.vo.ExtPrincipalVO;
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
 * IntegrationPrincipalController 测试
 *
 * <p>测试外部主体控制器的各个接口
 */
@DisplayName("IntegrationPrincipalController 测试")
class IntegrationPrincipalControllerTest extends SaTokenTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private ExtPrincipalService extPrincipalService;

  private ExtPrincipalVO extPrincipalVO;

  @BeforeEach
  void setUp() {
    extPrincipalVO =
        new ExtPrincipalVO(
            1L,
            1L,
            "飞书",
            "USER",
            "ou_abc123",
            "张三",
            "ACTIVE",
            LocalDateTime.of(2026, 1, 2, 10, 0, 0),
            "EMPLOYEE",
            100L,
            "AUTO_LINKED",
            LocalDateTime.of(2026, 1, 1, 10, 0, 0),
            LocalDateTime.of(2026, 1, 2, 10, 0, 0),
            List.of(new ExtPrincipalIdentifierVO(1L, "FEISHU_OPEN_ID", "ou_abc123", true)));
  }

  @Test
  @DisplayName("应该成功创建外部主体")
  void should_create_ext_principal_successfully() throws Exception {
    ExtPrincipalCreateDTO createDTO =
        new ExtPrincipalCreateDTO(
            1L,
            "USER",
            "ou_abc123",
            "张三",
            "{\"name\":\"张三\"}",
            List.of(new ExtPrincipalCreateDTO.IdentifierItem("FEISHU_OPEN_ID", "ou_abc123", true)));
    when(extPrincipalService.create(any(ExtPrincipalCreateDTO.class))).thenReturn(extPrincipalVO);

    mockMvc
        .perform(
            post("/app/integration/principals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.message").value("操作成功"))
        .andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.sourceId").value(1))
        .andExpect(jsonPath("$.data.principalType").value("USER"))
        .andExpect(jsonPath("$.data.displayName").value("张三"));
  }

  @Test
  @DisplayName("应该成功获取外部主体详情")
  void should_get_ext_principal_by_id_successfully() throws Exception {
    when(extPrincipalService.getById(1L)).thenReturn(extPrincipalVO);

    mockMvc
        .perform(get("/app/integration/principals/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.displayName").value("张三"))
        .andExpect(jsonPath("$.data.externalKey").value("ou_abc123"));
  }

  @Test
  @DisplayName("应该返回错误码当外部主体不存在")
  void should_return_error_when_ext_principal_not_found() throws Exception {
    when(extPrincipalService.getById(999L))
        .thenThrow(new BusinessException(ErrorCode.PRINCIPAL_NOT_FOUND));

    mockMvc
        .perform(get("/app/integration/principals/999"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.PRINCIPAL_NOT_FOUND.getCode()));
  }

  @Test
  @DisplayName("应该成功更新外部主体")
  void should_update_ext_principal_successfully() throws Exception {
    ExtPrincipalUpdateDTO updateDTO =
        new ExtPrincipalUpdateDTO("李四", "ACTIVE", "EMPLOYEE", 100L, "MANUAL_LINKED");
    ExtPrincipalVO updatedVO =
        new ExtPrincipalVO(
            1L,
            1L,
            "飞书",
            "USER",
            "ou_abc123",
            "李四",
            "ACTIVE",
            LocalDateTime.of(2026, 1, 2, 10, 0, 0),
            "EMPLOYEE",
            100L,
            "MANUAL_LINKED",
            LocalDateTime.of(2026, 1, 1, 10, 0, 0),
            LocalDateTime.of(2026, 1, 3, 10, 0, 0),
            List.of());
    when(extPrincipalService.update(eq(1L), any(ExtPrincipalUpdateDTO.class)))
        .thenReturn(updatedVO);

    mockMvc
        .perform(
            put("/app/integration/principals/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data.displayName").value("李四"))
        .andExpect(jsonPath("$.data.linkStatus").value("MANUAL_LINKED"));
  }

  @Test
  @DisplayName("应该成功删除外部主体")
  void should_delete_ext_principal_successfully() throws Exception {
    doNothing().when(extPrincipalService).delete(1L);

    mockMvc
        .perform(delete("/app/integration/principals/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("应该成功查询所有外部主体")
  void should_list_ext_principals_successfully() throws Exception {
    when(extPrincipalService.list(isNull(), isNull())).thenReturn(List.of(extPrincipalVO));

    mockMvc
        .perform(get("/app/integration/principals"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data.length()").value(1))
        .andExpect(jsonPath("$.data[0].displayName").value("张三"));
  }

  @Test
  @DisplayName("应该成功按条件查询外部主体")
  void should_list_ext_principals_with_filter_successfully() throws Exception {
    when(extPrincipalService.list(eq(1L), eq("AUTO_LINKED"))).thenReturn(List.of(extPrincipalVO));

    mockMvc
        .perform(
            get("/app/integration/principals")
                .param("sourceId", "1")
                .param("linkStatus", "AUTO_LINKED"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data.length()").value(1));
  }
}
