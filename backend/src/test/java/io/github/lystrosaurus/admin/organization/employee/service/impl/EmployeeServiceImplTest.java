package io.github.lystrosaurus.admin.organization.employee.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import io.github.lystrosaurus.admin.common.PageResult;
import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.organization.employee.dao.EmployeeDAO;
import io.github.lystrosaurus.admin.organization.employee.dao.EmployeeOrgDAO;
import io.github.lystrosaurus.admin.organization.employee.dto.EmployeeCreateDTO;
import io.github.lystrosaurus.admin.organization.employee.dto.EmployeeQueryDTO;
import io.github.lystrosaurus.admin.organization.employee.dto.EmployeeUpdateDTO;
import io.github.lystrosaurus.admin.organization.employee.entity.EmployeeOrg;
import io.github.lystrosaurus.admin.organization.employee.entity.HrEmployee;
import io.github.lystrosaurus.admin.organization.employee.mapstruct.EmployeeMapper;
import io.github.lystrosaurus.admin.organization.employee.vo.EmployeeDetailVO;
import io.github.lystrosaurus.admin.organization.employee.vo.EmployeeVO;
import io.github.lystrosaurus.admin.organization.orgunit.dao.OrgUnitDAO;
import io.github.lystrosaurus.admin.organization.orgunit.entity.OrgUnit;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** EmployeeServiceImpl 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EmployeeServiceImpl 测试")
class EmployeeServiceImplTest {

  @Mock private EmployeeDAO employeeDAO;

  @Mock private EmployeeOrgDAO employeeOrgDAO;

  @Mock private OrgUnitDAO orgUnitDAO;

  @Mock private EmployeeMapper employeeMapper;

  @InjectMocks private EmployeeServiceImpl employeeService;

  private HrEmployee testEmployee;
  private EmployeeCreateDTO createDTO;
  private EmployeeUpdateDTO updateDTO;
  private EmployeeVO employeeVO;
  private EmployeeDetailVO employeeDetailVO;

  @BeforeEach
  void setUp() {
    testEmployee = new HrEmployee();
    testEmployee.setId(1L);
    testEmployee.setEmployeeNo("EMP001");
    testEmployee.setName("张三");
    testEmployee.setMobile("13800138000");
    testEmployee.setEmail("zhangsan@example.com");
    testEmployee.setPrimaryOrgId(100L);
    testEmployee.setJobTitle("高级工程师");
    testEmployee.setEmploymentStatus("ACTIVE");
    testEmployee.setEntryDate(LocalDate.of(2024, 1, 15));
    testEmployee.setSourceType("MANUAL");
    testEmployee.setCreatedAt(LocalDateTime.now());

    createDTO =
        new EmployeeCreateDTO(
            "EMP001",
            "张三",
            "13800138000",
            "zhangsan@example.com",
            "高级工程师",
            100L,
            LocalDate.of(2024, 1, 15));

    updateDTO =
        new EmployeeUpdateDTO("张三丰", null, "13900139000", null, "技术总监", null, null, null, null);

    employeeVO =
        new EmployeeVO(
            1L,
            "EMP001",
            "张三",
            null,
            "13800138000",
            "zhangsan@example.com",
            "高级工程师",
            "ACTIVE",
            100L,
            null,
            LocalDate.of(2024, 1, 15),
            null,
            LocalDateTime.now());

    employeeDetailVO =
        new EmployeeDetailVO(
            1L,
            "EMP001",
            "张三",
            null,
            "13800138000",
            "zhangsan@example.com",
            "高级工程师",
            "ACTIVE",
            100L,
            null,
            LocalDate.of(2024, 1, 15),
            null,
            LocalDateTime.now(),
            Collections.emptyList());
  }

  @Test
  @DisplayName("应该成功创建员工")
  void should_create_employee_when_employee_no_not_exists() {
    // Given
    when(employeeDAO.existsByEmployeeNo("EMP001")).thenReturn(false);
    when(employeeMapper.toEntity(createDTO)).thenReturn(testEmployee);
    doAnswer(
            invocation -> {
              HrEmployee saved = invocation.getArgument(0);
              saved.setId(1L);
              return null;
            })
        .when(employeeDAO)
        .save(any(HrEmployee.class));
    when(employeeMapper.toVO(any(HrEmployee.class))).thenReturn(employeeVO);

    // When
    EmployeeVO result = employeeService.create(createDTO);

    // Then
    assertNotNull(result);
    assertEquals("EMP001", result.employeeNo());
    assertEquals("张三", result.name());
    verify(employeeDAO).existsByEmployeeNo("EMP001");
    verify(employeeDAO).save(any(HrEmployee.class));
  }

