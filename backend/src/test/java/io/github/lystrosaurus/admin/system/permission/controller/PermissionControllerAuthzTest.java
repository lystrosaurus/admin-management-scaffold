package io.github.lystrosaurus.admin.system.permission.controller;

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
import io.github.lystrosaurus.admin.system.permission.dto.PermissionCreateDTO;
import io.github.lystrosaurus.admin.system.permission.dto.PermissionUpdateDTO;
import io.github.lystrosaurus.admin.system.permission.service.PermissionService;
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
 * PermissionController 授权回归测试。
 *
 * <p>管理员（hasPermission=true）→ code 200；无权限用户（hasPermission=false）→ code 4001。
 */
@DisplayName("PermissionController 授权回归测试")
class PermissionControllerAuthzTest extends BaseTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @MockitoBean private PermissionService permissionService;

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

  // ===== findAll / findByRoleId: system:permission:view =====
  @Test
  @DisplayName("管理员可查询所有权限")
  void admin_can_find_all() throws Exception {
    mockMvc.perform(get("/app/permissions")).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("无权限用户查询所有权限被拒绝(code=4001)")
  void user_cannot_find_all() throws Exception {
    stpUtilMock.when(() -> StpUtil.hasPermission(anyString())).thenReturn(false);
    mockMvc
        .perform(get("/app/permissions"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.PERMISSION_DENIED.getCode()));
  }

  @Test
  @DisplayName("管理员可按角色查询权限")
  void admin_can_find_by_role() throws Exception {
    mockMvc
        .perform(get("/app/permissions/role/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("无权限用户按角色查询权限被拒绝(code=4001)")
  void user_cannot_find_by_role() throws Exception {
    stpUtilMock.when(() -> StpUtil.hasPermission(anyString())).thenReturn(false);
    mockMvc
        .perform(get("/app/permissions/role/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.PERMISSION_DENIED.getCode()));
  }

  // ===== create: system:permission:create =====
  @Test
  @DisplayName("管理员可创建权限")
  void admin_can_create() throws Exception {
    mockMvc
        .perform(
            post("/app/permissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new PermissionCreateDTO("user:create", "创建用户", "BUTTON", "system", "user", "create"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("无权限用户创建权限被拒绝(code=4001)")
  void user_cannot_create() throws Exception {
    stpUtilMock.when(() -> StpUtil.hasPermission(anyString())).thenReturn(false);
    mockMvc
        .perform(
            post("/app/permissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new PermissionCreateDTO("user:create", "创建用户", "BUTTON", "system", "user", "create"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.PERMISSION_DENIED.getCode()));
  }

  // ===== update: system:permission:update =====
  @Test
  @DisplayName("管理员可更新权限")
  void admin_can_update() throws Exception {
    mockMvc
        .perform(
            put("/app/permissions/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new PermissionUpdateDTO("新名称", "BUTTON", "system", "user", "create", "ENABLED"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("无权限用户更新权限被拒绝(code=4001)")
  void user_cannot_update() throws Exception {
    stpUtilMock.when(() -> StpUtil.hasPermission(anyString())).thenReturn(false);
    mockMvc
        .perform(
            put("/app/permissions/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new PermissionUpdateDTO("新名称", "BUTTON", "system", "user", "create", "ENABLED"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.PERMISSION_DENIED.getCode()));
  }

  // ===== deleteById: system:permission:delete =====
  @Test
  @DisplayName("管理员可删除权限")
  void admin_can_delete() throws Exception {
    mockMvc
        .perform(delete("/app/permissions/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("无权限用户删除权限被拒绝(code=4001)")
  void user_cannot_delete() throws Exception {
    stpUtilMock.when(() -> StpUtil.hasPermission(anyString())).thenReturn(false);
    mockMvc
        .perform(delete("/app/permissions/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.PERMISSION_DENIED.getCode()));
  }
}
