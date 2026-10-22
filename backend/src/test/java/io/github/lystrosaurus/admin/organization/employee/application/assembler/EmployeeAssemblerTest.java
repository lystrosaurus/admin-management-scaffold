package io.github.lystrosaurus.admin.organization.employee.application.assembler;

import static org.junit.jupiter.api.Assertions.*;

import io.github.lystrosaurus.admin.organization.employee.adapter.response.EmployeeDetailResponse;
import io.github.lystrosaurus.admin.organization.employee.adapter.response.EmployeeOrgResponse;
import io.github.lystrosaurus.admin.organization.employee.adapter.response.EmployeeResponse;
import io.github.lystrosaurus.admin.organization.employee.domain.model.Employee;
import io.github.lystrosaurus.admin.organization.employee.domain.model.EmployeeId;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** EmployeeAssembler 单元测试 */
@DisplayName("EmployeeAssembler 测试")
class EmployeeAssemblerTest {

  private final EmployeeAssembler assembler = new EmployeeAssembler();

  @Test
  @DisplayName("应该将员工聚合转换为 EmployeeResponse")
  void should_convert_employee_to_response() {
    // Given
    Employee employee =
        Employee.restore(
            EmployeeId.of(1L),
            "EMP001",
            "张三",
            "三哥",
            "13800138000",
            "zhangsan@example.com",
            100L,
            10L,
            "高级工程师",
            "ACTIVE",
            LocalDate.of(2024, 1, 15),
            null,
            "MANUAL",
            LocalDateTime.of(2024, 1, 15, 10, 30),
            0);

    // When
    EmployeeResponse response = assembler.toResponse(employee, "技术部", "linked_user");

    // Then
    assertNotNull(response);
    assertEquals(1L, response.id());
    assertEquals("EMP001", response.employeeNo());
    assertEquals("张三", response.name());
    assertEquals("三哥", response.preferredName());
    assertEquals("13800138000", response.mobile());
    assertEquals("zhangsan@example.com", response.email());
    assertEquals("高级工程师", response.jobTitle());
    assertEquals("ACTIVE", response.employmentStatus());
    assertEquals(100L, response.primaryOrgId());
    assertEquals("技术部", response.orgUnitName());
    assertEquals(LocalDate.of(2024, 1, 15), response.entryDate());
    assertNull(response.leaveDate());
    assertEquals(LocalDateTime.of(2024, 1, 15, 10, 30), response.createdAt());
    assertEquals(10L, response.userId());
    assertEquals("linked_user", response.username());
  }

  @Test
  @DisplayName("应该将员工聚合转换为 EmployeeDetailResponse（含 orgs）")
  void should_convert_employee_to_detail_response_with_orgs() {
    // Given
    Employee employee =
        Employee.restore(
            EmployeeId.of(1L),
            "EMP001",
            "张三",
            null,
            null,
            null,
            null,
            null,
            null,
            "ACTIVE",
            null,
            null,
            "MANUAL",
            LocalDateTime.now(),
            null);
    EmployeeOrgResponse org =
        new EmployeeOrgResponse(9L, 100L, "技术部", 1, "工程师", null, null, "ACTIVE");

    // When
    EmployeeDetailResponse response =
        assembler.toDetailResponse(employee, null, List.of(org), null);

    // Then
    assertNotNull(response);
    assertEquals(1L, response.id());
    assertEquals("EMP001", response.employeeNo());
    assertEquals("张三", response.name());
    assertNull(response.orgUnitName());
    assertEquals(1, response.orgs().size());
    assertEquals("技术部", response.orgs().get(0).orgName());
    assertNull(response.userId());
    assertNull(response.username());
  }

  @Test
  @DisplayName("可选字段为空时应原样返回 null")
  void should_keep_null_optional_fields() {
    Employee employee =
        Employee.restore(
            EmployeeId.of(2L),
            "EMP002",
            "李四",
            null,
            null,
            null,
            null,
            null,
            null,
            "ACTIVE",
            null,
            null,
            "MANUAL",
            null,
            null);

    EmployeeResponse response = assembler.toResponse(employee, null, null);

    assertNull(response.preferredName());
    assertNull(response.mobile());
    assertNull(response.email());
    assertNull(response.orgUnitName());
    assertNull(response.createdAt());
    assertNull(response.userId());
    assertNull(response.username());
  }

  @Test
  @DisplayName("空组织关联应转换为空列表")
  void should_convert_empty_orgs() {
    Employee employee =
        Employee.restore(
            EmployeeId.of(3L),
            "EMP003",
            "王五",
            null,
            null,
            null,
            null,
            null,
            null,
            "ACTIVE",
            null,
            null,
            "MANUAL",
            null,
            null);

    EmployeeDetailResponse response =
        assembler.toDetailResponse(employee, null, List.of(), null);

    assertNotNull(response.orgs());
    assertTrue(response.orgs().isEmpty());
  }
}
