package io.github.lystrosaurus.admin.shared.domain.acl;

/** 缓存失效端口：供 application 层在聚合变更后清理缓存，实现位于 infrastructure。 */
public interface CacheInvalidationPort {

  /** 失效单个缓存键 */
  void evict(String cacheName, Object key);

  /** 失效整个缓存命名空间 */
  void evictAll(String cacheName);
}