  @Test
  @DisplayName("创建员工时工号已存在应该抛出异常")
  void should_throw_exception_when_employee_no_already_exists() {
    // Given
    when(employeeDAO.existsByEmployeeNo("EMP001")).thenReturn(true);

    // When & Then
    BusinessException exception =
        assertThrows(BusinessException.class, () -> employeeService.create(createDTO));
    assertEquals(ErrorCode.EMPLOYEE_ALREADY_EXISTS.getCode(), exception.getCode());
    verify(employeeDAO).existsByEmployeeNo("EMP001");
    verify(employeeDAO, never()).save(any());
  }

  @Test
  @DisplayName("应该成功更新员工")
  void should_update_employee_when_employee_exists() {
    // Given
    when(employeeDAO.findById(1L)).thenReturn(testEmployee);
    when(employeeMapper.toVO(any(HrEmployee.class))).thenReturn(employeeVO);

    // When
    EmployeeVO result = employeeService.update(1L, updateDTO);

    // Then
    assertNotNull(result);
    verify(employeeDAO).findById(1L);
    verify(employeeDAO).update(any(HrEmployee.class));
    // 验证部分更新字段
    assertEquals("张三丰", testEmployee.getName());
    assertEquals("13900139000", testEmployee.getMobile());
    assertEquals("技术总监", testEmployee.getJobTitle());
  }

  @Test
  @DisplayName("更新员工时员工不存在应该抛出异常")
  void should_throw_exception_when_update_nonexistent_employee() {
    // Given
    when(employeeDAO.findById(999L)).thenReturn(null);

    // When & Then
    BusinessException exception =
        assertThrows(BusinessException.class, () -> employeeService.update(999L, updateDTO));
    assertEquals(ErrorCode.EMPLOYEE_NOT_FOUND.getCode(), exception.getCode());
    verify(employeeDAO).findById(999L);
    verify(employeeDAO, never()).update(any());
  }

  @Test
  @DisplayName("应该成功删除员工")
  void should_delete_employee_by_id() {
    // Given
    when(employeeDAO.findById(1L)).thenReturn(testEmployee);

    // When
    employeeService.deleteById(1L);

    // Then
    verify(employeeDAO).findById(1L);
    verify(employeeDAO).deleteById(1L);
  }

  @Test
  @DisplayName("应该成功查询员工详情（含组织信息）")
  void should_find_employee_detail_by_id() {
    // Given
    EmployeeOrg employeeOrg = new EmployeeOrg();
    employeeOrg.setId(1L);
    employeeOrg.setEmployeeId(1L);
    employeeOrg.setOrgId(100L);
    employeeOrg.setIsPrimary(1);
    employeeOrg.setPositionName("高级工程师");
    employeeOrg.setStartDate(LocalDate.of(2024, 1, 15));
    employeeOrg.setStatus("ACTIVE");

    OrgUnit orgUnit = new OrgUnit();
    orgUnit.setId(100L);
    orgUnit.setName("技术部");

    when(employeeDAO.findById(1L)).thenReturn(testEmployee);
    when(employeeOrgDAO.findByEmployeeId(1L)).thenReturn(Arrays.asList(employeeOrg));
    when(orgUnitDAO.findByIds(anyList())).thenReturn(Arrays.asList(orgUnit));
    when(employeeMapper.toDetailVO(testEmployee)).thenReturn(employeeDetailVO);

    // When
    EmployeeDetailVO result = employeeService.findById(1L);

    // Then
    assertNotNull(result);
    assertEquals(1L, result.id());
    verify(employeeDAO).findById(1L);
    verify(employeeOrgDAO).findByEmployeeId(1L);
    verify(orgUnitDAO).findByIds(anyList());
  }

  @Test
  @DisplayName("查询员工详情时员工不存在应该抛出异常")
  void should_throw_exception_when_find_nonexistent_employee() {
    // Given
    when(employeeDAO.findById(999L)).thenReturn(null);

    // When & Then
    BusinessException exception =
        assertThrows(BusinessException.class, () -> employeeService.findById(999L));
    assertEquals(ErrorCode.EMPLOYEE_NOT_FOUND.getCode(), exception.getCode());
  }

  @Test
  @DisplayName("应该成功分页查询员工")
  void should_find_employees_by_page() {
    // Given
    EmployeeQueryDTO queryDTO = new EmployeeQueryDTO("张三", "ACTIVE", 100L);
    List<HrEmployee> employees = Arrays.asList(testEmployee);
    when(employeeDAO.findByCondition("张三", "ACTIVE", 100L, 1, 10)).thenReturn(employees);
    when(employeeDAO.countByCondition("张三", "ACTIVE", 100L)).thenReturn(1L);
    when(employeeMapper.toVO(testEmployee)).thenReturn(employeeVO);

    // When
    PageResult<EmployeeVO> result = employeeService.findPage(queryDTO, 1, 10);

    // Then
    assertNotNull(result);
    assertEquals(1, result.items().size());
    assertEquals(1L, result.total());
    assertEquals(1, result.page());
    assertEquals(10, result.size());
  }
}
