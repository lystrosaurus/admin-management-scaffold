package io.github.lystrosaurus.admin.shared.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 写接口幂等注解（基于 Redis SETNX，由 {@code IdempotentAspect} 实现）。 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Idempotent {

  /** 幂等键前缀，用于区分不同接口。 */
  String prefix();

  /** 幂等键保留时长（秒）。 */
  int ttlSeconds() default 10;

  /** 重复请求时的错误消息。 */
  String message() default "重复请求，请稍后再试";
}
