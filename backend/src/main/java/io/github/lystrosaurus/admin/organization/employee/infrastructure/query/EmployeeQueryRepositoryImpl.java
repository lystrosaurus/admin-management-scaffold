package io.github.lystrosaurus.admin.organization.employee.infrastructure.query;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.lystrosaurus.admin.organization.employee.domain.acl.EmployeeQueryRepository;
import io.github.lystrosaurus.admin.organization.employee.domain.model.Employee;
import io.github.lystrosaurus.admin.organization.employee.domain.model.EmployeeOrgAssignment;
import io.github.lystrosaurus.admin.organization.employee.infrastructure.converter.EmployeePoConverter;
import io.github.lystrosaurus.admin.organization.employee.infrastructure.persistence.mapper.EmployeeMpMapper;
import io.github.lystrosaurus.admin.organization.employee.infrastructure.persistence.mapper.EmployeeOrgMpMapper;
import io.github.lystrosaurus.admin.organization.employee.infrastructure.persistence.po.EmployeeOrgPO;
import io.github.lystrosaurus.admin.organization.employee.infrastructure.persistence.po.EmployeePO;
import io.github.lystrosaurus.admin.shared.domain.acl.Page;
import io.github.lystrosaurus.admin.shared.domain.acl.PageQuery;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

/** 员工查询端口的 MyBatis-Plus 实现（读模型侧）。 */
@Repository
@RequiredArgsConstructor
public class EmployeeQueryRepositoryImpl implements EmployeeQueryRepository {

  private final EmployeeMpMapper employeeMpMapper;
  private final EmployeeOrgMpMapper employeeOrgMpMapper;
  private final EmployeePoConverter converter;

  @Override
  public Optional<Employee> findByEmployeeNo(String employeeNo) {
    return Optional.ofNullable(
            employeeMpMapper.selectOne(
                new LambdaQueryWrapper<EmployeePO>().eq(EmployeePO::getEmployeeNo, employeeNo)))
        .map(converter::toDomain);
  }

  @Override
  public Page<Employee> findPage(
      String keyword, String employmentStatus, Long primaryOrgId, PageQuery pageQuery) {
    com.baomidou.mybatisplus.extension.plugins.pagination.Page<EmployeePO> mpPage =
        new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(
            pageQuery.pageNum(), pageQuery.pageSize());
    LambdaQueryWrapper<EmployeePO> wrapper =
        buildConditionWrapper(keyword, employmentStatus, primaryOrgId);
    com.baomidou.mybatisplus.extension.plugins.pagination.Page<EmployeePO> result =
        employeeMpMapper.selectPage(mpPage, wrapper);
    List<Employee> records = result.getRecords().stream().map(converter::toDomain).toList();
    return new Page<>(records, result.getTotal(), pageQuery.pageNum(), pageQuery.pageSize());
  }

  @Override
  public long countByCondition(String keyword, String employmentStatus, Long primaryOrgId) {
    return employeeMpMapper.selectCount(
        buildConditionWrapper(keyword, employmentStatus, primaryOrgId));
  }

  @Override
  public List<Employee> findByIds(Collection<Long> ids) {
    if (ids == null || ids.isEmpty()) {
      return List.of();
    }
    return employeeMpMapper.selectBatchIds(ids).stream().map(converter::toDomain).toList();
  }

  @Override
  public List<EmployeeOrgAssignment> findOrgsByEmployeeId(Long employeeId) {
    return employeeOrgMpMapper
        .selectList(
            new LambdaQueryWrapper<EmployeeOrgPO>().eq(EmployeeOrgPO::getEmployeeId, employeeId))
        .stream()
        .map(converter::toAssignment)
        .toList();
  }

  /**
   * 构建条件查询 Wrapper
   *
   * @param keyword 关键词(模糊匹配姓名/工号/手机号)
   * @param employmentStatus 在职状态
   * @param primaryOrgId 主组织ID
   * @return LambdaQueryWrapper
   */
  private LambdaQueryWrapper<EmployeePO> buildConditionWrapper(
      String keyword, String employmentStatus, Long primaryOrgId) {
    LambdaQueryWrapper<EmployeePO> wrapper = new LambdaQueryWrapper<>();

    if (StringUtils.hasText(keyword)) {
      wrapper.and(
          w ->
              w.like(EmployeePO::getName, keyword)
                  .or()
                  .like(EmployeePO::getEmployeeNo, keyword)
                  .or()
                  .like(EmployeePO::getMobile, keyword));
    }

    if (StringUtils.hasText(employmentStatus)) {
      wrapper.eq(EmployeePO::getEmploymentStatus, employmentStatus);
    }

    if (primaryOrgId != null) {
      wrapper.eq(EmployeePO::getPrimaryOrgId, primaryOrgId);
    }

    wrapper.orderByDesc(EmployeePO::getCreatedAt);
    return wrapper;
  }
}
