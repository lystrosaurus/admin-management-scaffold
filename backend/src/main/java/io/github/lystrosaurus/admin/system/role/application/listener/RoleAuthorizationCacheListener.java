package io.github.lystrosaurus.admin.system.role.application.listener;

import io.github.lystrosaurus.admin.shared.domain.acl.CacheInvalidationPort;
import io.github.lystrosaurus.admin.system.role.domain.event.RoleMenuChanged;
import io.github.lystrosaurus.admin.system.role.domain.event.RolePermissionChanged;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/** 角色授权变更后的缓存失效监听者：事务提交后清理权限、角色、菜单缓存。 */
@Component
@RequiredArgsConstructor
public class RoleAuthorizationCacheListener {

  static final String PERMISSION_CODES_CACHE = "perm:codes:user";
  static final String ROLE_CACHE = "role:user";
  static final String MENU_CACHE = "menu:user";

  private final CacheInvalidationPort cacheInvalidationPort;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
  public void onRolePermissionChanged(RolePermissionChanged event) {
    evictAuthorizationCaches();
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
  public void onRoleMenuChanged(RoleMenuChanged event) {
    evictAuthorizationCaches();
  }

  private void evictAuthorizationCaches() {
    cacheInvalidationPort.evictAll(PERMISSION_CODES_CACHE);
    cacheInvalidationPort.evictAll(ROLE_CACHE);
    cacheInvalidationPort.evictAll(MENU_CACHE);
  }
}
