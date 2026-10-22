package io.github.lystrosaurus.admin.system.menu.adapter.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.system.menu.adapter.request.MenuCreateRequest;
import io.github.lystrosaurus.admin.system.menu.adapter.request.MenuUpdateRequest;
import io.github.lystrosaurus.admin.system.menu.adapter.response.MenuResponse;
import io.github.lystrosaurus.admin.system.menu.application.MenuApplicationService;
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

/** MenuController 授权回归测试 */
@DisplayName("MenuController 授权回归测试")
class MenuControllerAuthzTest extends BaseTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @MockitoBean private MenuApplicationService menuApplicationService;

  private MockedStatic<StpUtil> stpUtilMock;

  private MenuResponse sampleResponse() {
    return new MenuResponse(
        1L, 0L, "系统管理", "/system", "System", "setting", 1, (byte) 1, null, (byte) 1, (byte) 1,
        null);
  }

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

  private MenuCreateRequest createRequest() {
    return new MenuCreateRequest(null, "系统管理", "/system", "System", "setting", 1, (byte) 1, null,
        (byte) 1);
  }

  private MenuUpdateRequest updateRequest() {
    return new MenuUpdateRequest(
        "新名称", "/new", "New", "new", 2, (byte) 1, null, (byte) 1, (byte) 1);
  }

  @Test
  @DisplayName("管理员可创建菜单")
  void admin_can_create_menu() throws Exception {
    when(menuApplicationService.create(any())).thenReturn(sampleResponse());

    mockMvc
        .perform(
            post("/app/menus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("无权限用户创建菜单被拒绝(code=4001)")
  void user_cannot_create_menu() throws Exception {
    stpUtilMock.when(() -> StpUtil.hasPermission(anyString())).thenReturn(false);

    mockMvc
        .perform(
            post("/app/menus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.PERMISSION_DENIED.getCode()));
  }

  @Test
  @DisplayName("管理员可更新菜单")
  void admin_can_update_menu() throws Exception {
    when(menuApplicationService.update(any(), any())).thenReturn(sampleResponse());

    mockMvc
        .perform(
            put("/app/menus/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("无权限用户更新菜单被拒绝(code=4001)")
  void user_cannot_update_menu() throws Exception {
    stpUtilMock.when(() -> StpUtil.hasPermission(anyString())).thenReturn(false);

    mockMvc
        .perform(
            put("/app/menus/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.PERMISSION_DENIED.getCode()));
  }

  @Test
  @DisplayName("管理员可删除菜单")
  void admin_can_delete_menu() throws Exception {
    doNothing().when(menuApplicationService).deleteById(1L);

    mockMvc
        .perform(delete("/app/menus/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("无权限用户删除菜单被拒绝(code=4001)")
  void user_cannot_delete_menu() throws Exception {
    stpUtilMock.when(() -> StpUtil.hasPermission(anyString())).thenReturn(false);

    mockMvc
        .perform(delete("/app/menus/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.PERMISSION_DENIED.getCode()));
  }

  @Test
  @DisplayName("管理员可查看菜单树")
  void admin_can_view_menu_tree() throws Exception {
    when(menuApplicationService.findTree()).thenReturn(List.of(sampleResponse()));

    mockMvc
        .perform(get("/app/menus/tree"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("无权限用户查看菜单树被拒绝(code=4001)")
  void user_cannot_view_menu_tree() throws Exception {
    stpUtilMock.when(() -> StpUtil.hasPermission(anyString())).thenReturn(false);

    mockMvc
        .perform(get("/app/menus/tree"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.PERMISSION_DENIED.getCode()));
  }

  @Test
  @DisplayName("管理员可按角色查看菜单")
  void admin_can_view_role_menus() throws Exception {
    when(menuApplicationService.findByRoleId(2L)).thenReturn(List.of(sampleResponse()));

    mockMvc
        .perform(get("/app/menus/role/2"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("无权限用户按角色查看菜单被拒绝(code=4001)")
  void user_cannot_view_role_menus() throws Exception {
    stpUtilMock.when(() -> StpUtil.hasPermission(anyString())).thenReturn(false);

    mockMvc
        .perform(get("/app/menus/role/2"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.PERMISSION_DENIED.getCode()));
  }

  @Test
  @DisplayName("当前用户菜单接口无需显式权限")
  void user_can_view_own_menus() throws Exception {
    stpUtilMock.when(() -> StpUtil.hasPermission(anyString())).thenReturn(false);
    when(menuApplicationService.findByUserId(1L)).thenReturn(List.of(sampleResponse()));

    mockMvc
        .perform(get("/app/menus/user"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }
}
