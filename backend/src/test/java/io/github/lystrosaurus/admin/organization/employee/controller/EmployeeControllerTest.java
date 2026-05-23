package io.github.lystrosaurus.admin.organization.employee.controller;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.lystrosaurus.admin.common.PageResult;
import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.organization.employee.dto.EmployeeCreateDTO;
import io.github.lystrosaurus.admin.organization.employee.dto.EmployeeUpdateDTO;
import io.github.lystrosaurus.admin.organization.employee.service.EmployeeService;
import io.github.lystrosaurus.admin.organization.employee.vo.EmployeeDetailVO;
import io.github.lystrosaurus.admin.organization.employee.vo.EmployeeVO;
import io.github.lystrosaurus.admin.test.SaTokenTest;
import java.time.LocalDate;
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
 * EmployeeController 测试
 *
 * <p>测试员工控制器的各个接口
 */
@DisplayName("EmployeeController 测试")
class EmployeeControllerTest extends SaTokenTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private EmployeeService employeeService;

  private EmployeeVO employeeVO;
  private EmployeeDetailVO employeeDetailVO;

  @BeforeEach
  void setUp() {
    employeeVO =
        new EmployeeVO(
            1L,
            "EMP001",
            "张三",
            "三哥",
            "13800138000",
            "zhangsan@example.com",
            "高级工程师",
            "ACTIVE",
            100L,
            LocalDate.of(2024, 1, 15),
            null,
            LocalDateTime.now());

    employeeDetailVO =
        new EmployeeDetailVO(
            1L,
            "EMP001",
            "张三",
            "三哥",
            "13800138000",
            "zhangsan@example.com",
            "高级工程师",
            "ACTIVE",
            100L,
            LocalDate.of(2024, 1, 15),
            null,
            LocalDateTime.now(),
            List.of());
  }

  @Test
  @DisplayName("应该成功创建员工")
  void should_create_employee_successfully() throws Exception {
    EmployeeCreateDTO createDTO =
        new EmployeeCreateDTO(
            "EMP001",
            "张三",
            "13800138000",
            "zhangsan@example.com",
            "高级工程师",
            100L,
            LocalDate.of(2024, 1, 15));
    when(employeeService.create(any(EmployeeCreateDTO.class))).thenReturn(employeeVO);

    mockMvc
        .perform(
            post("/app/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.message").value("操作成功"))
        .andExpect(jsonPath("$.data.employeeNo").value("EMP001"))
        .andExpect(jsonPath("$.data.name").value("张三"));
  }

  @Test
  @DisplayName("应该返回400当员工工号为空")
  void should_return_400_when_employee_no_is_blank() throws Exception {
    EmployeeCreateDTO invalidDTO =
        new EmployeeCreateDTO("", "张三", "13800138000", null, null, null, null);

    mockMvc
        .perform(
            post("/app/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("应该返回400当员工姓名为空")
  void should_return_400_when_name_is_blank() throws Exception {
    EmployeeCreateDTO invalidDTO =
        new EmployeeCreateDTO("EMP001", "", "13800138000", null, null, null, null);

    mockMvc
        .perform(
            post("/app/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("应该成功获取员工详情")
  void should_get_employee_detail_successfully() throws Exception {
    when(employeeService.findById(1L)).thenReturn(employeeDetailVO);

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
    when(employeeService.findById(999L))
        .thenThrow(new BusinessException(ErrorCode.EMPLOYEE_NOT_FOUND));

    mockMvc
        .perform(get("/app/employees/999"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.EMPLOYEE_NOT_FOUND.getCode()));
  }

  @Test
  @DisplayName("应该成功更新员工")
  void should_update_employee_successfully() throws Exception {
    EmployeeUpdateDTO updateDTO =
        new EmployeeUpdateDTO("张三丰", null, "13900139000", null, "技术总监", null, null, null, null);
    EmployeeVO updatedVO =
        new EmployeeVO(
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
            LocalDateTime.now());
    when(employeeService.update(eq(1L), any(EmployeeUpdateDTO.class))).thenReturn(updatedVO);

    mockMvc
        .perform(
            put("/app/employees/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data.name").value("张三丰"))
        .andExpect(jsonPath("$.data.jobTitle").value("技术总监"));
  }

  @Test
  @DisplayName("应该成功删除员工")
  void should_delete_employee_successfully() throws Exception {
    doNothing().when(employeeService).deleteById(1L);

    mockMvc
        .perform(delete("/app/employees/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("应该成功分页查询员工")
  void should_find_page_successfully() throws Exception {
    PageResult<EmployeeVO> pageResult = new PageResult<>(List.of(employeeVO), 1, 1, 10);
    when(employeeService.findPage(any(), eq(1), eq(10))).thenReturn(pageResult);

    mockMvc
        .perform(get("/app/employees").param("page", "1").param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data.items").isArray())
        .andExpect(jsonPath("$.data.total").value(1));
  }
}
