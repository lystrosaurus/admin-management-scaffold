package io.github.lystrosaurus.admin.shared.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 聚合级分布式锁注解（基于 Redisson RLock，由 {@code AggregateLockAspect} 实现）。 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AggregateLock {

  /** 锁键前缀，用于区分不同聚合操作。 */
  String prefix();

  /** SpEL 表达式，引用方法参数（如 {@code #id}）。 */
  String key() default "#id";

  /** 获取锁的最长等待时间（秒）。 */
  long waitSeconds() default 3;

  /** 锁租约时间（秒）。 */
  long leaseSeconds() default 10;
}
