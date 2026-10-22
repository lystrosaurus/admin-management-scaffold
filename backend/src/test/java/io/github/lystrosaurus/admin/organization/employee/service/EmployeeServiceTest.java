package io.github.lystrosaurus.admin.organization.employee.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.organization.employee.dao.EmployeeDAO;
import io.github.lystrosaurus.admin.organization.employee.dao.EmployeeOrgDAO;
import io.github.lystrosaurus.admin.organization.employee.dto.EmployeeCreateDTO;
import io.github.lystrosaurus.admin.organization.employee.entity.HrEmployee;
import io.github.lystrosaurus.admin.organization.employee.vo.EmployeeVO;
import io.github.lystrosaurus.admin.system.user.domain.acl.UserRepository;
import io.github.lystrosaurus.admin.system.user.domain.model.User;
import io.github.lystrosaurus.admin.system.user.domain.model.UserId;
import io.github.lystrosaurus.admin.system.user.domain.model.UserStatus;
import java.util.Optional;
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

  @Mock private UserRepository userRepository;

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

  @Test
  @DisplayName("创建员工应回写关联用户名")
  void should_resolve_username_on_create() {
    // Given
    EmployeeCreateDTO dto = new EmployeeCreateDTO("EMP001", "张三", null, null, null, null, null, 10L);
    HrEmployee entity = new HrEmployee();
    entity.setId(1L);
    entity.setEmployeeNo("EMP001");
    entity.setName("张三");
    entity.setUserId(10L);
    User user =
        User.restore(
            UserId.of(10L),
            "linked_user",
            null,
            null,
            null,
            null,
            null,
            null,
            UserStatus.ENABLED,
            1,
            null,
            null,
            null,
            0);

    when(employeeDAO.existsByEmployeeNo("EMP001")).thenReturn(false);
    when(employeeMapper.toEntity(dto)).thenReturn(entity);
    when(userRepository.findById(UserId.of(10L))).thenReturn(Optional.of(user));

    // When
    EmployeeVO vo = employeeService.create(dto);

    // Then
    assertEquals(10L, vo.userId());
    assertEquals("linked_user", vo.username());
    verify(employeeDAO).save(entity);
  }
}
