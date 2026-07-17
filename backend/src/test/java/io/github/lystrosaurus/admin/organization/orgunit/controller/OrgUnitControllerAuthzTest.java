package io.github.lystrosaurus.admin.organization.orgunit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cn.dev33.satoken.stp.StpUtil;
import io.github.lystrosaurus.admin.BaseTest;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.organization.orgunit.dto.OrgUnitCreateDTO;
import io.github.lystrosaurus.admin.organization.orgunit.dto.OrgUnitUpdateDTO;
import io.github.lystrosaurus.admin.organization.orgunit.service.OrgUnitService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

/**
 * OrgUnitController 授权回归测试。
 *
 * <p>管理员（hasPermission=true）→ code 200；无权限用户（hasPermission=false）→ code 4001。
 * create 需 organization:org-unit:create；findById/findPage/findTree 需 organization:org-unit:view；update 需 organization:org-unit:update；delete 需 organization:org-unit:delete。
 */
@DisplayName("OrgUnitController 授权回归测试")
class OrgUnitControllerAuthzTest extends BaseTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @MockitoBean private OrgUnitService orgUnitService;

  private MockedStatic<StpUtil> stpUtilMock;

  @BeforeEach
  void setUp() {
    stpUtilMock = mockStatic(StpUtil.class);
    stpUtilMock.when(StpUtil::checkLogin).thenAnswer(inv -> null);
    stpUtilMock.when(StpUtil::isLogin).thenReturn(true);
    stpUtilMock.when(StpUtil::getLoginIdAsLong).thenReturn(1L);
    stpUtilMock.when(StpUtil::getTokenValue).thenReturn("mock-jwt-token");
    stpUtilMock.when(() -> StpUtil.checkPermission(anyString())).thenAnswer(inv -> null);
    stpUtilMock.when(() -> StpUtil.checkRole(anyString())).thenAnswer(inv -> null);
    stpUtilMock.when(() -> StpUtil.checkPermissionOr(anyString(), anyString())).thenAnswer(inv -> null);
    stpUtilMock.when(() -> StpUtil.checkPermissionAnd(anyString(), anyString())).thenAnswer(inv -> null);
    stpUtilMock.when(() -> StpUtil.hasRole(anyString())).thenReturn(true);
    stpUtilMock.when(() -> StpUtil.hasPermission(anyString())).thenReturn(true);
  }

  @AfterEach
  void tearDown() {
    if (stpUtilMock != null) {
      stpUtilMock.close();
    }
  }

  // ===== create: organization:org-unit:create =====
  @Test
  @DisplayName("管理员可创建组织单元")
  void admin_can_create() throws Exception {
    mockMvc
        .perform(
            post("/app/org-units")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new OrgUnitCreateDTO("OU001", "研发部", null, null, null))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("无权限用户创建组织单元被拒绝(code=4001)")
  void user_cannot_create() throws Exception {
    stpUtilMock.when(() -> StpUtil.hasPermission(anyString())).thenReturn(false);
    mockMvc
        .perform(
            post("/app/org-units")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new OrgUnitCreateDTO("OU001", "研发部", null, null, null))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.PERMISSION_DENIED.getCode()));
  }

  // ===== findById / findPage / findTree: organization:org-unit:view =====
  @Test
  @DisplayName("管理员可查询组织单元详情")
  void admin_can_find_by_id() throws Exception {
    mockMvc.perform(get("/app/org-units/1")).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("无权限用户查询组织单元详情被拒绝(code=4001)")
  void user_cannot_find_by_id() throws Exception {
    stpUtilMock.when(() -> StpUtil.hasPermission(anyString())).thenReturn(false);
    mockMvc.perform(get("/app/org-units/1")).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(ErrorCode.PERMISSION_DENIED.getCode()));
  }

  @Test
  @DisplayName("管理员可分页查询组织单元")
  void admin_can_find_page() throws Exception {
    mockMvc.perform(get("/app/org-units")).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("无权限用户分页查询组织单元被拒绝(code=4001)")
  void user_cannot_find_page() throws Exception {
    stpUtilMock.when(() -> StpUtil.hasPermission(anyString())).thenReturn(false);
    mockMvc.perform(get("/app/org-units")).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(ErrorCode.PERMISSION_DENIED.getCode()));
  }

  @Test
  @DisplayName("管理员可查询组织架构树")
  void admin_can_find_tree() throws Exception {
    mockMvc.perform(get("/app/org-units/tree")).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("无权限用户查询组织架构树被拒绝(code=4001)")
  void user_cannot_find_tree() throws Exception {
    stpUtilMock.when(() -> StpUtil.hasPermission(anyString())).thenReturn(false);
    mockMvc.perform(get("/app/org-units/tree")).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(ErrorCode.PERMISSION_DENIED.getCode()));
  }

  // ===== update: organization:org-unit:update =====
  @Test
  @DisplayName("管理员可更新组织单元")
  void admin_can_update() throws Exception {
    mockMvc
        .perform(
            put("/app/org-units/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new OrgUnitUpdateDTO(null, null, null, null, null))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("无权限用户更新组织单元被拒绝(code=4001)")
  void user_cannot_update() throws Exception {
    stpUtilMock.when(() -> StpUtil.hasPermission(anyString())).thenReturn(false);
    mockMvc
        .perform(
            put("/app/org-units/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new OrgUnitUpdateDTO(null, null, null, null, null))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.PERMISSION_DENIED.getCode()));
  }

  // ===== deleteById: organization:org-unit:delete =====
  @Test
  @DisplayName("管理员可删除组织单元")
  void admin_can_delete() throws Exception {
    mockMvc.perform(delete("/app/org-units/1")).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("无权限用户删除组织单元被拒绝(code=4001)")
  void user_cannot_delete() throws Exception {
    stpUtilMock.when(() -> StpUtil.hasPermission(anyString())).thenReturn(false);
    mockMvc.perform(delete("/app/org-units/1")).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(ErrorCode.PERMISSION_DENIED.getCode()));
  }
}
