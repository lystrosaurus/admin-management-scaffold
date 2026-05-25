package io.github.lystrosaurus.admin.organization.employee.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.organization.employee.dao.EmployeeDAO;
import io.github.lystrosaurus.admin.organization.employee.dao.EmployeeOrgDAO;
import io.github.lystrosaurus.admin.organization.employee.entity.HrEmployee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** EmployeeService 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("员工服务测试")
class EmployeeServiceTest {

  @Mock private EmployeeDAO employeeDAO;

  @Mock private EmployeeOrgDAO employeeOrgDAO;

  @Mock private io.github.lystrosaurus.admin.organization.orgunit.dao.OrgUnitDAO orgUnitDAO;

  @Mock
  private io.github.lystrosaurus.admin.organization.employee.mapstruct.EmployeeMapper
      employeeMapper;

  @InjectMocks private EmployeeService employeeService;

  private HrEmployee testEmployee;

  @BeforeEach
  void setUp() {
    testEmployee = new HrEmployee();
    testEmployee.setId(1L);
    testEmployee.setEmployeeNo("EMP001");
    testEmployee.setName("张三");
    testEmployee.setEmploymentStatus("ACTIVE");
  }

  @Test
  @DisplayName("应该成功删除员工")
  void should_delete_employee_successfully() {
    when(employeeDAO.findById(1L)).thenReturn(testEmployee);

    employeeService.deleteById(1L);

    verify(employeeDAO).deleteById(1L);
  }

  @Test
  @DisplayName("删除不存在的员工应该抛出异常")
  void should_throw_exception_when_employee_not_found() {
    when(employeeDAO.findById(999L)).thenReturn(null);

    BusinessException exception =
        assertThrows(BusinessException.class, () -> employeeService.deleteById(999L));
    assertEquals(ErrorCode.EMPLOYEE_NOT_FOUND.getCode(), exception.getCode());
  }
}
