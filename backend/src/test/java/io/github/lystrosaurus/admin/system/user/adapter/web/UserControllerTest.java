package io.github.lystrosaurus.admin.system.user.adapter.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.lystrosaurus.admin.shared.adapter.web.PageResult;
import io.github.lystrosaurus.admin.shared.domain.exception.DomainException;
import io.github.lystrosaurus.admin.system.user.adapter.request.ChangePasswordRequest;
import io.github.lystrosaurus.admin.system.user.adapter.request.EmployeeBindRequest;
import io.github.lystrosaurus.admin.system.user.adapter.request.UserCreateRequest;
import io.github.lystrosaurus.admin.system.user.adapter.request.UserUpdateRequest;
import io.github.lystrosaurus.admin.system.user.adapter.response.UserDetailResponse;
import io.github.lystrosaurus.admin.system.user.adapter.response.UserResponse;
import io.github.lystrosaurus.admin.system.user.application.UserApplicationService;
import io.github.lystrosaurus.admin.system.user.application.command.BindEmployeeCommand;
import io.github.lystrosaurus.admin.system.user.application.command.ChangePasswordCommand;
import io.github.lystrosaurus.admin.system.user.application.command.UserCreateCommand;
import io.github.lystrosaurus.admin.system.user.application.command.UserUpdateCommand;
import io.github.lystrosaurus.admin.system.user.domain.model.UserErrorCode;
import io.github.lystrosaurus.admin.test.SaTokenTest;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

/**
 * UserController 测试
 *
 * <p>测试用户控制器的各个接口
 */
