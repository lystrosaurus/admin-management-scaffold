package io.github.lystrosaurus.admin.auth.adapter.web;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cn.dev33.satoken.stp.StpUtil;
import io.github.lystrosaurus.admin.BaseTest;
import io.github.lystrosaurus.admin.auth.adapter.request.ExternalAccountBindRequest;
import io.github.lystrosaurus.admin.auth.application.ExternalAccountApplicationService;
import io.github.lystrosaurus.admin.exception.ErrorCode;
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
 * AuthExternalAccountController 授权回归测试。
 *
 * <p>管理员（hasPermission=true）→ code 200；无权限用户（hasPermission=false）→ code 4001。
 * 所有端点均需 auth:external-account:* 权限码；本测试是启用默认拒绝前的回归守门。
 */
@DisplayName("AuthExternalAccountController 授权回归测试")
class AuthExternalAccountControllerAuthzTest extends BaseTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @MockitoBean private ExternalAccountApplicationService externalAccountApplicationService;

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

  // ===== bind: auth:external-account:bind =====
  @Test
  @DisplayName("管理员可绑定三方账号")
  void admin_can_bind() throws Exception {
    mockMvc
        .perform(
            post("/app/auth/external-accounts/bind")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new ExternalAccountBindRequest(
                            null, null, null, null, null, null, null))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("无权限用户绑定三方账号被拒绝(code=4001)")
  void user_cannot_bind() throws Exception {
    stpUtilMock.when(() -> StpUtil.hasPermission(anyString())).thenReturn(false);
    mockMvc
        .perform(
            post("/app/auth/external-accounts/bind")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new ExternalAccountBindRequest(
                            null, null, null, null, null, null, null))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.PERMISSION_DENIED.getCode()));
  }

  // ===== unbind: auth:external-account:unbind =====
  @Test
  @DisplayName("管理员可解绑三方账号")
  void admin_can_unbind() throws Exception {
    mockMvc
        .perform(delete("/app/auth/external-accounts/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("无权限用户解绑三方账号被拒绝(code=4001)")
  void user_cannot_unbind() throws Exception {
    stpUtilMock.when(() -> StpUtil.hasPermission(anyString())).thenReturn(false);
    mockMvc
        .perform(delete("/app/auth/external-accounts/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.PERMISSION_DENIED.getCode()));
  }

  // ===== findById / listByUserId / listByEmployeeId: auth:external-account:view =====
  @Test
  @DisplayName("管理员可按ID查询三方账号")
  void admin_can_find_by_id() throws Exception {
    mockMvc
        .perform(get("/app/auth/external-accounts/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("无权限用户按ID查询三方账号被拒绝(code=4001)")
  void user_cannot_find_by_id() throws Exception {
    stpUtilMock.when(() -> StpUtil.hasPermission(anyString())).thenReturn(false);
    mockMvc
        .perform(get("/app/auth/external-accounts/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.PERMISSION_DENIED.getCode()));
  }

  @Test
  @DisplayName("管理员可按用户ID查询三方账号列表")
  void admin_can_list_by_user() throws Exception {
    mockMvc
        .perform(get("/app/auth/external-accounts/by-user/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("无权限用户按用户ID查询三方账号列表被拒绝(code=4001)")
  void user_cannot_list_by_user() throws Exception {
    stpUtilMock.when(() -> StpUtil.hasPermission(anyString())).thenReturn(false);
    mockMvc
        .perform(get("/app/auth/external-accounts/by-user/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.PERMISSION_DENIED.getCode()));
  }

  @Test
  @DisplayName("管理员可按员工ID查询三方账号列表")
  void admin_can_list_by_employee() throws Exception {
    mockMvc
        .perform(get("/app/auth/external-accounts/by-employee/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("无权限用户按员工ID查询三方账号列表被拒绝(code=4001)")
  void user_cannot_list_by_employee() throws Exception {
    stpUtilMock.when(() -> StpUtil.hasPermission(anyString())).thenReturn(false);
    mockMvc
        .perform(get("/app/auth/external-accounts/by-employee/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.PERMISSION_DENIED.getCode()));
  }
}
