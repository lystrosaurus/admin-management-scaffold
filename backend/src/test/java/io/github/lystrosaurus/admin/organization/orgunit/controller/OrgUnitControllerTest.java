package io.github.lystrosaurus.admin.organization.orgunit.controller;

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
import io.github.lystrosaurus.admin.common.PageResult;
import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.organization.orgunit.dto.OrgUnitCreateDTO;
import io.github.lystrosaurus.admin.organization.orgunit.dto.OrgUnitUpdateDTO;
import io.github.lystrosaurus.admin.organization.orgunit.service.OrgUnitService;
import io.github.lystrosaurus.admin.organization.orgunit.vo.OrgUnitTreeVO;
import io.github.lystrosaurus.admin.organization.orgunit.vo.OrgUnitVO;
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
 * OrgUnitController 测试
 *
 * <p>测试组织单元控制器的各个接口
 */
@DisplayName("OrgUnitController 测试")
class OrgUnitControllerTest extends SaTokenTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private OrgUnitService orgUnitService;

  private OrgUnitVO orgUnitVO;
  private OrgUnitTreeVO orgUnitTreeVO;

  @BeforeEach
  void setUp() {
    orgUnitVO =
        new OrgUnitVO(1L, "HQ", "总公司", 0L, "/1/", 1, null, 0, "ENABLED", LocalDateTime.now());

    orgUnitTreeVO = new OrgUnitTreeVO(1L, "HQ", "总公司", 0L, 1, null, 0, "ENABLED", List.of());
  }

  @Test
  @DisplayName("应该成功创建组织单元")
  void should_create_org_unit_successfully() throws Exception {
    OrgUnitCreateDTO createDTO = new OrgUnitCreateDTO("HQ", "总公司", 0L, null, 0);
    when(orgUnitService.create(any(OrgUnitCreateDTO.class))).thenReturn(orgUnitVO);

    mockMvc
        .perform(
            post("/app/org-units")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.message").value("操作成功"))
        .andExpect(jsonPath("$.data.code").value("HQ"))
        .andExpect(jsonPath("$.data.name").value("总公司"));
  }

  @Test
  @DisplayName("应该返回400当组织编码为空")
  void should_return_400_when_code_is_blank() throws Exception {
    OrgUnitCreateDTO invalidDTO = new OrgUnitCreateDTO("", "总公司", 0L, null, 0);

    mockMvc
        .perform(
            post("/app/org-units")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("应该返回400当组织名称为空")
  void should_return_400_when_name_is_blank() throws Exception {
    OrgUnitCreateDTO invalidDTO = new OrgUnitCreateDTO("HQ", "", 0L, null, 0);

    mockMvc
        .perform(
            post("/app/org-units")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("应该成功获取组织单元详情")
  void should_get_org_unit_detail_successfully() throws Exception {
    when(orgUnitService.findById(1L)).thenReturn(orgUnitVO);

    mockMvc
        .perform(get("/app/org-units/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.code").value("HQ"));
  }

  @Test
  @DisplayName("应该返回错误码当组织单元不存在")
  void should_return_error_when_org_unit_not_found() throws Exception {
    when(orgUnitService.findById(999L))
        .thenThrow(new BusinessException(ErrorCode.ORG_UNIT_NOT_FOUND));

    mockMvc
        .perform(get("/app/org-units/999"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.ORG_UNIT_NOT_FOUND.getCode()));
  }

  @Test
  @DisplayName("应该成功更新组织单元")
  void should_update_org_unit_successfully() throws Exception {
    OrgUnitUpdateDTO updateDTO = new OrgUnitUpdateDTO("集团总部", null, null, 1, null);
    OrgUnitVO updatedVO =
        new OrgUnitVO(1L, "HQ", "集团总部", 0L, "/1/", 1, null, 1, "ENABLED", LocalDateTime.now());
    when(orgUnitService.update(eq(1L), any(OrgUnitUpdateDTO.class))).thenReturn(updatedVO);

    mockMvc
        .perform(
            put("/app/org-units/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data.name").value("集团总部"));
  }

  @Test
  @DisplayName("应该成功删除组织单元")
  void should_delete_org_unit_successfully() throws Exception {
    doNothing().when(orgUnitService).deleteById(1L);

    mockMvc
        .perform(delete("/app/org-units/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("应该成功分页查询组织单元")
  void should_find_page_successfully() throws Exception {
    PageResult<OrgUnitVO> pageResult = new PageResult<>(List.of(orgUnitVO), 1, 1, 10);
    when(orgUnitService.findPage(any(), eq(1), eq(10))).thenReturn(pageResult);

    mockMvc
        .perform(get("/app/org-units").param("page", "1").param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data.items").isArray())
        .andExpect(jsonPath("$.data.total").value(1));
  }

  @Test
  @DisplayName("应该成功获取组织架构树")
  void should_get_org_tree_successfully() throws Exception {
    when(orgUnitService.findTree()).thenReturn(List.of(orgUnitTreeVO));

    mockMvc
        .perform(get("/app/org-units/tree"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data[0].code").value("HQ"));
  }
}
