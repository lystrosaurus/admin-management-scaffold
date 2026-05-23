package io.github.lystrosaurus.admin.system.user.controller;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.lystrosaurus.admin.common.PageResult;
import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.system.user.dto.ChangePasswordDTO;
import io.github.lystrosaurus.admin.system.user.dto.EmployeeBindDTO;
import io.github.lystrosaurus.admin.system.user.dto.UserCreateDTO;
import io.github.lystrosaurus.admin.system.user.dto.UserUpdateDTO;
import io.github.lystrosaurus.admin.system.user.service.UserService;
import io.github.lystrosaurus.admin.system.user.vo.UserDetailVO;
import io.github.lystrosaurus.admin.system.user.vo.UserVO;
import io.github.lystrosaurus.admin.test.SaTokenTest;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * UserController 测试
 *
 * <p>测试用户控制器的各个接口
 */
@DisplayName("UserController 测试")
class UserControllerTest extends SaTokenTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private UserService userService;

  private UserVO userVO;
  private UserDetailVO userDetailVO;

  @BeforeEach
  void setUp() {
    userVO =
        new UserVO(
            1L,
            "admin",
            "管理员",
            "13800138000",
            "admin@example.com",
            "ENABLED",
            LocalDateTime.now(),
            LocalDateTime.now());

    userDetailVO =
        new UserDetailVO(
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
    UserCreateDTO createDTO =
        new UserCreateDTO("admin", "password123", "管理员", "13800138000", "admin@example.com");
    when(userService.create(any(UserCreateDTO.class))).thenReturn(userVO);

    mockMvc
        .perform(
            post("/app/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.message").value("操作成功"))
        .andExpect(jsonPath("$.data.username").value("admin"))
        .andExpect(jsonPath("$.data.nickname").value("管理员"));
  }

  @Test
  @DisplayName("应该返回400当用户名为空")
  void should_return_400_when_username_is_blank() throws Exception {
    UserCreateDTO invalidDTO =
        new UserCreateDTO("", "password123", "管理员", "13800138000", "admin@example.com");

    mockMvc
        .perform(
            post("/app/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("应该返回400当密码为空")
  void should_return_400_when_password_is_blank() throws Exception {
    UserCreateDTO invalidDTO =
        new UserCreateDTO("admin", "", "管理员", "13800138000", "admin@example.com");

    mockMvc
        .perform(
            post("/app/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("应该成功获取用户详情")
  void should_get_user_detail_successfully() throws Exception {
    when(userService.findById(1L)).thenReturn(userDetailVO);

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
    when(userService.findById(999L)).thenThrow(new BusinessException(ErrorCode.USER_NOT_FOUND));

    mockMvc
        .perform(get("/app/users/999"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.USER_NOT_FOUND.getCode()));
  }

  @Test
  @DisplayName("应该成功更新用户")
  void should_update_user_successfully() throws Exception {
    UserUpdateDTO updateDTO = new UserUpdateDTO("新昵称", "13900139000", "new@example.com", "ENABLED");
    UserVO updatedUserVO =
        new UserVO(
            1L,
            "admin",
            "新昵称",
            "13900139000",
            "new@example.com",
            "ENABLED",
            LocalDateTime.now(),
            LocalDateTime.now());
    when(userService.update(eq(1L), any(UserUpdateDTO.class))).thenReturn(updatedUserVO);

    mockMvc
        .perform(
            put("/app/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data.nickname").value("新昵称"));
  }

  @Test
  @DisplayName("应该成功删除用户")
  void should_delete_user_successfully() throws Exception {
    doNothing().when(userService).deleteById(1L);

    mockMvc
        .perform(delete("/app/users/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("应该成功分页查询用户")
  void should_find_page_successfully() throws Exception {
    PageResult<UserVO> pageResult = new PageResult<>(List.of(userVO), 1, 1, 10);
    when(userService.findPage(any(), eq(1), eq(10))).thenReturn(pageResult);

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
    ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO("oldPassword", "newPassword");
    doNothing().when(userService).changePassword(eq(1L), any(ChangePasswordDTO.class));

    mockMvc
        .perform(
            put("/app/users/1/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changePasswordDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("应该返回400当旧密码为空")
  void should_return_400_when_old_password_is_blank() throws Exception {
    ChangePasswordDTO invalidDTO = new ChangePasswordDTO("", "newPassword");

    mockMvc
        .perform(
            put("/app/users/1/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("应该返回400当新密码为空")
  void should_return_400_when_new_password_is_blank() throws Exception {
    ChangePasswordDTO invalidDTO = new ChangePasswordDTO("oldPassword", "");

    mockMvc
        .perform(
            put("/app/users/1/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
        .andExpect(status().isBadRequest());
  }

  // ==================== 员工绑定测试 ====================

  @Test
  @DisplayName("应该成功绑定员工")
  void should_bind_employee_successfully() throws Exception {
    EmployeeBindDTO bindDTO = new EmployeeBindDTO(100L);
    doNothing().when(userService).bindEmployee(1L, 100L);

    mockMvc
        .perform(
            post("/app/users/1/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bindDTO)))
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
    EmployeeBindDTO bindDTO = new EmployeeBindDTO(999L);
    doThrow(new BusinessException(ErrorCode.USER_EMPLOYEE_NOT_FOUND))
        .when(userService)
        .bindEmployee(1L, 999L);

    mockMvc
        .perform(
            post("/app/users/1/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bindDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.USER_EMPLOYEE_NOT_FOUND.getCode()));
  }

  @Test
  @DisplayName("应该返回错误码当员工已被其他用户绑定")
  void should_return_error_when_employee_already_bound() throws Exception {
    EmployeeBindDTO bindDTO = new EmployeeBindDTO(100L);
    doThrow(new BusinessException(ErrorCode.USER_EMPLOYEE_ALREADY_BOUND))
        .when(userService)
        .bindEmployee(2L, 100L);

    mockMvc
        .perform(
            post("/app/users/2/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bindDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.USER_EMPLOYEE_ALREADY_BOUND.getCode()));
  }

  @Test
  @DisplayName("应该成功解绑员工")
  void should_unbind_employee_successfully() throws Exception {
    doNothing().when(userService).unbindEmployee(1L);

    mockMvc
        .perform(delete("/app/users/1/employee"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("应该返回错误码当解绑时用户不存在")
  void should_return_error_when_unbind_user_not_found() throws Exception {
    doThrow(new BusinessException(ErrorCode.USER_NOT_FOUND)).when(userService).unbindEmployee(999L);

    mockMvc
        .perform(delete("/app/users/999/employee"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.USER_NOT_FOUND.getCode()));
  }
}
