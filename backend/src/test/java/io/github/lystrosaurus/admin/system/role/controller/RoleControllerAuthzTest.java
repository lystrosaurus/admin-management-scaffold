package io.github.lystrosaurus.admin.system.role.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cn.dev33.satoken.stp.StpUtil;
import io.github.lystrosaurus.admin.BaseTest;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.system.role.dto.RoleCreateDTO;
import io.github.lystrosaurus.admin.system.role.service.RoleService;
import io.github.lystrosaurus.admin.system.role.vo.RoleVO;
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

/**
 * RoleController 授权回归测试（F-01 最小切片：仅覆盖 RoleController 的方法级 @RequiresPermission）。
 *
 * <p>不继承 SaTokenTest，避免 MockedStatic&lt;StpUtil&gt; 冲突；自行建立 MockedStatic。
 *
 * <p>断言口径：GlobalExceptionHandler 将 BusinessException 映射为 HTTP 200 + body {&quot;code&quot;:...}。
 *   - 管理员（hasPermission=true）→ code 200
 *   - 无权限用户（hasPermission=false）→ code ErrorCode.PERMISSION_DENIED(4001)
 */
@DisplayName("RoleController 授权回归测试")
class RoleControllerAuthzTest extends BaseTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @MockitoBean private RoleService roleService;

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
    // 默认：已登录用户拥有全部权限；负向用例在测试内覆写为 false
    stpUtilMock.when(() -> StpUtil.hasPermission(anyString())).thenReturn(true);
  }

  @AfterEach
  void tearDown() {
    if (stpUtilMock != null) {
      stpUtilMock.close();
    }
  }

  // ===================== 创建角色 =====================

  @Test
  @DisplayName("管理员可创建角色")
  void admin_can_create_role() throws Exception {
    RoleCreateDTO dto = new RoleCreateDTO("ADMIN", "管理员", "系统管理员", 1, "ALL");
    when(roleService.create(any(RoleCreateDTO.class)))
        .thenReturn(new RoleVO(1L, "ADMIN", "管理员", "系统管理员", "ENABLED", "ALL"));

    mockMvc
        .perform(
            post("/app/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("无权限用户创建角色被拒绝(code=4001)")
  void user_cannot_create_role() throws Exception {
    stpUtilMock.when(() -> StpUtil.hasPermission(anyString())).thenReturn(false);
    RoleCreateDTO dto = new RoleCreateDTO("ADMIN", "管理员", "系统管理员", 1, "ALL");

    mockMvc
        .perform(
            post("/app/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.PERMISSION_DENIED.getCode()));
  }

  // ===================== 删除角色 =====================

  @Test
  @DisplayName("管理员可删除角色")
  void admin_can_delete_role() throws Exception {
    doNothing().when(roleService).deleteById(1L);

    mockMvc
        .perform(delete("/app/roles/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("无权限用户删除角色被拒绝(code=4001)")
  void user_cannot_delete_role() throws Exception {
    stpUtilMock.when(() -> StpUtil.hasPermission(anyString())).thenReturn(false);
    doNothing().when(roleService).deleteById(1L);

    mockMvc
        .perform(delete("/app/roles/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.PERMISSION_DENIED.getCode()));
  }

  // ===================== 分配权限 =====================

  @Test
  @DisplayName("管理员可为角色分配权限")
  void admin_can_assign_permissions() throws Exception {
    doNothing().when(roleService).assignPermissions(any(), any());

    mockMvc
        .perform(
            post("/app/roles/1/permissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[1, 2, 3]"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("无权限用户分配权限被拒绝(code=4001)")
  void user_cannot_assign_permissions() throws Exception {
    stpUtilMock.when(() -> StpUtil.hasPermission(anyString())).thenReturn(false);

    mockMvc
        .perform(
            post("/app/roles/1/permissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[1, 2, 3]"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.PERMISSION_DENIED.getCode()));
  }

  // ===================== 分配菜单 =====================

  @Test
  @DisplayName("管理员可为角色分配菜单")
  void admin_can_assign_menus() throws Exception {
    doNothing().when(roleService).assignMenus(any(), any());

    mockMvc
        .perform(
            post("/app/roles/1/menus")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[1, 2, 3]"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("无权限用户分配菜单被拒绝(code=4001)")
  void user_cannot_assign_menus() throws Exception {
    stpUtilMock.when(() -> StpUtil.hasPermission(anyString())).thenReturn(false);

    mockMvc
        .perform(
            post("/app/roles/1/menus")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[1, 2, 3]"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.PERMISSION_DENIED.getCode()));
  }
}
