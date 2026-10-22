package io.github.lystrosaurus.admin.system.permission.adapter.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cn.dev33.satoken.stp.StpUtil;
import io.github.lystrosaurus.admin.BaseTest;
import io.github.lystrosaurus.admin.shared.kernel.exception.ErrorCode;
import io.github.lystrosaurus.admin.system.permission.adapter.request.PermissionCreateRequest;
import io.github.lystrosaurus.admin.system.permission.adapter.request.PermissionUpdateRequest;
import io.github.lystrosaurus.admin.system.permission.adapter.response.PermissionResponse;
import io.github.lystrosaurus.admin.system.permission.application.PermissionApplicationService;
import java.util.List;
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

/** PermissionController 测试 */
@DisplayName("PermissionController 测试")
class PermissionControllerTest extends BaseTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @MockitoBean private PermissionApplicationService permissionApplicationService;

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
    stpUtilMock
        .when(() -> StpUtil.checkPermissionOr(anyString(), anyString()))
        .thenAnswer(inv -> null);
    stpUtilMock
        .when(() -> StpUtil.checkPermissionAnd(anyString(), anyString()))
        .thenAnswer(inv -> null);
    stpUtilMock.when(() -> StpUtil.hasRole(anyString())).thenReturn(true);
    stpUtilMock.when(() -> StpUtil.hasPermission(anyString())).thenReturn(true);
  }

  @AfterEach
  void tearDown() {
    if (stpUtilMock != null) {
      stpUtilMock.close();
    }
  }

  @Test
  @DisplayName("应该成功创建权限")
  void should_create_permission_successfully() throws Exception {
    PermissionCreateRequest request =
        new PermissionCreateRequest("user:read", "查看用户", "API", "user", "user", "read");
    when(permissionApplicationService.create(any()))
        .thenReturn(new PermissionResponse(1L, "user:read", "查看用户", "API", "user", "user", "read"));

    mockMvc
        .perform(
            post("/app/permissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data.code").value("user:read"));
  }

  @Test
  @DisplayName("应该成功查询所有权限")
  void should_find_all_permissions_successfully() throws Exception {
    when(permissionApplicationService.findAll())
        .thenReturn(
            List.of(new PermissionResponse(1L, "user:read", "查看用户", "API", "user", "user", "read")));

    mockMvc
        .perform(get("/app/permissions"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data[0].code").value("user:read"));
  }

  @Test
  @DisplayName("应该成功更新权限")
  void should_update_permission_successfully() throws Exception {
    PermissionUpdateRequest request =
        new PermissionUpdateRequest("新名称", "API", "user", "user", "read", "ENABLED");
    when(permissionApplicationService.update(eq(1L), any()))
        .thenReturn(new PermissionResponse(1L, "user:read", "新名称", "API", "user", "user", "read"));

    mockMvc
        .perform(
            put("/app/permissions/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data.name").value("新名称"));
  }

  @Test
  @DisplayName("应该成功删除权限")
  void should_delete_permission_successfully() throws Exception {
    doNothing().when(permissionApplicationService).deleteById(1L);

    mockMvc
        .perform(delete("/app/permissions/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("应该成功按角色查询权限")
  void should_find_permissions_by_role_successfully() throws Exception {
    when(permissionApplicationService.findByRoleId(2L))
        .thenReturn(
            List.of(new PermissionResponse(1L, "user:read", "查看用户", "API", "user", "user", "read")));

    mockMvc
        .perform(get("/app/permissions/role/2"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data[0].code").value("user:read"));
  }

  @Test
  @DisplayName("无权限用户创建权限被拒绝(code=4001)")
  void user_cannot_create_permission() throws Exception {
    stpUtilMock.when(() -> StpUtil.hasPermission(anyString())).thenReturn(false);
    PermissionCreateRequest request =
        new PermissionCreateRequest("user:read", "查看用户", "API", "user", "user", "read");

    mockMvc
        .perform(
            post("/app/permissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.PERMISSION_DENIED.getCode()));
  }

  @Test
  @DisplayName("无权限用户更新权限被拒绝(code=4001)")
  void user_cannot_update_permission() throws Exception {
    stpUtilMock.when(() -> StpUtil.hasPermission(anyString())).thenReturn(false);
    PermissionUpdateRequest request =
        new PermissionUpdateRequest("新名称", "API", "user", "user", "read", "ENABLED");

    mockMvc
        .perform(
            put("/app/permissions/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.PERMISSION_DENIED.getCode()));
  }

  @Test
  @DisplayName("无权限用户删除权限被拒绝(code=4001)")
  void user_cannot_delete_permission() throws Exception {
    stpUtilMock.when(() -> StpUtil.hasPermission(anyString())).thenReturn(false);

    mockMvc
        .perform(delete("/app/permissions/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.PERMISSION_DENIED.getCode()));
  }

  @Test
  @DisplayName("无权限用户查询权限被拒绝(code=4001)")
  void user_cannot_view_permissions() throws Exception {
    stpUtilMock.when(() -> StpUtil.hasPermission(anyString())).thenReturn(false);

    mockMvc
        .perform(get("/app/permissions"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.PERMISSION_DENIED.getCode()));
  }
}
