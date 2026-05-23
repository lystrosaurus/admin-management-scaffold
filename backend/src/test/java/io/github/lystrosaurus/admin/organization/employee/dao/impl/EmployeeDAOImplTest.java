package io.github.lystrosaurus.admin.organization.employee.dao.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.lystrosaurus.admin.organization.employee.entity.HrEmployee;
import io.github.lystrosaurus.admin.organization.employee.mapper.HrEmployeeMapper;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** EmployeeDAOImpl 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EmployeeDAOImpl 测试")
class EmployeeDAOImplTest {

  @Mock private HrEmployeeMapper employeeMapper;

  @InjectMocks private EmployeeDAOImpl employeeDAO;

  private HrEmployee testEmployee;

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
  }

  @Test
  @DisplayName("应该根据ID查找员工")
  void should_find_employee_by_id() {
    when(employeeMapper.selectById(1L)).thenReturn(testEmployee);

    HrEmployee result = employeeDAO.findById(1L);

    assertNotNull(result);
    assertEquals("EMP001", result.getEmployeeNo());
    assertEquals("张三", result.getName());
    verify(employeeMapper).selectById(1L);
  }

  @Test
  @DisplayName("应该根据工号查找员工")
  void should_find_employee_by_employee_no() {
    when(employeeMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testEmployee);

    HrEmployee result = employeeDAO.findByEmployeeNo("EMP001");

    assertNotNull(result);
    assertEquals("EMP001", result.getEmployeeNo());
    verify(employeeMapper).selectOne(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该保存员工")
  void should_save_employee() {
    when(employeeMapper.insert(any(HrEmployee.class))).thenReturn(1);

    employeeDAO.save(testEmployee);

    verify(employeeMapper).insert(testEmployee);
  }

  @Test
  @DisplayName("应该更新员工")
  void should_update_employee() {
    when(employeeMapper.updateById(any(HrEmployee.class))).thenReturn(1);

    employeeDAO.update(testEmployee);

    verify(employeeMapper).updateById(testEmployee);
  }

  @Test
  @DisplayName("应该根据ID删除员工")
  void should_delete_employee_by_id() {
    when(employeeMapper.deleteById(1L)).thenReturn(1);

    employeeDAO.deleteById(1L);

    verify(employeeMapper).deleteById(1L);
  }

  @Test
  @DisplayName("应该根据条件查找员工列表")
  void should_find_employees_by_condition() {
    // Mock 分页查询
    @SuppressWarnings("unchecked")
    Page<HrEmployee> page = mock(Page.class);
    when(page.getRecords()).thenReturn(Arrays.asList(testEmployee));
    when(employeeMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
        .thenReturn(page);

    List<HrEmployee> result = employeeDAO.findByCondition("张三", "ACTIVE", 100L, 1, 10);

    assertEquals(1, result.size());
    assertEquals("张三", result.get(0).getName());
    verify(employeeMapper).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该根据条件统计员工数量")
  void should_count_employees_by_condition() {
    when(employeeMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(5L);

    long count = employeeDAO.countByCondition("张三", "ACTIVE", 100L);

    assertEquals(5L, count);
    verify(employeeMapper).selectCount(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该检查工号是否存在")
  void should_check_employee_no_exists() {
    when(employeeMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

    boolean exists = employeeDAO.existsByEmployeeNo("EMP001");

    assertTrue(exists);
    verify(employeeMapper).selectCount(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该检查工号不存在时返回false")
  void should_return_false_when_employee_no_not_exists() {
    when(employeeMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

    boolean exists = employeeDAO.existsByEmployeeNo("EMP999");

    assertFalse(exists);
    verify(employeeMapper).selectCount(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该检查工号是否存在且排除指定ID")
  void should_check_employee_no_exists_excluding_id() {
    when(employeeMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

    boolean exists = employeeDAO.existsByEmployeeNoAndIdNot("EMP001", 2L);

    assertTrue(exists);
    verify(employeeMapper).selectCount(any(LambdaQueryWrapper.class));
  }
}
