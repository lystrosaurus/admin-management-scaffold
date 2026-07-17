package io.github.lystrosaurus.admin.system.menu.controller;

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
import io.github.lystrosaurus.admin.system.menu.dto.MenuCreateDTO;
import io.github.lystrosaurus.admin.system.menu.dto.MenuUpdateDTO;
import io.github.lystrosaurus.admin.system.menu.service.MenuService;
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
 * MenuController 授权回归测试。
 *
 * <p>管理员（hasPermission=true）→ code 200；无权限用户（hasPermission=false）→ code 4001。
 * findUserMenuTree 方法级 {@code @PermitAll}：管理员与无权限用户均应 200。
 */
@DisplayName("MenuController 授权回归测试")
class MenuControllerAuthzTest extends BaseTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @MockitoBean private MenuService menuService;

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

  // ===== findTree / findByRoleId: system:menu:view =====
  @Test
  @DisplayName("管理员可获取菜单树")
  void admin_can_get_tree() throws Exception {
    mockMvc.perform(get("/app/menus/tree")).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("无权限用户获取菜单树被拒绝(code=4001)")
  void user_cannot_get_tree() throws Exception {
    stpUtilMock.when(() -> StpUtil.hasPermission(anyString())).thenReturn(false);
    mockMvc
        .perform(get("/app/menus/tree"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.PERMISSION_DENIED.getCode()));
  }

  @Test
  @DisplayName("管理员可按角色查询菜单")
  void admin_can_find_by_role() throws Exception {
    mockMvc
        .perform(get("/app/menus/role/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("无权限用户按角色查询菜单被拒绝(code=4001)")
  void user_cannot_find_by_role() throws Exception {
    stpUtilMock.when(() -> StpUtil.hasPermission(anyString())).thenReturn(false);
    mockMvc
        .perform(get("/app/menus/role/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.PERMISSION_DENIED.getCode()));
  }

  // ===== create: system:menu:create =====
  @Test
  @DisplayName("管理员可创建菜单")
  void admin_can_create() throws Exception {
    mockMvc
        .perform(
            post("/app/menus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new MenuCreateDTO(null, "系统管理", "/system", "Layout", "setting", 1, (byte) 1, null, (byte) 1))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("无权限用户创建菜单被拒绝(code=4001)")
  void user_cannot_create() throws Exception {
    stpUtilMock.when(() -> StpUtil.hasPermission(anyString())).thenReturn(false);
    mockMvc
        .perform(
            post("/app/menus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new MenuCreateDTO(null, "系统管理", "/system", "Layout", "setting", 1, (byte) 1, null, (byte) 1))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.PERMISSION_DENIED.getCode()));
  }

  // ===== update: system:menu:update =====
  @Test
  @DisplayName("管理员可更新菜单")
  void admin_can_update() throws Exception {
    mockMvc
        .perform(
            put("/app/menus/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new MenuUpdateDTO("新名称", "/new-path", "Component", "icon", 2, (byte) 1, "code", (byte) 1, (byte) 1))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("无权限用户更新菜单被拒绝(code=4001)")
  void user_cannot_update() throws Exception {
    stpUtilMock.when(() -> StpUtil.hasPermission(anyString())).thenReturn(false);
    mockMvc
        .perform(
            put("/app/menus/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new MenuUpdateDTO("新名称", "/new-path", "Component", "icon", 2, (byte) 1, "code", (byte) 1, (byte) 1))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.PERMISSION_DENIED.getCode()));
  }

  // ===== deleteById: system:menu:delete =====
  @Test
  @DisplayName("管理员可删除菜单")
  void admin_can_delete() throws Exception {
    mockMvc.perform(delete("/app/menus/1")).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("无权限用户删除菜单被拒绝(code=4001)")
  void user_cannot_delete() throws Exception {
    stpUtilMock.when(() -> StpUtil.hasPermission(anyString())).thenReturn(false);
    mockMvc
        .perform(delete("/app/menus/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.PERMISSION_DENIED.getCode()));
  }

  // ===== findUserMenuTree: 方法级 @PermitAll（逃生舱） =====
  @Test
  @DisplayName("管理员可获取自身菜单树(@PermitAll)")
  void admin_can_get_user_menu_tree() throws Exception {
    mockMvc
        .perform(get("/app/menus/user"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("无权限用户亦可获取自身菜单树(@PermitAll)")
  void user_can_get_user_menu_tree() throws Exception {
    stpUtilMock.when(() -> StpUtil.hasPermission(anyString())).thenReturn(false);
    mockMvc
        .perform(get("/app/menus/user"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }
}
