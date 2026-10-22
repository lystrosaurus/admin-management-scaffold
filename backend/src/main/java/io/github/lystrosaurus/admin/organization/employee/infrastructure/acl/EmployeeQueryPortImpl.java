package io.github.lystrosaurus.admin.organization.employee.infrastructure.acl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.lystrosaurus.admin.organization.employee.domain.acl.EmployeeQueryPort;
import io.github.lystrosaurus.admin.organization.employee.infrastructure.persistence.mapper.EmployeeMpMapper;
import io.github.lystrosaurus.admin.organization.employee.infrastructure.persistence.po.EmployeePO;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** 员工查询端口的防腐层实现：向其它上下文暴露最小只读契约。 */
@Component
@RequiredArgsConstructor
public class EmployeeQueryPortImpl implements EmployeeQueryPort {

  private final EmployeeMpMapper employeeMpMapper;

  @Override
  public boolean existsById(Long employeeId) {
    return employeeMpMapper.selectById(employeeId) != null;
  }

  @Override
  public Map<Long, String> findNamesByIds(Collection<Long> employeeIds) {
    if (employeeIds == null || employeeIds.isEmpty()) {
      return Map.of();
    }
    List<EmployeePO> employees = employeeMpMapper.selectBatchIds(employeeIds);
    return employees.stream()
        .collect(Collectors.toMap(EmployeePO::getId, EmployeePO::getName, (a, b) -> a));
  }

  @Override
  public long countAll() {
    return employeeMpMapper.selectCount(new LambdaQueryWrapper<>());
  }
}
