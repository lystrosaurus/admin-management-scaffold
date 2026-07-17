package io.github.lystrosaurus.admin.organization.employee.controller;

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
import io.github.lystrosaurus.admin.organization.employee.dto.EmployeeCreateDTO;
import io.github.lystrosaurus.admin.organization.employee.dto.EmployeeUpdateDTO;
import io.github.lystrosaurus.admin.organization.employee.service.EmployeeService;
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
 * EmployeeController 授权回归测试。
 *
 * <p>管理员（hasPermission=true）→ code 200；无权限用户（hasPermission=false）→ code 4001。
 * create 需 organization:employee:create；findById/findPage 需 organization:employee:view；update 需 organization:employee:update；delete 需 organization:employee:delete。
 */
@DisplayName("EmployeeController 授权回归测试")
class EmployeeControllerAuthzTest extends BaseTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @MockitoBean private EmployeeService employeeService;

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

  // ===== create: organization:employee:create =====
  @Test
  @DisplayName("管理员可创建员工")
  void admin_can_create() throws Exception {
    mockMvc
        .perform(
            post("/app/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new EmployeeCreateDTO("E001", "张三", null, null, null, null, null, null))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("无权限用户创建员工被拒绝(code=4001)")
  void user_cannot_create() throws Exception {
    stpUtilMock.when(() -> StpUtil.hasPermission(anyString())).thenReturn(false);
    mockMvc
        .perform(
            post("/app/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new EmployeeCreateDTO("E001", "张三", null, null, null, null, null, null))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.PERMISSION_DENIED.getCode()));
  }

  // ===== findById / findPage: organization:employee:view =====
  @Test
  @DisplayName("管理员可查询员工详情")
  void admin_can_find_by_id() throws Exception {
    mockMvc.perform(get("/app/employees/1")).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("无权限用户查询员工详情被拒绝(code=4001)")
  void user_cannot_find_by_id() throws Exception {
    stpUtilMock.when(() -> StpUtil.hasPermission(anyString())).thenReturn(false);
    mockMvc.perform(get("/app/employees/1")).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(ErrorCode.PERMISSION_DENIED.getCode()));
  }

  @Test
  @DisplayName("管理员可分页查询员工")
  void admin_can_find_page() throws Exception {
    mockMvc.perform(get("/app/employees")).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("无权限用户分页查询员工被拒绝(code=4001)")
  void user_cannot_find_page() throws Exception {
    stpUtilMock.when(() -> StpUtil.hasPermission(anyString())).thenReturn(false);
    mockMvc.perform(get("/app/employees")).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(ErrorCode.PERMISSION_DENIED.getCode()));
  }

  // ===== update: organization:employee:update =====
  @Test
  @DisplayName("管理员可更新员工")
  void admin_can_update() throws Exception {
    mockMvc
        .perform(
            put("/app/employees/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new EmployeeUpdateDTO(
                            null, null, null, null, null, null, null, null, null, null))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("无权限用户更新员工被拒绝(code=4001)")
  void user_cannot_update() throws Exception {
    stpUtilMock.when(() -> StpUtil.hasPermission(anyString())).thenReturn(false);
    mockMvc
        .perform(
            put("/app/employees/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new EmployeeUpdateDTO(
                            null, null, null, null, null, null, null, null, null, null))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.PERMISSION_DENIED.getCode()));
  }

  // ===== deleteById: organization:employee:delete =====
  @Test
  @DisplayName("管理员可删除员工")
  void admin_can_delete() throws Exception {
    mockMvc.perform(delete("/app/employees/1")).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("无权限用户删除员工被拒绝(code=4001)")
  void user_cannot_delete() throws Exception {
    stpUtilMock.when(() -> StpUtil.hasPermission(anyString())).thenReturn(false);
    mockMvc.perform(delete("/app/employees/1")).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(ErrorCode.PERMISSION_DENIED.getCode()));
  }
}