@DisplayName("UserController 测试")
class UserControllerTest extends SaTokenTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private UserApplicationService userApplicationService;

  private UserResponse userResponse;
  private UserDetailResponse userDetailResponse;

  @BeforeEach
  void setUp() {
    userResponse =
        new UserResponse(
            1L,
            "admin",
            "管理员",
            "13800138000",
            "admin@example.com",
            "ENABLED",
            LocalDateTime.now(),
            LocalDateTime.now());

    userDetailResponse =
        new UserDetailResponse(
            1L,
            "admin",
            "管理员",
            "13800138000",
            "admin@example.com",
            "ENABLED",
            null,
            0,
            LocalDateTime.now(),
            "127.0.0.1",
            List.of(),
            LocalDateTime.now());
  }

  @Test
  @DisplayName("应该成功创建用户")
  void should_create_user_successfully() throws Exception {
    UserCreateRequest createRequest =
        new UserCreateRequest("admin", "password123", "管理员", "13800138000", "admin@example.com");
    when(userApplicationService.create(any(UserCreateCommand.class))).thenReturn(userResponse);

    mockMvc
        .perform(
            post("/app/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.message").value("操作成功"))
        .andExpect(jsonPath("$.data.username").value("admin"))
        .andExpect(jsonPath("$.data.nickname").value("管理员"));
  }

  @Test
  @DisplayName("应该返回400当用户名为空")
  void should_return_400_when_username_is_blank() throws Exception {
    UserCreateRequest invalidRequest =
        new UserCreateRequest("", "password123", "管理员", "13800138000", "admin@example.com");

    mockMvc
        .perform(
            post("/app/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("应该返回400当密码为空")
  void should_return_400_when_password_is_blank() throws Exception {
    UserCreateRequest invalidRequest =
        new UserCreateRequest("admin", "", "管理员", "13800138000", "admin@example.com");

    mockMvc
        .perform(
            post("/app/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("应该成功获取用户详情")
  void should_get_user_detail_successfully() throws Exception {
    when(userApplicationService.findById(1L)).thenReturn(userDetailResponse);

    mockMvc
        .perform(get("/app/users/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.username").value("admin"));
  }

  @Test
  @DisplayName("应该返回404当用户不存在")
  void should_return_404_when_user_not_found() throws Exception {
    when(userApplicationService.findById(999L))
        .thenThrow(new DomainException(UserErrorCode.USER_NOT_FOUND));

    mockMvc
        .perform(get("/app/users/999"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(UserErrorCode.USER_NOT_FOUND.code()));
  }

  @Test
  @DisplayName("应该成功更新用户")
  void should_update_user_successfully() throws Exception {
    UserUpdateRequest updateRequest =
        new UserUpdateRequest("新昵称", "13900139000", "new@example.com", "ENABLED");
    UserResponse updatedResponse =
        new UserResponse(
            1L,
            "admin",
            "新昵称",
            "13900139000",
            "new@example.com",
            "ENABLED",
            LocalDateTime.now(),
            LocalDateTime.now());
    when(userApplicationService.update(eq(1L), any(UserUpdateCommand.class)))
        .thenReturn(updatedResponse);

    mockMvc
        .perform(
            put("/app/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data.nickname").value("新昵称"));
  }

  @Test
  @DisplayName("应该成功删除用户")
  void should_delete_user_successfully() throws Exception {
    doNothing().when(userApplicationService).deleteById(1L);

    mockMvc
        .perform(delete("/app/users/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("应该成功分页查询用户")
  void should_find_page_successfully() throws Exception {
    PageResult<UserResponse> pageResult = new PageResult<>(List.of(userResponse), 1, 1, 10);
    when(userApplicationService.findPage(any(), eq(1), eq(10))).thenReturn(pageResult);

    mockMvc
        .perform(get("/app/users").param("page", "1").param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data.items").isArray())
        .andExpect(jsonPath("$.data.total").value(1));
  }

  @Test
  @DisplayName("应该成功修改密码")
  void should_change_password_successfully() throws Exception {
    ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("oldPassword", "newPassword");
    doNothing()
        .when(userApplicationService)
        .changePassword(eq(1L), any(ChangePasswordCommand.class));

    mockMvc
        .perform(
            put("/app/users/1/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changePasswordRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("应该返回400当旧密码为空")
  void should_return_400_when_old_password_is_blank() throws Exception {
    ChangePasswordRequest invalidRequest = new ChangePasswordRequest("", "newPassword");

    mockMvc
        .perform(
            put("/app/users/1/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("应该返回400当新密码为空")
  void should_return_400_when_new_password_is_blank() throws Exception {
    ChangePasswordRequest invalidRequest = new ChangePasswordRequest("oldPassword", "");

    mockMvc
        .perform(
            put("/app/users/1/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }

  // ==================== 员工绑定测试 ====================

  @Test
  @DisplayName("应该成功绑定员工")
  void should_bind_employee_successfully() throws Exception {
    EmployeeBindRequest bindRequest = new EmployeeBindRequest(100L);
    doNothing().when(userApplicationService).bindEmployee(eq(1L), any(BindEmployeeCommand.class));

    mockMvc
        .perform(
            post("/app/users/1/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bindRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("应该返回400当员工ID为空")
  void should_return_400_when_employee_id_is_null() throws Exception {
    // 构造 employeeId 为 null 的 JSON
    mockMvc
        .perform(
            post("/app/users/1/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"employeeId\":null}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("应该返回错误码当员工不存在")
  void should_return_error_when_employee_not_found() throws Exception {
    EmployeeBindRequest bindRequest = new EmployeeBindRequest(999L);
    doThrow(new DomainException(UserErrorCode.EMPLOYEE_NOT_FOUND))
        .when(userApplicationService)
        .bindEmployee(eq(1L), any(BindEmployeeCommand.class));

    mockMvc
        .perform(
            post("/app/users/1/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bindRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(UserErrorCode.EMPLOYEE_NOT_FOUND.code()));
  }

  @Test
  @DisplayName("应该返回错误码当员工已被其他用户绑定")
  void should_return_error_when_employee_already_bound() throws Exception {
    EmployeeBindRequest bindRequest = new EmployeeBindRequest(100L);
    doThrow(new DomainException(UserErrorCode.USER_EMPLOYEE_ALREADY_BOUND))
        .when(userApplicationService)
        .bindEmployee(eq(2L), any(BindEmployeeCommand.class));

    mockMvc
        .perform(
            post("/app/users/2/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bindRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(UserErrorCode.USER_EMPLOYEE_ALREADY_BOUND.code()));
  }

  @Test
  @DisplayName("应该成功解绑员工")
  void should_unbind_employee_successfully() throws Exception {
    doNothing().when(userApplicationService).unbindEmployee(1L);

    mockMvc
        .perform(delete("/app/users/1/employee"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("应该返回错误码当解绑时用户不存在")
  void should_return_error_when_unbind_user_not_found() throws Exception {
    doThrow(new DomainException(UserErrorCode.USER_NOT_FOUND))
        .when(userApplicationService)
        .unbindEmployee(999L);

    mockMvc
        .perform(delete("/app/users/999/employee"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(UserErrorCode.USER_NOT_FOUND.code()));
  }
}
