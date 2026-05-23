package io.github.lystrosaurus.admin.organization.employee.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.lystrosaurus.admin.organization.employee.dao.EmployeeOrgDAO;
import io.github.lystrosaurus.admin.organization.employee.entity.EmployeeOrg;
import io.github.lystrosaurus.admin.organization.employee.mapper.SysEmployeeOrgMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** 员工-组织关联数据访问对象实现 */
@Service
@RequiredArgsConstructor
public class EmployeeOrgDAOImpl implements EmployeeOrgDAO {

  private final SysEmployeeOrgMapper employeeOrgMapper;

  @Override
  public void save(EmployeeOrg employeeOrg) {
    employeeOrgMapper.insert(employeeOrg);
  }

  @Override
  public List<EmployeeOrg> findByEmployeeId(Long employeeId) {
    return employeeOrgMapper.selectList(
        new LambdaQueryWrapper<EmployeeOrg>().eq(EmployeeOrg::getEmployeeId, employeeId));
  }

  @Override
  public List<EmployeeOrg> findByOrgId(Long orgId) {
    return employeeOrgMapper.selectList(
        new LambdaQueryWrapper<EmployeeOrg>().eq(EmployeeOrg::getOrgId, orgId));
  }

  @Override
  public void deleteByEmployeeId(Long employeeId) {
    employeeOrgMapper.delete(
        new LambdaQueryWrapper<EmployeeOrg>().eq(EmployeeOrg::getEmployeeId, employeeId));
  }

  @Override
  public void deleteByEmployeeIdAndOrgId(Long employeeId, Long orgId) {
    employeeOrgMapper.delete(
        new LambdaQueryWrapper<EmployeeOrg>()
            .eq(EmployeeOrg::getEmployeeId, employeeId)
            .eq(EmployeeOrg::getOrgId, orgId));
  }
}
