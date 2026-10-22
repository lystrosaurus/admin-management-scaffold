package io.github.lystrosaurus.admin.system.permission.infrastructure.acl;

import io.github.lystrosaurus.admin.system.permission.domain.acl.PermissionQueryPort;
import io.github.lystrosaurus.admin.system.permission.domain.acl.PermissionQueryRepository;
import io.github.lystrosaurus.admin.system.permission.domain.acl.PermissionSummary;
import io.github.lystrosaurus.admin.system.permission.domain.model.Permission;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/** 权限查询端口的跨上下文实现。 */
@Repository
@RequiredArgsConstructor
public class PermissionQueryPortImpl implements PermissionQueryPort {

  private final PermissionQueryRepository queryRepository;

  @Override
  public List<String> findCodesByUserId(Long userId) {
    return queryRepository.findByUserId(userId).stream()
        .map(Permission::code)
        .filter(code -> code != null && !code.isBlank())
        .toList();
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
