package io.github.lystrosaurus.admin.organization.employee.adapter.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.lystrosaurus.admin.common.PageResult;
import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.organization.employee.adapter.request.EmployeeCreateRequest;
import io.github.lystrosaurus.admin.organization.employee.adapter.request.EmployeeUpdateRequest;
import io.github.lystrosaurus.admin.organization.employee.adapter.response.EmployeeDetailResponse;
import io.github.lystrosaurus.admin.organization.employee.adapter.response.EmployeeResponse;
import io.github.lystrosaurus.admin.organization.employee.application.EmployeeApplicationService;
import io.github.lystrosaurus.admin.organization.employee.application.command.EmployeeCreateCommand;
import io.github.lystrosaurus.admin.organization.employee.application.command.EmployeeUpdateCommand;
import io.github.lystrosaurus.admin.test.SaTokenTest;
import java.time.LocalDate;
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
 * EmployeeController 测试
 *
 * <p>测试员工控制器的各个接口
 */
@DisplayName("EmployeeController 测试")
class EmployeeControllerTest extends SaTokenTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private EmployeeApplicationService employeeApplicationService;

  private EmployeeResponse employeeResponse;
  private EmployeeDetailResponse employeeDetailResponse;

  @BeforeEach
  void setUp() {
    employeeResponse =
        new EmployeeResponse(
            1L,
            "EMP001",
            "张三",
            "三哥",
            "13800138000",
            "zhangsan@example.com",
            "高级工程师",
            "ACTIVE",
            100L,
            null,
            LocalDate.of(2024, 1, 15),
            null,
            LocalDateTime.now(),
            null,
            null);

    employeeDetailResponse =
        new EmployeeDetailResponse(
            1L,
            "EMP001",
            "张三",
            "三哥",
            "13800138000",
            "zhangsan@example.com",
            "高级工程师",
            "ACTIVE",
            100L,
            null,
            LocalDate.of(2024, 1, 15),
            null,
            LocalDateTime.now(),
            List.of(),
            null,
            null);
  }

  @Test
  @DisplayName("应该成功创建员工")
  void should_create_employee_successfully() throws Exception {
    EmployeeCreateRequest createRequest =
        new EmployeeCreateRequest(
            "EMP001",
            "张三",
            "13800138000",
            "zhangsan@example.com",
            "高级工程师",
            100L,
            LocalDate.of(2024, 1, 15),
            null);
    when(employeeApplicationService.create(any(EmployeeCreateCommand.class)))
        .thenReturn(employeeResponse);

    mockMvc
        .perform(
            post("/app/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.message").value("操作成功"))
        .andExpect(jsonPath("$.data.employeeNo").value("EMP001"))
        .andExpect(jsonPath("$.data.name").value("张三"));
  }

  @Test
  @DisplayName("应该返回400当员工工号为空")
  void should_return_400_when_employee_no_is_blank() throws Exception {
    EmployeeCreateRequest invalidRequest =
        new EmployeeCreateRequest("", "张三", "13800138000", null, null, null, null, null);

    mockMvc
        .perform(
            post("/app/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("应该返回400当员工姓名为空")
  void should_return_400_when_name_is_blank() throws Exception {
    EmployeeCreateRequest invalidRequest =
        new EmployeeCreateRequest("EMP001", "", "13800138000", null, null, null, null, null);

    mockMvc
        .perform(
            post("/app/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("应该成功获取员工详情")
  void should_get_employee_detail_successfully() throws Exception {
    when(employeeApplicationService.findById(1L)).thenReturn(employeeDetailResponse);

    mockMvc
        .perform(get("/app/employees/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.employeeNo").value("EMP001"))
        .andExpect(jsonPath("$.data.name").value("张三"));
  }

  @Test
  @DisplayName("应该返回错误码当员工不存在")
  void should_return_error_when_employee_not_found() throws Exception {
    when(employeeApplicationService.findById(999L))
        .thenThrow(new BusinessException(ErrorCode.EMPLOYEE_NOT_FOUND));

    mockMvc
        .perform(get("/app/employees/999"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.EMPLOYEE_NOT_FOUND.getCode()));
  }

  @Test
  @DisplayName("应该成功更新员工")
  void should_update_employee_successfully() throws Exception {
    EmployeeUpdateRequest updateRequest =
        new EmployeeUpdateRequest(
            "张三丰", null, "13900139000", null, "技术总监", null, null, null, null, null);
    EmployeeResponse updatedResponse =
        new EmployeeResponse(
            1L,
            "EMP001",
            "张三丰",
            null,
            "13900139000",
            null,
            "技术总监",
            "ACTIVE",
            100L,
            null,
            null,
            null,
            LocalDateTime.now(),
            null,
            null);
    when(employeeApplicationService.update(eq(1L), any(EmployeeUpdateCommand.class)))
        .thenReturn(updatedResponse);

    mockMvc
        .perform(
            put("/app/employees/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data.name").value("张三丰"))
        .andExpect(jsonPath("$.data.jobTitle").value("技术总监"));
  }

  @Test
  @DisplayName("应该成功删除员工")
  void should_delete_employee_successfully() throws Exception {
    doNothing().when(employeeApplicationService).deleteById(1L);

    mockMvc
        .perform(delete("/app/employees/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("应该成功分页查询员工")
  void should_find_page_successfully() throws Exception {
    PageResult<EmployeeResponse> pageResult =
        new PageResult<>(List.of(employeeResponse), 1, 1, 10);
    when(employeeApplicationService.findPage(any(), eq(1), eq(10))).thenReturn(pageResult);

    mockMvc
        .perform(get("/app/employees").param("page", "1").param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data.items").isArray())
        .andExpect(jsonPath("$.data.total").value(1));
  }
}
