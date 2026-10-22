package io.github.lystrosaurus.admin.system.menu.application.listener;

import io.github.lystrosaurus.admin.shared.domain.acl.CacheInvalidationPort;
import io.github.lystrosaurus.admin.system.menu.domain.event.MenuCreated;
import io.github.lystrosaurus.admin.system.menu.domain.event.MenuUpdated;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/** 菜单变更后的缓存失效监听者：事务提交后清理用户菜单缓存。 */
@Component
@RequiredArgsConstructor
public class MenuCacheListener {

  static final String MENU_CACHE = "menu:user";

  private final CacheInvalidationPort cacheInvalidationPort;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
  public void onMenuCreated(MenuCreated event) {
    cacheInvalidationPort.evictAll(MENU_CACHE);
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
  public void onMenuUpdated(MenuUpdated event) {
    cacheInvalidationPort.evictAll(MENU_CACHE);
  }
}
