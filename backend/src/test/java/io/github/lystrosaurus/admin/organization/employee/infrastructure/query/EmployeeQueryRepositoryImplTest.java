package io.github.lystrosaurus.admin.organization.employee.infrastructure.query;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.lystrosaurus.admin.organization.employee.domain.model.Employee;
import io.github.lystrosaurus.admin.organization.employee.domain.model.EmployeeId;
import io.github.lystrosaurus.admin.organization.employee.domain.model.EmployeeOrgAssignment;
import io.github.lystrosaurus.admin.organization.employee.infrastructure.converter.EmployeePoConverter;
import io.github.lystrosaurus.admin.organization.employee.infrastructure.persistence.mapper.EmployeeMpMapper;
import io.github.lystrosaurus.admin.organization.employee.infrastructure.persistence.mapper.EmployeeOrgMpMapper;
import io.github.lystrosaurus.admin.organization.employee.infrastructure.persistence.po.EmployeeOrgPO;
import io.github.lystrosaurus.admin.organization.employee.infrastructure.persistence.po.EmployeePO;
import io.github.lystrosaurus.admin.shared.domain.acl.PageQuery;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** EmployeeQueryRepositoryImpl 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EmployeeQueryRepositoryImpl 测试")
class EmployeeQueryRepositoryImplTest {

  @Mock private EmployeeMpMapper employeeMapper;

  @Mock private EmployeeOrgMpMapper employeeOrgMapper;

  private EmployeeQueryRepositoryImpl queryRepository;

  private EmployeePO testPo;

  @BeforeEach
  void setUp() {
    EmployeePoConverter converter = new EmployeePoConverter();
    queryRepository =
        new EmployeeQueryRepositoryImpl(employeeMapper, employeeOrgMapper, converter);
    testPo =
        converter.toPo(
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
                null));
  }

  @Test
  @DisplayName("应该根据工号查找员工")
  void should_find_employee_by_employee_no() {
    when(employeeMapper.selectOne(any())).thenReturn(testPo);

    Optional<Employee> result = queryRepository.findByEmployeeNo("EMP001");

    assertTrue(result.isPresent());
    assertEquals("EMP001", result.get().employeeNo());
    verify(employeeMapper).selectOne(any());
  }

  @Test
  @DisplayName("应该根据条件查找员工列表")
  void should_find_employees_by_condition() {
    Page<EmployeePO> page = new Page<>(1, 10);
    page.setRecords(List.of(testPo));
    page.setTotal(1L);
    when(employeeMapper.selectPage(any(Page.class), any())).thenReturn(page);

    io.github.lystrosaurus.admin.shared.domain.acl.Page<Employee> result =
        queryRepository.findPage("张三", "ACTIVE", 100L, PageQuery.of(1, 10));

    assertEquals(1, result.records().size());
    assertEquals("张三", result.records().get(0).name());
    assertEquals(1L, result.total());
    verify(employeeMapper).selectPage(any(Page.class), any());
  }

  @Test
  @DisplayName("应该根据条件统计员工数量")
  void should_count_employees_by_condition() {
    when(employeeMapper.selectCount(any())).thenReturn(5L);

    long count = queryRepository.countByCondition("张三", "ACTIVE", 100L);

    assertEquals(5L, count);
    verify(employeeMapper).selectCount(any());
  }

  @Test
  @DisplayName("应该根据ID集合批量查找员工")
  void should_find_employees_by_ids() {
    when(employeeMapper.selectBatchIds(anyCollection())).thenReturn(List.of(testPo));

    List<Employee> result = queryRepository.findByIds(List.of(1L));

    assertEquals(1, result.size());
    assertEquals("EMP001", result.get(0).employeeNo());
  }

  @Test
  @DisplayName("应该查询员工的全部组织关联")
  void should_find_orgs_by_employee_id() {
    EmployeeOrgPO orgPo = new EmployeeOrgPO();
    orgPo.setId(9L);
    orgPo.setEmployeeId(1L);
    orgPo.setOrgId(100L);
    orgPo.setIsPrimary(1);
    orgPo.setStatus("ACTIVE");
    when(employeeOrgMapper.selectList(any())).thenReturn(List.of(orgPo));

    List<EmployeeOrgAssignment> result = queryRepository.findOrgsByEmployeeId(1L);

    assertEquals(1, result.size());
    assertEquals(100L, result.get(0).orgId());
    assertEquals(1, result.get(0).isPrimary());
  }
}
