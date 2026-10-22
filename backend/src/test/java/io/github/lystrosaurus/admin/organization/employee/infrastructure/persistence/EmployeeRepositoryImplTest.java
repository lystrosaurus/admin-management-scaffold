package io.github.lystrosaurus.admin.organization.employee.infrastructure.persistence;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.github.lystrosaurus.admin.organization.employee.domain.model.Employee;
import io.github.lystrosaurus.admin.organization.employee.domain.model.EmployeeId;
import io.github.lystrosaurus.admin.organization.employee.infrastructure.converter.EmployeePoConverter;
import io.github.lystrosaurus.admin.organization.employee.infrastructure.persistence.mapper.EmployeeMpMapper;
import io.github.lystrosaurus.admin.organization.employee.infrastructure.persistence.po.EmployeePO;
import io.github.lystrosaurus.admin.shared.domain.acl.DomainEventPublisher;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** EmployeeRepositoryImpl 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EmployeeRepositoryImpl 测试")
class EmployeeRepositoryImplTest {

  @Mock private EmployeeMpMapper employeeMapper;

  @Mock private DomainEventPublisher eventPublisher;

  private EmployeeRepositoryImpl employeeRepository;

  private Employee testEmployee;

  private EmployeePO testPo;

  @BeforeEach
  void setUp() {
    EmployeePoConverter converter = new EmployeePoConverter();
    employeeRepository = new EmployeeRepositoryImpl(employeeMapper, eventPublisher, converter);
    testEmployee =
        Employee.restore(
            EmployeeId.of(1L),
            "EMP001",
            "张三",
            null,
            "13800138000",
            "zhangsan@example.com",
            100L,
            null,
            "高级工程师",
            "ACTIVE",
            LocalDate.of(2024, 1, 15),
            null,
            "MANUAL",
            null,
            null);
    testPo = converter.toPo(testEmployee);
  }

  @Test
  @DisplayName("应该根据ID查找员工")
  void should_find_employee_by_id() {
    when(employeeMapper.selectById(1L)).thenReturn(testPo);

    Optional<Employee> result = employeeRepository.findById(EmployeeId.of(1L));

    assertTrue(result.isPresent());
    assertEquals("EMP001", result.get().employeeNo());
    assertEquals("张三", result.get().name());
    verify(employeeMapper).selectById(1L);
  }

  @Test
  @DisplayName("应该保存员工")
  void should_save_employee() {
    when(employeeMapper.exists(any(QueryWrapper.class))).thenReturn(false);
    when(employeeMapper.insert(any(EmployeePO.class))).thenReturn(1);

    employeeRepository.save(testEmployee);

    verify(employeeMapper).insert(any(EmployeePO.class));
    verify(eventPublisher).publishAll(testEmployee);
  }

  @Test
  @DisplayName("应该更新员工")
  void should_update_employee() {
    when(employeeMapper.exists(any(QueryWrapper.class))).thenReturn(true);
    when(employeeMapper.updateById(any(EmployeePO.class))).thenReturn(1);

    employeeRepository.save(testEmployee);

    verify(employeeMapper).updateById(any(EmployeePO.class));
  }

  @Test
  @DisplayName("应该根据ID删除员工")
  void should_delete_employee_by_id() {
    when(employeeMapper.deleteById(1L)).thenReturn(1);

    employeeRepository.deleteById(EmployeeId.of(1L));

    verify(employeeMapper).deleteById(1L);
  }

  @Test
  @DisplayName("应该检查工号是否存在")
  void should_check_employee_no_exists() {
    when(employeeMapper.selectCount(any())).thenReturn(1L);

    assertTrue(employeeRepository.existsByEmployeeNo("EMP001"));
  }

  @Test
  @DisplayName("应该检查工号不存在时返回false")
  void should_return_false_when_employee_no_not_exists() {
    when(employeeMapper.selectCount(any())).thenReturn(0L);

    assertFalse(employeeRepository.existsByEmployeeNo("EMP999"));
  }

  @Test
  @DisplayName("应该检查工号是否存在且排除指定ID")
  void should_check_employee_no_exists_excluding_id() {
    when(employeeMapper.selectCount(any())).thenReturn(1L);

    assertTrue(employeeRepository.existsByEmployeeNoAndIdNot("EMP001", EmployeeId.of(2L)));
  }
}
