package io.github.lystrosaurus.admin.shared.infrastructure.cache;

import io.github.lystrosaurus.admin.shared.domain.acl.CacheInvalidationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** 缓存失效端口的实现，委托给 {@link TwoLevelCache}。 */
@Component
@RequiredArgsConstructor
public class CacheInvalidationAdapter implements CacheInvalidationPort {

  private final TwoLevelCache twoLevelCache;

  @Override
  public void evict(String cacheName, Object key) {
    twoLevelCache.evict(cacheName, key);
  }

  @Override
  public void evictAll(String cacheName) {
    twoLevelCache.evictAll(cacheName);
  }
}
