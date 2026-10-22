package io.github.lystrosaurus.admin.system.role.infrastructure.acl;

import io.github.lystrosaurus.admin.system.role.domain.acl.RoleQueryPort;
import io.github.lystrosaurus.admin.system.role.domain.acl.RoleQueryRepository;
import io.github.lystrosaurus.admin.system.role.domain.acl.RoleSummary;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/** 角色查询端口的跨上下文实现。 */
@Repository
@RequiredArgsConstructor
public class RoleQueryPortImpl implements RoleQueryPort {

  private final RoleQueryRepository queryRepository;

  @Override
  public List<RoleSummary> findByUserId(Long userId) {
    return queryRepository.findByUserId(userId).stream()
        .map(
            role ->
                new RoleSummary(
                    role.id().value(),
                    role.code(),
                    role.name(),
                    role.description(),
                    role.status(),
                    role.dataScopeType()))
        .toList();
  }
}
