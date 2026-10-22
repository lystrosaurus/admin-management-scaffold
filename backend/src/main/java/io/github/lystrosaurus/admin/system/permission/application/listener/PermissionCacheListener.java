package io.github.lystrosaurus.admin.system.permission.application.listener;

import io.github.lystrosaurus.admin.shared.domain.acl.CacheInvalidationPort;
import io.github.lystrosaurus.admin.system.permission.domain.event.PermissionCreated;
import io.github.lystrosaurus.admin.system.permission.domain.event.PermissionUpdated;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/** 权限变更后的缓存失效监听者：事务提交后清理用户权限码缓存。 */
@Component
@RequiredArgsConstructor
public class PermissionCacheListener {

  static final String PERMISSION_CODES_CACHE = "perm:codes:user";

  private final CacheInvalidationPort cacheInvalidationPort;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
  public void onPermissionCreated(PermissionCreated event) {
    cacheInvalidationPort.evictAll(PERMISSION_CODES_CACHE);
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
  public void onPermissionUpdated(PermissionUpdated event) {
    cacheInvalidationPort.evictAll(PERMISSION_CODES_CACHE);
  }
}
