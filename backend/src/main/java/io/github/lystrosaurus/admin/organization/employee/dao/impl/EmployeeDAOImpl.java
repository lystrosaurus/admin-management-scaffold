package io.github.lystrosaurus.admin.organization.employee.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.lystrosaurus.admin.organization.employee.dao.EmployeeDAO;
import io.github.lystrosaurus.admin.organization.employee.entity.HrEmployee;
import io.github.lystrosaurus.admin.organization.employee.mapper.HrEmployeeMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 员工数据访问对象实现
 *
 * <p>使用 MyBatis-Plus Wrapper 构建查询条件。
 */
@Service
@RequiredArgsConstructor
public class EmployeeDAOImpl implements EmployeeDAO {

  private final HrEmployeeMapper employeeMapper;

  @Override
  public HrEmployee findById(Long id) {
    return employeeMapper.selectById(id);
  }

  @Override
  public HrEmployee findByEmployeeNo(String employeeNo) {
    return employeeMapper.selectOne(
        new LambdaQueryWrapper<HrEmployee>().eq(HrEmployee::getEmployeeNo, employeeNo));
  }

  @Override
  public void save(HrEmployee employee) {
    employeeMapper.insert(employee);
  }

  @Override
  public void update(HrEmployee employee) {
    employeeMapper.updateById(employee);
  }

  @Override
  public void deleteById(Long id) {
    employeeMapper.deleteById(id);
  }

  @Override
  public List<HrEmployee> findByCondition(
      String keyword, String employmentStatus, Long orgId, int page, int size) {
    Page<HrEmployee> pageParam = new Page<>(page, size);
    LambdaQueryWrapper<HrEmployee> wrapper =
        buildConditionWrapper(keyword, employmentStatus, orgId);
    return employeeMapper.selectPage(pageParam, wrapper).getRecords();
  }

  @Override
  public long countByCondition(String keyword, String employmentStatus, Long orgId) {
    LambdaQueryWrapper<HrEmployee> wrapper =
        buildConditionWrapper(keyword, employmentStatus, orgId);
    return employeeMapper.selectCount(wrapper);
  }

  @Override
  public boolean existsByEmployeeNo(String employeeNo) {
    return employeeMapper.selectCount(
            new LambdaQueryWrapper<HrEmployee>().eq(HrEmployee::getEmployeeNo, employeeNo))
        > 0;
  }

  @Override
  public boolean existsByEmployeeNoAndIdNot(String employeeNo, Long id) {
    return employeeMapper.selectCount(
            new LambdaQueryWrapper<HrEmployee>()
                .eq(HrEmployee::getEmployeeNo, employeeNo)
                .ne(HrEmployee::getId, id))
        > 0;
  }

  @Override
  public boolean existsById(Long id) {
    return employeeMapper.selectCount(
            new LambdaQueryWrapper<HrEmployee>().eq(HrEmployee::getId, id))
        > 0;
  }

  /**
   * 构建条件查询 Wrapper
   *
   * @param keyword 关键词(模糊匹配姓名/工号/手机号)
   * @param employmentStatus 在职状态
   * @param orgId 主组织ID
   * @return LambdaQueryWrapper
   */
  private LambdaQueryWrapper<HrEmployee> buildConditionWrapper(
      String keyword, String employmentStatus, Long orgId) {
    LambdaQueryWrapper<HrEmployee> wrapper = new LambdaQueryWrapper<>();

    if (StringUtils.hasText(keyword)) {
      wrapper.and(
          w ->
              w.like(HrEmployee::getName, keyword)
                  .or()
                  .like(HrEmployee::getEmployeeNo, keyword)
                  .or()
                  .like(HrEmployee::getMobile, keyword));
    }

    if (StringUtils.hasText(employmentStatus)) {
      wrapper.eq(HrEmployee::getEmploymentStatus, employmentStatus);
    }

    if (orgId != null) {
      wrapper.eq(HrEmployee::getPrimaryOrgId, orgId);
    }

    wrapper.orderByDesc(HrEmployee::getCreatedAt);
    return wrapper;
  }
}
