package io.github.lystrosaurus.admin.system.role.infrastructure.acl;

import io.github.lystrosaurus.admin.system.role.domain.acl.RoleQueryPort;
import io.github.lystrosaurus.admin.system.role.domain.acl.RoleQueryRepository;
import io.github.lystrosaurus.admin.shared.infrastructure.cache.TwoLevelCache;
import io.github.lystrosaurus.admin.system.role.domain.acl.RoleSummary;
import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import tools.jackson.core.type.TypeReference;

/** 角色查询端口的跨上下文实现。 */
@Repository
@RequiredArgsConstructor
public class RoleQueryPortImpl implements RoleQueryPort {

  private static final String CACHE_NAME = "role:user";
  private static final TypeReference<List<RoleSummary>> ROLE_LIST = new TypeReference<>() {};
  private static final Duration L1_TTL = Duration.ofSeconds(60);
  private static final Duration L2_TTL = Duration.ofSeconds(300);

  private final RoleQueryRepository queryRepository;
  private final TwoLevelCache twoLevelCache;

  @Override
  public List<RoleSummary> findByUserId(Long userId) {
    return twoLevelCache.get(
        CACHE_NAME,
        userId,
        ROLE_LIST,
        L1_TTL,
        L2_TTL,
        () ->
            queryRepository.findByUserId(userId).stream()
                .map(
                    role ->
                        new RoleSummary(
                            role.id().value(),
                            role.code(),
                            role.name(),
                            role.description(),
                            role.status(),
                            role.dataScopeType()))
                .toList());
  }
}
