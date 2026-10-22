package io.github.lystrosaurus.admin.organization.orgunit.infrastructure.query;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.lystrosaurus.admin.organization.orgunit.domain.acl.OrgUnitQueryRepository;
import io.github.lystrosaurus.admin.organization.orgunit.domain.model.OrgUnit;
import io.github.lystrosaurus.admin.organization.orgunit.infrastructure.converter.OrgUnitPoConverter;
import io.github.lystrosaurus.admin.organization.orgunit.infrastructure.persistence.mapper.OrgUnitMpMapper;
import io.github.lystrosaurus.admin.organization.orgunit.infrastructure.persistence.po.OrgUnitPO;
import io.github.lystrosaurus.admin.shared.domain.acl.Page;
import io.github.lystrosaurus.admin.shared.domain.acl.PageQuery;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

/** 组织单元查询端口的 MyBatis-Plus 实现（读模型侧）。 */
@Repository
@RequiredArgsConstructor
public class OrgUnitQueryRepositoryImpl implements OrgUnitQueryRepository {

  private final OrgUnitMpMapper orgUnitMpMapper;
  private final OrgUnitPoConverter converter;

  @Override
  public Optional<OrgUnit> findByCode(String code) {
    return Optional.ofNullable(
            orgUnitMpMapper.selectOne(
                new LambdaQueryWrapper<OrgUnitPO>().eq(OrgUnitPO::getCode, code)))
        .map(converter::toDomain);
  }

  @Override
  public Page<OrgUnit> findPage(
      String keyword, String status, Long parentId, PageQuery pageQuery) {
    com.baomidou.mybatisplus.extension.plugins.pagination.Page<OrgUnitPO> mpPage =
        new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(
            pageQuery.pageNum(), pageQuery.pageSize());
    com.baomidou.mybatisplus.extension.plugins.pagination.Page<OrgUnitPO> result =
        orgUnitMpMapper.selectPage(mpPage, buildConditionWrapper(keyword, status, parentId));
    List<OrgUnit> records = result.getRecords().stream().map(converter::toDomain).toList();
    return new Page<>(records, result.getTotal(), pageQuery.pageNum(), pageQuery.pageSize());
  }

  @Override
  public long countByCondition(String keyword, String status, Long parentId) {
    return orgUnitMpMapper.selectCount(buildConditionWrapper(keyword, status, parentId));
  }

  @Override
  public List<OrgUnit> findAll() {
    return orgUnitMpMapper
        .selectList(new LambdaQueryWrapper<OrgUnitPO>().orderByAsc(OrgUnitPO::getSortOrder))
        .stream()
        .map(converter::toDomain)
        .toList();
  }

  @Override
  public List<OrgUnit> findByParentId(Long parentId) {
    return orgUnitMpMapper
        .selectList(
            new LambdaQueryWrapper<OrgUnitPO>()
                .eq(OrgUnitPO::getParentId, parentId)
                .orderByAsc(OrgUnitPO::getSortOrder))
        .stream()
        .map(converter::toDomain)
        .toList();
  }

  @Override
  public List<OrgUnit> findByIds(Collection<Long> ids) {
    if (ids == null || ids.isEmpty()) {
      return List.of();
    }
    return orgUnitMpMapper.selectBatchIds(ids).stream().map(converter::toDomain).toList();
  }

  /**
   * 构建条件查询 Wrapper
   *
   * @param keyword 关键词(模糊匹配名称/编码)
   * @param status 状态
   * @param parentId 父节点ID
   * @return LambdaQueryWrapper
   */
  private LambdaQueryWrapper<OrgUnitPO> buildConditionWrapper(
      String keyword, String status, Long parentId) {
    LambdaQueryWrapper<OrgUnitPO> wrapper = new LambdaQueryWrapper<>();

    if (StringUtils.hasText(keyword)) {
      wrapper.and(w -> w.like(OrgUnitPO::getName, keyword).or().like(OrgUnitPO::getCode, keyword));
    }

    if (StringUtils.hasText(status)) {
      wrapper.eq(OrgUnitPO::getStatus, status);
    }

    if (parentId != null) {
      wrapper.eq(OrgUnitPO::getParentId, parentId);
    }

    wrapper.orderByAsc(OrgUnitPO::getSortOrder);
    return wrapper;
  }
}
