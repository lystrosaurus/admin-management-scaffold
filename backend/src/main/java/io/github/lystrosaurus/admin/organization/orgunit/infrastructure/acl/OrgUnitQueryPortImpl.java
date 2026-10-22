package io.github.lystrosaurus.admin.organization.orgunit.infrastructure.acl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.lystrosaurus.admin.organization.orgunit.domain.acl.OrgUnitQueryPort;
import io.github.lystrosaurus.admin.organization.orgunit.infrastructure.persistence.mapper.OrgUnitMpMapper;
import io.github.lystrosaurus.admin.organization.orgunit.infrastructure.persistence.po.OrgUnitPO;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** 组织单元查询端口的防腐层实现：向其它上下文暴露最小只读契约。 */
@Component
@RequiredArgsConstructor
public class OrgUnitQueryPortImpl implements OrgUnitQueryPort {

  private final OrgUnitMpMapper orgUnitMpMapper;

  @Override
  public Map<Long, String> findNamesByIds(Collection<Long> orgIds) {
    if (orgIds == null || orgIds.isEmpty()) {
      // 空 Map 必须允许以 null 为键查询（调用方可能传入未设置组织的 primaryOrgId），
      // Map.of() 在 get(null) 时会抛 NPE，故使用 Collections.emptyMap()。
      return Collections.emptyMap();
    }
    List<OrgUnitPO> orgUnits = orgUnitMpMapper.selectBatchIds(orgIds);
    return orgUnits.stream()
        .collect(Collectors.toMap(OrgUnitPO::getId, OrgUnitPO::getName, (a, b) -> a));
  }

  @Override
  public Set<Long> findDescendantIds(Long orgId) {
    List<OrgUnitPO> allOrgUnits =
        orgUnitMpMapper.selectList(
            new LambdaQueryWrapper<OrgUnitPO>().select(OrgUnitPO::getId, OrgUnitPO::getParentId));
    Map<Long, List<Long>> parentChildMap =
        allOrgUnits.stream()
            .collect(
                Collectors.groupingBy(
                    OrgUnitPO::getParentId, Collectors.mapping(OrgUnitPO::getId, Collectors.toList())));

    Set<Long> descendantIds = new HashSet<>();
    collectDescendantIds(parentChildMap, orgId, descendantIds);
    return descendantIds;
  }

  @Override
  public long countAll() {
    return orgUnitMpMapper.selectCount(new LambdaQueryWrapper<>());
  }

  private void collectDescendantIds(
      Map<Long, List<Long>> parentChildMap, Long parentId, Set<Long> result) {
    for (Long childId : parentChildMap.getOrDefault(parentId, List.of())) {
      if (result.add(childId)) {
        collectDescendantIds(parentChildMap, childId, result);
      }
    }
  }
}
