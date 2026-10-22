package io.github.lystrosaurus.admin.system.permission.infrastructure.acl;

import io.github.lystrosaurus.admin.system.permission.domain.acl.PermissionQueryPort;
import io.github.lystrosaurus.admin.system.permission.domain.acl.PermissionQueryRepository;
import io.github.lystrosaurus.admin.system.permission.domain.acl.PermissionSummary;
import io.github.lystrosaurus.admin.system.permission.domain.model.Permission;
import io.github.lystrosaurus.admin.shared.infrastructure.cache.TwoLevelCache;
import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import tools.jackson.core.type.TypeReference;

/** 权限查询端口的跨上下文实现。 */
@Repository
@RequiredArgsConstructor
public class PermissionQueryPortImpl implements PermissionQueryPort {

  private static final String CACHE_NAME = "perm:codes:user";
  private static final TypeReference<List<String>> CODE_LIST = new TypeReference<>() {};
  private static final Duration L1_TTL = Duration.ofSeconds(60);
  private static final Duration L2_TTL = Duration.ofSeconds(300);

  private final PermissionQueryRepository queryRepository;
  private final TwoLevelCache twoLevelCache;

  @Override
  public List<String> findCodesByUserId(Long userId) {
    return twoLevelCache.get(
        CACHE_NAME,
        userId,
        CODE_LIST,
        L1_TTL,
        L2_TTL,
        () ->
            queryRepository.findByUserId(userId).stream()
                .map(Permission::code)
                .filter(code -> code != null && !code.isBlank())
                .toList());
  }

  @Override
  public List<PermissionSummary> findByRoleId(Long roleId) {
    return queryRepository.findByRoleId(roleId).stream()
        .map(
            permission ->
                new PermissionSummary(
                    permission.id().value(),
                    permission.code(),
                    permission.name(),
                    permission.type(),
                    permission.module(),
                    permission.resource(),
                    permission.action()))
        .toList();
  }
}
