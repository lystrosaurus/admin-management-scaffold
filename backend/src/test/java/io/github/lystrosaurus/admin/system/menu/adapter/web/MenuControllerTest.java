package io.github.lystrosaurus.admin.system.menu.adapter.web;

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

/** MenuController 测试 */
@DisplayName("MenuController 测试")
class MenuControllerTest extends BaseTest {

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

  @Test
  @DisplayName("应该成功创建菜单")
  void should_create_menu_successfully() throws Exception {
    MenuCreateRequest request =
        new MenuCreateRequest(null, "系统管理", "/system", "System", "setting", 1, (byte) 1, null,
            (byte) 1);
    when(menuApplicationService.create(any())).thenReturn(sampleResponse());

    mockMvc
        .perform(
            post("/app/menus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data.name").value("系统管理"));
  }

  @Test
  @DisplayName("应该成功更新菜单")
  void should_update_menu_successfully() throws Exception {
    MenuUpdateRequest request =
        new MenuUpdateRequest("新名称", "/new", "New", "new", 2, (byte) 1, null, (byte) 1, (byte) 1);
    when(menuApplicationService.update(eq(1L), any())).thenReturn(sampleResponse());

    mockMvc
        .perform(
            put("/app/menus/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("应该成功删除菜单")
  void should_delete_menu_successfully() throws Exception {
    doNothing().when(menuApplicationService).deleteById(1L);

    mockMvc
        .perform(delete("/app/menus/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("应该成功获取菜单树")
  void should_find_menu_tree_successfully() throws Exception {
    when(menuApplicationService.findTree()).thenReturn(List.of(sampleResponse()));

    mockMvc
        .perform(get("/app/menus/tree"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data[0].name").value("系统管理"));
  }

  @Test
  @DisplayName("应该成功按角色查询菜单")
  void should_find_menus_by_role_successfully() throws Exception {
    when(menuApplicationService.findByRoleId(2L)).thenReturn(List.of(sampleResponse()));

    mockMvc
        .perform(get("/app/menus/role/2"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data[0].id").value(1));
  }

  @Test
  @DisplayName("应该成功获取当前用户菜单")
  void should_find_current_user_menus_successfully() throws Exception {
    when(menuApplicationService.findByUserId(1L)).thenReturn(List.of(sampleResponse()));

    mockMvc
        .perform(get("/app/menus/user"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data[0].name").value("系统管理"));
  }
}
