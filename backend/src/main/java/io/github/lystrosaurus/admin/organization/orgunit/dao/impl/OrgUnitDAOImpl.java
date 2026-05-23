package io.github.lystrosaurus.admin.organization.orgunit.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.lystrosaurus.admin.organization.orgunit.dao.OrgUnitDAO;
import io.github.lystrosaurus.admin.organization.orgunit.entity.OrgUnit;
import io.github.lystrosaurus.admin.organization.orgunit.mapper.SysOrgUnitMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/** 组织单元数据访问对象实现 */
@Service
@RequiredArgsConstructor
public class OrgUnitDAOImpl implements OrgUnitDAO {

  private final SysOrgUnitMapper orgUnitMapper;

  @Override
  public OrgUnit findById(Long id) {
    return orgUnitMapper.selectById(id);
  }

  @Override
  public OrgUnit findByCode(String code) {
    return orgUnitMapper.selectOne(new LambdaQueryWrapper<OrgUnit>().eq(OrgUnit::getCode, code));
  }

  @Override
  public void save(OrgUnit orgUnit) {
    orgUnitMapper.insert(orgUnit);
  }

  @Override
  public void update(OrgUnit orgUnit) {
    orgUnitMapper.updateById(orgUnit);
  }

  @Override
  public void deleteById(Long id) {
    orgUnitMapper.deleteById(id);
  }

  @Override
  public List<OrgUnit> findByCondition(
      String keyword, String status, Long parentId, int page, int size) {
    Page<OrgUnit> pageParam = new Page<>(page, size);
    LambdaQueryWrapper<OrgUnit> wrapper = buildConditionWrapper(keyword, status, parentId);
    return orgUnitMapper.selectPage(pageParam, wrapper).getRecords();
  }

  @Override
  public long countByCondition(String keyword, String status, Long parentId) {
    LambdaQueryWrapper<OrgUnit> wrapper = buildConditionWrapper(keyword, status, parentId);
    return orgUnitMapper.selectCount(wrapper);
  }

  @Override
  public List<OrgUnit> findAll() {
    return orgUnitMapper.selectList(new LambdaQueryWrapper<OrgUnit>());
  }

  @Override
  public List<OrgUnit> findByParentId(Long parentId) {
    return orgUnitMapper.selectList(
        new LambdaQueryWrapper<OrgUnit>().eq(OrgUnit::getParentId, parentId));
  }

  @Override
  public List<OrgUnit> findByIds(List<Long> ids) {
    if (ids == null || ids.isEmpty()) {
      return List.of();
    }
    return orgUnitMapper.selectList(new LambdaQueryWrapper<OrgUnit>().in(OrgUnit::getId, ids));
  }

  @Override
  public boolean existsByCode(String code) {
    return orgUnitMapper.selectCount(new LambdaQueryWrapper<OrgUnit>().eq(OrgUnit::getCode, code))
        > 0;
  }

  @Override
  public boolean existsByCodeAndIdNot(String code, Long id) {
    return orgUnitMapper.selectCount(
            new LambdaQueryWrapper<OrgUnit>().eq(OrgUnit::getCode, code).ne(OrgUnit::getId, id))
        > 0;
  }

  @Override
  public boolean hasChildren(Long id) {
    return orgUnitMapper.selectCount(new LambdaQueryWrapper<OrgUnit>().eq(OrgUnit::getParentId, id))
        > 0;
  }

  /**
   * 构建条件查询 Wrapper
   *
   * @param keyword 关键词
   * @param status 状态
   * @param parentId 父节点ID
   * @return LambdaQueryWrapper
   */
  private LambdaQueryWrapper<OrgUnit> buildConditionWrapper(
      String keyword, String status, Long parentId) {
    LambdaQueryWrapper<OrgUnit> wrapper = new LambdaQueryWrapper<>();

    if (StringUtils.hasText(keyword)) {
      wrapper.and(w -> w.like(OrgUnit::getName, keyword).or().like(OrgUnit::getCode, keyword));
    }

    if (StringUtils.hasText(status)) {
      wrapper.eq(OrgUnit::getStatus, status);
    }

    if (parentId != null) {
      wrapper.eq(OrgUnit::getParentId, parentId);
    }

    wrapper.orderByAsc(OrgUnit::getSortOrder).orderByAsc(OrgUnit::getId);
    return wrapper;
  }
}
