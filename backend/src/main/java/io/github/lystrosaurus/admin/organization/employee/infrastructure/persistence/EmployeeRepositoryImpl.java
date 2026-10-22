package io.github.lystrosaurus.admin.organization.employee.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.lystrosaurus.admin.organization.employee.domain.acl.EmployeeRepository;
import io.github.lystrosaurus.admin.organization.employee.domain.model.Employee;
import io.github.lystrosaurus.admin.organization.employee.domain.model.EmployeeId;
import io.github.lystrosaurus.admin.organization.employee.infrastructure.converter.EmployeePoConverter;
import io.github.lystrosaurus.admin.organization.employee.infrastructure.persistence.mapper.EmployeeMpMapper;
import io.github.lystrosaurus.admin.organization.employee.infrastructure.persistence.po.EmployeePO;
import io.github.lystrosaurus.admin.shared.domain.acl.DomainEventPublisher;
import io.github.lystrosaurus.admin.shared.infrastructure.persistence.GenericRepositoryImpl;
import org.springframework.stereotype.Repository;

/** 员工仓储的 MyBatis-Plus 实现。 */
@Repository
public class EmployeeRepositoryImpl
    extends GenericRepositoryImpl<Employee, EmployeeId, EmployeePO, EmployeeMpMapper>
    implements EmployeeRepository {

  private final EmployeePoConverter converter;

  public EmployeeRepositoryImpl(
      EmployeeMpMapper mapper, DomainEventPublisher eventPublisher, EmployeePoConverter converter) {
    super(mapper, eventPublisher);
    this.converter = converter;
  }

  @Override
  protected EmployeePO toPo(Employee employee) {
    return converter.toPo(employee);
  }

  @Override
  protected Employee toDomain(EmployeePO po) {
    return converter.toDomain(po);
  }

  @Override
  public boolean existsByEmployeeNo(String employeeNo) {
    return mapper.selectCount(
            new LambdaQueryWrapper<EmployeePO>().eq(EmployeePO::getEmployeeNo, employeeNo))
        > 0;
  }

  @Override
  public boolean existsByEmployeeNoAndIdNot(String employeeNo, EmployeeId id) {
    return mapper.selectCount(
            new LambdaQueryWrapper<EmployeePO>()
                .eq(EmployeePO::getEmployeeNo, employeeNo)
                .ne(EmployeePO::getId, id.value()))
        > 0;
  }
}
