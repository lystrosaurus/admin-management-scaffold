package io.github.lystrosaurus.admin.system.user.adapter.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
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
import io.github.lystrosaurus.admin.system.user.adapter.request.ChangePasswordRequest;
import io.github.lystrosaurus.admin.system.user.adapter.request.EmployeeBindRequest;
import io.github.lystrosaurus.admin.system.user.adapter.request.UserCreateRequest;
import io.github.lystrosaurus.admin.system.user.adapter.request.UserUpdateRequest;
import io.github.lystrosaurus.admin.system.user.application.UserApplicationService;
import io.github.lystrosaurus.admin.system.user.application.command.BindEmployeeCommand;
import io.github.lystrosaurus.admin.system.user.application.command.ChangePasswordCommand;
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
 * UserController 授权回归测试。
 *
 * <p>管理员（hasPermission=true）→ code 200；无权限用户（hasPermission=false）→ code 4001。
 */
@DisplayName("UserController 授权回归测试")
class UserControllerAuthzTest extends BaseTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @MockitoBean private UserApplicationService userApplicationService;

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

  // ===== create: system:user:create =====
  @Test
  @DisplayName("管理员可创建用户")
  void admin_can_create_user() throws Exception {
    mockMvc
        .perform(
            post("/app/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new UserCreateRequest("admin", "password123", "管理员", "13800138000", "a@e.com"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("无权限用户创建用户被拒绝(code=4001)")
  void user_cannot_create_user() throws Exception {
    stpUtilMock.when(() -> StpUtil.hasPermission(anyString())).thenReturn(false);
    mockMvc
        .perform(
            post("/app/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new UserCreateRequest("admin", "password123", "管理员", "13800138000", "a@e.com"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.PERMISSION_DENIED.getCode()));
  }

  // ===== findById: system:user:view =====
  @Test
  @DisplayName("管理员可查看用户")
  void admin_can_view_user() throws Exception {
    mockMvc.perform(get("/app/users/1")).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("无权限用户查看用户被拒绝(code=4001)")
  void user_cannot_view_user() throws Exception {
    stpUtilMock.when(() -> StpUtil.hasPermission(anyString())).thenReturn(false);
    mockMvc
        .perform(get("/app/users/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.PERMISSION_DENIED.getCode()));
  }

  // ===== findPage: system:user:view =====
  @Test
  @DisplayName("管理员可分页查询用户")
  void admin_can_find_page() throws Exception {
    mockMvc
        .perform(get("/app/users").param("page", "1").param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("无权限用户分页查询用户被拒绝(code=4001)")
  void user_cannot_find_page() throws Exception {
    stpUtilMock.when(() -> StpUtil.hasPermission(anyString())).thenReturn(false);
    mockMvc
        .perform(get("/app/users").param("page", "1").param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.PERMISSION_DENIED.getCode()));
  }

  // ===== update: system:user:update =====
  @Test
  @DisplayName("管理员可更新用户")
  void admin_can_update_user() throws Exception {
    mockMvc
        .perform(
            put("/app/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new UserUpdateRequest("新昵称", "13900139000", "new@e.com", "ENABLED"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("无权限用户更新用户被拒绝(code=4001)")
  void user_cannot_update_user() throws Exception {
    stpUtilMock.when(() -> StpUtil.hasPermission(anyString())).thenReturn(false);
    mockMvc
        .perform(
            put("/app/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new UserUpdateRequest("新昵称", "13900139000", "new@e.com", "ENABLED"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.PERMISSION_DENIED.getCode()));
  }

  // ===== deleteById: system:user:delete =====
  @Test
  @DisplayName("管理员可删除用户")
  void admin_can_delete_user() throws Exception {
    doNothing().when(userApplicationService).deleteById(any(Long.class));
    mockMvc.perform(delete("/app/users/1")).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("无权限用户删除用户被拒绝(code=4001)")
  void user_cannot_delete_user() throws Exception {
    stpUtilMock.when(() -> StpUtil.hasPermission(anyString())).thenReturn(false);
    mockMvc
        .perform(delete("/app/users/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.PERMISSION_DENIED.getCode()));
  }

  // ===== changePassword: system:user:reset-password =====
  @Test
  @DisplayName("管理员可修改密码")
  void admin_can_change_password() throws Exception {
    doNothing().when(userApplicationService).changePassword(any(Long.class), any(ChangePasswordCommand.class));
    mockMvc
        .perform(
            put("/app/users/1/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ChangePasswordRequest("old", "new"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("无权限用户修改密码被拒绝(code=4001)")
  void user_cannot_change_password() throws Exception {
    stpUtilMock.when(() -> StpUtil.hasPermission(anyString())).thenReturn(false);
    mockMvc
        .perform(
            put("/app/users/1/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ChangePasswordRequest("old", "new"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.PERMISSION_DENIED.getCode()));
  }

  // ===== bindEmployee: system:user:update =====
  @Test
  @DisplayName("管理员可绑定员工")
  void admin_can_bind_employee() throws Exception {
    doNothing().when(userApplicationService).bindEmployee(any(Long.class), any(BindEmployeeCommand.class));
    mockMvc
        .perform(
            post("/app/users/1/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new EmployeeBindRequest(100L))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("无权限用户绑定员工被拒绝(code=4001)")
  void user_cannot_bind_employee() throws Exception {
    stpUtilMock.when(() -> StpUtil.hasPermission(anyString())).thenReturn(false);
    mockMvc
        .perform(
            post("/app/users/1/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new EmployeeBindRequest(100L))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.PERMISSION_DENIED.getCode()));
  }
}
