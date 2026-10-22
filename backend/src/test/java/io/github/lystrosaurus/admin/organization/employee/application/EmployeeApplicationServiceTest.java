package io.github.lystrosaurus.admin.organization.employee.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.lystrosaurus.admin.organization.employee.adapter.response.EmployeeResponse;
import io.github.lystrosaurus.admin.organization.employee.application.assembler.EmployeeAssembler;
import io.github.lystrosaurus.admin.organization.employee.application.command.EmployeeCreateCommand;
import io.github.lystrosaurus.admin.organization.employee.domain.acl.EmployeeQueryRepository;
import io.github.lystrosaurus.admin.organization.employee.domain.acl.EmployeeRepository;
import io.github.lystrosaurus.admin.organization.employee.domain.model.Employee;
import io.github.lystrosaurus.admin.organization.employee.domain.model.EmployeeErrorCode;
import io.github.lystrosaurus.admin.organization.employee.domain.model.EmployeeId;
import io.github.lystrosaurus.admin.organization.orgunit.domain.acl.OrgUnitQueryPort;
import io.github.lystrosaurus.admin.shared.domain.acl.IdGenerator;
import io.github.lystrosaurus.admin.shared.domain.exception.DomainException;
import io.github.lystrosaurus.admin.system.user.domain.acl.UserRepository;
import io.github.lystrosaurus.admin.system.user.domain.model.User;
import io.github.lystrosaurus.admin.system.user.domain.model.UserId;
import io.github.lystrosaurus.admin.system.user.domain.model.UserStatus;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

/** EmployeeApplicationService 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("员工应用服务测试")
class EmployeeApplicationServiceTest {

  @Mock private EmployeeRepository employeeRepository;

  @Mock private EmployeeQueryRepository employeeQueryRepository;

  @Mock private OrgUnitQueryPort orgUnitQueryPort;

  @Mock private UserRepository userRepository;

  @Mock private IdGenerator idGenerator;

  @Spy private EmployeeAssembler assembler = new EmployeeAssembler();

  private EmployeeApplicationService employeeApplicationService;

  private Employee testEmployee;

  @BeforeEach
  void setUp() {
    employeeApplicationService =
        new EmployeeApplicationService(
            employeeRepository,
            employeeQueryRepository,
            orgUnitQueryPort,
            userRepository,
            idGenerator,
            assembler);
    testEmployee =
        Employee.restore(
            EmployeeId.of(1L),
            "EMP001",
            "张三",
            null,
            "13800138000",
            "zhangsan@example.com",
            null,
            10L,
            "高级工程师",
            "ACTIVE",
            null,
            null,
            "MANUAL",
            null,
            null);
  }

  @Test
  @DisplayName("应该成功删除员工")
  void should_delete_employee_successfully() {
    when(employeeRepository.findById(EmployeeId.of(1L))).thenReturn(Optional.of(testEmployee));

    employeeApplicationService.deleteById(1L);

    verify(employeeRepository).deleteById(EmployeeId.of(1L));
  }

  @Test
  @DisplayName("删除不存在的员工应该抛出异常")
  void should_throw_exception_when_employee_not_found() {
    when(employeeRepository.findById(EmployeeId.of(999L))).thenReturn(Optional.empty());

    DomainException exception =
        assertThrows(DomainException.class, () -> employeeApplicationService.deleteById(999L));
    assertEquals(EmployeeErrorCode.EMPLOYEE_NOT_FOUND.code(), exception.code());
  }

  @Test
  @DisplayName("创建员工应回写关联用户名")
  void should_resolve_username_on_create() {
    // Given
    EmployeeCreateCommand command =
        new EmployeeCreateCommand("EMP001", "张三", null, null, null, null, null, 10L);
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

    when(employeeRepository.existsByEmployeeNo("EMP001")).thenReturn(false);
    when(idGenerator.nextId()).thenReturn(1L);
    when(userRepository.findById(UserId.of(10L))).thenReturn(Optional.of(user));
    when(employeeRepository.findById(EmployeeId.of(1L))).thenReturn(Optional.of(testEmployee));

    // When
    EmployeeResponse response = employeeApplicationService.create(command);

    // Then
    assertEquals(10L, response.userId());
    assertEquals("linked_user", response.username());
    verify(employeeRepository).save(any(Employee.class));
  }
}
